package com.chaocraft.visual;

/**
 * Pure Java translation of the core Chao Viewer weight calculations.
 * Rendering code should consume this output rather than reimplementing the
 * formulas, keeping visual parity logic centralized and testable.
 */
public final class ChaoMorphResolver {
	private ChaoMorphResolver() {
	}

	public static ChaoMorphWeights resolve(ChaoAppearanceState state) {
		float age = state.age();
		float normal = state.normal();

		float adultNormal = normal * age;
		float adultSwim = state.swim() * age;
		float adultFly = state.fly() * age;
		float adultRun = state.run() * age;
		float adultPower = state.power() * age;

		AlignmentWeights alignment = resolveAlignment(state.alignment());
		float neutralFactor = alignment.neutral() / 100.0F;
		float heroFactor = alignment.hero() / 100.0F;
		float darkFactor = alignment.dark() / 100.0F;

		return new ChaoMorphWeights(
				adultNormal,
				adultSwim,
				adultFly,
				adultRun,
				adultPower,
				adultNormal * neutralFactor,
				adultSwim * neutralFactor,
				adultFly * neutralFactor,
				adultRun * neutralFactor,
				adultPower * neutralFactor,
				alignment.hero() * (1.0F - age),
				adultNormal * heroFactor,
				adultSwim * heroFactor,
				adultFly * heroFactor,
				adultRun * heroFactor,
				adultPower * heroFactor,
				alignment.dark() * (1.0F - age),
				adultNormal * darkFactor,
				adultSwim * darkFactor,
				adultFly * darkFactor,
				adultRun * darkFactor,
				adultPower * darkFactor
		);
	}

	public static AlignmentWeights resolveAlignment(float alignment) {
		if (alignment < 0.0F) {
			float dark = -alignment;
			return new AlignmentWeights(100.0F - dark, 0.0F, dark);
		}
		if (alignment > 0.0F) {
			float hero = alignment;
			return new AlignmentWeights(100.0F - hero, hero, 0.0F);
		}
		return new AlignmentWeights(100.0F, 0.0F, 0.0F);
	}

	public record AlignmentWeights(float neutral, float hero, float dark) {
	}
}
