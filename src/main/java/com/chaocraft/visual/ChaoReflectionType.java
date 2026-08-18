package com.chaocraft.visual;

/** Reflection/jewel modes exposed by Chao Viewer's ReflectionT enum. */
public enum ChaoReflectionType {
    NONE,
    SHINY,
    SILVER,
    GOLD,
    GARNET,
    RUBY,
    TOPAZ,
    SAPPHIRE,
    AQUAMARINE,
    AMETHYST,
    PERIDOT,
    EMERALD,
    ONYX,
    PEARL,
    MOON,
    BRIGHT,
    TT_METAL;

    public static ChaoReflectionType fromOrdinal(int ordinal) {
        ChaoReflectionType[] values = values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : NONE;
    }
}
