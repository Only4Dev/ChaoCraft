package com.chaocraft.visual;

/** Exact Chao Viewer ChaoMorphController.Head ordinal mapping. */
public enum ChaoHeadDecoType {
    NONE("None", false),
    EGGSHELL("Eggshell", false),
    COOKING_POT("Cooking Pot", false),
    WOOL_1("Wool 1", true),
    WOOL_2("Wool 2", true),
    WOOL_3("Wool 3", true),
    APPLE("Apple", false),
    PAPER_BAG("Paper Bag", false),
    CARDBOARD("Cardboard", false),
    BUCKET("Bucket", false),
    PUMPKIN("Pumpkin", false),
    POT("Pot", false),
    CAN("Can", false),
    MELON("Melon", false),
    TREE("Tree", false),
    SKULL("Skull", false);

    private final String displayName;
    private final boolean hidesEmotionBall;

    ChaoHeadDecoType(String displayName, boolean hidesEmotionBall) {
        this.displayName = displayName;
        this.hidesEmotionBall = hidesEmotionBall;
    }

    public String displayName() {
        return displayName;
    }

    /** Viewer disables all three normal EmotionBall renderers for Wool heads. */
    public boolean hidesEmotionBall() {
        return hidesEmotionBall;
    }

    public static ChaoHeadDecoType fromOrdinal(int ordinal) {
        ChaoHeadDecoType[] values = values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : NONE;
    }
}
