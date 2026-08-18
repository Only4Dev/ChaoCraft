package com.chaocraft.client.render;

import com.chaocraft.ChaoCraft;
import com.chaocraft.client.render.cache.ChaoGpuRenderCache;
import com.chaocraft.client.render.cache.ChaoRenderCache;
import com.chaocraft.client.render.cache.ChaoRenderStateQuantizer;
import com.chaocraft.client.render.debug.ChaoRenderMetrics;
import com.chaocraft.client.render.family.ChaoAdultFamily;
import com.chaocraft.client.render.family.ChaoAdultMaterialProfiles;
import com.chaocraft.client.render.family.ChaoChaosFamily;
import com.chaocraft.client.render.family.ChaoChaosMaterialProfiles;
import com.chaocraft.client.render.material.ChaoColor;
import com.chaocraft.client.render.material.ChaoPaletteResolver;
import com.chaocraft.client.render.material.ChaoPaletteState;
import com.chaocraft.client.render.material.ChaoReflectionMaterialRules;
import com.chaocraft.client.render.animal.ChaoAnimalPartCatalog;
import com.chaocraft.client.render.animal.ChaoAnimalAnchorProfiles;
import com.chaocraft.client.render.mesh.ChaoMeshLoader;
import com.chaocraft.client.render.mesh.ChaoMeshModel;
import com.chaocraft.entity.ChaoEntity;
import com.chaocraft.visual.ChaoAppearanceState;
import com.chaocraft.visual.ChaoMorphResolver;
import com.chaocraft.visual.ChaoMorphWeights;
import com.chaocraft.visual.ChaoVisualType;
import com.chaocraft.visual.ChaoReflectionType;
import com.chaocraft.visual.ChaoAnimalType;
import com.chaocraft.visual.ChaoAnimalParts.Slot;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUniform;
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
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Morphable Chao renderer.
 *
 * <p>Persistent GPU/VBO renderer. CP07.3 keeps presentation transforms such
 * as body yaw out of GPU cache identity: a Chao can turn every tick without
 * rebuilding geometry. Appearance changes and bounded light variants are the
 * only normal reasons to rebuild a VBO.</p>
 */
public final class ChaoRenderer extends EntityRenderer<ChaoEntity> {
    private static final Set<ChaoRenderer> INSTANCES = Collections.newSetFromMap(new WeakHashMap<>());
    private static final int MAX_BATCH_BUFFER_BYTES = 16 * 1024 * 1024;
    private static final int PREFERRED_BATCH_BUFFER_BYTES = 2 * 1024 * 1024;
    private static final Identifier CHILD_MODEL = ChaoCraft.id("models/chao/child.cmesh");
    private static final Identifier NEUTRAL_NORMAL_MODEL = ChaoCraft.id("models/chao/neutral_normal.cmesh");
    private static final Identifier HERO_NORMAL_MODEL = ChaoCraft.id("models/chao/hero_normal.cmesh");
    private static final Identifier DARK_NORMAL_MODEL = ChaoCraft.id("models/chao/dark_normal.cmesh");
    private static final Identifier NEUTRAL_BALL_MODEL = ChaoCraft.id("models/chao/emotion/neutral_ball.cmesh");
    private static final Identifier HERO_HALO_MODEL = ChaoCraft.id("models/chao/emotion/hero_halo.cmesh");
    private static final Identifier DARK_BALL_MODEL = ChaoCraft.id("models/chao/emotion/dark_ball.cmesh");
    private static final Identifier WHITE_TEXTURE = ChaoCraft.id("textures/entity/chao/white.png");
    private static final Identifier REFLECTION_SHINY = ChaoCraft.id("textures/entity/chao/reflection/shiny.png");
    private static final Identifier REFLECTION_SILVER = ChaoCraft.id("textures/entity/chao/reflection/silver.png");
    private static final Identifier REFLECTION_GOLD = ChaoCraft.id("textures/entity/chao/reflection/gold.png");
    private static final Identifier REFLECTION_GARNET = ChaoCraft.id("textures/entity/chao/reflection/garnet.png");
    private static final Identifier REFLECTION_RUBY = ChaoCraft.id("textures/entity/chao/reflection/ruby.png");
    private static final Identifier REFLECTION_TOPAZ = ChaoCraft.id("textures/entity/chao/reflection/topaz.png");
    private static final Identifier REFLECTION_SAPPHIRE = ChaoCraft.id("textures/entity/chao/reflection/sapphire.png");
    private static final Identifier REFLECTION_AQUAMARINE = ChaoCraft.id("textures/entity/chao/reflection/aquamarine.png");
    private static final Identifier REFLECTION_AMETHYST = ChaoCraft.id("textures/entity/chao/reflection/amethyst.png");
    private static final Identifier REFLECTION_PERIDOT = ChaoCraft.id("textures/entity/chao/reflection/peridot.png");
    private static final Identifier REFLECTION_EMERALD = ChaoCraft.id("textures/entity/chao/reflection/emerald.png");
    private static final Identifier REFLECTION_ONYX = ChaoCraft.id("textures/entity/chao/reflection/onyx.png");
    private static final Identifier REFLECTION_PEARL = ChaoCraft.id("textures/entity/chao/reflection/pearl.png");
    private static final Identifier REFLECTION_MOON = ChaoCraft.id("textures/entity/chao/reflection/moon.png");
    private static final Identifier REFLECTION_METAL = ChaoCraft.id("textures/entity/chao/reflection/metal.png");
    private static final Identifier BODY_MASK = ChaoCraft.id("textures/entity/chao/material/c_body.png");
    private static final Identifier CHILD_BODY_EXTRA_MASK = ChaoCraft.id("textures/entity/chao/material/hn_body2.png");
    private static final Identifier BELLY_MASK = ChaoCraft.id("textures/entity/chao/material/c_belly.png");
    private static final Identifier CHILD_BELLY_EXTRA_MASK = ChaoCraft.id("textures/entity/chao/material/c_belly_dark.png");
    private static final Identifier HORNS_MASK = ChaoCraft.id("textures/entity/chao/material/c_horns.png");
    private static final Identifier WINGS_MASK = ChaoCraft.id("textures/entity/chao/material/c_wings.png");
    private static final Identifier EYE_NORMAL = ChaoCraft.id("textures/entity/chao/face/eye01.png");
    private static final Identifier EYE_DARK = ChaoCraft.id("textures/entity/chao/face/eye_dark.png");
    private static final Identifier EYE_HERO = ChaoCraft.id("textures/entity/chao/face/eye_hero.png");
    private static final Identifier EYELID_DARK = ChaoCraft.id("textures/entity/chao/face/eyelid_dark.png");
    // Chao Viewer ChangeFace() indices 0..12 after its Eye1/Eye9/Eye10 remap.
    private static final Identifier[] EYE_TEXTURES = {
            ChaoCraft.id("textures/entity/chao/face/eyes/eye_00.png"),
            ChaoCraft.id("textures/entity/chao/face/eyes/eye_01.png"),
            ChaoCraft.id("textures/entity/chao/face/eyes/eye_02.png"),
            ChaoCraft.id("textures/entity/chao/face/eyes/eye_03.png"),
            ChaoCraft.id("textures/entity/chao/face/eyes/eye_04.png"),
            ChaoCraft.id("textures/entity/chao/face/eyes/eye_05.png"),
            ChaoCraft.id("textures/entity/chao/face/eyes/eye_06.png"),
            ChaoCraft.id("textures/entity/chao/face/eyes/eye_07.png"),
            ChaoCraft.id("textures/entity/chao/face/eyes/eye_08.png"),
            ChaoCraft.id("textures/entity/chao/face/eyes/eye_09.png"),
            ChaoCraft.id("textures/entity/chao/face/eyes/eye_10.png"),
            ChaoCraft.id("textures/entity/chao/face/eyes/eye_11.png"),
            ChaoCraft.id("textures/entity/chao/face/eyes/eye_12.png")
    };
    // Viewer CMouthM/CMouthS texture slots 1..18. Index 0 means clear/hidden.
    private static final Identifier[] MOUTH_TEXTURES = {
            null,
            ChaoCraft.id("textures/entity/chao/face/mouth/mouth_01.png"),
            ChaoCraft.id("textures/entity/chao/face/mouth/mouth_02.png"),
            ChaoCraft.id("textures/entity/chao/face/mouth/mouth_03.png"),
            ChaoCraft.id("textures/entity/chao/face/mouth/mouth_04.png"),
            ChaoCraft.id("textures/entity/chao/face/mouth/mouth_05.png"),
            ChaoCraft.id("textures/entity/chao/face/mouth/mouth_06.png"),
            ChaoCraft.id("textures/entity/chao/face/mouth/mouth_07.png"),
            ChaoCraft.id("textures/entity/chao/face/mouth/mouth_08.png"),
            ChaoCraft.id("textures/entity/chao/face/mouth/mouth_09.png"),
            ChaoCraft.id("textures/entity/chao/face/mouth/mouth_10.png"),
            ChaoCraft.id("textures/entity/chao/face/mouth/mouth_11.png"),
            ChaoCraft.id("textures/entity/chao/face/mouth/mouth_12.png"),
            ChaoCraft.id("textures/entity/chao/face/mouth/mouth_13.png"),
            ChaoCraft.id("textures/entity/chao/face/mouth/mouth_14.png"),
            ChaoCraft.id("textures/entity/chao/face/mouth/mouth_15.png"),
            ChaoCraft.id("textures/entity/chao/face/mouth/mouth_16.png"),
            ChaoCraft.id("textures/entity/chao/face/mouth/mouth_17.png"),
            ChaoCraft.id("textures/entity/chao/face/mouth/mouth_18.png")
    };
    private static final Identifier HERO_BODY_MASK = ChaoCraft.id("textures/entity/chao/material/hn_body.png");
    private static final Identifier HERO_BODY_EXTRA_MASK = ChaoCraft.id("textures/entity/chao/material/hn_body2.png");
    private static final Identifier HERO_BELLY_MASK = ChaoCraft.id("textures/entity/chao/material/hn_belly1.png");
    private static final Identifier HERO_WINGS_MASK = ChaoCraft.id("textures/entity/chao/material/hn_wings.png");
    private static final Identifier DARK_BODY_MASK = ChaoCraft.id("textures/entity/chao/material/dn_body1.png");
    private static final Identifier DARK_BODY_EXTRA_MASK = ChaoCraft.id("textures/entity/chao/material/dn_body2.png");
    private static final Identifier DARK_BELLY_MASK = ChaoCraft.id("textures/entity/chao/material/dn_belly.png");
    private static final Identifier DARK_WINGS_MASK = ChaoCraft.id("textures/entity/chao/material/dn_wings1.png");
    private static final Identifier DARK_WINGS_EXTRA_MASK = ChaoCraft.id("textures/entity/chao/material/dn_wings2.png");
    private static final float MODEL_SCALE = 0.18F;

