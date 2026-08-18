package com.chaocraft.client.render.family;

import com.chaocraft.ChaoCraft;
import com.chaocraft.visual.ChaoAppearanceState;
import net.minecraft.util.Identifier;

/** The three final Chaos Chao bodies selected by alignment in the Viewer. */
public enum ChaoChaosFamily {
    NEUTRAL("neutral_chaos", 0.0F, 5.88F, 0.72F),
    HERO("hero_chaos", 0.0F, 5.4F, 0.0F),
    DARK("dark_chaos", 0.0F, 5.4F, -0.5F);

    private final Identifier model;
    private final float emotionX;
    private final float emotionY;
    private final float emotionZ;

    ChaoChaosFamily(String modelName, float emotionX, float emotionY, float emotionZ) {
        this.model = ChaoCraft.id("models/chao/" + modelName + ".cmesh");
        this.emotionX = emotionX;
        this.emotionY = emotionY;
        this.emotionZ = emotionZ;
    }

    public Identifier model() { return model; }
    public float emotionX() { return emotionX; }
    public float emotionY() { return emotionY; }
    public float emotionZ() { return emotionZ; }

    public static ChaoChaosFamily resolve(ChaoAppearanceState state) {
        if (state.alignment() >= 50.0F) return HERO;
        if (state.alignment() <= -50.0F) return DARK;
        return NEUTRAL;
    }
}
