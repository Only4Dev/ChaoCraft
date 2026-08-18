package com.chaocraft.visual;

/**
 * SA2 Chao body color field, mirrored from Chao Viewer's ColorT/save mapping.
 * This is persistent gameplay appearance data rather than a Visual Lab-only tint.
 */
public enum ChaoColorType {
    NORMAL,
    WHITE,
    GREY,
    BLACK,
    BROWN,
    RED,
    ORANGE,
    YELLOW,
    GREEN,
    LIME_GREEN,
    SKY_BLUE,
    BLUE,
    PURPLE,
    PINK;

    public static ChaoColorType fromOrdinal(int ordinal) {
        ChaoColorType[] values = values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : NORMAL;
    }
}
