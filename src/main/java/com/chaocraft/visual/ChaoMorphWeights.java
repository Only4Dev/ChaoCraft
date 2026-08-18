package com.chaocraft.visual;

/**
 * Resolved Chao Viewer-style morph weights. The first five values drive adult
 * meshes; the remaining values are used by the Child mesh family.
 */
public record ChaoMorphWeights(
		float normal,
		float swim,
		float fly,
		float run,
		float power,
		float neutralNormal,
		float neutralSwim,
		float neutralFly,
		float neutralRun,
		float neutralPower,
		float heroNeutralBaby,
		float heroNormal,
		float heroSwim,
		float heroFly,
		float heroRun,
		float heroPower,
		float darkNeutralBaby,
		float darkNormal,
		float darkSwim,
		float darkFly,
		float darkRun,
		float darkPower
) {
}
