package com.chaocraft.client.render;

import com.chaocraft.ChaoCraft;
import com.chaocraft.client.render.mesh.ChaoMeshLoader;
import com.chaocraft.client.render.mesh.ChaoMeshModel;
import com.chaocraft.entity.ChaoEntity;
import com.chaocraft.visual.ChaoAppearanceState;
import com.chaocraft.visual.ChaoMorphResolver;
import com.chaocraft.visual.ChaoMorphWeights;
import com.chaocraft.visual.ChaoVisualType;
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
 * Morphable Chao renderer. CP03 adds the full Child mesh family and maps the
 * Viewer's 18 Child blend shapes from age, alignment, and evolution sliders.
 */
public final class ChaoRenderer extends EntityRenderer<ChaoEntity> {
	private static final Identifier CHILD_MODEL = ChaoCraft.id("models/chao/child.cmesh");
	private static final Identifier NEUTRAL_NORMAL_MODEL = ChaoCraft.id("models/chao/neutral_normal.cmesh");
	private static final Identifier WHITE_TEXTURE = ChaoCraft.id("textures/entity/chao/white.png");
	private static final float MODEL_SCALE = 0.18F;

	private ChaoMeshModel childModel;
	private ChaoMeshModel neutralNormalModel;
	private boolean childLoadAttempted;
	private boolean neutralNormalLoadAttempted;

	public ChaoRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.shadowRadius = 0.30F;
	}

	@Override
	public void render(ChaoEntity entity, float yaw, float tickDelta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light) {
		ChaoAppearanceState state = entity.getAppearanceState();
		ChaoMeshModel model = getModel(state.type());
		if (model == null) {
			renderFallback(matrices, vertexConsumers, light);
			super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
			return;
		}

		ChaoMorphWeights weights = ChaoMorphResolver.resolve(state);
		float[] morphWeights = buildMorphWeights(model, state.type(), weights);

		matrices.push();
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - yaw));
		// The extracted Child family is authored Z-up while the adult family is Y-up.
		// Bake the same -90° X correction used when inspecting the FBX in Blender.
		// After the Unity -> Minecraft Z mirror, this is +90° in render space.
		if (state.type() == ChaoVisualType.CHILD) {
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
		}
		matrices.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);

		VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(WHITE_TEXTURE));
		for (ChaoMeshModel.Segment segment : model.segments()) {
			renderSegment(segment, morphWeights, matrices, vertices, light);
		}
		matrices.pop();

		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
	}

	private static float[] buildMorphWeights(ChaoMeshModel model, ChaoVisualType type, ChaoMorphWeights weights) {
		float[] resolved = new float[model.morphNames().size()];
		if (type == ChaoVisualType.CHILD) {
			setMorph(model, resolved, "NNormal", weights.neutralNormal());
			setMorph(model, resolved, "NSwim", weights.neutralSwim());
			setMorph(model, resolved, "NFly", weights.neutralFly());
			setMorph(model, resolved, "NRun", weights.neutralRun());
			setMorph(model, resolved, "NPower", weights.neutralPower());
			setMorph(model, resolved, "HNB", weights.heroNeutralBaby());
			setMorph(model, resolved, "HNormal", weights.heroNormal());
			setMorph(model, resolved, "HSwim", weights.heroSwim());
			setMorph(model, resolved, "HFly", weights.heroFly());
			setMorph(model, resolved, "HRun", weights.heroRun());
			setMorph(model, resolved, "HPower", weights.heroPower());
			setMorph(model, resolved, "DNB", weights.darkNeutralBaby());
			setMorph(model, resolved, "DNormal", weights.darkNormal());
			setMorph(model, resolved, "DSwim", weights.darkSwim());
			setMorph(model, resolved, "DFly", weights.darkFly());
			setMorph(model, resolved, "DRun", weights.darkRun());
			setMorph(model, resolved, "DPower", weights.darkPower());
		} else {
			setMorph(model, resolved, "Normal", weights.normal());
			setMorph(model, resolved, "Swim", weights.swim());
			setMorph(model, resolved, "Fly", weights.fly());
			setMorph(model, resolved, "Run", weights.run());
			setMorph(model, resolved, "Power", weights.power());
		}
		// SizeDown remains zero until Animal Parts are integrated.
		return resolved;
	}

	private static void setMorph(ChaoMeshModel model, float[] resolved, String name, float percent) {
		int index = model.morphIndex(name);
		if (index >= 0) {
			resolved[index] = percent / 100.0F;
		}
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

		for (int triangle = 0; triangle < indices.length; triangle += 3) {
			// Unity -> Minecraft mirrors Z, so reverse B/C to preserve front faces.
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

		int morphCount = Math.min(weights.length, segment.morphPositionDeltas().length);
		for (int morph = 0; morph < morphCount; morph++) {
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

		z = -z;
		nz = -nz;

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

	private ChaoMeshModel getModel(ChaoVisualType type) {
		return switch (type) {
			case CHILD -> getChildModel();
			case NORMAL -> getNeutralNormalModel();
		};
	}

	private ChaoMeshModel getChildModel() {
		if (!childLoadAttempted) {
			childLoadAttempted = true;
			childModel = loadModel(CHILD_MODEL);
		}
		return childModel;
	}

	private ChaoMeshModel getNeutralNormalModel() {
		if (!neutralNormalLoadAttempted) {
			neutralNormalLoadAttempted = true;
			neutralNormalModel = loadModel(NEUTRAL_NORMAL_MODEL);
		}
		return neutralNormalModel;
	}

	private ChaoMeshModel loadModel(Identifier identifier) {
		Optional<Resource> resource = MinecraftClient.getInstance().getResourceManager().getResource(identifier);
		if (resource.isEmpty()) {
			ChaoCraft.LOGGER.error("Missing Chao mesh resource: {}", identifier);
			return null;
		}

		try (InputStream input = resource.get().getInputStream()) {
			ChaoMeshModel model = ChaoMeshLoader.load(input);
			ChaoCraft.LOGGER.info("Loaded Chao morph mesh {} ({} segments, {} morphs)", identifier,
					model.segments().size(), model.morphNames().size());
			return model;
		} catch (IOException exception) {
			ChaoCraft.LOGGER.error("Failed to load Chao mesh: {}", identifier, exception);
			return null;
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
