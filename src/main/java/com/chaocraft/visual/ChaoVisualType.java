package com.chaocraft.visual;

/** Visual family selected by the Chao appearance pipeline. */
public enum ChaoVisualType {
    CHILD,
    NORMAL,
    SWIM,
    FLY,
    RUN,
    POWER,
    CHAOS;

    public boolean isChild() {
        return this == CHILD;
    }

    public static ChaoVisualType fromOrdinal(int ordinal) {
        ChaoVisualType[] values = values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : CHILD;
    }
}
