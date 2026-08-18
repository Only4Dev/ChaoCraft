package com.chaocraft.client.render.material;

/** Resolved Chao Viewer color channels needed by the current material families. */
public record ChaoPaletteState(
		ChaoColor base,
		ChaoColor body,
		ChaoColor belly,
		ChaoColor extra,
		ChaoColor extra2,
		ChaoColor horns,
		ChaoColor wings,
		ChaoColor wingsBase,
		ChaoColor bodyCover,
		ChaoColor wingsCover
) {
}
