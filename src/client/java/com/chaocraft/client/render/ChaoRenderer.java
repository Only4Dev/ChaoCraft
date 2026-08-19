package com.chaocraft.client.render;

import com.chaocraft.ChaoCraft;
import com.chaocraft.client.render.cache.ChaoGpuRenderCache;
import com.chaocraft.client.animation.ChaoAnimationClip;
import com.chaocraft.client.animation.ChaoAnimationPose;
import com.chaocraft.client.animation.ChaoAnimationRepository;
import com.chaocraft.client.animation.ChaoSa2RigNodeRegistry;
import com.chaocraft.client.animation.ChaoSa2BindProfile;
import com.chaocraft.client.animation.ChaoSa2BindProfileRegistry;
import com.chaocraft.client.render.cache.ChaoGpuMemoryException;
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
import com.chaocraft.client.render.shader.ChaoMaterialShader;
import com.chaocraft.client.render.shader.ChaoReflectionShader;
import com.chaocraft.client.render.shader.ChaoReflectionSkinningShader;
import com.chaocraft.client.render.shader.ChaoShaderPackCompat;
import com.chaocraft.client.render.shader.ChaoSkinningShader;
import com.chaocraft.client.render.animal.ChaoAnimalPartCatalog;
import com.chaocraft.client.render.animal.ChaoAnimalAnchorProfiles;
import com.chaocraft.client.render.animal.ChaoChaosAnchorProfiles;
import com.chaocraft.client.render.deco.ChaoHeadDecoAnchorProfiles;
import com.chaocraft.client.render.deco.ChaoHeadDecoCatalog;
import com.chaocraft.client.render.mesh.ChaoMeshLoader;
import com.chaocraft.client.render.mesh.ChaoMeshModel;
import com.chaocraft.client.render.mesh.ChaoMeshRepository;
import com.chaocraft.client.render.mesh.ChaoSkinnedVertexFormat;
import com.chaocraft.entity.ChaoEntity;
import com.chaocraft.visual.ChaoAppearanceState;
import com.chaocraft.visual.ChaoColorType;
import com.chaocraft.visual.ChaoMorphResolver;
import com.chaocraft.visual.ChaoMorphWeights;
import com.chaocraft.visual.ChaoVisualType;
import com.chaocraft.visual.ChaoReflectionType;
import com.chaocraft.visual.ChaoAnimalType;
import com.chaocraft.visual.ChaoAnimalParts;
import com.chaocraft.visual.ChaoAnimalParts.Slot;
import com.chaocraft.visual.ChaoHeadDecoType;
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
import net.minecraft.client.render.VertexFormat;
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
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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

    // Viewer cubemaps are as small as 16x16 per face and use bilinear filtering
    // with clamp. Keep lookups away from face borders so linear filtering never
    // blends two unrelated faces of the vertical six-face strip.
    private static final float CUBEMAP_FACE_EDGE_INSET = 1.0F / 32.0F;

    // F8 is a debug scrubber, not gameplay. Coalesce rapid slider changes so
    // OpenGL receives the final visual state instead of dozens of transient VBO uploads.
    private static final long PREVIEW_BUILD_DEBOUNCE_NANOS = 120_000_000L;
    private static final Identifier CHILD_MODEL = ChaoCraft.id("models/chao/child.cmesh");
    private static final String NEUTRAL_NORMAL_BIND_PROFILE = "al_nnn";
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

    private static final int ATTACHMENT_NODE_BASE = 64;

    private static int attachmentRigNode(int sa2Node) {
        return ATTACHMENT_NODE_BASE + sa2Node;
    }

    private static boolean isAttachmentRigNode(int rigNode) {
        return rigNode >= ATTACHMENT_NODE_BASE
                && rigNode < ATTACHMENT_NODE_BASE + ChaoAnimationPose.NODE_COUNT;
    }

    private static int decodeAttachmentRigNode(int rigNode) {
        return rigNode - ATTACHMENT_NODE_BASE;
    }

    private final ChaoRenderCache morphCache = new ChaoRenderCache();
    private final ChaoGpuRenderCache gpuCache = new ChaoGpuRenderCache();

    // F8 is a debug viewer, not the production world pipeline. Keep its VBOs
    // isolated so it can preserve the old full-bright entity-shader presentation
    // without reintroducing light variants into the optimized world cache.
    private final ChaoGpuRenderCache previewGpuCache = new ChaoGpuRenderCache(false);
    private final Map<ChaoAdultFamily, ChaoMeshModel> adultModels = new EnumMap<>(ChaoAdultFamily.class);
    private final EnumSet<ChaoAdultFamily> adultLoadAttempted = EnumSet.noneOf(ChaoAdultFamily.class);
    private final Map<ChaoChaosFamily, ChaoMeshModel> chaosModels = new EnumMap<>(ChaoChaosFamily.class);
    private final EnumSet<ChaoChaosFamily> chaosLoadAttempted = EnumSet.noneOf(ChaoChaosFamily.class);
    private final Map<Identifier, ChaoMeshModel> animalModels = new LinkedHashMap<>();
    private final Set<Identifier> animalLoadAttempted = new java.util.HashSet<>();
    private final Map<Identifier, ChaoMeshModel> headDecoModels = new LinkedHashMap<>();
    private final Set<Identifier> headDecoLoadAttempted = new java.util.HashSet<>();

    /**
     * Head decorations are immutable source meshes and must never be duplicated
     * into every complete Chao VisualKey. One GPU copy per decoration type is
     * enough; family/evolution anchor translation is draw-time state.
     */
    private final Map<ChaoHeadDecoType, List<ChaoGpuRenderCache.DrawBatch>> sharedHeadDecoBatches =
            new java.util.EnumMap<>(ChaoHeadDecoType.class);

    /**
     * Immutable Animal Part geometry shared across all Chao visual states.
     *
     * <p>Keyed only by source asset identity. Chao-specific anchor translation,
     * animation node transform and body morph state are draw-time concerns and
     * must not multiply GPU copies of the same rigid Viewer part.</p>
     */
    private final Map<SharedAnimalPartKey, List<ChaoGpuRenderCache.DrawBatch>> sharedAnimalPartBatches =
            new LinkedHashMap<>();

    private boolean sharedGpuWarmupScheduled;
    private boolean sharedGpuWarmupComplete;
    private boolean sharedGpuWarmupRunning;

    /**
     * Finite palette/material profiles for base Chao states.
     *
     * <p>These are CPU-side immutable palette results only; they deliberately
     * do not allocate one body VBO per Color/Tone combination.</p>
     */
    private final Map<BasePaletteKey, ChaoPaletteState> basePaletteRuntimeCache =
            new LinkedHashMap<>();

    // F8 records the latest requested state and only uploads once the slider
    // has been stable briefly. The previous VBO remains visible during scrubbing.
    private final Map<UUID, PreviewGpuState> previewGpuStates = new HashMap<>();
    private long lastNeutralAdultSkinDiagnosticNanos;

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

        /*
         * Do not warm here. EntityRenderer construction happens before the
         * initial CLIENT_RESOURCES reload, which caused CP12H.2.1 to warm twice.
         * The renderer-cache reload listener calls clearAllCaches(true), and that
         * path schedules the single authoritative warmup after invalidation.
         */
    }

    private void scheduleSharedGpuWarmup() {
        if (sharedGpuWarmupScheduled || sharedGpuWarmupComplete) {
            return;
        }
        sharedGpuWarmupScheduled = true;
        MinecraftClient.getInstance().execute(() -> {
            sharedGpuWarmupScheduled = false;
            if (!sharedGpuWarmupComplete) {
                warmFiniteSharedResources();
            }
        });
    }

    /**
     * Hard cache invalidation. Reserved for resource reload/client teardown where
     * meshes, textures or shaders may actually have changed.
     */
    public static void clearAllCaches(boolean invalidateModels) {
        runOnRenderThread(() -> {
            synchronized (INSTANCES) {
                for (ChaoRenderer renderer : List.copyOf(INSTANCES)) {
                    renderer.clearLocalCaches(invalidateModels);
                }
            }
        });
    }

    /**
     * Disconnect from a world without destroying reusable production VBOs.
     *
     * <p>Entity UUID bindings and transient CPU morph arrays are world-local;
     * immutable visual-state VBOs are client resources and remain warm.</p>
     */
    public static void detachWorldCaches() {
        runOnRenderThread(() -> {
            synchronized (INSTANCES) {
                for (ChaoRenderer renderer : List.copyOf(INSTANCES)) {
                    renderer.gpuCache.detachWorldBindingsKeepWarm();
                    renderer.previewGpuCache.clear();
                    renderer.previewGpuStates.clear();
                    renderer.morphCache.clear();
                }
            }
        });
    }

    /** Rebase retained warm VBOs to the newly joined ClientWorld clock. */
    public static void beginWorldCaches(long worldTick) {
        runOnRenderThread(() -> {
            synchronized (INSTANCES) {
                for (ChaoRenderer renderer : List.copyOf(INSTANCES)) {
                    renderer.gpuCache.beginWorld(worldTick);
                    renderer.previewGpuCache.clear();
                    renderer.previewGpuStates.clear();
                    renderer.morphCache.clear();
                }
            }
        });
    }

    /** Runs bounded production-cache maintenance even in frames with zero Chao draws. */
    public static void maintainCaches(long worldTick) {
        runOnRenderThread(() -> {
            synchronized (INSTANCES) {
                for (ChaoRenderer renderer : List.copyOf(INSTANCES)) {
                    renderer.gpuCache.maintenance(worldTick);
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
                    renderer.previewGpuCache.removeAndEvict(entityId);
                    renderer.previewGpuStates.remove(entityId);
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
        previewGpuCache.clear();
        previewGpuStates.clear();
        morphCache.clear();
        clearSharedAnimalPartBatches();
        clearSharedHeadDecoBatches();
        if (!invalidateModels) {
            return;
        }
        adultModels.clear();
        adultLoadAttempted.clear();
        chaosModels.clear();
        chaosLoadAttempted.clear();
        animalModels.clear();
        animalLoadAttempted.clear();
        headDecoModels.clear();
        headDecoLoadAttempted.clear();
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
        basePaletteRuntimeCache.clear();
        sharedGpuWarmupComplete = false;
        sharedGpuWarmupScheduled = false;
        scheduleSharedGpuWarmup();
    }

    /**
     * Prepares every finite reusable visual resource once per client resource
     * lifetime. Full morph/body combinations are deliberately excluded.
     */
    private void warmFiniteSharedResources() {
        RenderSystem.assertOnRenderThread();
        if (sharedGpuWarmupComplete || sharedGpuWarmupRunning) {
            return;
        }

        sharedGpuWarmupRunning = true;
        long started = System.nanoTime();
        int headDecoTypes = 0;
        int animalPartKeys = 0;
        int baseModels = 0;
        int paletteProfiles = 0;
        int bindProfiles = 0;
        int warmBodyProfiles = 0;
        long warmBodyBytes = 0L;
        long attachmentBytes = 0L;

        try {
            // Finite immutable CPU rig data. Parse once per Minecraft process;
            // every later Chao/model shares these precomputed bind/inverse-bind matrices.
            bindProfiles = ChaoSa2BindProfileRegistry.preload();

            // Base model references. ChaoMeshRepository already preloaded the
            // underlying CPU arrays; this binds them into renderer-local lookup
            // maps before gameplay needs them.
            if (getChildModel() != null) baseModels++;
            for (ChaoAdultFamily family : ChaoAdultFamily.values()) {
                if (getAdultModel(family) != null) baseModels++;
            }
            for (ChaoChaosFamily family : ChaoChaosFamily.values()) {
                if (getChaosModel(family) != null) baseModels++;
            }
            if (getNeutralNormalModel() != null) baseModels++;
            if (getHeroNormalModel() != null) baseModels++;
            if (getDarkNormalModel() != null) baseModels++;
            if (getNeutralBallModel() != null) baseModels++;
            if (getHeroHaloModel() != null) baseModels++;
            if (getDarkBallModel() != null) baseModels++;

            /*
             * Color/Tone are finite material choices, but full palettes also
             * depend on type/alignment/evolution. Pre-resolve every base family
             * profile for all 14 colors x both tone modes so switching ordinary
             * base appearances does not pay palette composition during gameplay.
             *
             * We intentionally cache only CPU palette state here. Uploading a
             * complete body VBO for every color/tone would multiply native memory
             * for geometry that is otherwise identical.
             */
            float[] baseAlignments = {-100.0F, 0.0F, 100.0F};
            for (ChaoVisualType visualType : ChaoVisualType.values()) {
                for (float alignment : baseAlignments) {
                    ChaoAppearanceState baseState = ChaoAppearanceState.DEFAULT
                            .withType(visualType)
                            .withAge(visualType == ChaoVisualType.CHILD ? 0.0F : 1.0F)
                            .withAlignment(alignment);
                    ChaoMorphWeights baseWeights = ChaoMorphResolver.resolve(baseState);

                    for (ChaoColorType color : ChaoColorType.values()) {
                        for (boolean monotone : new boolean[] {false, true}) {
                            ChaoAppearanceState paletteState = baseState
                                    .withColorType(color)
                                    .withMonotone(monotone);
                            BasePaletteKey key = BasePaletteKey.from(paletteState);
                            basePaletteRuntimeCache.put(
                                    key,
                                    ChaoPaletteResolver.resolve(paletteState, baseWeights)
                            );
                            paletteProfiles++;
                        }
                    }
                }
            }

            /*
             * Pre-upload the 21 canonical no-accessory bodies (7 visual types x
             * Neutral/Hero/Dark alignment). The production cache ignores baked
             * light when ChaoCraft owns the shader, so these VBOs are immediately
             * reusable on first world entry. We intentionally do NOT expand this
             * across colors/parts/reflections; that combinatorial space remains
             * demand-built and bounded by the shared cache budget.
             */
            if (!ChaoShaderPackCompat.isShaderPackInUse()) {
                for (ChaoVisualType visualType : ChaoVisualType.values()) {
                    for (float alignment : baseAlignments) {
                        ChaoAppearanceState warmState = canonicalizeBodyCacheState(
                                ChaoAppearanceState.DEFAULT
                                        .withType(visualType)
                                        .withAge(visualType == ChaoVisualType.CHILD ? 0.0F : 1.0F)
                                        .withAlignment(alignment));
                        ChaoMeshModel warmModel = getModel(warmState);
                        if (warmModel == null) continue;
                        ChaoGpuRenderCache.Entry warmed = gpuCache.prewarm(
                                warmState, warmModel, 0, 0L, 0,
                                () -> buildWarmGpuBatches(warmState, warmModel, 0x00F000F0));
                        if (warmed != null) {
                            warmBodyProfiles++;
                            warmBodyBytes += warmed.estimatedBytes();
                        }
                    }
                }
            }

            for (ChaoHeadDecoType type : ChaoHeadDecoType.values()) {
                if (type == ChaoHeadDecoType.NONE) continue;
                List<ChaoGpuRenderCache.DrawBatch> batches =
                        getSharedHeadDecoBatches(type);
                if (!batches.isEmpty()) {
                    headDecoTypes++;
                    attachmentBytes += batches.stream()
                            .mapToLong(ChaoGpuRenderCache.DrawBatch::estimatedBytes)
                            .sum();
                }
            }

            // Both source variants are finite and small. Catalog.resolve() skips
            // combinations that do not actually exist.
            for (boolean adult : new boolean[] {false, true}) {
                for (ChaoAnimalType animal : ChaoAnimalType.values()) {
                    if (animal == ChaoAnimalType.NONE) continue;
                    for (Slot slot : Slot.values()) {
                        List<ChaoGpuRenderCache.DrawBatch> batches =
                                getSharedAnimalPartBatches(adult, animal, slot);
                        if (!batches.isEmpty()) {
                            animalPartKeys++;
                            attachmentBytes += batches.stream()
                                    .mapToLong(ChaoGpuRenderCache.DrawBatch::estimatedBytes)
                                    .sum();
                        }
                    }
                }
            }

            sharedGpuWarmupComplete = true;
            double elapsedMs = (System.nanoTime() - started) / 1_000_000.0;
            ChaoCraft.LOGGER.info(
                    "[Performance] Shared GPU warmup complete: {} SA2 bind profiles, "
                            + "{} base model refs, {} palette profiles, {} canonical body VBOs ({} KiB), "
                            + "{} HeadDeco types, {} AnimalPart keys, {} KiB attachment VBOs in {} ms",
                    bindProfiles,
                    baseModels,
                    paletteProfiles,
                    warmBodyProfiles,
                    (warmBodyBytes + 1023L) / 1024L,
                    headDecoTypes,
                    animalPartKeys,
                    (attachmentBytes + 1023L) / 1024L,
                    String.format(java.util.Locale.ROOT, "%.1f", elapsedMs));
        } catch (OutOfMemoryError | RuntimeException failure) {
            // Warmup is an optimization, never a correctness requirement.
            // Keep successfully-created shared VBOs and let remaining resources
            // fall back to the normal one-time lazy path.
            ChaoCraft.LOGGER.warn(
                    "[Performance] Shared GPU warmup stopped early; "
                            + "retaining completed resources and falling back to lazy loading ({})",
                    failure.getClass().getSimpleName());
        } finally {
            sharedGpuWarmupRunning = false;
        }
    }

    private void clearSharedAnimalPartBatches() {
        for (List<ChaoGpuRenderCache.DrawBatch> batches : sharedAnimalPartBatches.values()) {
            for (ChaoGpuRenderCache.DrawBatch batch : batches) {
                batch.close();
            }
        }
        sharedAnimalPartBatches.clear();
    }

    private void clearSharedHeadDecoBatches() {
        for (List<ChaoGpuRenderCache.DrawBatch> batches : sharedHeadDecoBatches.values()) {
            for (ChaoGpuRenderCache.DrawBatch batch : batches) {
                batch.close();
            }
        }
        sharedHeadDecoBatches.clear();
    }

    @Override
    public void render(ChaoEntity entity, float yaw, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light) {
        long renderStarted = System.nanoTime();
        ChaoAppearanceState state = ChaoRenderStateQuantizer.quantize(entity.getAppearanceState());
        ChaoAnimationClip worldAnimation = resolveVisualLabStressAnimation(entity);
        ChaoAnimationPose worldPose = worldAnimation == null
                ? null
                : resolvePreviewAnimationPose(
                        state, worldAnimation, visualLabStressFrame(entity, worldAnimation, tickDelta));
        try {
            ChaoGpuRenderCache.Entry gpuEntry = prepareGpuEntry(entity, state, light, worldPose != null);
            if (gpuEntry == null) {
                // A crowded scene may defer a first-time VBO build for a few
                // frames. Rendering a slime fallback made normal cache warm-up
                // visually noisy and broke Chao appearance parity. Keep the Chao
                // invisible until its real visual state is ready instead.
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
            Matrix4f[] worldRenderPalette = worldPose == null
                    ? null
                    : createRenderSpaceSkinPalette(worldPose, state.type());
            drawGpuBatches(
                    gpuEntry, matrices, parentNormal, light, false, worldPose,
                    state.reflectionType(), state.type(), worldRenderPalette);
            drawAnimalParts(state, matrices, parentNormal, light, false, worldPose, worldRenderPalette);
            drawHeadDeco(state, matrices, parentNormal, light, false, worldPose, worldRenderPalette);
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
        renderGuiPreview(entity, localGuiMatrices, light, yaw, pitch, null, 0.0D);
    }

    /**
     * Animation-Lab preview. The clip/frame are draw-time state and deliberately
     * stay outside VisualKey/VBO cache identity.
     */
    public void renderGuiPreview(ChaoEntity entity, MatrixStack localGuiMatrices, int light,
            float yaw, float pitch, ChaoAnimationClip animation, double animationFrame) {
        ChaoAppearanceState state = ChaoRenderStateQuantizer.quantize(entity.getAppearanceState());

        ChaoGpuRenderCache.Entry gpuEntry = preparePreviewGpuEntry(entity, state);
        if (gpuEntry == null) {
            return;
        }

        // Animation is pure draw-time state. Child remains on the already-approved
        // GPU skinning path. CP12I.2C evaluates Neutral Normal against al_nnn and
        // applies those deltas to rigid SA2-node batches; every other Adult/Chaos
        // remains outside this checkpoint.
        ChaoAnimationPose drawPose = resolvePreviewAnimationPose(state, animation, animationFrame);

        // DrawContext contains only the screen-local transform. Minecraft's GUI
        // projection also relies on RenderSystem's global model-view stack.
        MatrixStack matrices = new MatrixStack();
        matrices.peek().getPositionMatrix().set(RenderSystem.getModelViewStack().peek().getPositionMatrix());
        matrices.peek().getPositionMatrix().mul(localGuiMatrices.peek().getPositionMatrix());
        matrices.peek().getNormalMatrix().set(RenderSystem.getModelViewStack().peek().getNormalMatrix());
        matrices.peek().getNormalMatrix().mul(localGuiMatrices.peek().getNormalMatrix());

        matrices.push();
        Matrix3f parentNormal = new Matrix3f(matrices.peek().getNormalMatrix());
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - yaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pitch));

        /*
         * SA Tools and Minecraft view the converted Chao front with opposite
         * horizontal presentation parity. The rig itself must NOT be mirrored:
         * node3/node6/etc already occupy the same +X side as the corresponding
         * Viewer geometry, and attachments such as nodes4/11 depend on that.
         *
         * Mirror only the Animation/Visual Lab presentation so an asymmetric
         * source frame can be compared directly against SA Tools frame-for-frame.
         * This is equivalent to the user's successful mirror-camera test and
         * deliberately stays outside VBO/VisualKey/pose semantics.
         */
        matrices.scale(-MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);

        Matrix4f[] previewRenderPalette = drawPose == null
                ? null
                : createRenderSpaceSkinPalette(drawPose, state.type());
        drawGpuBatches(
                gpuEntry, matrices, parentNormal, 0x00F000F0, true, drawPose,
                state.reflectionType(), state.type(), previewRenderPalette);
        drawAnimalParts(
                state, matrices, parentNormal, 0x00F000F0, true, drawPose, previewRenderPalette);
        drawHeadDeco(state, matrices, parentNormal, 0x00F000F0, true, drawPose, previewRenderPalette);
        matrices.pop();
    }


    /**
     * HeadDeco mesh identity is not body geometry identity.
     *
     * <p>The base Chao only needs to know whether Head is retracted and whether
     * the emotion ball is hidden. This prevents changing Eggshell -> Pumpkin ->
     * Skull from producing three duplicate full-body GPU states.</p>
     */
    private static ChaoAppearanceState canonicalizeBodyCacheState(ChaoAppearanceState state) {
        ChaoAppearanceState canonicalState = state;

        // Exact HeadDeco asset identity does not change body vertices. The body
        // only cares whether head replacement is active and whether Emotion hides.
        if (canonicalState.headDeco() != ChaoHeadDecoType.NONE) {
            ChaoHeadDecoType canonicalDeco = canonicalState.headDeco().hidesEmotionBall()
                    ? ChaoHeadDecoType.WOOL_1
                    : ChaoHeadDecoType.EGGSHELL;
            canonicalState = canonicalState.withHeadDeco(canonicalDeco);
        }

        // Animal TYPE is attachment identity, not body identity. Preserve only
        // occupied slots because Viewer SizeDown/replacement morphs depend on
        // whether a slot exists, not which animal supplied the rigid mesh.
        ChaoAnimalParts canonicalParts = ChaoAnimalParts.NONE;
        for (Slot slot : Slot.values()) {
            if (state.animalParts().get(slot) != ChaoAnimalType.NONE) {
                canonicalParts = canonicalParts.with(slot, ChaoAnimalType.BEAR);
            }
        }
        canonicalState = canonicalState.withAnimalParts(canonicalParts);

        // Full-reflection jewel/metal colors use identical body geometry and
        // cubemap UVs. Keep special Bright/Shiny/TTMetal categories separate,
        // but collapse every full-reflection texture to GOLD in cache identity.
        ChaoReflectionType reflection = canonicalState.reflectionType();
        if (isFullReflectionTextureVariant(reflection)) {
            canonicalState = canonicalState.withReflectionType(ChaoReflectionType.GOLD);
        }

        return canonicalState;
    }

    private static boolean isFullReflectionTextureVariant(ChaoReflectionType type) {
        return switch (type) {
            case SILVER, GOLD, GARNET, RUBY, TOPAZ, SAPPHIRE, AQUAMARINE,
                    AMETHYST, PERIDOT, EMERALD, ONYX, PEARL, MOON -> true;
            case NONE, BRIGHT, SHINY, TT_METAL -> false;
        };
    }


    private ChaoGpuRenderCache.Entry preparePreviewGpuEntry(
            ChaoEntity entity, ChaoAppearanceState state) {
        ChaoAppearanceState cacheState = canonicalizeBodyCacheState(state);
        ChaoMeshModel model = getModel(cacheState);
        if (model == null) {
            return null;
        }

        long worldTick = entity.getWorld().getTime();
        int previewLight = 0x00F000F0;
        UUID previewId = entity.getUuid();
        long now = System.nanoTime();

        PreviewGpuState pending = previewGpuStates.get(previewId);
        ChaoGpuRenderCache.Entry bound = previewGpuCache.getBound(previewId);

        if (pending == null) {
            // Initial preview: make it immediately eligible for its first build.
            pending = new PreviewGpuState(cacheState, now - PREVIEW_BUILD_DEBOUNCE_NANOS);
            previewGpuStates.put(previewId, pending);
        } else if (!pending.requestedState().equals(cacheState)) {
            pending = new PreviewGpuState(cacheState, now);
            previewGpuStates.put(previewId, pending);

            // Keep rendering the previous stable preview while the slider is moving.
            if (bound != null) {
                return bound;
            }
        }

        if (bound != null
                && previewGpuCache.isBoundTo(previewId, cacheState, model, previewLight)) {
            return bound;
        }

        if (now - pending.changedNanos() < PREVIEW_BUILD_DEBOUNCE_NANOS) {
            return bound;
        }

        ChaoGpuRenderCache.Entry resolved = previewGpuCache.get(
                entity,
                cacheState,
                model,
                previewLight,
                worldTick,
                () -> buildTransientGpuBatches(entity, cacheState, model, previewLight, true)
        );

        // If the latest state was successfully installed, release at most the
        // previous debug VBO. This happens once per settled slider state, not
        // once per mouse-motion event.
        if (previewGpuCache.isBoundTo(previewId, cacheState, model, previewLight)) {
            previewGpuCache.evictIdleEntries(2, false);
        }
        return resolved;
    }

    private ChaoGpuRenderCache.Entry prepareGpuEntry(ChaoEntity entity, ChaoAppearanceState state, int light) {
        return prepareGpuEntry(entity, state, light, false);
    }

    private ChaoGpuRenderCache.Entry prepareGpuEntry(ChaoEntity entity, ChaoAppearanceState state,
            int light, boolean riggedWorldQa) {
        ChaoAppearanceState cacheState = canonicalizeBodyCacheState(state);
        ChaoMeshModel model = getModel(cacheState);
        if (model == null) {
            return null;
        }

        long worldTick = entity.getWorld().getTime();

        // Geometry/appearance is cacheable; Minecraft lighting, exact HeadDeco
        // mesh identity and animation pose are draw-time/shared attachment state.
        int cacheLight = ChaoShaderPackCompat.isShaderPackInUse() ? light : 0;
        int layoutVariant = riggedWorldQa ? 1 : 0;
        return gpuCache.get(
                entity,
                cacheState,
                model,
                cacheLight,
                worldTick,
                layoutVariant,
                () -> buildTransientGpuBatches(entity, cacheState, model, light, riggedWorldQa)
        );
    }

    /**
     * Performs expensive morph/palette preparation only on a real GPU cache miss.
     *
     * <p>Prepared CPU position/normal arrays are upload scratch data, not persistent
     * entity state. Dropping their cache entry after upload avoids keeping a second
     * morphed copy of every visible Chao in Java heap.</p>
     */
    private List<ChaoGpuRenderCache.DrawBatch> buildTransientGpuBatches(
            ChaoEntity entity, ChaoAppearanceState state, ChaoMeshModel model, int light, boolean riggedPreview) {
        ChaoMorphWeights weights = ChaoMorphResolver.resolve(state);
        float[] morphWeights = buildMorphWeights(model, state.type(), weights);
        ChaoPaletteState palette = resolvePaletteRuntime(state, weights);
        ChaoRenderCache.Entry prepared = morphCache.get(entity, state, model, morphWeights, palette);

        try {
            return buildGpuBatches(model, prepared, state, prepared.palette(), light, riggedPreview, null);
        } finally {
            // The uploaded immutable VBO is now the persistent representation.
            morphCache.remove(entity.getUuid());
        }
    }

    /** Startup-only body build that avoids creating/binding a synthetic entity. */
    private List<ChaoGpuRenderCache.DrawBatch> buildWarmGpuBatches(
            ChaoAppearanceState state, ChaoMeshModel model, int light) {
        ChaoMorphWeights weights = ChaoMorphResolver.resolve(state);
        float[] morphWeights = buildMorphWeights(model, state.type(), weights);
        ChaoPaletteState palette = resolvePaletteRuntime(state, weights);
        ChaoRenderCache.Entry prepared = ChaoRenderCache.prepare(
                state, model, morphWeights, palette);
        return buildGpuBatches(model, prepared, state, palette, light, false, null);
    }

    /**
     * Uses a prewarmed immutable palette for exact base profiles and falls back
     * to the authoritative resolver for continuously-morphed/growing Chao.
     */
    private ChaoPaletteState resolvePaletteRuntime(
            ChaoAppearanceState state, ChaoMorphWeights weights) {
        ChaoPaletteState cached = basePaletteRuntimeCache.get(BasePaletteKey.from(state));
        if (cached != null && BasePaletteKey.isBaseProfile(state)) {
            return cached;
        }
        return ChaoPaletteResolver.resolve(state, weights);
    }

    private record BasePaletteKey(
            ChaoVisualType type,
            int ageBits,
            int alignmentBits,
            ChaoColorType color,
            boolean monotone) {

        static BasePaletteKey from(ChaoAppearanceState state) {
            return new BasePaletteKey(
                    state.type(),
                    Float.floatToIntBits(state.age()),
                    Float.floatToIntBits(state.alignment()),
                    state.colorType(),
                    state.monotone()
            );
        }

        static boolean isBaseProfile(ChaoAppearanceState state) {
            boolean baseAge = state.type() == ChaoVisualType.CHILD
                    ? state.age() == 0.0F
                    : state.age() == 1.0F;
            boolean baseAlignment = state.alignment() == -100.0F
                    || state.alignment() == 0.0F
                    || state.alignment() == 100.0F;

            // Base adult/Chaos model identity is carried by ChaoVisualType.
            // Child has no evolution progress at age 0.
            boolean baseEvolution = state.type() == ChaoVisualType.CHILD
                    || state.normal() == 100.0F
                    || state.swim() == 100.0F
                    || state.fly() == 100.0F
                    || state.run() == 100.0F
                    || state.power() == 100.0F;

            return baseAge && baseAlignment && baseEvolution;
        }
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
            int light, boolean riggedPreview, ChaoAnimationPose cpuPose) {
        ChaoVisualType type = state.type();
        ChaoAdultFamily adultFamily = type == ChaoVisualType.CHILD || type == ChaoVisualType.CHAOS
                ? null : ChaoAdultFamily.resolve(state);
        ChaoChaosFamily chaosFamily = type == ChaoVisualType.CHAOS ? ChaoChaosFamily.resolve(state) : null;
        RenderSystem.assertOnRenderThread();

        Map<BatchKey, List<DrawSource>> grouped = new LinkedHashMap<>();
        Map<BatchKey, List<DrawSource>> reflectionGrouped = new LinkedHashMap<>();
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
                    // Viewer reflection is a property of the finished material.
                    // Keep environment passes separate so no later body/belly mask
                    // can be drawn over Gold/Silver/Jewel reflection.
                    addReflectionPass(reflectionGrouped, state.reflectionType(), segment, preparedSegment, submesh);
                }
            }
        }

        // Alpha composition now matches the Viewer material contract:
        //   Ref=1.0 -> cubemap replaces the completed base material.
        //   Ref=0.4 -> Shiny/TTMetal retain 60% of the completed base material.
        grouped.putAll(reflectionGrouped);

        // Production keeps CP11R.18's highly merged batches. Only the isolated
        // Animation Lab preview expands those groups into rigid SA2 nodes for
        // CP12B validation; this avoids increasing world draw calls before the
        // final GPU node-palette vertex format lands.
        if (riggedPreview
                && type != ChaoVisualType.CHILD
                && isNeutralNormalAdultSkinningTarget(state)) {
            /*
             * CP12I.2C: Neutral Normal currently has no retained smooth-skin
             * metadata (the CP12I.2B probe measured 0/643 weighted vertices).
             * Preserve the already-cached geometry and recover SA2 node ownership
             * at batch granularity instead: each rigid batch carries its node id and
             * receives al_nnn's bind-relative delta at draw time. No frame data enters
             * VisualKey/VBO identity and no geometry is rebuilt while the clip plays.
             */
            grouped = splitNeutralNormalRigGroups(grouped);
        } else if (riggedPreview && cpuPose == null
                && type != ChaoVisualType.CHILD) {
            // Keep the pre-CP12I diagnostic/static preview behavior for every other
            // Adult/Chaos until its own checkpoint.
            grouped = splitRigGroups(grouped);
        }

        Matrix4f localPositionMatrix = createLocalPositionMatrix(type);
        Matrix3f localNormalMatrix = createLocalNormalMatrix(type);
        Matrix4f[] cpuSkinPalette = cpuPose == null
                ? null
                : createRenderSpaceSkinPalette(cpuPose, type);
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
                //
                // CP12I.2 Neutral Adult must also keep skinned and static sources
                // in separate upload groups. Adult material batching can legitimately
                // merge both kinds under one BatchKey; using allMatch(hasSkin) on that
                // mixed chunk silently downgraded the whole batch to static vertices.
                List<List<DrawSource>> skinCompatibleGroups = List.of(group.getValue());
                for (List<DrawSource> compatibleSources : skinCompatibleGroups) {
                    for (List<DrawSource> chunk : partitionSources(compatibleSources, layer)) {
                    int expectedVertices = estimatedVertexCount(chunk);
                    int bufferBytes;
                    boolean gpuSkinned = riggedPreview
                            && type == ChaoVisualType.CHILD
                            && chunk.stream().allMatch(source -> source.segment().hasSkin());
                    VertexFormat vertexFormat = gpuSkinned
                            ? ChaoSkinnedVertexFormat.FORMAT
                            : layer.getVertexFormat();
                    bufferBytes = estimatedBufferBytes(vertexFormat, expectedVertices);
                    if (bufferBytes < 0) {
                        ChaoCraft.LOGGER.error("Skipping oversized Chao render batch ({} vertices, texture {})",
                                expectedVertices, key.texture());
                        continue;
                    }

                    BufferBuilder builder = new BufferBuilder(Math.max(256, Math.min(expectedVertices, 32 * 1024)));
                    builder.begin(layer.getDrawMode(), vertexFormat);
                    int batchLight = key.fullbright() ? 0x00F000F0 : light;
                    for (DrawSource source : chunk) {
                        appendSource(builder, source, key.color(), batchLight,
                                localPositionMatrix, localNormalMatrix, key.uvMode(),
                                cpuSkinPalette, gpuSkinned);
                    }

                    BufferBuilder.BuiltBuffer built = builder.end();
                    if (built.isEmpty()) {
                        built.release();
                        continue;
                    }

                    VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
                    try {
                        vertexBuffer.bind();
                        uploadVertexBufferChecked(vertexBuffer, built);
                    } catch (RuntimeException exception) {
                        vertexBuffer.close();
                        throw exception;
                    } finally {
                        VertexBuffer.unbind();
                    }
                    batches.add(new ChaoGpuRenderCache.DrawBatch(
                            layer,
                            vertexBuffer,
                            bufferBytes,
                            key.uvMode() == UvMode.CUBEMAP_STRIP,
                            key.reflectionEmission(),
                            key.rigNode(),
                            gpuSkinned
                    ));
                    }
                }
            }

            if (!state.headDeco().hidesEmotionBall()) {
                batches.addAll(buildEmotionBatches(state, palette, light));
            }
            return List.copyOf(batches);
        } catch (RuntimeException | OutOfMemoryError exception) {
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
            MaterialRole role = resolveMaterialRole(
                    ChaoVisualType.CHILD, AdultNormalVariant.NEUTRAL, segmentName, submeshIndex);
            if (role == MaterialRole.EYELID) {
                // Viewer reflects the Eyelid material, but its transparent texture
                // must still mask the cubemap. Our standard layered renderer cannot
                // multiply that mask into the environment pass, so only reflect the
                // fully opaque eyelid mode. Hidden/masked eyelids must never cover
                // the actual eye texture.
                return state.resolvedEyelid() == 2;
            }
            return role == MaterialRole.BODY || role == MaterialRole.BELLY || role == MaterialRole.HORNS;
        }

        if (state.type() == ChaoVisualType.CHAOS) {
            ChaoChaosMaterialProfiles.Spec spec =
                    ChaoChaosMaterialProfiles.resolve(chaosFamily, segmentName, submeshIndex);
            if (spec.kind() == ChaoChaosMaterialProfiles.Kind.EYELID) {
                return state.resolvedEyelid() == 2;
            }
            // Viewer ReflMaterials includes Chaos body materials, never eyes/mouth.
            return spec.kind() == ChaoChaosMaterialProfiles.Kind.BODY;
        }

        ChaoAdultMaterialProfiles.MaterialSpec spec =
                ChaoAdultMaterialProfiles.resolve(adultFamily, segmentName, submeshIndex);
        if (spec.kind() == ChaoAdultMaterialProfiles.Kind.EYELID && state.resolvedEyelid() != 2) {
            return false;
        }
        return ChaoReflectionMaterialRules.isReflectiveAdult(
                adultFamily.name(), spec.debugName(),
                spec.kind() == ChaoAdultMaterialProfiles.Kind.EYELID);
    }

    /** Viewer ReflectionT + ReflectiveTextures mapping. ReflMaterials membership is handled separately. */
    private static void addReflectionPass(Map<BatchKey, List<DrawSource>> grouped,
            ChaoReflectionType reflection, ChaoMeshModel.Segment segment,
            ChaoRenderCache.PreparedSegment prepared, ChaoMeshModel.Submesh submesh) {
        if (reflection == ChaoReflectionType.NONE) return;

        if (reflection == ChaoReflectionType.BRIGHT) {
            // Viewer Bright: _Cube=null, _Ref=0, _Emission=.5.
            BatchKey key = new BatchKey(
                    WHITE_TEXTURE, new ChaoColor(1F, 1F, 1F, 0.18F),
                    true, UvMode.MESH, true, 0.5F, -1);
            grouped.computeIfAbsent(key, ignored -> new ArrayList<>())
                    .add(new DrawSource(segment, prepared, submesh, RigSide.ALL));
            return;
        }

        Identifier texture = reflectionTexture(reflection);
        if (texture == null) return;

        // Exact values assigned by ChaoMorphController.SetReflection().
        float ref = (reflection == ChaoReflectionType.SHINY
                || reflection == ChaoReflectionType.TT_METAL) ? 0.4F : 1.0F;
        float emission = reflection == ChaoReflectionType.SHINY ? 0.5F : 0.1F;

        // Keep the exact Viewer cubemap asset, but sample it dynamically at DRAW
        // time. The vertex alpha carries Viewer's _Ref over the already-rendered
        // base Chao material. No Minecraft-world reflection probe is involved.
        BatchKey reflectionKey = new BatchKey(
                texture, new ChaoColor(1F, 1F, 1F, ref),
                true, UvMode.CUBEMAP_STRIP, false, emission, -1);
        grouped.computeIfAbsent(reflectionKey, ignored -> new ArrayList<>())
                .add(new DrawSource(segment, prepared, submesh, RigSide.ALL));
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

    /**
     * Maps a simulated reflection vector onto AssetRipper's vertical six-face
     * cubemap strip. The source Viewer imports these assets as real Cube
     * textures with bilinear filtering and Clamp wrapping; ChaoCraft stores the
     * exact six faces as one vertical PNG and emulates that lookup here.
     */
    private static float cubeUvU(float x, float y, float z) {
        float ax = Math.abs(x), ay = Math.abs(y), az = Math.abs(z);
        float u;
        if (ax >= ay && ax >= az) u = x >= 0 ? -z / Math.max(ax, 1e-6F) : z / Math.max(ax, 1e-6F);
        else if (ay >= ax && ay >= az) u = x / Math.max(ay, 1e-6F);
        else u = z >= 0 ? x / Math.max(az, 1e-6F) : -x / Math.max(az, 1e-6F);
        return clampCubemapFaceCoordinate(u * 0.5F + 0.5F);
    }

    private static float cubeUvV(float x, float y, float z) {
        float ax = Math.abs(x), ay = Math.abs(y), az = Math.abs(z);
        int face;
        float v;
        if (ax >= ay && ax >= az) { face = x >= 0 ? 0 : 1; v = -y / Math.max(ax, 1e-6F); }
        else if (ay >= ax && ay >= az) { face = y >= 0 ? 2 : 3; v = (y >= 0 ? z : -z) / Math.max(ay, 1e-6F); }
        else { face = z >= 0 ? 4 : 5; v = -y / Math.max(az, 1e-6F); }

        float local = clampCubemapFaceCoordinate(1.0F - (v * 0.5F + 0.5F));
        return (face + local) / 6.0F;
    }

    private static float clampCubemapFaceCoordinate(float value) {
        return Math.max(CUBEMAP_FACE_EDGE_INSET,
                Math.min(1.0F - CUBEMAP_FACE_EDGE_INSET, value));
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
        BatchKey key = new BatchKey(texture, color, translucent, UvMode.MESH, false, 0.0F, -1);
        grouped.computeIfAbsent(key, ignored -> new ArrayList<>())
                .add(new DrawSource(segment, prepared, submesh, RigSide.ALL));
    }

    /**
     * Expands already-composed material groups into original SA2 rigid nodes for
     * the F8 Animation Lab. Production remains merged and unaffected.
     */
    private static Map<BatchKey, List<DrawSource>> splitRigGroups(
            Map<BatchKey, List<DrawSource>> grouped) {
        Map<BatchKey, List<DrawSource>> result = new LinkedHashMap<>();
        for (Map.Entry<BatchKey, List<DrawSource>> entry : grouped.entrySet()) {
            BatchKey base = entry.getKey();
            for (DrawSource source : entry.getValue()) {
                String name = source.segment().name().toLowerCase(java.util.Locale.ROOT);
                if (name.contains("arms")) {
                    addRigSplit(result, base, source, 3, RigSide.POSITIVE_X);
                    addRigSplit(result, base, source, 10, RigSide.NEGATIVE_X);
                } else if (name.contains("legs")) {
                    addRigSplit(result, base, source, 6, RigSide.POSITIVE_X);
                    addRigSplit(result, base, source, 13, RigSide.NEGATIVE_X);
                } else if (name.contains("wings")) {
                    addRigSplit(result, base, source, 37, RigSide.POSITIVE_X);
                    addRigSplit(result, base, source, 39, RigSide.NEGATIVE_X);
                } else {
                    int node = name.contains("tail") ? 8
                            : name.contains("head") ? 16 : 1;
                    addRigSplit(result, base, source, node, RigSide.ALL);
                }
            }
        }
        return result;
    }

    /**
     * Exact visible-node ownership for the isolated Neutral Normal Adult control.
     *
     * <p>The Adult .cmesh keeps SA2's semantic part/material separation but no
     * smooth skin weights. Neutral Normal can therefore use the same universal
     * 0..39 rig contract as Child: torso/tail and paired limbs are rigid node
     * owners, while the Head material slots preserve eye/eyelid/mouth ownership.
     * Geometry stays in immutable shared VBOs; only the node delta changes at
     * draw time.</p>
     */
    private static Map<BatchKey, List<DrawSource>> splitNeutralNormalRigGroups(
            Map<BatchKey, List<DrawSource>> grouped) {
        Map<BatchKey, List<DrawSource>> result = new LinkedHashMap<>();
        for (Map.Entry<BatchKey, List<DrawSource>> entry : grouped.entrySet()) {
            BatchKey base = entry.getKey();
            for (DrawSource source : entry.getValue()) {
                String name = source.segment().name().toLowerCase(java.util.Locale.ROOT);
                if (name.contains("arms")) {
                    addRigSplit(result, base, source, 3, RigSide.POSITIVE_X);
                    addRigSplit(result, base, source, 10, RigSide.NEGATIVE_X);
                } else if (name.contains("legs")) {
                    addRigSplit(result, base, source, 6, RigSide.POSITIVE_X);
                    addRigSplit(result, base, source, 13, RigSide.NEGATIVE_X);
                } else if (name.contains("wings")) {
                    addRigSplit(result, base, source, 37, RigSide.POSITIVE_X);
                    addRigSplit(result, base, source, 39, RigSide.NEGATIVE_X);
                } else if (name.contains("tail")) {
                    addRigSplit(result, base, source, 8, RigSide.ALL);
                } else if (name.contains("head")) {
                    int submeshIndex = source.segment().submeshes().indexOf(source.submesh());
                    switch (submeshIndex) {
                        case 1 -> {
                            addRigSplit(result, base, source, 18, RigSide.POSITIVE_X);
                            addRigSplit(result, base, source, 21, RigSide.NEGATIVE_X);
                        }
                        case 2 -> {
                            addRigSplit(result, base, source, 19, RigSide.POSITIVE_X);
                            addRigSplit(result, base, source, 22, RigSide.NEGATIVE_X);
                        }
                        case 3, 4 -> addRigSplit(result, base, source, 27, RigSide.ALL);
                        default -> addRigSplit(result, base, source, 16, RigSide.ALL);
                    }
                } else {
                    addRigSplit(result, base, source, 1, RigSide.ALL);
                }
            }
        }
        return result;
    }

    private static void addRigSplit(Map<BatchKey, List<DrawSource>> grouped,
            BatchKey base, DrawSource source, int rigNode, RigSide side) {
        BatchKey key = new BatchKey(
                base.texture(), base.color(), base.translucent(), base.uvMode(),
                base.fullbright(), base.reflectionEmission(), rigNode);
        grouped.computeIfAbsent(key, ignored -> new ArrayList<>())
                .add(new DrawSource(source.segment(), source.prepared(), source.submesh(), side));
    }

    private static List<List<DrawSource>> splitBySkinCapability(List<DrawSource> sources) {
        List<DrawSource> skinned = new ArrayList<>();
        List<DrawSource> staticSources = new ArrayList<>();
        for (DrawSource source : sources) {
            (source.segment().hasSkin() ? skinned : staticSources).add(source);
        }
        if (skinned.isEmpty()) return List.of(List.copyOf(staticSources));
        if (staticSources.isEmpty()) return List.of(List.copyOf(skinned));
        return List.of(List.copyOf(skinned), List.copyOf(staticSources));
    }

    private static List<List<DrawSource>> partitionSources(List<DrawSource> sources, RenderLayer layer) {
        if (sources.isEmpty()) return List.of();
        int vertexSize = Math.max(1, layer.getVertexFormat().getVertexSizeByte());
        int preferredVertices = Math.max(1, (PREFERRED_BATCH_BUFFER_BYTES - 256) / vertexSize);
        int preferredTriangles = Math.max(1, preferredVertices / 4);
        int preferredIndices = preferredTriangles * 3;
        List<List<DrawSource>> chunks = new ArrayList<>();
        List<DrawSource> current = new ArrayList<>();
        long currentVertices = 0L;

        for (DrawSource source : sources) {
            int validIndices = Math.max(0, source.submesh().indexCount());
            validIndices -= validIndices % 3;
            int firstIndex = source.submesh().firstIndex();

            // CP11 originally split only between complete DrawSources. A single
            // large submesh could therefore still allocate one 6+ MiB native
            // BufferBuilder. Slice large sources on triangle boundaries so normal
            // uploads remain near the preferred ~2 MiB allocation.
            for (int consumed = 0; consumed < validIndices; ) {
                int sliceIndices = Math.min(preferredIndices, validIndices - consumed);
                ChaoMeshModel.Submesh slice = consumed == 0 && sliceIndices == validIndices
                        ? source.submesh()
                        : new ChaoMeshModel.Submesh(firstIndex + consumed, sliceIndices);
                DrawSource slicedSource = slice == source.submesh()
                        ? source
                        : new DrawSource(source.segment(), source.prepared(), slice, source.side());
                long sliceVertices = (sliceIndices / 3L) * 4L;

                if (!current.isEmpty() && currentVertices + sliceVertices > preferredVertices) {
                    chunks.add(List.copyOf(current));
                    current = new ArrayList<>();
                    currentVertices = 0L;
                }

                current.add(slicedSource);
                currentVertices += sliceVertices;
                consumed += sliceIndices;

                if (currentVertices >= preferredVertices) {
                    chunks.add(List.copyOf(current));
                    current = new ArrayList<>();
                    currentVertices = 0L;
                }
            }
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
    /**
     * Uploads a newly built VBO and converts driver-side GL_OUT_OF_MEMORY into
     * the cache's recoverable allocation-pressure path. This runs only on cache
     * misses/uploads, never in the steady-state render loop.
     */
    private static void uploadVertexBufferChecked(
            VertexBuffer vertexBuffer, BufferBuilder.BuiltBuffer built) {
        /*
         * Minecraft 1.20.1 VertexBuffer.upload(BuiltBuffer) takes ownership of the
         * BuiltBuffer upload payload and releases it as part of the upload path.
         * Do not release it again here: doing so double-releases the native buffer
         * and crashes the renderer with "Buffer has already been released!".
         */
        vertexBuffer.upload(built);
        int error = GL11.glGetError();
        if (error == GL11.GL_OUT_OF_MEMORY) {
            throw new ChaoGpuMemoryException("OpenGL failed to allocate Chao VBO data");
        }
    }

    private static int estimatedBufferBytes(RenderLayer layer, int expectedVertices) {
        return estimatedBufferBytes(layer.getVertexFormat(), expectedVertices);
    }

    private static int estimatedBufferBytes(VertexFormat format, int expectedVertices) {
        long bytes = (long) Math.max(1, expectedVertices) * format.getVertexSizeByte() + 256L;
        if (bytes > MAX_BATCH_BUFFER_BYTES) {
            return -1;
        }
        return (int) Math.max(256L, bytes);
    }

    private static void appendSource(BufferBuilder builder, DrawSource source, ChaoColor color, int light,
            Matrix4f positionMatrix, Matrix3f normalMatrix, UvMode uvMode,
            Matrix4f[] cpuSkinPalette, boolean writeSkinAttributes) {
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
            if (!source.side().accepts(segment, source.prepared(), triangle)) {
                continue;
            }
            appendVertex(builder, source, segment.indices()[triangle], red, green, blue, alpha,
                    light, positionMatrix, normalMatrix, uvMode, cpuSkinPalette, writeSkinAttributes);
            appendVertex(builder, source, segment.indices()[triangle + 2], red, green, blue, alpha,
                    light, positionMatrix, normalMatrix, uvMode, cpuSkinPalette, writeSkinAttributes);
            appendVertex(builder, source, segment.indices()[triangle + 1], red, green, blue, alpha,
                    light, positionMatrix, normalMatrix, uvMode, cpuSkinPalette, writeSkinAttributes);
            // Entity RenderLayers use QUADS; duplicate the last vertex to preserve
            // the source triangle as a degenerate quad, matching CP02 topology.
            appendVertex(builder, source, segment.indices()[triangle + 1], red, green, blue, alpha,
                    light, positionMatrix, normalMatrix, uvMode, cpuSkinPalette, writeSkinAttributes);
        }
    }

    private static void appendVertex(BufferBuilder builder, DrawSource source, int vertexIndex,
            float red, float green, float blue, float alpha, int light,
            Matrix4f positionMatrix, Matrix3f normalMatrix, UvMode uvMode,
            Matrix4f[] cpuSkinPalette, boolean writeSkinAttributes) {
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

        if (cpuSkinPalette != null && segment.hasSkin()) {
            int influence0 = segment.skinInfluence0(vertexIndex);
            int influence1 = segment.skinInfluence1(vertexIndex);

            int bone0 = influence0 & 0x3F;
            int bone1 = influence1 & 0x3F;
            float weight0 = ((influence0 >>> 6) & 0x3FF) / 1023.0F;
            float weight1 = ((influence1 >>> 6) & 0x3FF) / 1023.0F;
            float weightTotal = weight0 + weight1;
            if (weightTotal > 0.000001F) {
                weight0 /= weightTotal;
                weight1 /= weightTotal;
            } else {
                weight0 = 1.0F;
                weight1 = 0.0F;
            }

            Matrix4f boneMatrix0 = cpuSkinPalette[bone0];
            Matrix4f boneMatrix1 = cpuSkinPalette[bone1];

            float skinnedX = weight0 * transformPositionX(boneMatrix0, tx, ty, tz)
                    + weight1 * transformPositionX(boneMatrix1, tx, ty, tz);
            float skinnedY = weight0 * transformPositionY(boneMatrix0, tx, ty, tz)
                    + weight1 * transformPositionY(boneMatrix1, tx, ty, tz);
            float skinnedZ = weight0 * transformPositionZ(boneMatrix0, tx, ty, tz)
                    + weight1 * transformPositionZ(boneMatrix1, tx, ty, tz);

            float skinnedNx = weight0 * transformDirectionX(boneMatrix0, tnx, tny, tnz)
                    + weight1 * transformDirectionX(boneMatrix1, tnx, tny, tnz);
            float skinnedNy = weight0 * transformDirectionY(boneMatrix0, tnx, tny, tnz)
                    + weight1 * transformDirectionY(boneMatrix1, tnx, tny, tnz);
            float skinnedNz = weight0 * transformDirectionZ(boneMatrix0, tnx, tny, tnz)
                    + weight1 * transformDirectionZ(boneMatrix1, tnx, tny, tnz);

            tx = skinnedX;
            ty = skinnedY;
            tz = skinnedZ;
            tnx = skinnedNx;
            tny = skinnedNy;
            tnz = skinnedNz;
        }

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

        float outU = uvMode == UvMode.CUBEMAP_STRIP
                ? cubeUvU(envX, envY, envZ)
                : segment.uvs()[uv];
        float outV = uvMode == UvMode.CUBEMAP_STRIP
                ? cubeUvV(envX, envY, envZ)
                : 1.0F - segment.uvs()[uv + 1];

        if (writeSkinAttributes && segment.hasSkin()) {
            int influence0 = segment.skinInfluence0(vertexIndex);
            int influence1 = segment.skinInfluence1(vertexIndex);

            // Preserve the full vanilla entity layout, then append the two
            // dedicated skin channels. BufferVertexConsumer.uv writes SHORT2.
            builder.vertex(tx, ty, tz);
            builder.color(
                    Math.round(red * 255.0F),
                    Math.round(green * 255.0F),
                    Math.round(blue * 255.0F),
                    Math.round(alpha * 255.0F));
            builder.texture(outU, outV);
            builder.overlay(OverlayTexture.DEFAULT_UV);
            builder.light(light);
            builder.uv((short) influence0, (short) 0, 3);
            builder.uv((short) influence1, (short) 0, 4);
            builder.normal(tnx, tny, tnz);
            builder.next();
        } else {
            builder.vertex(
                    tx, ty, tz,
                    red, green, blue, alpha,
                    outU, outV,
                    OverlayTexture.DEFAULT_UV, light,
                    tnx, tny, tnz
            );
        }
    }

    /** Resolves only the explicit Visual Lab stress animation. Normal gameplay Chao return null. */
    private static ChaoAnimationClip resolveVisualLabStressAnimation(ChaoEntity entity) {
        int requested = entity.getVisualLabAnimation();
        List<ChaoAnimationClip> clips = ChaoAnimationRepository.clips();
        if (requested < 0 || clips.isEmpty()) return null;
        return clips.get(Math.floorMod(requested, clips.size()));
    }

    private static double visualLabStressFrame(ChaoEntity entity, ChaoAnimationClip clip, float tickDelta) {
        if (clip.frames() <= 0) return 0.0D;
        double timeline = (entity.age + tickDelta) * 1.5D + entity.getVisualLabAnimationPhase();
        return timeline % clip.frames();
    }

    private static ChaoAnimationPose resolvePreviewAnimationPose(
            ChaoAppearanceState state, ChaoAnimationClip animation, double animationFrame) {
        if (animation == null) {
            return null;
        }
        if (state.type().isChild()) {
            // Safety control: CP12I.2 must not alter the validated Child path.
            return ChaoAnimationPose.sample(animation, animationFrame, true);
        }
        if (isNeutralNormalAdultSkinningTarget(state)) {
            Optional<ChaoSa2BindProfile> profile =
                    ChaoSa2BindProfileRegistry.find(NEUTRAL_NORMAL_BIND_PROFILE);
            if (profile.isPresent()) {
                return ChaoAnimationPose.sample(animation, animationFrame, profile.get(), false);
            }
        }
        return ChaoAnimationPose.sample(animation, animationFrame, false);
    }

    /**
     * Neutral Normal is the isolated Adult control case for proving that the
     * universal 40-node animation can be evaluated against a non-Child bind
     * profile without changing production cache identity or Child skinning.
     */
    private static boolean isNeutralNormalAdultSkinningTarget(ChaoAppearanceState state) {
        return state.type() == ChaoVisualType.NORMAL
                && resolveAdultNormalVariant(state) == AdultNormalVariant.NEUTRAL;
    }

    /**
     * Convert Viewer-space SA2 deltas into the ACTUAL coordinate space occupied
     * by prepared/cached Child vertices.
     *
     * <p>The approved static renderer performs two basis changes before a Child
     * vertex reaches its VBO:</p>
     *
     * <pre>
     * ChaoRenderCache.prepareSegment():      F = FlipZ
     * createLocalPositionMatrix(CHILD):      R = RotateX(+90)
     *
     * M_total = R * F
     * D_render = M_total * D_viewer * inverse(M_total)
     * </pre>
     *
     * <p>Offline Mirror v4 compared this exact formula against Blender's direct
     * al_ncn golden reference and reduced total Sprint error from ~1.10-1.53 RMS
     * to ~7e-6-1.1e-5 RMS across all six Child segments.</p>
     */
    private static final Matrix4f VIEWER_TO_RENDER_CHILD = new Matrix4f()
            .identity().rotateX((float) Math.toRadians(90.0F)).scale(1.0F, 1.0F, -1.0F);
    private static final Matrix4f RENDER_TO_VIEWER_CHILD = new Matrix4f(VIEWER_TO_RENDER_CHILD).invert();
    private static final Matrix4f VIEWER_TO_RENDER_ADULT = new Matrix4f()
            .identity().scale(1.0F, 1.0F, -1.0F);
    private static final Matrix4f RENDER_TO_VIEWER_ADULT = new Matrix4f(VIEWER_TO_RENDER_ADULT).invert();

    private static Matrix4f[] createRenderSpaceSkinPalette(
            ChaoAnimationPose pose, ChaoVisualType type) {
        Matrix4f viewerToRender = type == ChaoVisualType.CHILD
                ? VIEWER_TO_RENDER_CHILD : VIEWER_TO_RENDER_ADULT;
        Matrix4f renderToViewer = type == ChaoVisualType.CHILD
                ? RENDER_TO_VIEWER_CHILD : RENDER_TO_VIEWER_ADULT;
        Matrix4f[] palette = new Matrix4f[ChaoAnimationPose.NODE_COUNT];

        for (int node = 0; node < palette.length; node++) {
            palette[node] = new Matrix4f(viewerToRender)
                    .mul(pose.delta(node))
                    .mul(renderToViewer);
        }
        return palette;
    }

    /**
     * Convert one rigid SA2 node delta from Viewer coordinates into the prepared
     * VBO coordinate space used by the selected Chao family.
     */
    private static Matrix4f createRigidRenderDelta(
            ChaoAnimationPose pose, int nodeIndex, ChaoVisualType visualType) {
        Matrix4f viewerToRender = visualType == ChaoVisualType.CHILD
                ? VIEWER_TO_RENDER_CHILD : VIEWER_TO_RENDER_ADULT;
        Matrix4f renderToViewer = visualType == ChaoVisualType.CHILD
                ? RENDER_TO_VIEWER_CHILD : RENDER_TO_VIEWER_ADULT;
        return new Matrix4f(viewerToRender)
                .mul(pose.delta(nodeIndex))
                .mul(renderToViewer);
    }

    private static float transformPositionX(Matrix4f m, float x, float y, float z) {
        return m.m00() * x + m.m10() * y + m.m20() * z + m.m30();
    }

    private static float transformPositionY(Matrix4f m, float x, float y, float z) {
        return m.m01() * x + m.m11() * y + m.m21() * z + m.m31();
    }

    private static float transformPositionZ(Matrix4f m, float x, float y, float z) {
        return m.m02() * x + m.m12() * y + m.m22() * z + m.m32();
    }

    private static float transformDirectionX(Matrix4f m, float x, float y, float z) {
        return m.m00() * x + m.m10() * y + m.m20() * z;
    }

    private static float transformDirectionY(Matrix4f m, float x, float y, float z) {
        return m.m01() * x + m.m11() * y + m.m21() * z;
    }

    private static float transformDirectionZ(Matrix4f m, float x, float y, float z) {
        return m.m02() * x + m.m12() * y + m.m22() * z;
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

    /**
     * Uploads the verified full-space SA2 palette without changing VBO identity.
     *
     * <p>Prepared Child vertices already include FlipZ and the renderer's fixed
     * +90 X rotation, so the exact basis change is Mtotal = R * FlipZ.</p>
     */
    private static final Matrix4f IDENTITY_BONE_MATRIX = new Matrix4f().identity();

    private static void uploadSkinningUniforms(ShaderProgram shader, Matrix4f[] palette) {
        ChaoRenderMetrics.onSkinPaletteUpload();
        GlUniform enabled = shader.getUniform("SkinningEnabled");
        if (enabled != null) enabled.set(palette == null ? 0.0F : 1.0F);

        for (int node = 0; node < ChaoAnimationPose.NODE_COUNT; node++) {
            GlUniform uniform = shader.getUniform("Bone" + node);
            if (uniform != null) uniform.set(palette == null ? IDENTITY_BONE_MATRIX : palette[node]);
        }
    }

    private static void drawGpuBatches(List<ChaoGpuRenderCache.DrawBatch> batches,
            MatrixStack matrices, Matrix3f restoreNormalMatrix, int packedLight,
            boolean legacyPreview, ChaoAnimationPose pose) {
        drawGpuBatchesInternal(
                batches, matrices, restoreNormalMatrix, packedLight,
                legacyPreview, pose, ChaoReflectionType.NONE, ChaoVisualType.CHILD, null);
    }

    /**
     * Draws an already-resolved shared batch list with the active Chao body basis.
     * Shared attachment caches are independent from the owning Chao, so callers
     * provide reflection and visual type only at draw time.
     */
    private static void drawGpuBatches(List<ChaoGpuRenderCache.DrawBatch> batches,
            MatrixStack matrices, Matrix3f restoreNormalMatrix, int packedLight,
            boolean legacyPreview, ChaoAnimationPose pose,
            ChaoReflectionType reflectionOverride, ChaoVisualType skinningVisualType) {
        drawGpuBatchesInternal(
                batches, matrices, restoreNormalMatrix, packedLight,
                legacyPreview, pose, reflectionOverride, skinningVisualType, null);
    }

    private static void drawGpuBatches(List<ChaoGpuRenderCache.DrawBatch> batches,
            MatrixStack matrices, Matrix3f restoreNormalMatrix, int packedLight,
            boolean legacyPreview, ChaoAnimationPose pose,
            ChaoReflectionType reflectionOverride, ChaoVisualType skinningVisualType,
            Matrix4f[] renderSpacePalette) {
        drawGpuBatchesInternal(
                batches, matrices, restoreNormalMatrix, packedLight,
                legacyPreview, pose, reflectionOverride, skinningVisualType, renderSpacePalette);
    }

    private static void drawGpuBatches(ChaoGpuRenderCache.Entry entry, MatrixStack matrices,
            Matrix3f restoreNormalMatrix, int packedLight, boolean legacyPreview,
            ChaoAnimationPose pose) {
        drawGpuBatches(
                entry, matrices, restoreNormalMatrix, packedLight,
                legacyPreview, pose, ChaoReflectionType.NONE);
    }

    private static void drawGpuBatches(ChaoGpuRenderCache.Entry entry, MatrixStack matrices,
            Matrix3f restoreNormalMatrix, int packedLight, boolean legacyPreview,
            ChaoAnimationPose pose, ChaoReflectionType reflectionOverride) {
        drawGpuBatches(entry, matrices, restoreNormalMatrix, packedLight, legacyPreview,
                pose, reflectionOverride, ChaoVisualType.CHILD);
    }

    private static void drawGpuBatches(ChaoGpuRenderCache.Entry entry, MatrixStack matrices,
            Matrix3f restoreNormalMatrix, int packedLight, boolean legacyPreview,
            ChaoAnimationPose pose, ChaoReflectionType reflectionOverride,
            ChaoVisualType skinningVisualType) {
        if (entry == null || entry.isClosed()) {
            return;
        }
        drawGpuBatchesInternal(
                entry.batches(), matrices, restoreNormalMatrix, packedLight,
                legacyPreview, pose, reflectionOverride, skinningVisualType, null);
    }

    private static void drawGpuBatches(ChaoGpuRenderCache.Entry entry, MatrixStack matrices,
            Matrix3f restoreNormalMatrix, int packedLight, boolean legacyPreview,
            ChaoAnimationPose pose, ChaoReflectionType reflectionOverride,
            ChaoVisualType skinningVisualType, Matrix4f[] renderSpacePalette) {
        if (entry == null || entry.isClosed()) {
            return;
        }
        drawGpuBatchesInternal(
                entry.batches(), matrices, restoreNormalMatrix, packedLight,
                legacyPreview, pose, reflectionOverride, skinningVisualType, renderSpacePalette);
    }

    private static void drawGpuBatchesInternal(List<ChaoGpuRenderCache.DrawBatch> batches,
            MatrixStack matrices, Matrix3f restoreNormalMatrix, int packedLight,
            boolean legacyPreview, ChaoAnimationPose pose,
            ChaoReflectionType reflectionOverride, ChaoVisualType skinningVisualType,
            Matrix4f[] sharedRenderSpacePalette) {
        RenderSystem.assertOnRenderThread();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        Matrix4f baseModelViewMatrix = matrices.peek().getPositionMatrix();
        Matrix3f baseNormalMatrix = matrices.peek().getNormalMatrix();
        Matrix4f projectionMatrix = RenderSystem.getProjectionMatrix();

        int blockLight = (packedLight >> 4) & 0xF;
        int skyLight = (packedLight >> 20) & 0xF;
        float lightU = (blockLight + 0.5F) / 16.0F;
        float lightV = (skyLight + 0.5F) / 16.0F;
        boolean shaderPackActive = ChaoShaderPackCompat.isShaderPackInUse();

        // One basis conversion per Chao draw, shared by body, rigid nodes and all
        // skinning shader batches. Previously this rebuilt/inverted 40 matrices
        // once per batch, which scaled badly after Adult attachments became animated.
        Matrix4f[] renderSpacePalette = sharedRenderSpacePalette != null
                ? sharedRenderSpacePalette
                : (pose == null ? null : createRenderSpaceSkinPalette(pose, skinningVisualType));

        // Reuse temporary matrices instead of allocating two JOML objects for every
        // rigid node batch. Shader uniforms copy the values before the next batch.
        Matrix4f rigidModelViewScratch = new Matrix4f();
        Matrix3f rigidNormalScratch = new Matrix3f();
        Matrix3f rigidDeltaNormalScratch = new Matrix3f();
        boolean regularSkinPaletteUploaded = false;
        boolean reflectionSkinPaletteUploaded = false;

        for (ChaoGpuRenderCache.DrawBatch batch : batches) {
            if (batch.vertexBuffer().isClosed()) {
                continue;
            }

            Matrix4f modelViewMatrix = baseModelViewMatrix;
            Matrix3f normalMatrix = baseNormalMatrix;
            if (pose != null && batch.rigNode() >= 0) {
                Matrix4f delta;
                if (isAttachmentRigNode(batch.rigNode())) {
                    int sa2Node = decodeAttachmentRigNode(batch.rigNode());
                    // Emotion, Animal Parts and HeadDeco all use the same universal
                    // SA2 attachment nodes. Convert the node delta through the
                    // active body's prepared-VBO basis instead of assuming Child.
                    delta = renderSpacePalette[sa2Node];
                } else {
                    // Adult/Chaos rigid nodes use the same SA2 animation channels,
                    // converted into the prepared VBO basis just like Child.
                    delta = renderSpacePalette[batch.rigNode()];
                }

                modelViewMatrix = rigidModelViewScratch.set(baseModelViewMatrix).mul(delta);
                delta.get3x3(rigidDeltaNormalScratch);
                normalMatrix = rigidNormalScratch.set(baseNormalMatrix).mul(rigidDeltaNormalScratch);
            }

            RenderLayer layer = batch.layer();
            layer.startDrawing();
            try {
                // Full-reflection variants share one cached GOLD geometry/VBO.
                // Swap only the cubemap-strip texture at draw time.
                if (batch.reflection()
                        && isFullReflectionTextureVariant(reflectionOverride)) {
                    Identifier actualReflectionTexture =
                            reflectionTexture(reflectionOverride);
                    if (actualReflectionTexture != null) {
                        RenderSystem.setShaderTexture(0, actualReflectionTexture);
                    }
                }

                ShaderProgram shader = RenderSystem.getShader();

                if (!shaderPackActive) {
                    if (batch.skinned() && batch.reflection()
                            && ChaoReflectionSkinningShader.isAvailable()) {
                        shader = ChaoReflectionSkinningShader.get();
                        shader.addSampler("Sampler0", RenderSystem.getShaderTexture(0));
                        shader.addSampler("Sampler2", RenderSystem.getShaderTexture(2));
                        if (!reflectionSkinPaletteUploaded) {
                            uploadSkinningUniforms(shader, renderSpacePalette);
                            reflectionSkinPaletteUploaded = true;
                        }

                        GlUniform emissionUniform = shader.getUniform("Emission");
                        if (emissionUniform != null) emissionUniform.set(batch.reflectionEmission());

                        GlUniform cameraRotationUniform = shader.getUniform("CameraRotation");
                        if (cameraRotationUniform != null) {
                            cameraRotationUniform.set(RenderSystem.getInverseViewRotationMatrix());
                        }

                        GlUniform previewUniform = shader.getUniform("PreviewFullBright");
                        if (previewUniform != null) {
                            previewUniform.set(legacyPreview ? 1.0F : 0.0F);
                        }
                    } else if (batch.skinned() && ChaoSkinningShader.isAvailable()) {
                        shader = ChaoSkinningShader.get();
                        shader.addSampler("Sampler0", RenderSystem.getShaderTexture(0));
                        shader.addSampler("Sampler1", RenderSystem.getShaderTexture(1));
                        shader.addSampler("Sampler2", RenderSystem.getShaderTexture(2));
                        RenderSystem.setupShaderLights(shader);
                        if (!regularSkinPaletteUploaded) {
                            uploadSkinningUniforms(shader, renderSpacePalette);
                            regularSkinPaletteUploaded = true;
                        }
                    } else if (batch.reflection() && ChaoReflectionShader.isAvailable()) {
                        // Reflection is intentionally shared: its camera-reactive
                        // cubemap behavior is already validated in world and F8.
                        shader = ChaoReflectionShader.get();
                        shader.addSampler("Sampler0", RenderSystem.getShaderTexture(0));
                        shader.addSampler("Sampler2", RenderSystem.getShaderTexture(2));

                        GlUniform emissionUniform = shader.getUniform("Emission");
                        if (emissionUniform != null) emissionUniform.set(batch.reflectionEmission());

                        GlUniform cameraRotationUniform = shader.getUniform("CameraRotation");
                        if (cameraRotationUniform != null) {
                            cameraRotationUniform.set(RenderSystem.getInverseViewRotationMatrix());
                        }

                        GlUniform previewUniform = shader.getUniform("PreviewFullBright");
                        if (previewUniform != null) {
                            previewUniform.set(legacyPreview ? 1.0F : 0.0F);
                        }
                    } else if (!legacyPreview && ChaoMaterialShader.isAvailable()) {
                        // Production-only custom material shader. F8 deliberately
                        // keeps the standard shader selected by layer.startDrawing().
                        shader = ChaoMaterialShader.get();
                        shader.addSampler("Sampler0", RenderSystem.getShaderTexture(0));
                        shader.addSampler("Sampler2", RenderSystem.getShaderTexture(2));

                        GlUniform emissionUniform = shader.getUniform("Emission");
                        if (emissionUniform != null) emissionUniform.set(batch.reflectionEmission());

                        GlUniform lightDir0 = shader.getUniform("LightDir0");
                        if (lightDir0 != null) lightDir0.set(0.20F, 1.00F, -0.70F);

                        GlUniform lightDir1 = shader.getUniform("LightDir1");
                        if (lightDir1 != null) lightDir1.set(-0.20F, 1.00F, 0.70F);
                    }

                    if (shader != null && !legacyPreview) {
                        GlUniform lightUvUniform = shader.getUniform("LightUv");
                        if (lightUvUniform != null) lightUvUniform.set(lightU, lightV);
                    }
                }

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

                ChaoRenderMetrics.onGpuBatchDraw(batch.skinned(), batch.reflection());
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


    /**
     * Draws the currently selected Animal Parts from shared immutable VBOs.
     *
     * <p>Anchor translation and Child SA2 node pose are draw-time state. Exact
     * animal identity therefore never causes a duplicate full-body VBO.</p>
     */
    private void drawAnimalParts(
            ChaoAppearanceState state,
            MatrixStack matrices,
            Matrix3f restoreNormalMatrix,
            int packedLight,
            boolean legacyPreview,
            ChaoAnimationPose pose,
            Matrix4f[] renderSpacePalette) {
        if (state.animalParts().isEmpty()) {
            return;
        }

        boolean adult = state.type() != ChaoVisualType.CHILD;
        ChaoAdultFamily adultFamily =
                state.type() == ChaoVisualType.CHILD || state.type() == ChaoVisualType.CHAOS
                        ? null
                        : ChaoAdultFamily.resolve(state);
        ChaoChaosFamily chaosFamily =
                state.type() == ChaoVisualType.CHAOS
                        ? ChaoChaosFamily.resolve(state)
                        : null;

        for (Slot slot : Slot.values()) {
            if (state.headDeco() != ChaoHeadDecoType.NONE
                    && (slot == Slot.FACE || slot == Slot.FOREHEAD
                        || slot == Slot.HORNS || slot == Slot.EARS)) {
                continue;
            }

            ChaoAnimalType animal = state.animalParts().get(slot);
            if (animal == ChaoAnimalType.NONE) {
                continue;
            }

            List<ChaoGpuRenderCache.DrawBatch> batches =
                    getSharedAnimalPartBatches(adult, animal, slot);
            if (batches.isEmpty()) {
                continue;
            }

            Vector3f anchor =
                    resolveAnimalAnchor(state, adultFamily, chaosFamily, slot);

            matrices.push();
            matrices.translate(anchor.x, anchor.y, -anchor.z);
            drawGpuBatches(
                    batches, matrices, restoreNormalMatrix,
                    packedLight, legacyPreview, pose,
                    state.reflectionType(), state.type(), renderSpacePalette);
            matrices.pop();
        }
    }

    private List<ChaoGpuRenderCache.DrawBatch> getSharedAnimalPartBatches(
            boolean adult, ChaoAnimalType animal, Slot slot) {
        SharedAnimalPartKey key = new SharedAnimalPartKey(adult, animal, slot);
        List<ChaoGpuRenderCache.DrawBatch> cached = sharedAnimalPartBatches.get(key);
        if (cached != null
                && cached.stream().noneMatch(batch -> batch.vertexBuffer().isClosed())) {
            return cached;
        }

        if (cached != null) {
            for (ChaoGpuRenderCache.DrawBatch batch : cached) {
                batch.close();
            }
            sharedAnimalPartBatches.remove(key);
        }

        ChaoAnimalPartCatalog.PartSpec spec =
                ChaoAnimalPartCatalog.resolve(adult, animal, slot);
        if (spec == null || !spec.visible()) {
            return List.of();
        }

        ChaoMeshModel partModel = getAnimalModel(spec.model());
        if (partModel == null) {
            return List.of();
        }

        // The Chao-specific anchor is deliberately zero here. It is applied
        // in drawAnimalParts(), allowing this VBO to survive every slider change.
        Matrix4f positionMatrix =
                createAnimalPositionMatrix(new Vector3f(), spec);
        Matrix3f normalMatrix = createAnimalNormalMatrix(spec);

        // Child and Adult/Chaos share the exact same 0-39 attachment semantics.
        // The adult flag selects only the Viewer source asset; animation ownership
        // remains universal and is evaluated draw-time, never baked per frame.
        ChildAnimalAttachment attachment = childAnimalAttachment(slot);

        List<ChaoGpuRenderCache.DrawBatch> result = new ArrayList<>();
        try {
            int materialIndex = 0;
            for (ChaoMeshModel.Segment segment : partModel.segments()) {
                for (ChaoMeshModel.Submesh submesh : segment.submeshes()) {
                    ChaoAnimalPartCatalog.MaterialSpec mat =
                            spec.materials().isEmpty()
                                    ? new ChaoAnimalPartCatalog.MaterialSpec(
                                            WHITE_TEXTURE, 1F, 1F, 1F, 1F)
                                    : spec.materials().get(Math.min(
                                            materialIndex,
                                            spec.materials().size() - 1));
                    materialIndex++;

                    if (attachment.bilateral()) {
                        ChaoGpuRenderCache.DrawBatch positive =
                                buildStaticTexturedSideBatch(
                                        segment, submesh, mat, 0x00F000F0,
                                        positionMatrix, normalMatrix,
                                        attachment.positiveXNode(), 1);
                        if (positive != null) result.add(positive);

                        ChaoGpuRenderCache.DrawBatch negative =
                                buildStaticTexturedSideBatch(
                                        segment, submesh, mat, 0x00F000F0,
                                        positionMatrix, normalMatrix,
                                        attachment.negativeXNode(), -1);
                        if (negative != null) result.add(negative);
                    } else {
                        ChaoGpuRenderCache.DrawBatch batch =
                                buildStaticTexturedBatch(
                                        partModel, segment, submesh, mat,
                                        0x00F000F0, positionMatrix, normalMatrix,
                                        attachmentRigNode(
                                                attachment.positiveXNode()));
                        if (batch != null) result.add(batch);
                    }
                }
            }

            List<ChaoGpuRenderCache.DrawBatch> immutable = List.copyOf(result);
            sharedAnimalPartBatches.put(key, immutable);
            long bytes = immutable.stream()
                    .mapToLong(ChaoGpuRenderCache.DrawBatch::estimatedBytes)
                    .sum();
            if (!sharedGpuWarmupRunning) {
                ChaoCraft.LOGGER.info(
                        "[Performance] Warmed shared AnimalPart {} / {} / {} once: {} VBOs, {} KiB",
                        adult ? "adult" : "child",
                        animal, slot, immutable.size(), (bytes + 1023L) / 1024L);
            }
            return immutable;
        } catch (RuntimeException | OutOfMemoryError failure) {
            for (ChaoGpuRenderCache.DrawBatch batch : result) {
                batch.close();
            }
            throw failure;
        }
    }

    private static ChildAnimalAttachment childAnimalAttachment(Slot slot) {
        return switch (slot) {
            case ARMS -> new ChildAnimalAttachment(
                    ChaoSa2RigNodeRegistry.LEFT_ARM,
                    ChaoSa2RigNodeRegistry.RIGHT_ARM);
            case LEGS -> new ChildAnimalAttachment(
                    ChaoSa2RigNodeRegistry.LEFT_LEG,
                    ChaoSa2RigNodeRegistry.RIGHT_LEG);
            case TAIL -> new ChildAnimalAttachment(
                    ChaoSa2RigNodeRegistry.TAIL, -1);
            case WINGS -> new ChildAnimalAttachment(
                    ChaoSa2RigNodeRegistry.LEFT_WING,
                    ChaoSa2RigNodeRegistry.RIGHT_WING);
            case FACE -> new ChildAnimalAttachment(
                    ChaoSa2RigNodeRegistry.MOUTH, -1);
            case HORNS, EARS -> new ChildAnimalAttachment(
                    ChaoSa2RigNodeRegistry.LEFT_UPPER_HEAD,
                    ChaoSa2RigNodeRegistry.RIGHT_UPPER_HEAD);
            case FOREHEAD -> new ChildAnimalAttachment(
                    ChaoSa2RigNodeRegistry.FOREHEAD_ATTACHMENT, -1);
        };
    }

    private ChaoGpuRenderCache.DrawBatch buildStaticTexturedSideBatch(
            ChaoMeshModel.Segment segment,
            ChaoMeshModel.Submesh submesh,
            ChaoAnimalPartCatalog.MaterialSpec material,
            int light,
            Matrix4f positionMatrix,
            Matrix3f normalMatrix,
            int sa2Node,
            int sideSign) {
        boolean translucent = material.a() < 0.999F;
        RenderLayer layer = translucent
                ? RenderLayer.getEntityTranslucent(material.texture())
                : RenderLayer.getEntityCutoutNoCull(material.texture());

        int first = submesh.firstIndex();
        int end = first + submesh.indexCount();
        if (first < 0 || end > segment.indices().length
                || submesh.indexCount() % 3 != 0) {
            return null;
        }

        int keptTriangles = 0;
        for (int triangle = first; triangle < end; triangle += 3) {
            if (triangleBelongsToXSide(segment, triangle, sideSign)) {
                keptTriangles++;
            }
        }
        if (keptTriangles == 0) return null;

        int expectedVertices = keptTriangles * 4;
        int bufferBytes = estimatedBufferBytes(layer, expectedVertices);
        if (bufferBytes < 0) return null;

        BufferBuilder builder = new BufferBuilder(
                Math.max(256, Math.min(expectedVertices, 32 * 1024)));
        builder.begin(layer.getDrawMode(), layer.getVertexFormat());

        ChaoColor color = new ChaoColor(
                material.r(), material.g(), material.b(), material.a());

        for (int triangle = first; triangle < end; triangle += 3) {
            if (!triangleBelongsToXSide(segment, triangle, sideSign)) {
                continue;
            }

            appendStaticVertex(
                    builder, segment, segment.indices()[triangle],
                    color, light, positionMatrix, normalMatrix);
            appendStaticVertex(
                    builder, segment, segment.indices()[triangle + 2],
                    color, light, positionMatrix, normalMatrix);
            appendStaticVertex(
                    builder, segment, segment.indices()[triangle + 1],
                    color, light, positionMatrix, normalMatrix);
            appendStaticVertex(
                    builder, segment, segment.indices()[triangle + 1],
                    color, light, positionMatrix, normalMatrix);
        }

        BufferBuilder.BuiltBuffer built = builder.end();
        if (built.isEmpty()) {
            built.release();
            return null;
        }

        VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        try {
            vertexBuffer.bind();
            uploadVertexBufferChecked(vertexBuffer, built);
        } catch (RuntimeException | OutOfMemoryError failure) {
            vertexBuffer.close();
            throw failure;
        } finally {
            VertexBuffer.unbind();
        }

        return new ChaoGpuRenderCache.DrawBatch(
                layer, vertexBuffer, bufferBytes, false, 0.0F,
                attachmentRigNode(sa2Node)
        );
    }

    private static boolean triangleBelongsToXSide(
            ChaoMeshModel.Segment segment, int triangleIndex, int sideSign) {
        int a = segment.indices()[triangleIndex] * 3;
        int b = segment.indices()[triangleIndex + 1] * 3;
        int c = segment.indices()[triangleIndex + 2] * 3;

        float centroidX = (
                segment.positions()[a]
                + segment.positions()[b]
                + segment.positions()[c]
        ) / 3.0F;

        return sideSign > 0 ? centroidX >= 0.0F : centroidX < 0.0F;
    }

    private record ChildAnimalAttachment(
            int positiveXNode,
            int negativeXNode) {
        boolean bilateral() {
            return negativeXNode >= 0;
        }
    }

    private record SharedAnimalPartKey(
            boolean adult,
            ChaoAnimalType animal,
            Slot slot) {
    }


    /**
     * Returns one immutable GPU copy of a Viewer HeadDeco.
     *
     * <p>The source Hats -90 X orientation is baked once, but the Chao-specific
     * anchor translation is intentionally NOT baked. That translation is applied
     * every draw, which makes the same VBO reusable by every Chao and by every
     * slider/evolution state.</p>
     */
    private List<ChaoGpuRenderCache.DrawBatch> getSharedHeadDecoBatches(ChaoHeadDecoType type) {
        if (type == ChaoHeadDecoType.NONE) {
            return List.of();
        }

        List<ChaoGpuRenderCache.DrawBatch> cached = sharedHeadDecoBatches.get(type);
        if (cached != null && cached.stream().noneMatch(batch -> batch.vertexBuffer().isClosed())) {
            return cached;
        }
        if (cached != null) {
            for (ChaoGpuRenderCache.DrawBatch batch : cached) {
                batch.close();
            }
            sharedHeadDecoBatches.remove(type);
        }

        ChaoHeadDecoCatalog.HeadSpec spec = ChaoHeadDecoCatalog.resolve(type);
        if (spec == null) return List.of();
        ChaoMeshModel model = getHeadDecoModel(spec.model());
        if (model == null) return List.of();

        // Viewer scene parent `Hats` is Euler(-90,0,0). This source-space
        // correction is invariant and safe to bake into the one shared VBO.
        Quaternionf sourceRotation = new Quaternionf().rotationX((float) Math.toRadians(-90.0F));
        Quaternionf rotation = new Quaternionf(
                -sourceRotation.x, -sourceRotation.y, sourceRotation.z, sourceRotation.w).normalize();
        Matrix4f positionMatrix = new Matrix4f().identity().rotate(rotation);
        Matrix3f normalMatrix = new Matrix3f().identity().rotate(rotation);

        List<ChaoGpuRenderCache.DrawBatch> result = new ArrayList<>();
        try {
            int materialIndex = 0;
            for (ChaoMeshModel.Segment segment : model.segments()) {
                for (ChaoMeshModel.Submesh submesh : segment.submeshes()) {
                    ChaoHeadDecoCatalog.MaterialSpec mat = spec.materials().isEmpty()
                            ? new ChaoHeadDecoCatalog.MaterialSpec(
                                    WHITE_TEXTURE, 1F, 1F, 1F, 1F, false, 0.0F)
                            : spec.materials().get(Math.min(materialIndex, spec.materials().size() - 1));
                    materialIndex++;

                    ChaoGpuRenderCache.DrawBatch batch;
                    if (mat.reflective()) {
                        batch = buildStaticReflectionBatch(
                                segment, submesh, mat.texture(), mat.a(), mat.emission(),
                                0x00F000F0, positionMatrix, normalMatrix,
                                attachmentRigNode(ChaoSa2RigNodeRegistry.HEAD));
                    } else {
                        batch = buildStaticTexturedBatch(
                                model, segment, submesh,
                                new ChaoAnimalPartCatalog.MaterialSpec(
                                        mat.texture(), mat.r(), mat.g(), mat.b(), mat.a()),
                                0x00F000F0, positionMatrix, normalMatrix,
                                attachmentRigNode(ChaoSa2RigNodeRegistry.HEAD));
                    }
                    if (batch != null) result.add(batch);
                }
            }

            List<ChaoGpuRenderCache.DrawBatch> immutable = List.copyOf(result);
            sharedHeadDecoBatches.put(type, immutable);
            long bytes = immutable.stream().mapToLong(ChaoGpuRenderCache.DrawBatch::estimatedBytes).sum();
            if (!sharedGpuWarmupRunning) {
                ChaoCraft.LOGGER.info(
                        "[Performance] Warmed shared HeadDeco {} once: {} VBOs, {} KiB",
                        type, immutable.size(), (bytes + 1023L) / 1024L);
            }
            return immutable;
        } catch (RuntimeException | OutOfMemoryError failure) {
            for (ChaoGpuRenderCache.DrawBatch batch : result) {
                batch.close();
            }
            throw failure;
        }
    }

    /** Draws the shared HeadDeco at this Chao's current Viewer anchor. */
    private void drawHeadDeco(ChaoAppearanceState state, MatrixStack matrices,
            Matrix3f restoreNormalMatrix, int packedLight, boolean legacyPreview,
            ChaoAnimationPose pose, Matrix4f[] renderSpacePalette) {
        if (state.headDeco() == ChaoHeadDecoType.NONE) {
            return;
        }

        List<ChaoGpuRenderCache.DrawBatch> batches = getSharedHeadDecoBatches(state.headDeco());
        if (batches.isEmpty()) {
            return;
        }

        ChaoAdultFamily adultFamily =
                state.type() == ChaoVisualType.CHILD || state.type() == ChaoVisualType.CHAOS
                        ? null
                        : ChaoAdultFamily.resolve(state);
        ChaoChaosFamily chaosFamily =
                state.type() == ChaoVisualType.CHAOS ? ChaoChaosFamily.resolve(state) : null;

        Vector3f anchor;
        if (chaosFamily != null) {
            anchor = ChaoChaosAnchorProfiles.resolveHat(chaosFamily);
        } else if (adultFamily != null) {
            anchor = ChaoHeadDecoAnchorProfiles.resolve(adultFamily, state);
        } else {
            anchor = new Vector3f();
        }

        matrices.push();
        matrices.translate(anchor.x, anchor.y, -anchor.z);
        drawGpuBatches(
                batches, matrices, restoreNormalMatrix, packedLight,
                legacyPreview, pose, state.reflectionType(), state.type(), renderSpacePalette
        );
        matrices.pop();
    }

    private ChaoMeshModel getHeadDecoModel(Identifier id) {
        if (headDecoLoadAttempted.add(id)) {
            ChaoMeshModel model = loadModel(id);
            if (model != null) headDecoModels.put(id, model);
        }
        return headDecoModels.get(id);
    }

    /**
     * Static HeadDeco reflection pass using the same Viewer cubemap shader as Chao
     * reflection materials. No Minecraft world reflection is captured.
     */
    private ChaoGpuRenderCache.DrawBatch buildStaticReflectionBatch(
            ChaoMeshModel.Segment segment, ChaoMeshModel.Submesh submesh,
            Identifier cubemap, float ref, float emission, int light,
            Matrix4f positionMatrix, Matrix3f normalMatrix) {
        return buildStaticReflectionBatch(
                segment, submesh, cubemap, ref, emission, light,
                positionMatrix, normalMatrix, -1
        );
    }

    private ChaoGpuRenderCache.DrawBatch buildStaticReflectionBatch(
            ChaoMeshModel.Segment segment, ChaoMeshModel.Submesh submesh,
            Identifier cubemap, float ref, float emission, int light,
            Matrix4f positionMatrix, Matrix3f normalMatrix, int rigNode) {
        RenderLayer layer = RenderLayer.getEntityTranslucent(cubemap);
        int expectedVertices = (Math.max(0, submesh.indexCount()) / 3) * 4;
        int bufferBytes = estimatedBufferBytes(layer, expectedVertices);
        if (bufferBytes < 0) return null;

        BufferBuilder builder = new BufferBuilder(
                Math.max(256, Math.min(expectedVertices, 32 * 1024)));
        builder.begin(layer.getDrawMode(), layer.getVertexFormat());
        appendStaticSubmesh(builder, segment, submesh,
                new ChaoColor(1F, 1F, 1F, ref),
                light, positionMatrix, normalMatrix);

        BufferBuilder.BuiltBuffer built = builder.end();
        if (built.isEmpty()) {
            built.release();
            return null;
        }

        VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        try {
            vertexBuffer.bind();
            uploadVertexBufferChecked(vertexBuffer, built);
        } catch (RuntimeException | OutOfMemoryError exception) {
            vertexBuffer.close();
            throw exception;
        } finally {
            VertexBuffer.unbind();
        }
        return new ChaoGpuRenderCache.DrawBatch(
                layer, vertexBuffer, bufferBytes, true, emission, rigNode);
    }

    private ChaoGpuRenderCache.DrawBatch buildStaticTexturedBatch(ChaoMeshModel model,
            ChaoMeshModel.Segment segment, ChaoMeshModel.Submesh submesh,
            ChaoAnimalPartCatalog.MaterialSpec material, int light,
            Matrix4f positionMatrix, Matrix3f normalMatrix) {
        return buildStaticTexturedBatch(
                model, segment, submesh, material, light,
                positionMatrix, normalMatrix, -1
        );
    }

    private ChaoGpuRenderCache.DrawBatch buildStaticTexturedBatch(ChaoMeshModel model,
            ChaoMeshModel.Segment segment, ChaoMeshModel.Submesh submesh,
            ChaoAnimalPartCatalog.MaterialSpec material, int light,
            Matrix4f positionMatrix, Matrix3f normalMatrix, int rigNode) {
        boolean translucent = material.a() < 0.999F;
        RenderLayer layer = translucent ? RenderLayer.getEntityTranslucent(material.texture())
                : RenderLayer.getEntityCutoutNoCull(material.texture());
        int expectedVertices = (Math.max(0, submesh.indexCount()) / 3) * 4;
        int bufferBytes = estimatedBufferBytes(layer, expectedVertices);
        if (bufferBytes < 0) return null;
        BufferBuilder builder = new BufferBuilder(Math.max(256, Math.min(expectedVertices, 32 * 1024)));
        builder.begin(layer.getDrawMode(), layer.getVertexFormat());
        appendStaticSubmesh(builder, segment, submesh,
                new ChaoColor(material.r(), material.g(), material.b(), material.a()),
                light, positionMatrix, normalMatrix);
        BufferBuilder.BuiltBuffer built = builder.end();
        if (built.isEmpty()) { built.release(); return null; }
        VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        try {
            vertexBuffer.bind();
            uploadVertexBufferChecked(vertexBuffer, built);
        } catch (RuntimeException exception) {
            vertexBuffer.close();
            throw exception;
        } finally { VertexBuffer.unbind(); }
        return new ChaoGpuRenderCache.DrawBatch(
                layer, vertexBuffer, bufferBytes, false, 0.0F, rigNode
        );
    }

    private static Vector3f resolveAnimalAnchor(
            ChaoAppearanceState state,
            ChaoAdultFamily adultFamily,
            ChaoChaosFamily chaosFamily,
            Slot slot) {
        if (state.type() == ChaoVisualType.CHILD) {
            // Scene Child anchors are at origin except Tail, whose parent is y=-.14.
            return slot == Slot.TAIL ? new Vector3f(0F, -0.14F, 0F) : new Vector3f();
        }
        if (chaosFamily != null) {
            // Viewer Chaos types do NOT use CalcPartsLocations(PaletteGroup).
            // They call SetDecoLoc() with fixed NChaos/HChaos/DChaos Palette values.
            return ChaoChaosAnchorProfiles.resolve(chaosFamily, slot);
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
                    variant, anchor, state.tiltedHalo(), palette, light, state.type()
            );
            if (batch != null) {
                result.add(batch);
            }
        }
        return result;
    }

    private ChaoGpuRenderCache.DrawBatch buildEmotionBatch(EmotionVariant variant, EmotionAnchor anchor,
            boolean tiltedHalo, ChaoPaletteState palette, int light, ChaoVisualType visualType) {
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
        BufferBuilder builder = new BufferBuilder(Math.max(256, Math.min(expectedVertices, 32 * 1024)));
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
            uploadVertexBufferChecked(vertexBuffer, built);
        } catch (RuntimeException exception) {
            vertexBuffer.close();
            throw exception;
        } finally {
            VertexBuffer.unbind();
        }
        int rigNode = attachmentRigNode(ChaoSa2RigNodeRegistry.EMOTION);
        return new ChaoGpuRenderCache.DrawBatch(
                layer, vertexBuffer, bufferBytes, false, 0.0F, rigNode
        );
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
        ChaoMeshModel preloaded = ChaoMeshRepository.get(identifier);
        if (preloaded != null) {
            return preloaded;
        }

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

    private record BatchKey(
            Identifier texture,
            ChaoColor color,
            boolean translucent,
            UvMode uvMode,
            boolean fullbright,
            float reflectionEmission,
            int rigNode
    ) {
    }

    private record PreviewGpuState(ChaoAppearanceState requestedState, long changedNanos) {
    }

    private enum RigSide {
        ALL,
        POSITIVE_X,
        NEGATIVE_X;

        boolean accepts(ChaoMeshModel.Segment segment, ChaoRenderCache.PreparedSegment prepared, int triangleIndex) {
            if (this == ALL) return true;
            int i0 = segment.indices()[triangleIndex] * 3;
            int i1 = segment.indices()[triangleIndex + 1] * 3;
            int i2 = segment.indices()[triangleIndex + 2] * 3;
            float centroidX = (prepared.positions()[i0] + prepared.positions()[i1] + prepared.positions()[i2]) / 3.0F;
            return this == POSITIVE_X ? centroidX >= 0.0F : centroidX < 0.0F;
        }
    }

    private record DrawSource(
            ChaoMeshModel.Segment segment,
            ChaoRenderCache.PreparedSegment prepared,
            ChaoMeshModel.Submesh submesh,
            RigSide side
    ) {
    }
}
