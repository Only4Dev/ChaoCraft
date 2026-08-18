package com.chaocraft.client.render.material;

/** Resolved Chao Viewer color channels used by ChaoMaterial emulation. */
public record ChaoPaletteState(
        ChaoColor base,
        ChaoColor body,
        ChaoColor belly,
        ChaoColor extra,
        ChaoColor extra2,
        ChaoColor extra3,
        ChaoColor horns,
        ChaoColor wings,
        ChaoColor wingsBase,
        ChaoColor wingsExtra,
        ChaoColor bodyCover,
        ChaoColor wingsCover,
        ChaoColor emotionBall
) {
    /** Compatibility constructor for the Child/Normal pipeline from CP06. */
    public ChaoPaletteState(ChaoColor base, ChaoColor body, ChaoColor belly,
            ChaoColor extra, ChaoColor extra2, ChaoColor horns,
            ChaoColor wings, ChaoColor wingsBase, ChaoColor bodyCover,
            ChaoColor wingsCover, ChaoColor emotionBall) {
        this(base, body, belly, extra, extra2, ChaoColor.TRANSPARENT,
                horns, wings, wingsBase, ChaoColor.TRANSPARENT,
                bodyCover, wingsCover, emotionBall);
    }
}
