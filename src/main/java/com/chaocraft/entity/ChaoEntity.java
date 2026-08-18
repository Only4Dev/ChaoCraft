package com.chaocraft.entity;

import com.chaocraft.visual.ChaoAppearanceState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.world.World;

/**
 * Real Chao entity foundation. The current renderer is only a placeholder;
 * gameplay and appearance state already live on the Chao itself so replacing
 * the renderer later does not require replacing the entity.
 */
public class ChaoEntity extends PathAwareEntity {
	private static final String NBT_VISUAL_AGE = "VisualAge";
	private static final String NBT_ALIGNMENT = "Alignment";
	private static final String NBT_SWIM = "VisualSwim";
	private static final String NBT_FLY = "VisualFly";
	private static final String NBT_RUN = "VisualRun";
	private static final String NBT_POWER = "VisualPower";

	private static final TrackedData<Float> VISUAL_AGE = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> ALIGNMENT = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> VISUAL_SWIM = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> VISUAL_FLY = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> VISUAL_RUN = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> VISUAL_POWER = DataTracker.registerData(ChaoEntity.class, TrackedDataHandlerRegistry.FLOAT);

	public ChaoEntity(EntityType<? extends ChaoEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 0;

		// Temporary visible identifier while the custom Chao renderer is built.
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
		// Intentionally empty. SA2-compatible Chao behavior will be implemented
		// by ChaoCraft's own behavior scheduler instead of vanilla mob goals.
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(VISUAL_AGE, ChaoAppearanceState.DEFAULT.age());
		this.dataTracker.startTracking(ALIGNMENT, ChaoAppearanceState.DEFAULT.alignment());
		this.dataTracker.startTracking(VISUAL_SWIM, ChaoAppearanceState.DEFAULT.swim());
		this.dataTracker.startTracking(VISUAL_FLY, ChaoAppearanceState.DEFAULT.fly());
		this.dataTracker.startTracking(VISUAL_RUN, ChaoAppearanceState.DEFAULT.run());
		this.dataTracker.startTracking(VISUAL_POWER, ChaoAppearanceState.DEFAULT.power());
	}

	public ChaoAppearanceState getAppearanceState() {
		return new ChaoAppearanceState(
				this.dataTracker.get(VISUAL_AGE),
				this.dataTracker.get(ALIGNMENT),
				this.dataTracker.get(VISUAL_SWIM),
				this.dataTracker.get(VISUAL_FLY),
				this.dataTracker.get(VISUAL_RUN),
				this.dataTracker.get(VISUAL_POWER)
		);
	}

	public void setAppearanceState(ChaoAppearanceState state) {
		this.dataTracker.set(VISUAL_AGE, state.age());
		this.dataTracker.set(ALIGNMENT, state.alignment());
		this.dataTracker.set(VISUAL_SWIM, state.swim());
		this.dataTracker.set(VISUAL_FLY, state.fly());
		this.dataTracker.set(VISUAL_RUN, state.run());
		this.dataTracker.set(VISUAL_POWER, state.power());
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		ChaoAppearanceState state = getAppearanceState();
		nbt.putFloat(NBT_VISUAL_AGE, state.age());
		nbt.putFloat(NBT_ALIGNMENT, state.alignment());
		nbt.putFloat(NBT_SWIM, state.swim());
		nbt.putFloat(NBT_FLY, state.fly());
		nbt.putFloat(NBT_RUN, state.run());
		nbt.putFloat(NBT_POWER, state.power());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		setAppearanceState(new ChaoAppearanceState(
				nbt.contains(NBT_VISUAL_AGE) ? nbt.getFloat(NBT_VISUAL_AGE) : ChaoAppearanceState.DEFAULT.age(),
				nbt.contains(NBT_ALIGNMENT) ? nbt.getFloat(NBT_ALIGNMENT) : ChaoAppearanceState.DEFAULT.alignment(),
				nbt.contains(NBT_SWIM) ? nbt.getFloat(NBT_SWIM) : ChaoAppearanceState.DEFAULT.swim(),
				nbt.contains(NBT_FLY) ? nbt.getFloat(NBT_FLY) : ChaoAppearanceState.DEFAULT.fly(),
				nbt.contains(NBT_RUN) ? nbt.getFloat(NBT_RUN) : ChaoAppearanceState.DEFAULT.run(),
				nbt.contains(NBT_POWER) ? nbt.getFloat(NBT_POWER) : ChaoAppearanceState.DEFAULT.power()
		));
	}

	@Override
	public boolean cannotDespawn() {
		return true;
	}
}
