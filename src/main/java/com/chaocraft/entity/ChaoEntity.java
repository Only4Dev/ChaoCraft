package com.chaocraft.entity;

import com.chaocraft.config.ChaoServerConfig;
import com.chaocraft.visual.ChaoAppearanceState;
import com.chaocraft.visual.ChaoColorType;
import com.chaocraft.visual.ChaoReflectionType;
import com.chaocraft.visual.ChaoAnimalType;
import com.chaocraft.visual.ChaoAnimalParts;
import com.chaocraft.visual.ChaoHeadDecoType;
import com.chaocraft.visual.ChaoVisualType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

/** Persistent server-authoritative Chao entity foundation. */
public class ChaoEntity extends PathAwareEntity {
    private static final int SIMULATION_RANGE_RECHECK_TICKS = 20;

    // Server-only gate for ChaoCraft-owned AI/lifecycle/stat simulation. Vanilla
    // entity bookkeeping still ticks normally; future gameplay systems must run
    // through tickChaoSimulation() (or check isChaoSimulationActive()).
    private boolean chaoSimulationActive = true;
    private int simulationRangeRecheckCooldown = -1;
    private static final String NBT_VISUAL_TYPE = "VisualType";
    private static final String NBT_VISUAL_AGE = "VisualAge";
    private static final String NBT_ALIGNMENT = "Alignment";
    private static final String NBT_SWIM = "VisualSwim";
    private static final String NBT_FLY = "VisualFly";
    private static final String NBT_RUN = "VisualRun";
    private static final String NBT_POWER = "VisualPower";
    private static final String NBT_COLOR_TYPE = "ColorType";
    private static final String NBT_MONOTONE = "Monotone";
    private static final String NBT_REFLECTION_TYPE = "ReflectionType";
    private static final String NBT_ANIMAL_ARMS = "AnimalArms";
    private static final String NBT_ANIMAL_LEGS = "AnimalLegs";
    private static final String NBT_ANIMAL_TAIL = "AnimalTail";
    private static final String NBT_ANIMAL_WINGS = "AnimalWings";
    private static final String NBT_ANIMAL_FACE = "AnimalFace";
    private static final String NBT_ANIMAL_HORNS = "AnimalHorns";
    private static final String NBT_ANIMAL_EARS = "AnimalEars";
    private static final String NBT_ANIMAL_FOREHEAD = "AnimalForehead";
    private static final String NBT_HEAD_DECO = "HeadDeco";
    private static final String NBT_CUSTOM_EYES = "CustomEyes";
    private static final String NBT_EYES = "Eyes";
    private static final String NBT_EYELID = "Eyelid";
    private static final String NBT_MOUTH = "Mouth";
    private static final String NBT_CUSTOM_MOUTH = "CustomMouth";
    private static final String NBT_MOUTH_MID = "MouthMid";
    private static final String NBT_MOUTH_SIDE = "MouthSide";
    private static final String NBT_CUSTOM_BALL = "CustomEmotionBall";
    private static final String NBT_NEUTRAL_BALL = "NeutralBall";
    private static final String NBT_HERO_BALL = "HeroBall";
    private static final String NBT_DARK_BALL = "DarkBall";
    private static final String NBT_TILTED_HALO = "TiltedHalo";

