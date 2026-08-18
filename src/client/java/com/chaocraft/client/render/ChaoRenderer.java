package com.chaocraft.client.render;

import com.chaocraft.ChaoCraft;
import com.chaocraft.client.render.mesh.ChaoMeshLoader;
import com.chaocraft.client.render.mesh.ChaoMeshModel;
import com.chaocraft.client.render.mesh.ChaoMorphTarget;
import com.chaocraft.entity.ChaoEntity;
import com.chaocraft.visual.ChaoMorphResolver;
import com.chaocraft.visual.ChaoMorphWeights;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * First real Chao renderer. It consumes the morph data compiled from the
 * Chao Viewer meshes and applies Viewer-compatible blend weights at runtime.
 */
public final class ChaoRenderer extends EntityRenderer<ChaoEntity> {
	private static final Identifier NEUTRAL_NORMAL_MODEL = ChaoCraft.id("models/chao/neutral_normal.cmesh");
	private static final Identifier WHITE_TEXTURE = ChaoCraft.id("textures/entity/chao/white.png");
	private static final float MODEL_SCALE = 0.18F;

	private ChaoMeshModel neutralNormalModel;
	private boolean modelLoadAttempted;
	private boolean modelLoadFailed;

	public ChaoRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.shadowRadius = 0.30F;
	}

	@Override
	public void render(ChaoEntity entity, float yaw, float tickDelta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light) {
		ChaoMeshModel model = getNeutralNormalModel();
		if (model == null) {
			renderFallback(matrices, vertexConsumers, light);
			super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
			return;
		}

		ChaoMorphWeights weights = ChaoMorphResolver.resolve(entity.getAppearanceState());
		float[] morphWeights = {
				weights.normal() / 100.0F,
				weights.swim() / 100.0F,
				weights.fly() / 100.0F,
				weights.run() / 100.0F,
				weights.power() / 100.0F,
				0.0F
		};

		matrices.push();
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - yaw));

		/*
		 * Keep MatrixStack free of reflections. Unity -> Minecraft handedness is
		 * converted explicitly per vertex below (Z is mirrored and triangle winding
		 * is reversed). A negative scale inside MatrixStack makes Minecraft's normal
		 * matrix take the reflected transform path and produced view-dependent
		 * lighting on the imported smooth normals.
		 */
		matrices.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);

		VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(WHITE_TEXTURE));
		for (ChaoMeshModel.Segment segment : model.segments()) {
			renderSegment(segment, morphWeights, matrices, vertices, light);
		}
		matrices.pop();

		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
	}

	private static void renderSegment(ChaoMeshModel.Segment segment, float[] weights,
			MatrixStack matrices, VertexConsumer vertices, int light) {
		MatrixStack.Entry entry = matrices.peek();
		Matrix4f positionMatrix = entry.getPositionMatrix();
		Matrix3f normalMatrix = entry.getNormalMatrix();
		int[] color = segmentColor(segment.name());

		int[] indices = segment.indices();
		if (indices.length % 3 != 0) {
			ChaoCraft.LOGGER.warn("Skipping malformed Chao mesh segment {}: index count {} is not divisible by 3",
					segment.name(), indices.length);
			return;
		}

		/*
		 * Unity stores these meshes as triangle lists, while Minecraft's standard
		 * entity RenderLayer uses QUADS. Feeding three vertices at a time caused
		 * unrelated triangles to be grouped into quads, producing the long spikes
		 * and holes seen in CP02. Emit each source triangle as A/B/C/C: Minecraft
		 * triangulates that quad into the original triangle plus one degenerate
		 * triangle, preserving the source topology without changing render state.
		 */
		for (int triangle = 0; triangle < indices.length; triangle += 3) {
			/*
			 * Mirroring Z changes handedness, so reverse B/C to preserve the original
			 * front-face orientation. A/C/B/B still represents one triangle through
			 * Minecraft's quad entity layer (the second generated triangle degenerates).
			 */
			emitVertex(segment, indices[triangle], weights, positionMatrix, normalMatrix, vertices, light, color);
			emitVertex(segment, indices[triangle + 2], weights, positionMatrix, normalMatrix, vertices, light, color);
			emitVertex(segment, indices[triangle + 1], weights, positionMatrix, normalMatrix, vertices, light, color);
			emitVertex(segment, indices[triangle + 1], weights, positionMatrix, normalMatrix, vertices, light, color);
		}
	}

	private static void emitVertex(ChaoMeshModel.Segment segment, int vertexIndex, float[] weights,
			Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertices, int light, int[] color) {
		int p = vertexIndex * 3;
		int uv = vertexIndex * 2;

		float x = segment.positions()[p];
		float y = segment.positions()[p + 1];
		float z = segment.positions()[p + 2];
		float nx = segment.normals()[p];
		float ny = segment.normals()[p + 1];
		float nz = segment.normals()[p + 2];

		for (int morph = 0; morph < weights.length; morph++) {
			float weight = weights[morph];
			if (weight == 0.0F) {
				continue;
			}
			float[] positionDelta = segment.morphPositionDeltas()[morph];
			float[] normalDelta = segment.morphNormalDeltas()[morph];
			x += positionDelta[p] * weight;
			y += positionDelta[p + 1] * weight;
			z += positionDelta[p + 2] * weight;
			nx += normalDelta[p] * weight;
			ny += normalDelta[p + 1] * weight;
			nz += normalDelta[p + 2] * weight;
		}

		/*
		 * Convert Unity handedness explicitly. Positions and normal vectors both
		 * mirror Z; triangle winding is reversed by renderSegment(). Morph deltas are
		 * already accumulated above, so the conversion applies to the final shape.
		 */
		z = -z;
		nz = -nz;

		/*
		 * Resolve the final normal before it reaches VertexConsumer. Keeping this
		 * explicit avoids relying on the reflected MatrixStack path and guarantees a
		 * unit world/view-space normal after entity rotation and model scaling.
		 */
		Vector3f transformedNormal = new Vector3f(nx, ny, nz);
		normalMatrix.transform(transformedNormal);
		if (transformedNormal.lengthSquared() > 0.000001F) {
			transformedNormal.normalize();
		} else {
			transformedNormal.set(0.0F, 1.0F, 0.0F);
		}

		vertices.vertex(positionMatrix, x, y, z)
				.color(color[0], color[1], color[2], 255)
				.texture(segment.uvs()[uv], 1.0F - segment.uvs()[uv + 1])
				.overlay(OverlayTexture.DEFAULT_UV)
				.light(light)
				.normal(transformedNormal.x(), transformedNormal.y(), transformedNormal.z())
				.next();
	}

	private static int[] segmentColor(String name) {
		if (name.contains("Belly")) {
			return new int[]{255, 239, 198};
		}
		if (name.contains("Wings")) {
			return new int[]{244, 231, 156};
		}
		return new int[]{255, 220, 122};
	}

	private ChaoMeshModel getNeutralNormalModel() {
		if (modelLoadAttempted) {
			return neutralNormalModel;
		}
		modelLoadAttempted = true;

		Optional<Resource> resource = MinecraftClient.getInstance().getResourceManager().getResource(NEUTRAL_NORMAL_MODEL);
		if (resource.isEmpty()) {
			failModelLoad("Missing Chao mesh resource: " + NEUTRAL_NORMAL_MODEL, null);
			return null;
		}

		try (InputStream input = resource.get().getInputStream()) {
			neutralNormalModel = ChaoMeshLoader.load(input);
			ChaoCraft.LOGGER.info("Loaded Chao morph mesh {} ({} segments)", NEUTRAL_NORMAL_MODEL,
					neutralNormalModel.segments().size());
			return neutralNormalModel;
		} catch (IOException exception) {
			failModelLoad("Failed to load Chao mesh: " + NEUTRAL_NORMAL_MODEL, exception);
			return null;
		}
	}

	private void failModelLoad(String message, Exception exception) {
		modelLoadFailed = true;
		if (exception == null) {
			ChaoCraft.LOGGER.error(message);
		} else {
			ChaoCraft.LOGGER.error(message, exception);
		}
	}

	private static void renderFallback(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		matrices.push();
		matrices.scale(0.58F, 0.58F, 0.58F);
		matrices.translate(-0.5D, 0.0D, -0.5D);
		MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(
				Blocks.SLIME_BLOCK.getDefaultState(), matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV
		);
		matrices.pop();
	}

	@Override
	public Identifier getTexture(ChaoEntity entity) {
		return WHITE_TEXTURE;
	}
}
