package com.chaocraft.client.render;

import com.chaocraft.ChaoCraft;
import com.chaocraft.client.render.cache.ChaoGpuRenderCache;
import com.chaocraft.client.render.cache.ChaoRenderCache;
import com.chaocraft.client.render.material.ChaoColor;
import com.chaocraft.client.render.material.ChaoPaletteResolver;
import com.chaocraft.client.render.material.ChaoPaletteState;
import com.chaocraft.client.render.mesh.ChaoMeshLoader;
import com.chaocraft.client.render.mesh.ChaoMeshModel;
import com.chaocraft.entity.ChaoEntity;
import com.chaocraft.visual.ChaoAppearanceState;
import com.chaocraft.visual.ChaoMorphResolver;
import com.chaocraft.visual.ChaoMorphWeights;
import com.chaocraft.visual.ChaoVisualType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Morphable Chao renderer.
 *
 * <p>CP04.2 turns the visual prototype into a persistent GPU/VBO renderer.
 * Morphing and material batching happen only when the visual state, lighting,
 * or baked body yaw changes; normal frames only issue cached VBO draw calls.</p>
 */
public final class ChaoRenderer extends EntityRenderer<ChaoEntity> {
    private static final Identifier CHILD_MODEL = ChaoCraft.id("models/chao/child.cmesh");
    private static final Identifier NEUTRAL_NORMAL_MODEL = ChaoCraft.id("models/chao/neutral_normal.cmesh");
    private static final Identifier WHITE_TEXTURE = ChaoCraft.id("textures/entity/chao/white.png");
    private static final Identifier BODY_MASK = ChaoCraft.id("textures/entity/chao/material/c_body.png");
    private static final Identifier CHILD_BODY_EXTRA_MASK = ChaoCraft.id("textures/entity/chao/material/hn_body2.png");
    private static final Identifier BELLY_MASK = ChaoCraft.id("textures/entity/chao/material/c_belly.png");
    private static final Identifier CHILD_BELLY_EXTRA_MASK = ChaoCraft.id("textures/entity/chao/material/c_belly_dark.png");
    private static final Identifier HORNS_MASK = ChaoCraft.id("textures/entity/chao/material/c_horns.png");
    private static final Identifier WINGS_MASK = ChaoCraft.id("textures/entity/chao/material/c_wings.png");
    private static final Identifier EYE_NORMAL = ChaoCraft.id("textures/entity/chao/face/eye01.png");
    private static final float MODEL_SCALE = 0.18F;

    private final ChaoRenderCache morphCache = new ChaoRenderCache();
    private final ChaoGpuRenderCache gpuCache = new ChaoGpuRenderCache();

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
        ChaoPaletteState palette = ChaoPaletteResolver.resolve(state, weights);
        ChaoRenderCache.Entry prepared = morphCache.get(entity, state, model, morphWeights, palette);

        // Quantizing to whole degrees prevents interpolated body-yaw values from
        // rebuilding large VBOs every render frame while remaining visually smooth.
        float bakedYaw = Math.round(yaw);
        long worldTick = entity.getWorld().getTime();
        ChaoGpuRenderCache.Entry gpuEntry = gpuCache.get(
                entity,
                state,
                model,
                light,
                bakedYaw,
                worldTick,
                () -> buildGpuBatches(model, prepared, state.type(), prepared.palette(), light, bakedYaw)
        );

