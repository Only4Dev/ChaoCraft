package com.chaocraft.client.render.material;

import com.chaocraft.visual.ChaoColorType;

/** Exact non-custom BodyCover colors from ChaoMorphController.Update(). */
public final class ChaoBodyColorResolver {
    private ChaoBodyColorResolver() {}

    public static ChaoColor resolve(ChaoColorType type, ChaoColor normalColor) {
        return switch (type) {
            case NORMAL -> normalColor;
            case WHITE -> ChaoColor.WHITE;
            case GREY -> ChaoColor.rgb(128, 128, 128);
            case BLACK -> ChaoColor.rgb(64, 64, 64);
            case BROWN -> ChaoColor.rgb(200, 128, 0);
            case RED -> ChaoColor.rgb(255, 0, 0);
            case ORANGE -> ChaoColor.rgb(255, 128, 0);
            case YELLOW -> ChaoColor.rgb(255, 255, 0);
            case GREEN -> ChaoColor.rgb(0, 255, 0);
            case LIME_GREEN -> ChaoColor.rgb(128, 255, 0);
            case SKY_BLUE -> ChaoColor.rgb(0, 255, 255);
            case BLUE -> ChaoColor.rgb(64, 128, 255);
            case PURPLE -> ChaoColor.rgb(255, 0, 255);
            case PINK -> ChaoColor.rgb(255, 128, 255);
        };
    }
}