    private static final TrackedData<Integer> VISUAL_TYPE = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> VISUAL_AGE = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> ALIGNMENT = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> VISUAL_SWIM = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> VISUAL_FLY = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> VISUAL_RUN = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> VISUAL_POWER = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> COLOR_TYPE = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> MONOTONE = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> REFLECTION_TYPE = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ANIMAL_ARMS = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ANIMAL_LEGS = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ANIMAL_TAIL = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ANIMAL_WINGS = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ANIMAL_FACE = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ANIMAL_HORNS = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ANIMAL_EARS = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ANIMAL_FOREHEAD = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> HEAD_DECO = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> CUSTOM_EYES = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> EYES = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> EYELID = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> MOUTH = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> CUSTOM_MOUTH = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> MOUTH_MID = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> MOUTH_SIDE = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> CUSTOM_BALL = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> NEUTRAL_BALL = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> HERO_BALL = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> DARK_BALL = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> TILTED_HALO = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    // Development-only animation stress metadata. -1 means normal gameplay rendering.
    // These fields are intentionally not persisted to NBT.
    private static final TrackedData<Integer> VISUAL_LAB_ANIMATION = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> VISUAL_LAB_ANIMATION_PHASE = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public ChaoEntity(EntityType<? extends ChaoEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 0;

        if (!world.isClient) {
            this.setCustomName(Text.literal("Chao [VIS prototype]"));
            this.setCustomNameVisible(true);
        }
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.20D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected void initGoals() {
        // SA2-compatible behavior will use ChaoCraft's own scheduler later.
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        ChaoAppearanceState defaults = ChaoAppearanceState.DEFAULT;
        this.dataTracker.startTracking(VISUAL_TYPE, defaults.type().ordinal());
        this.dataTracker.startTracking(VISUAL_AGE, defaults.age());
        this.dataTracker.startTracking(ALIGNMENT, defaults.alignment());
        this.dataTracker.startTracking(VISUAL_SWIM, defaults.swim());
        this.dataTracker.startTracking(VISUAL_FLY, defaults.fly());
        this.dataTracker.startTracking(VISUAL_RUN, defaults.run());
        this.dataTracker.startTracking(VISUAL_POWER, defaults.power());
        this.dataTracker.startTracking(COLOR_TYPE, defaults.colorType().ordinal());
        this.dataTracker.startTracking(MONOTONE, defaults.monotone());
        this.dataTracker.startTracking(REFLECTION_TYPE, defaults.reflectionType().ordinal());
        this.dataTracker.startTracking(ANIMAL_ARMS, defaults.animalParts().arms().ordinal());
        this.dataTracker.startTracking(ANIMAL_LEGS, defaults.animalParts().legs().ordinal());
        this.dataTracker.startTracking(ANIMAL_TAIL, defaults.animalParts().tail().ordinal());
        this.dataTracker.startTracking(ANIMAL_WINGS, defaults.animalParts().wings().ordinal());
        this.dataTracker.startTracking(ANIMAL_FACE, defaults.animalParts().face().ordinal());
        this.dataTracker.startTracking(ANIMAL_HORNS, defaults.animalParts().horns().ordinal());
        this.dataTracker.startTracking(ANIMAL_EARS, defaults.animalParts().ears().ordinal());
        this.dataTracker.startTracking(ANIMAL_FOREHEAD, defaults.animalParts().forehead().ordinal());
        this.dataTracker.startTracking(HEAD_DECO, defaults.headDeco().ordinal());
        this.dataTracker.startTracking(CUSTOM_EYES, defaults.customEyes());
        this.dataTracker.startTracking(EYES, defaults.eyes());
        this.dataTracker.startTracking(EYELID, defaults.eyelid());
        this.dataTracker.startTracking(MOUTH, defaults.mouth());
        this.dataTracker.startTracking(CUSTOM_MOUTH, defaults.customMouth());
        this.dataTracker.startTracking(MOUTH_MID, defaults.mouthMid());
        this.dataTracker.startTracking(MOUTH_SIDE, defaults.mouthSide());
        this.dataTracker.startTracking(CUSTOM_BALL, defaults.customEmotionBall());
        this.dataTracker.startTracking(NEUTRAL_BALL, defaults.neutralBall());
        this.dataTracker.startTracking(HERO_BALL, defaults.heroBall());
        this.dataTracker.startTracking(DARK_BALL, defaults.darkBall());
        this.dataTracker.startTracking(TILTED_HALO, defaults.tiltedHalo());
        this.dataTracker.startTracking(VISUAL_LAB_ANIMATION, -1);
        this.dataTracker.startTracking(VISUAL_LAB_ANIMATION_PHASE, 0);
    }

    /** Development-only animation used by Visual Lab stress matrices. */
    public int getVisualLabAnimation() {
        return this.dataTracker.get(VISUAL_LAB_ANIMATION);
    }

    public int getVisualLabAnimationPhase() {
        return this.dataTracker.get(VISUAL_LAB_ANIMATION_PHASE);
    }

    public void setVisualLabAnimation(int clipIndex, int phase) {
        this.dataTracker.set(VISUAL_LAB_ANIMATION, clipIndex);
        this.dataTracker.set(VISUAL_LAB_ANIMATION_PHASE, Math.max(0, phase));
    }

    public ChaoAppearanceState getAppearanceState() {
        return new ChaoAppearanceState(
                ChaoVisualType.fromOrdinal(this.dataTracker.get(VISUAL_TYPE)),
                this.dataTracker.get(VISUAL_AGE),
                this.dataTracker.get(ALIGNMENT),
                this.dataTracker.get(VISUAL_SWIM),
                this.dataTracker.get(VISUAL_FLY),
                this.dataTracker.get(VISUAL_RUN),
                this.dataTracker.get(VISUAL_POWER),
                ChaoColorType.fromOrdinal(this.dataTracker.get(COLOR_TYPE)),
                this.dataTracker.get(MONOTONE),
                ChaoReflectionType.fromOrdinal(this.dataTracker.get(REFLECTION_TYPE)),
                new ChaoAnimalParts(
                        ChaoAnimalType.fromOrdinal(this.dataTracker.get(ANIMAL_ARMS)),
                        ChaoAnimalType.fromOrdinal(this.dataTracker.get(ANIMAL_LEGS)),
                        ChaoAnimalType.fromOrdinal(this.dataTracker.get(ANIMAL_TAIL)),
                        ChaoAnimalType.fromOrdinal(this.dataTracker.get(ANIMAL_WINGS)),
                        ChaoAnimalType.fromOrdinal(this.dataTracker.get(ANIMAL_FACE)),
                        ChaoAnimalType.fromOrdinal(this.dataTracker.get(ANIMAL_HORNS)),
                        ChaoAnimalType.fromOrdinal(this.dataTracker.get(ANIMAL_EARS)),
                        ChaoAnimalType.fromOrdinal(this.dataTracker.get(ANIMAL_FOREHEAD))
                ),
                ChaoHeadDecoType.fromOrdinal(this.dataTracker.get(HEAD_DECO)),
                this.dataTracker.get(CUSTOM_EYES),
                this.dataTracker.get(EYES),
                this.dataTracker.get(EYELID),
                this.dataTracker.get(MOUTH),
                this.dataTracker.get(CUSTOM_MOUTH),
                this.dataTracker.get(MOUTH_MID),
                this.dataTracker.get(MOUTH_SIDE),
                this.dataTracker.get(CUSTOM_BALL),
                this.dataTracker.get(NEUTRAL_BALL),
                this.dataTracker.get(HERO_BALL),
                this.dataTracker.get(DARK_BALL),
                this.dataTracker.get(TILTED_HALO)
        );
    }

    public void setAppearanceState(ChaoAppearanceState state) {
        this.dataTracker.set(VISUAL_TYPE, state.type().ordinal());
        this.dataTracker.set(VISUAL_AGE, state.age());
        this.dataTracker.set(ALIGNMENT, state.alignment());
        this.dataTracker.set(VISUAL_SWIM, state.swim());
        this.dataTracker.set(VISUAL_FLY, state.fly());
        this.dataTracker.set(VISUAL_RUN, state.run());
        this.dataTracker.set(VISUAL_POWER, state.power());
        this.dataTracker.set(COLOR_TYPE, state.colorType().ordinal());
        this.dataTracker.set(MONOTONE, state.monotone());
        this.dataTracker.set(REFLECTION_TYPE, state.reflectionType().ordinal());
        this.dataTracker.set(ANIMAL_ARMS, state.animalParts().arms().ordinal());
        this.dataTracker.set(ANIMAL_LEGS, state.animalParts().legs().ordinal());
        this.dataTracker.set(ANIMAL_TAIL, state.animalParts().tail().ordinal());
        this.dataTracker.set(ANIMAL_WINGS, state.animalParts().wings().ordinal());
        this.dataTracker.set(ANIMAL_FACE, state.animalParts().face().ordinal());
        this.dataTracker.set(ANIMAL_HORNS, state.animalParts().horns().ordinal());
        this.dataTracker.set(ANIMAL_EARS, state.animalParts().ears().ordinal());
        this.dataTracker.set(ANIMAL_FOREHEAD, state.animalParts().forehead().ordinal());
        this.dataTracker.set(HEAD_DECO, state.headDeco().ordinal());
        this.dataTracker.set(CUSTOM_EYES, state.customEyes());
        this.dataTracker.set(EYES, state.eyes());
        this.dataTracker.set(EYELID, state.eyelid());
        this.dataTracker.set(MOUTH, state.mouth());
        this.dataTracker.set(CUSTOM_MOUTH, state.customMouth());
        this.dataTracker.set(MOUTH_MID, state.mouthMid());
        this.dataTracker.set(MOUTH_SIDE, state.mouthSide());
        this.dataTracker.set(CUSTOM_BALL, state.customEmotionBall());
        this.dataTracker.set(NEUTRAL_BALL, state.neutralBall());
        this.dataTracker.set(HERO_BALL, state.heroBall());
        this.dataTracker.set(DARK_BALL, state.darkBall());
        this.dataTracker.set(TILTED_HALO, state.tiltedHalo());
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        ChaoAppearanceState state = getAppearanceState();
        nbt.putInt(NBT_VISUAL_TYPE, state.type().ordinal());
        nbt.putFloat(NBT_VISUAL_AGE, state.age());
        nbt.putFloat(NBT_ALIGNMENT, state.alignment());
        nbt.putFloat(NBT_SWIM, state.swim());
        nbt.putFloat(NBT_FLY, state.fly());
        nbt.putFloat(NBT_RUN, state.run());
        nbt.putFloat(NBT_POWER, state.power());
        nbt.putInt(NBT_COLOR_TYPE, state.colorType().ordinal());
        nbt.putBoolean(NBT_MONOTONE, state.monotone());
        nbt.putInt(NBT_REFLECTION_TYPE, state.reflectionType().ordinal());
        nbt.putInt(NBT_ANIMAL_ARMS, state.animalParts().arms().ordinal());
        nbt.putInt(NBT_ANIMAL_LEGS, state.animalParts().legs().ordinal());
        nbt.putInt(NBT_ANIMAL_TAIL, state.animalParts().tail().ordinal());
        nbt.putInt(NBT_ANIMAL_WINGS, state.animalParts().wings().ordinal());
        nbt.putInt(NBT_ANIMAL_FACE, state.animalParts().face().ordinal());
        nbt.putInt(NBT_ANIMAL_HORNS, state.animalParts().horns().ordinal());
        nbt.putInt(NBT_ANIMAL_EARS, state.animalParts().ears().ordinal());
        nbt.putInt(NBT_ANIMAL_FOREHEAD, state.animalParts().forehead().ordinal());
        nbt.putInt(NBT_HEAD_DECO, state.headDeco().ordinal());
        nbt.putBoolean(NBT_CUSTOM_EYES, state.customEyes());
        nbt.putInt(NBT_EYES, state.eyes());
        nbt.putInt(NBT_EYELID, state.eyelid());
        nbt.putInt(NBT_MOUTH, state.mouth());
        nbt.putBoolean(NBT_CUSTOM_MOUTH, state.customMouth());
        nbt.putInt(NBT_MOUTH_MID, state.mouthMid());
        nbt.putInt(NBT_MOUTH_SIDE, state.mouthSide());
        nbt.putBoolean(NBT_CUSTOM_BALL, state.customEmotionBall());
        nbt.putBoolean(NBT_NEUTRAL_BALL, state.neutralBall());
        nbt.putBoolean(NBT_HERO_BALL, state.heroBall());
        nbt.putBoolean(NBT_DARK_BALL, state.darkBall());
        nbt.putBoolean(NBT_TILTED_HALO, state.tiltedHalo());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        ChaoAppearanceState defaults = ChaoAppearanceState.DEFAULT;
        setAppearanceState(new ChaoAppearanceState(
                nbt.contains(NBT_VISUAL_TYPE) ? ChaoVisualType.fromOrdinal(nbt.getInt(NBT_VISUAL_TYPE)) : defaults.type(),
                nbt.contains(NBT_VISUAL_AGE) ? nbt.getFloat(NBT_VISUAL_AGE) : defaults.age(),
                nbt.contains(NBT_ALIGNMENT) ? nbt.getFloat(NBT_ALIGNMENT) : defaults.alignment(),
                nbt.contains(NBT_SWIM) ? nbt.getFloat(NBT_SWIM) : defaults.swim(),
                nbt.contains(NBT_FLY) ? nbt.getFloat(NBT_FLY) : defaults.fly(),
                nbt.contains(NBT_RUN) ? nbt.getFloat(NBT_RUN) : defaults.run(),
                nbt.contains(NBT_POWER) ? nbt.getFloat(NBT_POWER) : defaults.power(),
                nbt.contains(NBT_COLOR_TYPE) ? ChaoColorType.fromOrdinal(nbt.getInt(NBT_COLOR_TYPE)) : defaults.colorType(),
                nbt.contains(NBT_MONOTONE) ? nbt.getBoolean(NBT_MONOTONE) : defaults.monotone(),
                nbt.contains(NBT_REFLECTION_TYPE) ? ChaoReflectionType.fromOrdinal(nbt.getInt(NBT_REFLECTION_TYPE)) : defaults.reflectionType(),
                new ChaoAnimalParts(
                        nbt.contains(NBT_ANIMAL_ARMS) ? ChaoAnimalType.fromOrdinal(nbt.getInt(NBT_ANIMAL_ARMS)) : defaults.animalParts().arms(),
                        nbt.contains(NBT_ANIMAL_LEGS) ? ChaoAnimalType.fromOrdinal(nbt.getInt(NBT_ANIMAL_LEGS)) : defaults.animalParts().legs(),
                        nbt.contains(NBT_ANIMAL_TAIL) ? ChaoAnimalType.fromOrdinal(nbt.getInt(NBT_ANIMAL_TAIL)) : defaults.animalParts().tail(),
                        nbt.contains(NBT_ANIMAL_WINGS) ? ChaoAnimalType.fromOrdinal(nbt.getInt(NBT_ANIMAL_WINGS)) : defaults.animalParts().wings(),
                        nbt.contains(NBT_ANIMAL_FACE) ? ChaoAnimalType.fromOrdinal(nbt.getInt(NBT_ANIMAL_FACE)) : defaults.animalParts().face(),
                        nbt.contains(NBT_ANIMAL_HORNS) ? ChaoAnimalType.fromOrdinal(nbt.getInt(NBT_ANIMAL_HORNS)) : defaults.animalParts().horns(),
                        nbt.contains(NBT_ANIMAL_EARS) ? ChaoAnimalType.fromOrdinal(nbt.getInt(NBT_ANIMAL_EARS)) : defaults.animalParts().ears(),
                        nbt.contains(NBT_ANIMAL_FOREHEAD) ? ChaoAnimalType.fromOrdinal(nbt.getInt(NBT_ANIMAL_FOREHEAD)) : defaults.animalParts().forehead()
                ),
                nbt.contains(NBT_HEAD_DECO) ? ChaoHeadDecoType.fromOrdinal(nbt.getInt(NBT_HEAD_DECO)) : defaults.headDeco(),
                nbt.contains(NBT_CUSTOM_EYES) ? nbt.getBoolean(NBT_CUSTOM_EYES) : defaults.customEyes(),
                nbt.contains(NBT_EYES) ? nbt.getInt(NBT_EYES) : defaults.eyes(),
                nbt.contains(NBT_EYELID) ? nbt.getInt(NBT_EYELID) : defaults.eyelid(),
                nbt.contains(NBT_MOUTH) ? nbt.getInt(NBT_MOUTH) : defaults.mouth(),
                nbt.contains(NBT_CUSTOM_MOUTH) ? nbt.getBoolean(NBT_CUSTOM_MOUTH) : defaults.customMouth(),
                nbt.contains(NBT_MOUTH_MID) ? nbt.getInt(NBT_MOUTH_MID) : defaults.mouthMid(),
                nbt.contains(NBT_MOUTH_SIDE) ? nbt.getInt(NBT_MOUTH_SIDE) : defaults.mouthSide(),
                nbt.contains(NBT_CUSTOM_BALL) ? nbt.getBoolean(NBT_CUSTOM_BALL) : defaults.customEmotionBall(),
                nbt.contains(NBT_NEUTRAL_BALL) ? nbt.getBoolean(NBT_NEUTRAL_BALL) : defaults.neutralBall(),
                nbt.contains(NBT_HERO_BALL) ? nbt.getBoolean(NBT_HERO_BALL) : defaults.heroBall(),
                nbt.contains(NBT_DARK_BALL) ? nbt.getBoolean(NBT_DARK_BALL) : defaults.darkBall(),
                nbt.contains(NBT_TILTED_HALO) ? nbt.getBoolean(NBT_TILTED_HALO) : defaults.tiltedHalo()
        ));
    }

    @Override
    public void tick() {
        super.tick();
        if (getWorld().isClient) {
            return;
        }

        int simulationDistance = ChaoServerConfig.get().simulationDistanceBlocks();
        if (simulationDistance <= 0) {
            chaoSimulationActive = true;
        } else {
            // Spread range checks across ticks so a large garden does not create
            // a once-per-second CPU spike when hundreds of Chao share one cadence.
            if (simulationRangeRecheckCooldown < 0) {
                simulationRangeRecheckCooldown = Math.floorMod(getId(), SIMULATION_RANGE_RECHECK_TICKS);
            }
            if (simulationRangeRecheckCooldown-- <= 0) {
                chaoSimulationActive = computeSimulationRangeActive(simulationDistance);
                simulationRangeRecheckCooldown = SIMULATION_RANGE_RECHECK_TICKS;
            }
        }
        if (chaoSimulationActive) {
            tickChaoSimulation();
        }
    }

    /**
     * Entry point for all future server-authoritative Chao simulation work.
     * Keeping expensive behavior behind one gate prevents individual systems
     * from accidentally ignoring the configured simulation distance.
     */
    protected void tickChaoSimulation() {
        // Lifecycle, AI, emotions, relationships, stats, breeding, etc. will be
        // attached here incrementally. The visual prototype has no gameplay tick yet.
    }

    public boolean isChaoSimulationActive() {
        return !getWorld().isClient && chaoSimulationActive;
    }

    private boolean computeSimulationRangeActive(int distance) {
        if (!(getWorld() instanceof ServerWorld serverWorld)) {
            return false;
        }

        double maxDistanceSquared = (double) distance * (double) distance;
        for (ServerPlayerEntity player : serverWorld.getPlayers()) {
            if (!player.isSpectator() && squaredDistanceTo(player) <= maxDistanceSquared) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }
}