        drawGpuBatches(gpuEntry, matrices);
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
        return resolved;
    }

    private static void setMorph(ChaoMeshModel model, float[] resolved, String name, float percent) {
        int index = model.morphIndex(name);
        if (index >= 0) {
            resolved[index] = percent / 100.0F;
        }
    }

    private static List<ChaoGpuRenderCache.DrawBatch> buildGpuBatches(ChaoMeshModel model,
            ChaoRenderCache.Entry prepared, ChaoVisualType type, ChaoPaletteState palette,
            int light, float bakedYaw) {
        RenderSystem.assertOnRenderThread();

        Map<BatchKey, List<DrawSource>> grouped = new LinkedHashMap<>();
        for (ChaoMeshModel.Segment segment : model.segments()) {
            ChaoRenderCache.PreparedSegment preparedSegment = prepared.segment(segment);
            for (int submeshIndex = 0; submeshIndex < segment.submeshes().size(); submeshIndex++) {
                MaterialRole role = resolveMaterialRole(type, segment.name(), submeshIndex);
                collectMaterialPasses(
                        grouped,
                        role,
                        type,
                        segment,
                        preparedSegment,
                        segment.submeshes().get(submeshIndex),
                        palette
                );
            }
        }

        Matrix4f localPositionMatrix = createLocalPositionMatrix(type, bakedYaw);
        Matrix3f localNormalMatrix = createLocalNormalMatrix(type, bakedYaw);
        List<ChaoGpuRenderCache.DrawBatch> batches = new ArrayList<>(grouped.size());

        try {
            for (Map.Entry<BatchKey, List<DrawSource>> group : grouped.entrySet()) {
                BatchKey key = group.getKey();
                RenderLayer layer = key.translucent()
                        ? RenderLayer.getEntityTranslucent(key.texture())
                        : RenderLayer.getEntityCutoutNoCull(key.texture());

                BufferBuilder builder = new BufferBuilder(Math.max(256, layer.getExpectedBufferSize()));
                builder.begin(layer.getDrawMode(), layer.getVertexFormat());
                for (DrawSource source : group.getValue()) {
                    appendSource(builder, source, key.color(), light, localPositionMatrix, localNormalMatrix);
                }

                BufferBuilder.BuiltBuffer built = builder.end();
                if (built.isEmpty()) {
                    built.release();
                    continue;
                }

                VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
                try {
                    vertexBuffer.bind();
                    // VertexBuffer#upload consumes/releases the BuiltBuffer in 1.20.1.
                    // Releasing it again here causes "Buffer has already been released!".
                    vertexBuffer.upload(built);
                } catch (RuntimeException exception) {
                    vertexBuffer.close();
                    throw exception;
                } finally {
                    VertexBuffer.unbind();
                }
                batches.add(new ChaoGpuRenderCache.DrawBatch(layer, vertexBuffer));
            }
            return List.copyOf(batches);
        } catch (RuntimeException exception) {
            for (ChaoGpuRenderCache.DrawBatch batch : batches) {
                batch.close();
            }
            throw exception;
        }
    }

    private static void collectMaterialPasses(Map<BatchKey, List<DrawSource>> grouped, MaterialRole role,
            ChaoVisualType type, ChaoMeshModel.Segment segment, ChaoRenderCache.PreparedSegment prepared,
            ChaoMeshModel.Submesh submesh, ChaoPaletteState palette) {
        switch (role) {
            case BODY -> {
                addPass(grouped, WHITE_TEXTURE, palette.base().multiply(palette.bodyCover()), false,
                        segment, prepared, submesh);
                addPass(grouped, BODY_MASK, palette.body(), true, segment, prepared, submesh);
                if (type == ChaoVisualType.CHILD && palette.extra2().alpha8() > 0) {
                    addPass(grouped, CHILD_BODY_EXTRA_MASK, palette.extra2(), true, segment, prepared, submesh);
                }
            }
            case BELLY -> {
                addPass(grouped, WHITE_TEXTURE, palette.base().multiply(palette.bodyCover()), false,
                        segment, prepared, submesh);
                addPass(grouped, BELLY_MASK, palette.belly(), true, segment, prepared, submesh);
                if (type == ChaoVisualType.CHILD && palette.extra().alpha8() > 0) {
                    addPass(grouped, CHILD_BELLY_EXTRA_MASK, palette.extra(), true, segment, prepared, submesh);
                }
            }
            case HORNS -> {
                addPass(grouped, WHITE_TEXTURE, palette.base().multiply(palette.bodyCover()), false,
                        segment, prepared, submesh);
                addPass(grouped, HORNS_MASK, palette.horns(), true, segment, prepared, submesh);
            }
            case WINGS -> {
                addPass(grouped, WHITE_TEXTURE, palette.wingsBase().multiply(palette.wingsCover()), false,
                        segment, prepared, submesh);
                addPass(grouped, WINGS_MASK, palette.wings(), true, segment, prepared, submesh);
            }
            case EYES -> addPass(grouped, EYE_NORMAL, ChaoColor.WHITE, false, segment, prepared, submesh);
            case HIDDEN -> {
                // Default eyelids and mouth are transparent in the Viewer.
            }
        }
    }

    private static void addPass(Map<BatchKey, List<DrawSource>> grouped, Identifier texture, ChaoColor color,
            boolean translucent, ChaoMeshModel.Segment segment, ChaoRenderCache.PreparedSegment prepared,
            ChaoMeshModel.Submesh submesh) {
        if (color.alpha8() == 0) {
            return;
        }
        BatchKey key = new BatchKey(texture, color, translucent);
        grouped.computeIfAbsent(key, ignored -> new ArrayList<>())
                .add(new DrawSource(segment, prepared, submesh));
    }

    private static void appendSource(BufferBuilder builder, DrawSource source, ChaoColor color, int light,
            Matrix4f positionMatrix, Matrix3f normalMatrix) {
        ChaoMeshModel.Segment segment = source.segment();
        ChaoMeshModel.Submesh submesh = source.submesh();
        int first = submesh.firstIndex();
        int end = first + submesh.indexCount();
        if (first < 0 || end > segment.indices().length || submesh.indexCount() % 3 != 0) {
            ChaoCraft.LOGGER.warn("Skipping malformed Chao submesh {}:{}", segment.name(), first);
            return;
        }

        float red = color.r();
        float green = color.g();
        float blue = color.b();
        float alpha = color.a();

        for (int triangle = first; triangle < end; triangle += 3) {
            appendVertex(builder, source, segment.indices()[triangle], red, green, blue, alpha,
                    light, positionMatrix, normalMatrix);
            appendVertex(builder, source, segment.indices()[triangle + 2], red, green, blue, alpha,
                    light, positionMatrix, normalMatrix);
            appendVertex(builder, source, segment.indices()[triangle + 1], red, green, blue, alpha,
                    light, positionMatrix, normalMatrix);
            // Entity RenderLayers use QUADS; duplicate the last vertex to preserve
            // the source triangle as a degenerate quad, matching CP02 topology.
            appendVertex(builder, source, segment.indices()[triangle + 1], red, green, blue, alpha,
                    light, positionMatrix, normalMatrix);
        }
    }

    private static void appendVertex(BufferBuilder builder, DrawSource source, int vertexIndex,
            float red, float green, float blue, float alpha, int light,
            Matrix4f positionMatrix, Matrix3f normalMatrix) {
        ChaoMeshModel.Segment segment = source.segment();
        ChaoRenderCache.PreparedSegment prepared = source.prepared();
        int p = vertexIndex * 3;
        int uv = vertexIndex * 2;

        float x = prepared.positions()[p];
        float y = prepared.positions()[p + 1];
        float z = prepared.positions()[p + 2];
        float nx = prepared.normals()[p];
        float ny = prepared.normals()[p + 1];
        float nz = prepared.normals()[p + 2];

        float tx = positionMatrix.m00() * x + positionMatrix.m10() * y + positionMatrix.m20() * z + positionMatrix.m30();
        float ty = positionMatrix.m01() * x + positionMatrix.m11() * y + positionMatrix.m21() * z + positionMatrix.m31();
        float tz = positionMatrix.m02() * x + positionMatrix.m12() * y + positionMatrix.m22() * z + positionMatrix.m32();

        float tnx = normalMatrix.m00() * nx + normalMatrix.m10() * ny + normalMatrix.m20() * nz;
        float tny = normalMatrix.m01() * nx + normalMatrix.m11() * ny + normalMatrix.m21() * nz;
        float tnz = normalMatrix.m02() * nx + normalMatrix.m12() * ny + normalMatrix.m22() * nz;
        float normalLengthSquared = tnx * tnx + tny * tny + tnz * tnz;
        if (normalLengthSquared > 0.000001F) {
            float inverseLength = (float) (1.0D / Math.sqrt(normalLengthSquared));
            tnx *= inverseLength;
            tny *= inverseLength;
            tnz *= inverseLength;
        } else {
            tnx = 0.0F;
            tny = 1.0F;
            tnz = 0.0F;
        }

        builder.vertex(
                tx, ty, tz,
                red, green, blue, alpha,
                segment.uvs()[uv], 1.0F - segment.uvs()[uv + 1],
                OverlayTexture.DEFAULT_UV, light,
                tnx, tny, tnz
        );
    }

    private static Matrix4f createLocalPositionMatrix(ChaoVisualType type, float bakedYaw) {
        Matrix4f matrix = new Matrix4f().identity()
                .rotateY((float) Math.toRadians(180.0F - bakedYaw));
        if (type == ChaoVisualType.CHILD) {
            matrix.rotateX((float) Math.toRadians(90.0F));
        }
        return matrix.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
    }

    private static Matrix3f createLocalNormalMatrix(ChaoVisualType type, float bakedYaw) {
        Matrix3f matrix = new Matrix3f().identity()
                .rotateY((float) Math.toRadians(180.0F - bakedYaw));
        if (type == ChaoVisualType.CHILD) {
            matrix.rotateX((float) Math.toRadians(90.0F));
        }
        return matrix;
    }

    private static void drawGpuBatches(ChaoGpuRenderCache.Entry entry, MatrixStack matrices) {
        RenderSystem.assertOnRenderThread();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        Matrix4f modelViewMatrix = matrices.peek().getPositionMatrix();
        Matrix4f projectionMatrix = RenderSystem.getProjectionMatrix();

        for (ChaoGpuRenderCache.DrawBatch batch : entry.batches()) {
            RenderLayer layer = batch.layer();
            layer.startDrawing();
            try {
                ShaderProgram shader = RenderSystem.getShader();
                if (shader == null) {
                    continue;
                }
                batch.vertexBuffer().bind();
                try {
                    batch.vertexBuffer().draw(modelViewMatrix, projectionMatrix, shader);
                } finally {
                    VertexBuffer.unbind();
                }
            } finally {
                layer.endDrawing();
            }
        }
    }

    private static MaterialRole resolveMaterialRole(ChaoVisualType type, String segmentName, int submeshIndex) {
        if (segmentName.contains("Arms") || segmentName.contains("Legs") || segmentName.contains("Tail")) {
            return MaterialRole.BODY;
        }
        if (segmentName.contains("Wings")) {
            return MaterialRole.WINGS;
        }
        if (segmentName.contains("Belly")) {
            return submeshIndex == 0 ? MaterialRole.BELLY : MaterialRole.BODY;
        }
        if (segmentName.contains("Head")) {
            if (type == ChaoVisualType.CHILD) {
                return switch (submeshIndex) {
                    case 0 -> MaterialRole.BODY;
                    case 1 -> MaterialRole.HORNS;
                    case 2 -> MaterialRole.EYES;
                    default -> MaterialRole.HIDDEN;
                };
            }
            return switch (submeshIndex) {
                case 0 -> MaterialRole.BODY;
                case 1 -> MaterialRole.EYES;
                default -> MaterialRole.HIDDEN;
            };
        }
        return MaterialRole.BODY;
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

    private enum MaterialRole {
        BODY,
        BELLY,
        HORNS,
        WINGS,
        EYES,
        HIDDEN
    }

    private record BatchKey(Identifier texture, ChaoColor color, boolean translucent) {
    }

    private record DrawSource(
            ChaoMeshModel.Segment segment,
            ChaoRenderCache.PreparedSegment prepared,
            ChaoMeshModel.Submesh submesh
    ) {
    }
}