    private final ChaoRenderCache morphCache = new ChaoRenderCache();
    private final ChaoGpuRenderCache gpuCache = new ChaoGpuRenderCache();
    private final Map<ChaoAdultFamily, ChaoMeshModel> adultModels = new EnumMap<>(ChaoAdultFamily.class);
    private final EnumSet<ChaoAdultFamily> adultLoadAttempted = EnumSet.noneOf(ChaoAdultFamily.class);
    private final Map<ChaoChaosFamily, ChaoMeshModel> chaosModels = new EnumMap<>(ChaoChaosFamily.class);
    private final EnumSet<ChaoChaosFamily> chaosLoadAttempted = EnumSet.noneOf(ChaoChaosFamily.class);
    private final Map<Identifier, ChaoMeshModel> animalModels = new LinkedHashMap<>();
    private final Set<Identifier> animalLoadAttempted = new java.util.HashSet<>();

    private ChaoMeshModel childModel;
    private ChaoMeshModel neutralNormalModel;
    private ChaoMeshModel heroNormalModel;
    private ChaoMeshModel darkNormalModel;
    private ChaoMeshModel neutralBallModel;
    private ChaoMeshModel heroHaloModel;
    private ChaoMeshModel darkBallModel;
    private boolean childLoadAttempted;
    private boolean neutralNormalLoadAttempted;
    private boolean heroNormalLoadAttempted;
    private boolean darkNormalLoadAttempted;
    private boolean neutralBallLoadAttempted;
    private boolean heroHaloLoadAttempted;
    private boolean darkBallLoadAttempted;

