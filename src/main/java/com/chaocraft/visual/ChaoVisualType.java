package com.chaocraft.visual;

/**
 * Visual mesh family selected by the Chao appearance pipeline.
 * More SA2 adult families will be added as their meshes are integrated.
 */
public enum ChaoVisualType {
	CHILD,
	NORMAL;

	public static ChaoVisualType fromOrdinal(int ordinal) {
		ChaoVisualType[] values = values();
		return ordinal >= 0 && ordinal < values.length ? values[ordinal] : CHILD;
	}
}
