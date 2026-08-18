package com.chaocraft.client.render.family;

import com.chaocraft.ChaoCraft;
import com.chaocraft.visual.ChaoAppearanceState;
import com.chaocraft.visual.ChaoVisualType;
import net.minecraft.util.Identifier;

/** Exact adult family selected by Chao Viewer from type + alignment. */
public enum ChaoAdultFamily {
    NN(ChaoVisualType.NORMAL, Alignment.NEUTRAL, "neutral_normal", 0.0F, 5.8F, 0.0F),
    HN(ChaoVisualType.NORMAL, Alignment.HERO, "hero_normal", 0.0F, 5.18F, -0.12F),
    DN(ChaoVisualType.NORMAL, Alignment.DARK, "dark_normal", 0.0F, 5.38F, -1.54F),

    NS(ChaoVisualType.SWIM, Alignment.NEUTRAL, "neutral_swim", 0.0F, 5.14F, 0.45F),
    HS(ChaoVisualType.SWIM, Alignment.HERO, "hero_swim", 0.0F, 5.37F, 0.17F),
    DS(ChaoVisualType.SWIM, Alignment.DARK, "dark_swim", 0.0F, 5.62F, -0.75F),

    NF(ChaoVisualType.FLY, Alignment.NEUTRAL, "neutral_fly", 0.0F, 5.68F, 0.64F),
    HF(ChaoVisualType.FLY, Alignment.HERO, "hero_fly", 0.0F, 5.23F, 0.0F),
    DF(ChaoVisualType.FLY, Alignment.DARK, "dark_fly", 0.0F, 5.23F, 0.0F),

    NR(ChaoVisualType.RUN, Alignment.NEUTRAL, "neutral_run", 0.0F, 6.0F, 0.0F),
    HR(ChaoVisualType.RUN, Alignment.HERO, "hero_run", 0.0F, 5.3F, 0.27F),
    DR(ChaoVisualType.RUN, Alignment.DARK, "dark_run", 0.0F, 5.2F, 0.0F),

    NP(ChaoVisualType.POWER, Alignment.NEUTRAL, "neutral_power", 0.0F, 5.75F, 0.78F),
    HP(ChaoVisualType.POWER, Alignment.HERO, "hero_power", 0.0F, 5.64F, -0.19F),
    DP(ChaoVisualType.POWER, Alignment.DARK, "dark_power", 0.0F, 6.0F, 0.26F);

    private final ChaoVisualType type;
    private final Alignment alignment;
    private final Identifier model;
    private final float emotionX;
    private final float emotionY;
    private final float emotionZ;

    ChaoAdultFamily(ChaoVisualType type, Alignment alignment, String modelName,
            float emotionX, float emotionY, float emotionZ) {
        this.type = type;
        this.alignment = alignment;
        this.model = ChaoCraft.id("models/chao/" + modelName + ".cmesh");
        this.emotionX = emotionX;
        this.emotionY = emotionY;
        this.emotionZ = emotionZ;
    }

    public ChaoVisualType type() { return type; }
    public Alignment alignment() { return alignment; }
    public Identifier model() { return model; }
    public float emotionX() { return emotionX; }
    public float emotionY() { return emotionY; }
    public float emotionZ() { return emotionZ; }

    public static ChaoAdultFamily resolve(ChaoAppearanceState state) {
        ChaoVisualType type = state.type().isChild() ? ChaoVisualType.NORMAL : state.type();
        Alignment alignment = state.alignment() >= 50.0F
                ? Alignment.HERO
                : state.alignment() <= -50.0F ? Alignment.DARK : Alignment.NEUTRAL;
        for (ChaoAdultFamily family : values()) {
            if (family.type == type && family.alignment == alignment) {
                return family;
            }
        }
        return NN;
    }

    public enum Alignment { NEUTRAL, HERO, DARK }
}