    public ChaoRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.30F;
        synchronized (INSTANCES) {
            INSTANCES.add(this);
        }
    }

    /** Releases client GPU/CPU caches on disconnect or resource reload. */
    public static void clearAllCaches(boolean invalidateModels) {
        runOnRenderThread(() -> {
            synchronized (INSTANCES) {
                for (ChaoRenderer renderer : List.copyOf(INSTANCES)) {
                    renderer.clearLocalCaches(invalidateModels);
                }
            }
        });
    }

    /** Immediately releases VBOs owned by a temporary client-only preview entity. */
    public static void releaseEntity(UUID entityId) {
        runOnRenderThread(() -> {
            synchronized (INSTANCES) {
                for (ChaoRenderer renderer : List.copyOf(INSTANCES)) {
                    renderer.gpuCache.remove(entityId);
                    renderer.morphCache.remove(entityId);
                }
            }
        });
    }

    /**
     * GL objects are render-thread owned. Fabric lifecycle callbacks are not
     * assumed to execute on that thread; defer native cleanup when necessary.
     */
    private static void runOnRenderThread(Runnable action) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.isOnThread()) {
            action.run();
        } else {
            client.execute(action);
        }
    }

    private void clearLocalCaches(boolean invalidateModels) {
        gpuCache.clear();
        morphCache.clear();
        if (!invalidateModels) {
            return;
        }
        adultModels.clear();
        adultLoadAttempted.clear();
        chaosModels.clear();
        chaosLoadAttempted.clear();
        animalModels.clear();
        animalLoadAttempted.clear();
        childModel = null;
        neutralNormalModel = null;
        heroNormalModel = null;
        darkNormalModel = null;
        neutralBallModel = null;
        heroHaloModel = null;
        darkBallModel = null;
        childLoadAttempted = false;
        neutralNormalLoadAttempted = false;
        heroNormalLoadAttempted = false;
        darkNormalLoadAttempted = false;
        neutralBallLoadAttempted = false;
        heroHaloLoadAttempted = false;
        darkBallLoadAttempted = false;
    }

    @Override
    public void render(ChaoEntity entity, float yaw, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light) {
        long renderStarted = System.nanoTime();
        ChaoAppearanceState state = ChaoRenderStateQuantizer.quantize(entity.getAppearanceState());
        try {
            ChaoGpuRenderCache.Entry gpuEntry = prepareGpuEntry(entity, state, light);
            if (gpuEntry == null) {
                renderFallback(matrices, vertexConsumers, light);
                super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
                return;
            }

            // Presentation belongs at draw time. Keeping yaw out of the baked VBO is
            // essential for moving Chao: turning in the world or in Visual Lab must
            // never allocate/re-upload mesh buffers.
            matrices.push();
            Matrix3f parentNormal = new Matrix3f(matrices.peek().getNormalMatrix());
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - yaw));
            matrices.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
            drawGpuBatches(gpuEntry, matrices, parentNormal);
            matrices.pop();

            super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        } finally {
            ChaoRenderMetrics.onRender(System.nanoTime() - renderStarted,
                    state.reflectionType() != ChaoReflectionType.NONE);
        }
    }

    /**
     * Draws the exact production batches into an existing GUI matrix stack.
     * The caller owns screen-space translation/scale; this method only applies
     * Chao-local presentation transforms and never inserts the preview into a world.
     */
    public void renderGuiPreview(ChaoEntity entity, MatrixStack localGuiMatrices, int light, float yaw, float pitch) {
        ChaoAppearanceState state = ChaoRenderStateQuantizer.quantize(entity.getAppearanceState());
        ChaoGpuRenderCache.Entry gpuEntry = prepareGpuEntry(entity, state, light);
        if (gpuEntry == null) {
            return;
        }

        // DrawContext contains only the screen-local transform. Minecraft's GUI
        // projection also relies on RenderSystem's global model-view stack (most
        // importantly its GUI depth translation). Direct VertexBuffer draws must
        // compose both matrices or the model is clipped and the panel looks empty.
        MatrixStack matrices = new MatrixStack();
        matrices.peek().getPositionMatrix().set(RenderSystem.getModelViewStack().peek().getPositionMatrix());
        matrices.peek().getPositionMatrix().mul(localGuiMatrices.peek().getPositionMatrix());
        matrices.peek().getNormalMatrix().set(RenderSystem.getModelViewStack().peek().getNormalMatrix());
        matrices.peek().getNormalMatrix().mul(localGuiMatrices.peek().getNormalMatrix());

        matrices.push();
        Matrix3f parentNormal = new Matrix3f(matrices.peek().getNormalMatrix());
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - yaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pitch));
        matrices.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
        drawGpuBatches(gpuEntry, matrices, parentNormal);
        matrices.pop();
    }

    private ChaoGpuRenderCache.Entry prepareGpuEntry(ChaoEntity entity, ChaoAppearanceState state, int light) {
        ChaoMeshModel model = getModel(state);
        if (model == null) {
            return null;
        }

        ChaoMorphWeights weights = ChaoMorphResolver.resolve(state);
        float[] morphWeights = buildMorphWeights(model, state.type(), weights);
        ChaoPaletteState palette = ChaoPaletteResolver.resolve(state, weights);
        ChaoRenderCache.Entry prepared = morphCache.get(entity, state, model, morphWeights, palette);
        long worldTick = entity.getWorld().getTime();
        return gpuCache.get(
                entity,
                state,
                model,
                light,
                worldTick,
                () -> buildGpuBatches(model, prepared, state, prepared.palette(), light)
        );
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
        } else if (type != ChaoVisualType.CHAOS) {
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

    private List<ChaoGpuRenderCache.DrawBatch> buildGpuBatches(ChaoMeshModel model,
            ChaoRenderCache.Entry prepared, ChaoAppearanceState state, ChaoPaletteState palette,
            int light) {
        ChaoVisualType type = state.type();
        ChaoAdultFamily adultFamily = type == ChaoVisualType.CHILD || type == ChaoVisualType.CHAOS
                ? null : ChaoAdultFamily.resolve(state);
        ChaoChaosFamily chaosFamily = type == ChaoVisualType.CHAOS ? ChaoChaosFamily.resolve(state) : null;
        RenderSystem.assertOnRenderThread();

        Map<BatchKey, List<DrawSource>> grouped = new LinkedHashMap<>();
        for (ChaoMeshModel.Segment segment : model.segments()) {
            ChaoRenderCache.PreparedSegment preparedSegment = prepared.segment(segment);
            for (int submeshIndex = 0; submeshIndex < segment.submeshes().size(); submeshIndex++) {
                ChaoMeshModel.Submesh submesh = segment.submeshes().get(submeshIndex);
                if (type == ChaoVisualType.CHILD) {
                    MaterialRole role = resolveMaterialRole(type, AdultNormalVariant.NEUTRAL, segment.name(), submeshIndex);
                    collectMaterialPasses(grouped, role, state, type, AdultNormalVariant.NEUTRAL,
                            segment, preparedSegment, submesh, palette);
                } else if (type == ChaoVisualType.CHAOS) {
                    collectChaosMaterialPasses(grouped, chaosFamily, state, segment, preparedSegment,
                            submesh, submeshIndex, palette);
                } else {
                    collectAdultMaterialPasses(grouped, adultFamily, state, segment, preparedSegment,
                            submesh, submeshIndex, palette);
                }

                if (state.reflectionType() != ChaoReflectionType.NONE
                        && shouldReflectMaterial(state, adultFamily, chaosFamily, segment.name(), submeshIndex)) {
                    addReflectionPass(grouped, state.reflectionType(), segment, preparedSegment, submesh);
                }
            }
        }

        Matrix4f localPositionMatrix = createLocalPositionMatrix(type);
        Matrix3f localNormalMatrix = createLocalNormalMatrix(type);
        List<ChaoGpuRenderCache.DrawBatch> batches = new ArrayList<>(grouped.size());

        try {
            for (Map.Entry<BatchKey, List<DrawSource>> group : grouped.entrySet()) {
                BatchKey key = group.getKey();
                RenderLayer layer = key.translucent()
                        ? RenderLayer.getEntityTranslucent(key.texture())
                        : RenderLayer.getEntityCutoutNoCull(key.texture());

                // A single 6+ MiB native BufferBuilder was enough to fail when a
                // heavily modded Windows session was near its commit limit. Split
                // compatible sources into ~2 MiB upload chunks. Total geometry is
                // unchanged, but no routine Chao build needs one giant contiguous
                // native allocation.
                for (List<DrawSource> chunk : partitionSources(group.getValue(), layer)) {
                    int expectedVertices = estimatedVertexCount(chunk);
                    int bufferBytes = estimatedBufferBytes(layer, expectedVertices);
                    if (bufferBytes < 0) {
                        ChaoCraft.LOGGER.error("Skipping oversized Chao render batch ({} vertices, texture {})",
                                expectedVertices, key.texture());
                        continue;
                    }
                    BufferBuilder builder = new BufferBuilder(bufferBytes);
                    builder.begin(layer.getDrawMode(), layer.getVertexFormat());
                    int batchLight = key.fullbright() ? 0x00F000F0 : light;
                    for (DrawSource source : chunk) {
                        appendSource(builder, source, key.color(), batchLight, localPositionMatrix, localNormalMatrix, key.uvMode());
                    }

                    BufferBuilder.BuiltBuffer built = builder.end();
                    if (built.isEmpty()) {
                        built.release();
                        continue;
                    }

                    VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
                    try {
                        vertexBuffer.bind();
                        vertexBuffer.upload(built);
                    } catch (RuntimeException exception) {
                        vertexBuffer.close();
                        throw exception;
                    } finally {
                        VertexBuffer.unbind();
                    }
                    batches.add(new ChaoGpuRenderCache.DrawBatch(layer, vertexBuffer, bufferBytes));
                }
            }

            batches.addAll(buildAnimalPartBatches(state, adultFamily, light));
            batches.addAll(buildEmotionBatches(state, palette, light));
            return List.copyOf(batches);
        } catch (RuntimeException exception) {
            for (ChaoGpuRenderCache.DrawBatch batch : batches) {
                batch.close();
            }
            throw exception;
        }
    }

    private static void collectAdultMaterialPasses(Map<BatchKey, List<DrawSource>> grouped,
            ChaoAdultFamily family, ChaoAppearanceState state, ChaoMeshModel.Segment segment,
            ChaoRenderCache.PreparedSegment prepared, ChaoMeshModel.Submesh submesh, int submeshIndex,
            ChaoPaletteState palette) {
        ChaoAdultMaterialProfiles.MaterialSpec spec = ChaoAdultMaterialProfiles.resolve(
                family, segment.name(), submeshIndex);
        switch (spec.kind()) {
            case CHAO -> {
                ChaoAdultMaterialProfiles.ColorRef coverRef = spec.cover();
                // ChangeNeutralSwim() uses Extra2/Extra3 cover only for Normal-color
                // Chao. A real SA2 Color replaces those special covers with the
                // global BodyCover just like the Viewer source.
                if (family == ChaoAdultFamily.NS
                        && state.colorType() != com.chaocraft.visual.ChaoColorType.NORMAL
                        && (coverRef == ChaoAdultMaterialProfiles.ColorRef.EXTRA2
                            || coverRef == ChaoAdultMaterialProfiles.ColorRef.EXTRA3)) {
                    coverRef = ChaoAdultMaterialProfiles.ColorRef.BODY_COVER;
                }
                ChaoColor cover = ChaoAdultMaterialProfiles.color(coverRef, palette);
                boolean wingMaterial = spec.debugName().toLowerCase(java.util.Locale.ROOT).contains("wing");

                // Change*() sets the layered body colors to white in monotone mode,
                // while wing colors are assigned outside that branch and retain their
                // palette values. Keep that source distinction here rather than
                // approximating monotone as a blanket tint.
                ChaoAdultMaterialProfiles.ColorRef color1 = state.monotone() && !wingMaterial
                        ? ChaoAdultMaterialProfiles.ColorRef.WHITE : spec.color1();
                ChaoAdultMaterialProfiles.ColorRef color2 = state.monotone() && !wingMaterial
                        ? ChaoAdultMaterialProfiles.ColorRef.WHITE : spec.color2();
                ChaoAdultMaterialProfiles.ColorRef color3 = state.monotone() && !wingMaterial
                        ? ChaoAdultMaterialProfiles.ColorRef.WHITE : spec.color3();
                ChaoAdultMaterialProfiles.ColorRef color4 = state.monotone() && !wingMaterial
                        ? ChaoAdultMaterialProfiles.ColorRef.WHITE : spec.color4();

                addPass(grouped, WHITE_TEXTURE,
                        ChaoAdultMaterialProfiles.color(color1, palette).multiply(cover), false,
                        segment, prepared, submesh);
                addAdultLayer(grouped, spec.layer2(), color2, cover, palette, segment, prepared, submesh);
                addAdultLayer(grouped, spec.layer3(), color3, cover, palette, segment, prepared, submesh);
                addAdultLayer(grouped, spec.layer4(), color4, cover, palette, segment, prepared, submesh);
            }
            case EYE -> addPass(grouped, resolveEyeTexture(state), ChaoColor.WHITE, false, segment, prepared, submesh);
            case EYELID -> {
                int eyelid = state.resolvedEyelid();
                ChaoColor eyelidColor = state.monotone()
                        ? palette.bodyCover()
                        : palette.base().multiply(palette.bodyCover());
                if (eyelid == 1) {
                    addPass(grouped, EYELID_DARK, eyelidColor, true, segment, prepared, submesh);
                } else if (eyelid == 2) {
                    addPass(grouped, WHITE_TEXTURE, eyelidColor, false, segment, prepared, submesh);
                }
            }
            case MOUTH_MID -> {
                Identifier texture = resolveMouthTexture(state.resolvedMouthMid());
                if (texture != null) addPass(grouped, texture, ChaoColor.WHITE, true, segment, prepared, submesh);
            }
            case MOUTH_SIDE -> {
                Identifier texture = resolveMouthTexture(state.resolvedMouthSide());
                if (texture != null) addPass(grouped, texture, ChaoColor.WHITE, true, segment, prepared, submesh);
            }
            case HIDDEN -> { }
        }
    }

    /** Exact material-slot behavior of ChangeNeutral/Hero/DarkChaos(). */
    private static void collectChaosMaterialPasses(Map<BatchKey, List<DrawSource>> grouped,
            ChaoChaosFamily family, ChaoAppearanceState state, ChaoMeshModel.Segment segment,
            ChaoRenderCache.PreparedSegment prepared, ChaoMeshModel.Submesh submesh, int submeshIndex,
            ChaoPaletteState palette) {
        ChaoChaosMaterialProfiles.Spec spec = ChaoChaosMaterialProfiles.resolve(family, segment.name(), submeshIndex);
        switch (spec.kind()) {
            case BODY -> {
                // Chaos body maps are normal texture maps, not ChaoMaterial layer
                // masks. In monotone mode the Viewer clears _MainTex entirely.
                // Neutral InnerHead is the sole exception: ChangeNeutralChaos()
                // never clears/tints that material.
                boolean keepSourceTexture = !state.monotone() || !spec.coverTint();
                Identifier texture = keepSourceTexture ? spec.texture() : WHITE_TEXTURE;
                ChaoColor color = spec.coverTint() ? palette.bodyCover() : ChaoColor.WHITE;
                addPass(grouped, texture, color, false, segment, prepared, submesh);
            }
            case EYE -> addPass(grouped, resolveEyeTexture(state), ChaoColor.WHITE, false,
                    segment, prepared, submesh);
            case EYELID -> {
                int eyelid = state.resolvedEyelid();
                ChaoColor color = palette.bodyCover();
                if (!state.monotone() && family == ChaoChaosFamily.DARK) {
                    color = ChaoColor.rgb(49, 52, 49).multiply(color);
                }
                if (eyelid == 1) {
                    addPass(grouped, EYELID_DARK, color, true, segment, prepared, submesh);
                } else if (eyelid == 2) {
                    addPass(grouped, WHITE_TEXTURE, color, false, segment, prepared, submesh);
                }
            }
            case MOUTH_MID -> {
                Identifier texture = resolveMouthTexture(state.resolvedMouthMid());
                if (texture != null) addPass(grouped, texture, ChaoColor.WHITE, true, segment, prepared, submesh);
            }
            case MOUTH_SIDE -> {
                Identifier texture = resolveMouthTexture(state.resolvedMouthSide());
                if (texture != null) addPass(grouped, texture, ChaoColor.WHITE, true, segment, prepared, submesh);
            }
            case HIDDEN -> { }
        }
    }


    private static boolean shouldReflectMaterial(ChaoAppearanceState state, ChaoAdultFamily adultFamily,
            ChaoChaosFamily chaosFamily, String segmentName, int submeshIndex) {
        if (state.type() == ChaoVisualType.CHILD) {
            MaterialRole role = resolveMaterialRole(ChaoVisualType.CHILD, AdultNormalVariant.NEUTRAL, segmentName, submeshIndex);
            return role == MaterialRole.BODY || role == MaterialRole.BELLY || role == MaterialRole.HORNS || role == MaterialRole.EYELID;
        }
        if (state.type() == ChaoVisualType.CHAOS) {
            ChaoChaosMaterialProfiles.Spec spec = ChaoChaosMaterialProfiles.resolve(chaosFamily, segmentName, submeshIndex);
            // The Viewer ReflMaterials lists all shared Chaos body materials plus the eyelid; eyes/mouth remain unreflected.
            return spec.kind() == ChaoChaosMaterialProfiles.Kind.BODY || spec.kind() == ChaoChaosMaterialProfiles.Kind.EYELID;
        }
        ChaoAdultMaterialProfiles.MaterialSpec spec = ChaoAdultMaterialProfiles.resolve(adultFamily, segmentName, submeshIndex);
        return ChaoReflectionMaterialRules.isReflectiveAdult(adultFamily.name(), spec.debugName(),
                spec.kind() == ChaoAdultMaterialProfiles.Kind.EYELID);
    }

    /** Viewer ReflectionT + ReflectiveTextures mapping. ReflMaterials membership is handled separately. */
    private static void addReflectionPass(Map<BatchKey, List<DrawSource>> grouped, ChaoReflectionType reflection,
            ChaoMeshModel.Segment segment, ChaoRenderCache.PreparedSegment prepared, ChaoMeshModel.Submesh submesh) {
        if (reflection == ChaoReflectionType.NONE) return;

        if (reflection == ChaoReflectionType.BRIGHT) {
            // Bright has no cubemap in SetReflection(); it is emission-only (_Emission=.5).
            BatchKey key = new BatchKey(WHITE_TEXTURE, new ChaoColor(1F, 1F, 1F, 0.18F), true, UvMode.MESH, true);
            grouped.computeIfAbsent(key, ignored -> new ArrayList<>()).add(new DrawSource(segment, prepared, submesh));
            return;
        }

        Identifier texture = reflectionTexture(reflection);
        if (texture == null) return;
        float ref = (reflection == ChaoReflectionType.SHINY || reflection == ChaoReflectionType.TT_METAL) ? 0.4F : 1.0F;
        // SetReflection() gives Shiny .5 emission and jewel/metal modes .1. Rendering
        // the cubemap pass fullbright preserves that self-lit reflective contribution.
        BatchKey key = new BatchKey(texture, new ChaoColor(1F, 1F, 1F, ref), true, UvMode.CUBEMAP_STRIP, true);
        grouped.computeIfAbsent(key, ignored -> new ArrayList<>()).add(new DrawSource(segment, prepared, submesh));
    }

    private static Identifier reflectionTexture(ChaoReflectionType type) {
        return switch (type) {
            case SHINY -> REFLECTION_SHINY;
            case SILVER -> REFLECTION_SILVER;
            case GOLD -> REFLECTION_GOLD;
            case GARNET -> REFLECTION_GARNET;
            case RUBY -> REFLECTION_RUBY;
            case TOPAZ -> REFLECTION_TOPAZ;
            case SAPPHIRE -> REFLECTION_SAPPHIRE;
            case AQUAMARINE -> REFLECTION_AQUAMARINE;
            case AMETHYST -> REFLECTION_AMETHYST;
            case PERIDOT -> REFLECTION_PERIDOT;
            case EMERALD -> REFLECTION_EMERALD;
            case ONYX -> REFLECTION_ONYX;
            case PEARL -> REFLECTION_PEARL;
            case MOON -> REFLECTION_MOON;
            case TT_METAL -> REFLECTION_METAL;
            case NONE, BRIGHT -> null;
        };
    }

    /** Maps a local-space normal onto AssetRipper's vertical six-face cubemap strip. */
    private static float cubeUvU(float x, float y, float z) {
        float ax = Math.abs(x), ay = Math.abs(y), az = Math.abs(z);
        float u;
        if (ax >= ay && ax >= az) u = x >= 0 ? -z / Math.max(ax, 1e-6F) : z / Math.max(ax, 1e-6F);
        else if (ay >= ax && ay >= az) u = x / Math.max(ay, 1e-6F);
        else u = z >= 0 ? x / Math.max(az, 1e-6F) : -x / Math.max(az, 1e-6F);
        return u * 0.5F + 0.5F;
    }

    private static float cubeUvV(float x, float y, float z) {
        float ax = Math.abs(x), ay = Math.abs(y), az = Math.abs(z);
        int face;
        float v;
        if (ax >= ay && ax >= az) { face = x >= 0 ? 0 : 1; v = -y / Math.max(ax, 1e-6F); }
        else if (ay >= ax && ay >= az) { face = y >= 0 ? 2 : 3; v = (y >= 0 ? z : -z) / Math.max(ay, 1e-6F); }
        else { face = z >= 0 ? 4 : 5; v = -y / Math.max(az, 1e-6F); }
        float local = 1.0F - (v * 0.5F + 0.5F);
        return (face + local) / 6.0F;
    }

    private static void addAdultLayer(Map<BatchKey, List<DrawSource>> grouped, Identifier texture,
            ChaoAdultMaterialProfiles.ColorRef colorRef, ChaoColor cover, ChaoPaletteState palette,
            ChaoMeshModel.Segment segment, ChaoRenderCache.PreparedSegment prepared, ChaoMeshModel.Submesh submesh) {
        if (texture == null) return;
        ChaoColor color = ChaoAdultMaterialProfiles.color(colorRef, palette).multiply(cover);
        addPass(grouped, texture, color, true, segment, prepared, submesh);
    }

    private static void collectMaterialPasses(Map<BatchKey, List<DrawSource>> grouped, MaterialRole role,
            ChaoAppearanceState state, ChaoVisualType type, AdultNormalVariant adultVariant, ChaoMeshModel.Segment segment,
            ChaoRenderCache.PreparedSegment prepared, ChaoMeshModel.Submesh submesh, ChaoPaletteState palette) {
        switch (role) {
            case BODY -> {
                ChaoColor childMonoLayer = state.colorType() == com.chaocraft.visual.ChaoColorType.NORMAL
                        ? palette.base() : ChaoColor.WHITE;
                ChaoColor bodyBase = type == ChaoVisualType.CHILD && state.monotone()
                        ? childMonoLayer.multiply(palette.bodyCover())
                        : palette.base().multiply(palette.bodyCover());
                addPass(grouped, WHITE_TEXTURE, bodyBase, false, segment, prepared, submesh);
                if (type == ChaoVisualType.CHILD) {
                    if (!state.monotone()) {
                        addPass(grouped, BODY_MASK, palette.body().multiply(palette.bodyCover()), true, segment, prepared, submesh);
                        if (palette.extra2().alpha8() > 0) {
                            addPass(grouped, CHILD_BODY_EXTRA_MASK, palette.extra2().multiply(palette.bodyCover()), true, segment, prepared, submesh);
                        }
                    }
                } else if (adultVariant == AdultNormalVariant.HERO) {
                    addPass(grouped, HERO_BODY_MASK, palette.body(), true, segment, prepared, submesh);
                    if (palette.extra().alpha8() > 0) {
                        addPass(grouped, HERO_BODY_EXTRA_MASK, palette.extra(), true, segment, prepared, submesh);
                    }
                } else if (adultVariant == AdultNormalVariant.DARK) {
                    // ChaoMaterial applies _ColorC (BodyCover) to the fully composed
                    // body material. Multiplying each emulated layer by the same
                    // cover preserves that operation through standard alpha blending.
                    addPass(grouped, DARK_BODY_MASK, palette.body().multiply(palette.bodyCover()), true,
                            segment, prepared, submesh);
                    addPass(grouped, DARK_BODY_EXTRA_MASK, palette.extra().multiply(palette.bodyCover()), true,
                            segment, prepared, submesh);
                } else {
                    addPass(grouped, BODY_MASK, palette.body(), true, segment, prepared, submesh);
                }
            }
            case BELLY -> {
                ChaoColor childMonoLayer = state.colorType() == com.chaocraft.visual.ChaoColorType.NORMAL
                        ? palette.base() : ChaoColor.WHITE;
                ChaoColor bellyBase = type == ChaoVisualType.CHILD && state.monotone()
                        ? childMonoLayer.multiply(palette.bodyCover())
                        : palette.base().multiply(palette.bodyCover());
                addPass(grouped, WHITE_TEXTURE, bellyBase, false, segment, prepared, submesh);
                if (type == ChaoVisualType.CHILD) {
                    if (!state.monotone()) {
                        addPass(grouped, BELLY_MASK, palette.belly().multiply(palette.bodyCover()), true, segment, prepared, submesh);
                        if (palette.extra().alpha8() > 0) {
                            addPass(grouped, CHILD_BELLY_EXTRA_MASK, palette.extra().multiply(palette.bodyCover()), true, segment, prepared, submesh);
                        }
                    }
                } else if (adultVariant == AdultNormalVariant.HERO) {
                    addPass(grouped, HERO_BELLY_MASK, palette.belly(), true, segment, prepared, submesh);
                } else if (adultVariant == AdultNormalVariant.DARK) {
                    addPass(grouped, DARK_BELLY_MASK, palette.belly().multiply(palette.bodyCover()), true,
                            segment, prepared, submesh);
                } else {
                    addPass(grouped, BELLY_MASK, palette.belly(), true, segment, prepared, submesh);
                }
            }
            case HORNS -> {
                ChaoColor layer = type == ChaoVisualType.CHILD && state.monotone()
                        ? (state.colorType() == com.chaocraft.visual.ChaoColorType.NORMAL ? palette.base() : ChaoColor.WHITE)
                        : palette.base();
                addPass(grouped, WHITE_TEXTURE, layer.multiply(palette.bodyCover()), false, segment, prepared, submesh);
                if (!(type == ChaoVisualType.CHILD && state.monotone())) {
                    addPass(grouped, HORNS_MASK, palette.horns().multiply(palette.bodyCover()), true, segment, prepared, submesh);
                }
            }
            case WINGS -> {
                addPass(grouped, WHITE_TEXTURE, palette.wingsBase().multiply(palette.wingsCover()), false,
                        segment, prepared, submesh);
                if (type == ChaoVisualType.CHILD) {
                    addPass(grouped, WINGS_MASK, palette.wings().multiply(palette.wingsCover()), true, segment, prepared, submesh);
                } else if (adultVariant == AdultNormalVariant.HERO) {
                    addPass(grouped, HERO_WINGS_MASK, palette.wings(), true, segment, prepared, submesh);
                } else if (adultVariant == AdultNormalVariant.DARK) {
                    addPass(grouped, DARK_WINGS_MASK, palette.wings().multiply(palette.wingsCover()), true,
                            segment, prepared, submesh);
                    addPass(grouped, DARK_WINGS_EXTRA_MASK,
                            ChaoColor.rgb(222, 64, 222).multiply(palette.wingsCover()), true,
                            segment, prepared, submesh);
                } else {
                    addPass(grouped, WINGS_MASK, palette.wings(), true, segment, prepared, submesh);
                }
            }
            case EYES -> addPass(grouped, resolveEyeTexture(state), ChaoColor.WHITE,
                    false, segment, prepared, submesh);
            case EYELID -> {
                int eyelid = state.resolvedEyelid();
                ChaoColor eyelidColor;
                if (type == ChaoVisualType.CHILD && state.monotone()) {
                    eyelidColor = state.colorType() == com.chaocraft.visual.ChaoColorType.NORMAL
                            ? palette.base() : ChaoColor.WHITE;
                } else {
                    eyelidColor = palette.base().multiply(palette.bodyCover());
                }
                if (eyelid == 1) {
                    addPass(grouped, EYELID_DARK, eyelidColor, true, segment, prepared, submesh);
                } else if (eyelid == 2) {
                    // Viewer ChangeFace() sets _MainTex = null for eyelid mode 2. Unity then
                    // renders the eyelid material's full white fallback, tinted by head color.
                    addPass(grouped, WHITE_TEXTURE, eyelidColor, false, segment, prepared, submesh);
                }
            }
            case MOUTH_MID -> {
                Identifier texture = resolveMouthTexture(state.resolvedMouthMid());
                if (texture != null) {
                    addPass(grouped, texture, ChaoColor.WHITE, true, segment, prepared, submesh);
                }
            }
            case MOUTH_SIDE -> {
                Identifier texture = resolveMouthTexture(state.resolvedMouthSide());
                if (texture != null) {
                    addPass(grouped, texture, ChaoColor.WHITE, true, segment, prepared, submesh);
                }
            }
            case HIDDEN -> {
                // Intentionally unused source material slot.
            }
        }
    }

    private static void addPass(Map<BatchKey, List<DrawSource>> grouped, Identifier texture, ChaoColor color,
            boolean translucent, ChaoMeshModel.Segment segment, ChaoRenderCache.PreparedSegment prepared,
            ChaoMeshModel.Submesh submesh) {
        if (color.alpha8() == 0) {
            return;
        }
        BatchKey key = new BatchKey(texture, color, translucent, UvMode.MESH, false);
        grouped.computeIfAbsent(key, ignored -> new ArrayList<>())
                .add(new DrawSource(segment, prepared, submesh));
    }

    private static List<List<DrawSource>> partitionSources(List<DrawSource> sources, RenderLayer layer) {
        if (sources.isEmpty()) return List.of();
        int vertexSize = Math.max(1, layer.getVertexFormat().getVertexSizeByte());
        int preferredVertices = Math.max(1, (PREFERRED_BATCH_BUFFER_BYTES - 256) / vertexSize);
        List<List<DrawSource>> chunks = new ArrayList<>();
        List<DrawSource> current = new ArrayList<>();
        long currentVertices = 0L;
        for (DrawSource source : sources) {
            long sourceVertices = (Math.max(0, source.submesh().indexCount()) / 3L) * 4L;
            if (!current.isEmpty() && currentVertices + sourceVertices > preferredVertices) {
                chunks.add(List.copyOf(current));
                current.clear();
                currentVertices = 0L;
            }
            current.add(source);
            currentVertices += sourceVertices;
        }
        if (!current.isEmpty()) chunks.add(List.copyOf(current));
        return chunks;
    }

    private static int estimatedVertexCount(List<DrawSource> sources) {
        long vertices = 0L;
        for (DrawSource source : sources) {
            int indices = Math.max(0, source.submesh().indexCount());
            vertices += (indices / 3L) * 4L;
            if (vertices > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }
        }
        return (int) vertices;
    }

    private static int estimatedStaticVertexCount(ChaoMeshModel model) {
        long vertices = 0L;
        for (ChaoMeshModel.Segment segment : model.segments()) {
            for (ChaoMeshModel.Submesh submesh : segment.submeshes()) {
                vertices += (Math.max(0, submesh.indexCount()) / 3L) * 4L;
                if (vertices > Integer.MAX_VALUE) {
                    return Integer.MAX_VALUE;
                }
            }
        }
        return (int) vertices;
    }

    /**
     * BufferBuilder allocates native memory. Reserve close to the final size so a
     * large Chao/halo does not repeatedly grow direct buffers, while rejecting a
     * corrupt asset before it can request an unbounded allocation.
     */
    private static int estimatedBufferBytes(RenderLayer layer, int expectedVertices) {
        long bytes = (long) Math.max(1, expectedVertices) * layer.getVertexFormat().getVertexSizeByte() + 256L;
        if (bytes > MAX_BATCH_BUFFER_BYTES) {
            return -1;
        }
        return (int) Math.max(256L, bytes);
    }

    private static void appendSource(BufferBuilder builder, DrawSource source, ChaoColor color, int light,
            Matrix4f positionMatrix, Matrix3f normalMatrix, UvMode uvMode) {
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
                    light, positionMatrix, normalMatrix, uvMode);
            appendVertex(builder, source, segment.indices()[triangle + 2], red, green, blue, alpha,
                    light, positionMatrix, normalMatrix, uvMode);
            appendVertex(builder, source, segment.indices()[triangle + 1], red, green, blue, alpha,
                    light, positionMatrix, normalMatrix, uvMode);
            // Entity RenderLayers use QUADS; duplicate the last vertex to preserve
            // the source triangle as a degenerate quad, matching CP02 topology.
            appendVertex(builder, source, segment.indices()[triangle + 1], red, green, blue, alpha,
                    light, positionMatrix, normalMatrix, uvMode);
        }
    }

    private static void appendVertex(BufferBuilder builder, DrawSource source, int vertexIndex,
            float red, float green, float blue, float alpha, int light,
            Matrix4f positionMatrix, Matrix3f normalMatrix, UvMode uvMode) {
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

        float envX = tnx;
        float envY = tny;
        float envZ = tnz;
        if (uvMode == UvMode.CUBEMAP_STRIP) {
            // Viewer reflections are Cubemap lookups, not ordinary UV textures.
            // Approximate the cubemap reflection vector for a camera looking down
            // local +Z: R = V - 2 * dot(V,N) * N. This produces the broad moving
            // metal/jewel highlights seen in the Viewer instead of painting the
            // cubemap strip directly over the model.
            float dot = tnz;
            envX = -2.0F * dot * tnx;
            envY = -2.0F * dot * tny;
            envZ = 1.0F - 2.0F * dot * tnz;
        }

        builder.vertex(
                tx, ty, tz,
                red, green, blue, alpha,
                uvMode == UvMode.CUBEMAP_STRIP ? cubeUvU(envX, envY, envZ) : segment.uvs()[uv],
                uvMode == UvMode.CUBEMAP_STRIP ? cubeUvV(envX, envY, envZ) : 1.0F - segment.uvs()[uv + 1],
                OverlayTexture.DEFAULT_UV, light,
                tnx, tny, tnz
        );
    }

    private static Matrix4f createLocalPositionMatrix(ChaoVisualType type) {
        Matrix4f matrix = new Matrix4f().identity();
        if (type == ChaoVisualType.CHILD) {
            matrix.rotateX((float) Math.toRadians(90.0F));
        }
        return matrix;
    }

    private static Matrix3f createLocalNormalMatrix(ChaoVisualType type) {
        Matrix3f matrix = new Matrix3f().identity();
        if (type == ChaoVisualType.CHILD) {
            matrix.rotateX((float) Math.toRadians(90.0F));
        }
        return matrix;
    }

    private static void drawGpuBatches(ChaoGpuRenderCache.Entry entry, MatrixStack matrices, Matrix3f restoreNormalMatrix) {
        RenderSystem.assertOnRenderThread();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        Matrix4f modelViewMatrix = matrices.peek().getPositionMatrix();
        Matrix3f normalMatrix = matrices.peek().getNormalMatrix();
        Matrix4f projectionMatrix = RenderSystem.getProjectionMatrix();

        if (entry.isClosed()) {
            return;
        }
        for (ChaoGpuRenderCache.DrawBatch batch : entry.batches()) {
            if (batch.vertexBuffer().isClosed()) {
                continue;
            }
            RenderLayer layer = batch.layer();
            layer.startDrawing();
            try {
                ShaderProgram shader = RenderSystem.getShader();
                if (shader == null) {
                    continue;
                }

                // Immediate entity rendering CPU-transforms normals before they hit
                // the shader. Our reusable VBO keeps them in local space, so provide
                // the draw-time normal matrix when that standard uniform exists.
                GlUniform normalUniform = shader.getUniform("NormalMat");
                if (normalUniform != null) {
                    normalUniform.set(normalMatrix);
                }

                batch.vertexBuffer().bind();
                try {
                    batch.vertexBuffer().draw(modelViewMatrix, projectionMatrix, shader);
                } finally {
                    VertexBuffer.unbind();
                }

                // Do not leak our entity transform into the next renderer sharing
                // Minecraft's shader program. Upload the parent normal matrix while
                // this RenderLayer still owns the active shader.
                if (normalUniform != null) {
                    normalUniform.set(restoreNormalMatrix);
                }
            } finally {
                layer.endDrawing();
            }
        }
    }

    private static MaterialRole resolveMaterialRole(ChaoVisualType type, AdultNormalVariant adultVariant,
            String segmentName, int submeshIndex) {
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
                // ChangeChild(): body, horns, eyes, eyelid, mouth side, mouth mid.
                return switch (submeshIndex) {
                    case 0 -> MaterialRole.BODY;
                    case 1 -> MaterialRole.HORNS;
                    case 2 -> MaterialRole.EYES;
                    case 3 -> MaterialRole.EYELID;
                    case 4 -> MaterialRole.MOUTH_SIDE;
                    case 5 -> MaterialRole.MOUTH_MID;
                    default -> MaterialRole.HIDDEN;
                };
            }
            if (adultVariant == AdultNormalVariant.DARK) {
                // ChangeDarkNormal(): body, eyes, eyelid, body, mouth mid, mouth side.
                // The second body slot is real and must not be discarded.
                return switch (submeshIndex) {
                    case 0, 3 -> MaterialRole.BODY;
                    case 1 -> MaterialRole.EYES;
                    case 2 -> MaterialRole.EYELID;
                    case 4 -> MaterialRole.MOUTH_MID;
                    case 5 -> MaterialRole.MOUTH_SIDE;
                    default -> MaterialRole.HIDDEN;
                };
            }
            // ChangeNeutralNormal()/ChangeHeroNormal(): body, eyes, eyelid, mouth mid, mouth side.
            return switch (submeshIndex) {
                case 0 -> MaterialRole.BODY;
                case 1 -> MaterialRole.EYES;
                case 2 -> MaterialRole.EYELID;
                case 3 -> MaterialRole.MOUTH_MID;
                case 4 -> MaterialRole.MOUTH_SIDE;
                default -> MaterialRole.HIDDEN;
            };
        }
        return MaterialRole.BODY;
    }

    private static Identifier resolveEyeTexture(ChaoAppearanceState state) {
        return EYE_TEXTURES[state.resolvedEyes()];
    }

    private static Identifier resolveMouthTexture(int index) {
        return index <= 0 || index >= MOUTH_TEXTURES.length ? null : MOUTH_TEXTURES[index];
    }


    /** Renders the eight AnimalObject slots using the exact Viewer scene meshes/materials. */
    private List<ChaoGpuRenderCache.DrawBatch> buildAnimalPartBatches(ChaoAppearanceState state,
            ChaoAdultFamily adultFamily, int light) {
        if (state.animalParts().isEmpty()) return List.of();
        boolean adult = state.type() != ChaoVisualType.CHILD;
        List<ChaoGpuRenderCache.DrawBatch> result = new ArrayList<>();
        for (Slot slot : Slot.values()) {
            ChaoAnimalType animal = state.animalParts().get(slot);
            if (animal == ChaoAnimalType.NONE) continue;
            ChaoAnimalPartCatalog.PartSpec spec = ChaoAnimalPartCatalog.resolve(adult, animal, slot);
            if (spec == null) continue;
            ChaoMeshModel partModel = getAnimalModel(spec.model());
            if (partModel == null) continue;

            Vector3f anchor = resolveAnimalAnchor(state, adultFamily, slot);
            Matrix4f positionMatrix = createAnimalPositionMatrix(anchor, spec);
            Matrix3f normalMatrix = createAnimalNormalMatrix(spec);
            int materialIndex = 0;
            for (ChaoMeshModel.Segment segment : partModel.segments()) {
                for (ChaoMeshModel.Submesh submesh : segment.submeshes()) {
                    ChaoAnimalPartCatalog.MaterialSpec mat = spec.materials().isEmpty()
                            ? new ChaoAnimalPartCatalog.MaterialSpec(WHITE_TEXTURE, 1F,1F,1F,1F)
                            : spec.materials().get(Math.min(materialIndex, spec.materials().size() - 1));
                    materialIndex++;
                    ChaoGpuRenderCache.DrawBatch batch = buildStaticTexturedBatch(
                            partModel, segment, submesh, mat, light, positionMatrix, normalMatrix);
                    if (batch != null) result.add(batch);
                }
            }
        }
        return result;
    }

    private ChaoGpuRenderCache.DrawBatch buildStaticTexturedBatch(ChaoMeshModel model,
            ChaoMeshModel.Segment segment, ChaoMeshModel.Submesh submesh,
            ChaoAnimalPartCatalog.MaterialSpec material, int light,
            Matrix4f positionMatrix, Matrix3f normalMatrix) {
        boolean translucent = material.a() < 0.999F;
        RenderLayer layer = translucent ? RenderLayer.getEntityTranslucent(material.texture())
                : RenderLayer.getEntityCutoutNoCull(material.texture());
        int expectedVertices = (Math.max(0, submesh.indexCount()) / 3) * 4;
        int bufferBytes = estimatedBufferBytes(layer, expectedVertices);
        if (bufferBytes < 0) return null;
        BufferBuilder builder = new BufferBuilder(bufferBytes);
        builder.begin(layer.getDrawMode(), layer.getVertexFormat());
        appendStaticSubmesh(builder, segment, submesh,
                new ChaoColor(material.r(), material.g(), material.b(), material.a()),
                light, positionMatrix, normalMatrix);
        BufferBuilder.BuiltBuffer built = builder.end();
        if (built.isEmpty()) { built.release(); return null; }
        VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        try {
            vertexBuffer.bind();
            vertexBuffer.upload(built);
        } catch (RuntimeException exception) {
            vertexBuffer.close();
            throw exception;
        } finally { VertexBuffer.unbind(); }
        return new ChaoGpuRenderCache.DrawBatch(layer, vertexBuffer, bufferBytes);
    }

    private static Vector3f resolveAnimalAnchor(ChaoAppearanceState state, ChaoAdultFamily adultFamily, Slot slot) {
        if (state.type() == ChaoVisualType.CHILD) {
            // Scene Child anchors are at origin except Tail, whose parent is y=-.14.
            return slot == Slot.TAIL ? new Vector3f(0F, -0.14F, 0F) : new Vector3f();
        }
        if (adultFamily != null) return ChaoAnimalAnchorProfiles.resolve(adultFamily, state, slot);
        return new Vector3f();
    }

    private static Matrix4f createAnimalPositionMatrix(Vector3f anchor, ChaoAnimalPartCatalog.PartSpec spec) {
        Vector3f pos = new Vector3f(anchor).add(spec.position());
        Quaternionf q = spec.rotation();
        // Reflect Unity's transform through Z to match appendStaticVertex handedness conversion.
        Quaternionf converted = new Quaternionf(-q.x, -q.y, q.z, q.w).normalize();
        return new Matrix4f().identity()
                .translate(pos.x, pos.y, -pos.z)
                .rotate(converted)
                .scale(spec.scale());
    }

    private static Matrix3f createAnimalNormalMatrix(ChaoAnimalPartCatalog.PartSpec spec) {
        Quaternionf q = spec.rotation();
        Quaternionf converted = new Quaternionf(-q.x, -q.y, q.z, q.w).normalize();
        Vector3f scale = spec.scale();
        Matrix3f matrix = new Matrix3f().identity().rotate(converted);
        // All Viewer animal parts currently use unit scale; retain inverse-scale support for safety.
        if (scale.x != 0F && scale.y != 0F && scale.z != 0F) {
            matrix.scale(1F / scale.x, 1F / scale.y, 1F / scale.z);
        }
        return matrix;
    }

    private ChaoMeshModel getAnimalModel(Identifier id) {
        if (animalLoadAttempted.add(id)) {
            ChaoMeshModel model = loadModel(id);
            if (model != null) animalModels.put(id, model);
        }
        return animalModels.get(id);
    }

    private List<ChaoGpuRenderCache.DrawBatch> buildEmotionBatches(ChaoAppearanceState state,
            ChaoPaletteState palette, int light) {
        List<EmotionVariant> variants = resolveEmotionVariants(state);
        if (variants.isEmpty()) {
            return List.of();
        }

        EmotionAnchor anchor = resolveEmotionAnchor(state);
        List<ChaoGpuRenderCache.DrawBatch> result = new ArrayList<>(variants.size());
        for (EmotionVariant variant : variants) {
            ChaoGpuRenderCache.DrawBatch batch = buildEmotionBatch(
                    variant, anchor, state.tiltedHalo(), palette, light
            );
            if (batch != null) {
                result.add(batch);
            }
        }
        return result;
    }

    private ChaoGpuRenderCache.DrawBatch buildEmotionBatch(EmotionVariant variant, EmotionAnchor anchor,
            boolean tiltedHalo, ChaoPaletteState palette, int light) {
        ChaoMeshModel model = switch (variant) {
            case NEUTRAL -> getNeutralBallModel();
            case HERO -> getHeroHaloModel();
            case DARK -> getDarkBallModel();
        };
        if (model == null || model.segments().isEmpty()) {
            return null;
        }

        RenderLayer layer = RenderLayer.getEntityCutoutNoCull(WHITE_TEXTURE);
        int expectedVertices = estimatedStaticVertexCount(model);
        int bufferBytes = estimatedBufferBytes(layer, expectedVertices);
        if (bufferBytes < 0) {
            ChaoCraft.LOGGER.error("Skipping oversized Chao emotion batch {} ({} vertices)", variant, expectedVertices);
            return null;
        }
        BufferBuilder builder = new BufferBuilder(bufferBytes);
        builder.begin(layer.getDrawMode(), layer.getVertexFormat());

        EmotionShape shape = resolveEmotionShape(variant, tiltedHalo);
        Matrix4f positionMatrix = createEmotionPositionMatrix(anchor, shape);
        Matrix3f normalMatrix = createEmotionNormalMatrix(shape);
        ChaoColor color = palette.emotionBall();

        for (ChaoMeshModel.Segment segment : model.segments()) {
            for (ChaoMeshModel.Submesh submesh : segment.submeshes()) {
                appendStaticSubmesh(builder, segment, submesh, color, light, positionMatrix, normalMatrix);
            }
        }

        BufferBuilder.BuiltBuffer built = builder.end();
        if (built.isEmpty()) {
            built.release();
            return null;
        }

        VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        try {
            vertexBuffer.bind();
            vertexBuffer.upload(built);
        } catch (RuntimeException exception) {
            vertexBuffer.close();
            throw exception;
        } finally {
            VertexBuffer.unbind();
        }
        return new ChaoGpuRenderCache.DrawBatch(layer, vertexBuffer, bufferBytes);
    }

    private static void appendStaticSubmesh(BufferBuilder builder, ChaoMeshModel.Segment segment,
            ChaoMeshModel.Submesh submesh, ChaoColor color, int light,
            Matrix4f positionMatrix, Matrix3f normalMatrix) {
        int first = submesh.firstIndex();
        int end = first + submesh.indexCount();
        if (first < 0 || end > segment.indices().length || submesh.indexCount() % 3 != 0) {
            ChaoCraft.LOGGER.warn("Skipping malformed Chao emotion submesh {}:{}", segment.name(), first);
            return;
        }

        for (int triangle = first; triangle < end; triangle += 3) {
            appendStaticVertex(builder, segment, segment.indices()[triangle], color, light, positionMatrix, normalMatrix);
            appendStaticVertex(builder, segment, segment.indices()[triangle + 2], color, light, positionMatrix, normalMatrix);
            appendStaticVertex(builder, segment, segment.indices()[triangle + 1], color, light, positionMatrix, normalMatrix);
            appendStaticVertex(builder, segment, segment.indices()[triangle + 1], color, light, positionMatrix, normalMatrix);
        }
    }

    private static void appendStaticVertex(BufferBuilder builder, ChaoMeshModel.Segment segment, int vertexIndex,
            ChaoColor color, int light, Matrix4f positionMatrix, Matrix3f normalMatrix) {
        int p = vertexIndex * 3;
        int uv = vertexIndex * 2;

        float x = segment.positions()[p];
        float y = segment.positions()[p + 1];
        float z = -segment.positions()[p + 2];
        float nx = segment.normals()[p];
        float ny = segment.normals()[p + 1];
        float nz = -segment.normals()[p + 2];

        float tx = positionMatrix.m00() * x + positionMatrix.m10() * y + positionMatrix.m20() * z + positionMatrix.m30();
        float ty = positionMatrix.m01() * x + positionMatrix.m11() * y + positionMatrix.m21() * z + positionMatrix.m31();
        float tz = positionMatrix.m02() * x + positionMatrix.m12() * y + positionMatrix.m22() * z + positionMatrix.m32();

        float tnx = normalMatrix.m00() * nx + normalMatrix.m10() * ny + normalMatrix.m20() * nz;
        float tny = normalMatrix.m01() * nx + normalMatrix.m11() * ny + normalMatrix.m21() * nz;
        float tnz = normalMatrix.m02() * nx + normalMatrix.m12() * ny + normalMatrix.m22() * nz;
        float lengthSquared = tnx * tnx + tny * tny + tnz * tnz;
        if (lengthSquared > 0.000001F) {
            float inverseLength = (float) (1.0D / Math.sqrt(lengthSquared));
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
                color.r(), color.g(), color.b(), color.a(),
                segment.uvs()[uv], 1.0F - segment.uvs()[uv + 1],
                OverlayTexture.DEFAULT_UV, light,
                tnx, tny, tnz
        );
    }

    private static List<EmotionVariant> resolveEmotionVariants(ChaoAppearanceState state) {
        if (state.customEmotionBall()) {
            List<EmotionVariant> result = new ArrayList<>(3);
            if (state.neutralBall()) {
                result.add(EmotionVariant.NEUTRAL);
            }
            if (state.darkBall()) {
                result.add(EmotionVariant.DARK);
            }
            if (state.heroBall()) {
                result.add(EmotionVariant.HERO);
            }
            return result;
        }

        // Exact SetEmotionBall() auto selection from the Viewer.
        if (state.type() == ChaoVisualType.CHILD) {
            return List.of(EmotionVariant.NEUTRAL);
        }
        if (state.alignment() <= -50.0F) {
            return List.of(EmotionVariant.DARK);
        }
        if (state.alignment() >= 50.0F) {
            return List.of(EmotionVariant.HERO);
        }
        return List.of(EmotionVariant.NEUTRAL);
    }

    /** EmotionSphere parent position is selected by the active Chao family/palette. */
    private static EmotionAnchor resolveEmotionAnchor(ChaoAppearanceState state) {
        if (state.type() == ChaoVisualType.CHILD) {
            // ChangeChild() always uses CN.EmotionBallPos, including Hero/Dark Child morphs.
            return new EmotionAnchor(0.0F, 5.0F, 0.2F);
        }
        if (state.type() == ChaoVisualType.CHAOS) {
            ChaoChaosFamily family = ChaoChaosFamily.resolve(state);
            return new EmotionAnchor(family.emotionX(), family.emotionY(), family.emotionZ());
        }
        ChaoAdultFamily family = ChaoAdultFamily.resolve(state);
        return new EmotionAnchor(family.emotionX(), family.emotionY(), family.emotionZ());
    }

    /** Child object transforms under Unity's EmotionSphere parent. */
    private static EmotionShape resolveEmotionShape(EmotionVariant variant, boolean tiltedHalo) {
        return switch (variant) {
            case NEUTRAL -> new EmotionShape(0.84F, new Quaternionf());
            case DARK -> new EmotionShape(0.50267804F,
                    // Unity DarkBall = Euler(-90,0,0); Z reflection flips X rotation.
                    new Quaternionf(0.7071068F, 0.0F, 0.0F, 0.7071068F));
            case HERO -> {
                if (tiltedHalo) {
                    // Scene transform produced by the Viewer TiltedHalo option.
                    yield new EmotionShape(0.85260004F,
                            new Quaternionf(-0.7596453F, -0.04722686F, 0.055514425F, 0.6462406F));
                }
                // GameController default: SetHaloRotaion(Euler(90,0,0)).
                yield new EmotionShape(0.85260004F,
                        new Quaternionf(-0.7071068F, 0.0F, 0.0F, 0.7071068F));
            }
        };
    }

    private static Matrix4f createEmotionPositionMatrix(EmotionAnchor anchor, EmotionShape shape) {
        return new Matrix4f().identity()
                .translate(anchor.x(), anchor.y(), -anchor.z())
                .rotate(shape.rotation())
                .scale(shape.scale());
    }

    private static Matrix3f createEmotionNormalMatrix(EmotionShape shape) {
        return new Matrix3f().identity().rotate(shape.rotation());
    }

    private ChaoMeshModel getModel(ChaoAppearanceState state) {
        if (state.type() == ChaoVisualType.CHILD) {
            return getChildModel();
        }
        if (state.type() == ChaoVisualType.CHAOS) {
            return getChaosModel(ChaoChaosFamily.resolve(state));
        }
        return getAdultModel(ChaoAdultFamily.resolve(state));
    }

    private ChaoMeshModel getChaosModel(ChaoChaosFamily family) {
        if (chaosLoadAttempted.add(family)) {
            chaosModels.put(family, loadModel(family.model()));
        }
        return chaosModels.get(family);
    }

    private ChaoMeshModel getAdultModel(ChaoAdultFamily family) {
        if (adultLoadAttempted.add(family)) {
            adultModels.put(family, loadModel(family.model()));
        }
        return adultModels.get(family);
    }

    private static AdultNormalVariant resolveAdultNormalVariant(ChaoAppearanceState state) {
        if (state.type() == ChaoVisualType.NORMAL) {
            if (state.alignment() >= 50.0F) {
                return AdultNormalVariant.HERO;
            }
            if (state.alignment() <= -50.0F) {
                return AdultNormalVariant.DARK;
            }
        }
        return AdultNormalVariant.NEUTRAL;
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

    private ChaoMeshModel getHeroNormalModel() {
        if (!heroNormalLoadAttempted) {
            heroNormalLoadAttempted = true;
            heroNormalModel = loadModel(HERO_NORMAL_MODEL);
        }
        return heroNormalModel;
    }

    private ChaoMeshModel getDarkNormalModel() {
        if (!darkNormalLoadAttempted) {
            darkNormalLoadAttempted = true;
            darkNormalModel = loadModel(DARK_NORMAL_MODEL);
        }
        return darkNormalModel;
    }

    private ChaoMeshModel getNeutralBallModel() {
        if (!neutralBallLoadAttempted) {
            neutralBallLoadAttempted = true;
            neutralBallModel = loadModel(NEUTRAL_BALL_MODEL);
        }
        return neutralBallModel;
    }

    private ChaoMeshModel getHeroHaloModel() {
        if (!heroHaloLoadAttempted) {
            heroHaloLoadAttempted = true;
            heroHaloModel = loadModel(HERO_HALO_MODEL);
        }
        return heroHaloModel;
    }

    private ChaoMeshModel getDarkBallModel() {
        if (!darkBallLoadAttempted) {
            darkBallLoadAttempted = true;
            darkBallModel = loadModel(DARK_BALL_MODEL);
        }
        return darkBallModel;
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

    private enum AdultNormalVariant {
        NEUTRAL,
        HERO,
        DARK
    }

    private enum MaterialRole {
        BODY,
        BELLY,
        HORNS,
        WINGS,
        EYES,
        EYELID,
        MOUTH_MID,
        MOUTH_SIDE,
        HIDDEN
    }

    private enum EmotionVariant {
        NEUTRAL,
        HERO,
        DARK
    }

    private record EmotionAnchor(float x, float y, float z) {
    }

    private record EmotionShape(float scale, Quaternionf rotation) {
    }

    private enum UvMode { MESH, CUBEMAP_STRIP }

    private record BatchKey(Identifier texture, ChaoColor color, boolean translucent, UvMode uvMode, boolean fullbright) {
    }

    private record DrawSource(
            ChaoMeshModel.Segment segment,
            ChaoRenderCache.PreparedSegment prepared,
            ChaoMeshModel.Submesh submesh
    ) {
    }
}
