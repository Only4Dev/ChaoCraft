package com.chaocraft.visual;

/**
 * Loader-agnostic visual state consumed by the Chao appearance pipeline.
 * Values intentionally mirror the Chao Viewer controls so rendering can be
 * driven by the same inputs without coupling simulation code to Minecraft.
 */
public record ChaoAppearanceState(
		ChaoVisualType type,
		float age,
		float alignment,
		float swim,
		float fly,
		float run,
		float power
) {
	public static final ChaoAppearanceState DEFAULT = new ChaoAppearanceState(
			ChaoVisualType.CHILD, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F
	);

	public ChaoAppearanceState {
		if (type == null) {
			type = ChaoVisualType.CHILD;
		}
		age = clamp(age, 0.0F, 1.0F);
		alignment = clamp(alignment, -100.0F, 100.0F);
		swim = clamp(swim, 0.0F, 100.0F);
		fly = clamp(fly, 0.0F, 100.0F);
		run = clamp(run, 0.0F, 100.0F);
		power = clamp(power, 0.0F, 100.0F);
	}

	/** Compatibility constructor for code that does not need to choose a family. */
	public ChaoAppearanceState(float age, float alignment, float swim, float fly, float run, float power) {
		this(ChaoVisualType.CHILD, age, alignment, swim, fly, run, power);
	}

	public float normal() {
		return Math.max(0.0F, 100.0F - swim - fly - run - power);
	}

	/**
	 * Reproduces the Viewer's normal slider behavior: the edited channel keeps
	 * its requested value and the other three are reduced proportionally only
	 * when the four evolution channels would otherwise exceed 100.
	 */
	public ChaoAppearanceState withEvolution(EvolutionChannel channel, float requestedValue) {
		float value = clamp(requestedValue, 0.0F, 100.0F);
		float newSwim = swim;
		float newFly = fly;
		float newRun = run;
		float newPower = power;

		switch (channel) {
			case SWIM -> newSwim = value;
			case FLY -> newFly = value;
			case RUN -> newRun = value;
			case POWER -> newPower = value;
		}

		float otherTotal = switch (channel) {
			case SWIM -> newFly + newRun + newPower;
			case FLY -> newSwim + newRun + newPower;
			case RUN -> newSwim + newFly + newPower;
			case POWER -> newSwim + newFly + newRun;
		};
		float remaining = 100.0F - value;

		if (otherTotal > remaining && otherTotal > 0.0F) {
			float scale = remaining / otherTotal;
			switch (channel) {
				case SWIM -> {
					newFly *= scale;
					newRun *= scale;
					newPower *= scale;
				}
				case FLY -> {
					newSwim *= scale;
					newRun *= scale;
					newPower *= scale;
				}
				case RUN -> {
					newSwim *= scale;
					newFly *= scale;
					newPower *= scale;
				}
				case POWER -> {
					newSwim *= scale;
					newFly *= scale;
					newRun *= scale;
				}
			}
		}

		return new ChaoAppearanceState(type, age, alignment, newSwim, newFly, newRun, newPower);
	}

	public ChaoAppearanceState withType(ChaoVisualType value) {
		return new ChaoAppearanceState(value, age, alignment, swim, fly, run, power);
	}

	public ChaoAppearanceState withAge(float value) {
		return new ChaoAppearanceState(type, value, alignment, swim, fly, run, power);
	}

	public ChaoAppearanceState withAlignment(float value) {
		return new ChaoAppearanceState(type, age, value, swim, fly, run, power);
	}

	private static float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}

	public enum EvolutionChannel {
		SWIM,
		FLY,
		RUN,
		POWER
	}
}
