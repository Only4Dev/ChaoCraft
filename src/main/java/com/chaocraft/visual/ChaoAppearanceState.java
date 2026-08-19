package com.chaocraft.visual;

import com.chaocraft.visual.ChaoAnimalParts.Slot;

/**
 * Loader-agnostic visual state consumed by the Chao appearance pipeline.
 * Values intentionally mirror Chao Viewer controls/data so simulation can stay
 * server-authoritative while clients render the same canonical appearance.
 */
public record ChaoAppearanceState(
        ChaoVisualType type,
        float age,
        float alignment,
        float swim,
        float fly,
        float run,
        float power,
        ChaoColorType colorType,
        boolean monotone,
        ChaoReflectionType reflectionType,
        ChaoAnimalParts animalParts,
        ChaoHeadDecoType headDeco,
        boolean customEyes,
        int eyes,
        int eyelid,
        int mouth,
        boolean customMouth,
        int mouthMid,
        int mouthSide,
        boolean customEmotionBall,
        boolean neutralBall,
        boolean heroBall,
        boolean darkBall,
        boolean tiltedHalo
) {
    public static final ChaoAppearanceState DEFAULT = new ChaoAppearanceState(
            ChaoVisualType.CHILD,
            0.0F, 0.0F,
            0.0F, 0.0F, 0.0F, 0.0F,
            ChaoColorType.NORMAL, false,
            ChaoReflectionType.NONE, ChaoAnimalParts.NONE, ChaoHeadDecoType.NONE,
            false, 0, 0,
            0, false, 0, 0,
            false, true, false, false,
            false
    );

    public ChaoAppearanceState {
        if (type == null) type = ChaoVisualType.CHILD;
        if (colorType == null) colorType = ChaoColorType.NORMAL;
        if (reflectionType == null) reflectionType = ChaoReflectionType.NONE;
        if (animalParts == null) animalParts = ChaoAnimalParts.NONE;
        if (headDeco == null) headDeco = ChaoHeadDecoType.NONE;

        age = finiteClamp(age, 0.0F, 1.0F, 0.0F);
        alignment = finiteClamp(alignment, -100.0F, 100.0F, 0.0F);
        swim = finiteClamp(swim, 0.0F, 100.0F, 0.0F);
        fly = finiteClamp(fly, 0.0F, 100.0F, 0.0F);
        run = finiteClamp(run, 0.0F, 100.0F, 0.0F);
        power = finiteClamp(power, 0.0F, 100.0F, 0.0F);

        float evolutionTotal = swim + fly + run + power;
        if (evolutionTotal > 100.0001F) {
            float scale = 100.0F / evolutionTotal;
            swim *= scale;
            fly *= scale;
            run *= scale;
            power *= scale;
        }

        eyes = clamp(eyes, 0, 12);
        eyelid = clamp(eyelid, 0, 2);
        mouth = clamp(mouth, 0, 12);
        mouthMid = clamp(mouthMid, 0, 18);
        mouthSide = clamp(mouthSide, 0, 18);
    }

    /** Compatibility constructor for callers that predate HeadDeco. */
    public ChaoAppearanceState(
            ChaoVisualType type, float age, float alignment,
            float swim, float fly, float run, float power,
            ChaoColorType colorType, boolean monotone,
            ChaoReflectionType reflectionType, ChaoAnimalParts animalParts,
            boolean customEyes, int eyes, int eyelid,
            int mouth, boolean customMouth, int mouthMid, int mouthSide,
            boolean customEmotionBall, boolean neutralBall, boolean heroBall, boolean darkBall,
            boolean tiltedHalo
    ) {
        this(type, age, alignment, swim, fly, run, power,
                colorType, monotone, reflectionType, animalParts, ChaoHeadDecoType.NONE,
                customEyes, eyes, eyelid, mouth, customMouth, mouthMid, mouthSide,
                customEmotionBall, neutralBall, heroBall, darkBall, tiltedHalo);
    }

    /** Compatibility constructor for CP08/CP09 callers that predate reflection/animal parts. */
    public ChaoAppearanceState(
            ChaoVisualType type, float age, float alignment,
            float swim, float fly, float run, float power,
            ChaoColorType colorType, boolean monotone,
            boolean customEyes, int eyes, int eyelid,
            int mouth, boolean customMouth, int mouthMid, int mouthSide,
            boolean customEmotionBall, boolean neutralBall, boolean heroBall, boolean darkBall,
            boolean tiltedHalo
    ) {
        this(type, age, alignment, swim, fly, run, power,
                colorType, monotone, ChaoReflectionType.NONE, ChaoAnimalParts.NONE, ChaoHeadDecoType.NONE,
                customEyes, eyes, eyelid, mouth, customMouth, mouthMid, mouthSide,
                customEmotionBall, neutralBall, heroBall, darkBall, tiltedHalo);
    }

    /** Compatibility constructor for old visual-only callers. */
    public ChaoAppearanceState(ChaoVisualType type, float age, float alignment,
            float swim, float fly, float run, float power) {
        this(type, age, alignment, swim, fly, run, power,
                ChaoColorType.NORMAL, false,
                ChaoReflectionType.NONE, ChaoAnimalParts.NONE, ChaoHeadDecoType.NONE,
                false, 0, 0,
                0, false, 0, 0,
                false, true, false, false,
                false);
    }

    public ChaoAppearanceState(float age, float alignment, float swim, float fly, float run, float power) {
        this(ChaoVisualType.CHILD, age, alignment, swim, fly, run, power);
    }

    public float normal() {
        return Math.max(0.0F, 100.0F - swim - fly - run - power);
    }

    public int resolvedEyes() {
        if (customEyes) return eyes;
        if (type == ChaoVisualType.CHILD) return 0;
        if (type == ChaoVisualType.CHAOS) {
            if (alignment <= -50.0F) return 11;
            if (alignment >= 50.0F) return 12;
            return 10;
        }
        if (alignment <= -50.0F) return 1;
        if (alignment >= 50.0F) return 2;
        return 0;
    }

    public int resolvedEyelid() {
        if (customEyes) return eyelid;
        return type != ChaoVisualType.CHILD && alignment <= -50.0F ? 1 : 0;
    }

    public int resolvedMouthMid() {
        if (customMouth) return mouthMid;
        return switch (mouth) {
            case 1 -> 1;
            case 2 -> 3;
            case 3 -> 4;
            case 4 -> 5;
            case 5 -> 6;
            case 6 -> 7;
            case 7 -> 8;
            case 8 -> 10;
            case 9 -> 12;
            case 10 -> 14;
            case 11 -> 15;
            case 12 -> 17;
            default -> 0;
        };
    }

    public int resolvedMouthSide() {
        if (customMouth) return mouthSide;
        return switch (mouth) {
            case 1 -> 2;
            case 7 -> 9;
            case 8 -> 11;
            case 9 -> 13;
            case 11 -> 16;
            case 12 -> 18;
            default -> 0;
        };
    }

    public ChaoAppearanceState withEvolution(EvolutionChannel channel, float requestedValue) {
        float value = clamp(requestedValue, 0.0F, 100.0F);
        float newSwim = swim;
        float newFly = fly;
        float newRun = run;
        float newPower = power;
        switch (channel) {
            case SWIM -> newSwim = value;
            case FLY -> newFly = value;
            case RUN -> newRun = value;
            case POWER -> newPower = value;
        }

        float otherTotal = switch (channel) {
            case SWIM -> newFly + newRun + newPower;
            case FLY -> newSwim + newRun + newPower;
            case RUN -> newSwim + newFly + newPower;
            case POWER -> newSwim + newFly + newRun;
        };
        float remaining = 100.0F - value;
        if (otherTotal > remaining && otherTotal > 0.0F) {
            float scale = remaining / otherTotal;
            switch (channel) {
                case SWIM -> { newFly *= scale; newRun *= scale; newPower *= scale; }
                case FLY -> { newSwim *= scale; newRun *= scale; newPower *= scale; }
                case RUN -> { newSwim *= scale; newFly *= scale; newPower *= scale; }
                case POWER -> { newSwim *= scale; newFly *= scale; newRun *= scale; }
            }
        }
        return copy(type, age, alignment, newSwim, newFly, newRun, newPower,
                colorType, monotone, reflectionType, animalParts, headDeco,
                customEyes, eyes, eyelid, mouth, customMouth, mouthMid, mouthSide,
                customEmotionBall, neutralBall, heroBall, darkBall, tiltedHalo);
    }

    public ChaoAppearanceState withType(ChaoVisualType value) {
        return copy(value, age, alignment, swim, fly, run, power, colorType, monotone, reflectionType, animalParts, headDeco,
                customEyes, eyes, eyelid, mouth, customMouth, mouthMid, mouthSide,
                customEmotionBall, neutralBall, heroBall, darkBall, tiltedHalo);
    }

    public ChaoAppearanceState withAge(float value) {
        return copy(type, value, alignment, swim, fly, run, power, colorType, monotone, reflectionType, animalParts, headDeco,
                customEyes, eyes, eyelid, mouth, customMouth, mouthMid, mouthSide,
                customEmotionBall, neutralBall, heroBall, darkBall, tiltedHalo);
    }

    public ChaoAppearanceState withAlignment(float value) {
        return copy(type, age, value, swim, fly, run, power, colorType, monotone, reflectionType, animalParts, headDeco,
                customEyes, eyes, eyelid, mouth, customMouth, mouthMid, mouthSide,
                customEmotionBall, neutralBall, heroBall, darkBall, tiltedHalo);
    }

    public ChaoAppearanceState withAutoEyes() {
        return copy(type, age, alignment, swim, fly, run, power, colorType, monotone, reflectionType, animalParts, headDeco,
                false, eyes, eyelid, mouth, customMouth, mouthMid, mouthSide,
                customEmotionBall, neutralBall, heroBall, darkBall, tiltedHalo);
    }

    public ChaoAppearanceState withEyes(int value) {
        return copy(type, age, alignment, swim, fly, run, power, colorType, monotone, reflectionType, animalParts, headDeco,
                true, value, eyelid, mouth, customMouth, mouthMid, mouthSide,
                customEmotionBall, neutralBall, heroBall, darkBall, tiltedHalo);
    }

    public ChaoAppearanceState withEyelid(int value) {
        return copy(type, age, alignment, swim, fly, run, power, colorType, monotone, reflectionType, animalParts, headDeco,
                true, eyes, value, mouth, customMouth, mouthMid, mouthSide,
                customEmotionBall, neutralBall, heroBall, darkBall, tiltedHalo);
    }

    public ChaoAppearanceState withMouth(int value) {
        return copy(type, age, alignment, swim, fly, run, power, colorType, monotone, reflectionType, animalParts, headDeco,
                customEyes, eyes, eyelid, value, false, mouthMid, mouthSide,
                customEmotionBall, neutralBall, heroBall, darkBall, tiltedHalo);
    }

    public ChaoAppearanceState withAdvancedMouth(int mid, int side) {
        return copy(type, age, alignment, swim, fly, run, power, colorType, monotone, reflectionType, animalParts, headDeco,
                customEyes, eyes, eyelid, mouth, true, mid, side,
                customEmotionBall, neutralBall, heroBall, darkBall, tiltedHalo);
    }

    public ChaoAppearanceState withStandardMouthMode() {
        return copy(type, age, alignment, swim, fly, run, power, colorType, monotone, reflectionType, animalParts, headDeco,
                customEyes, eyes, eyelid, mouth, false, mouthMid, mouthSide,
                customEmotionBall, neutralBall, heroBall, darkBall, tiltedHalo);
    }

    public ChaoAppearanceState withAutoEmotionBall() {
        return copy(type, age, alignment, swim, fly, run, power, colorType, monotone, reflectionType, animalParts, headDeco,
                customEyes, eyes, eyelid, mouth, customMouth, mouthMid, mouthSide,
                false, neutralBall, heroBall, darkBall, tiltedHalo);
    }

    public ChaoAppearanceState withCustomEmotionBalls(boolean neutral, boolean hero, boolean dark) {
        return copy(type, age, alignment, swim, fly, run, power, colorType, monotone, reflectionType, animalParts, headDeco,
                customEyes, eyes, eyelid, mouth, customMouth, mouthMid, mouthSide,
                true, neutral, hero, dark, tiltedHalo);
    }

    public ChaoAppearanceState withTiltedHalo(boolean value) {
        return copy(type, age, alignment, swim, fly, run, power, colorType, monotone, reflectionType, animalParts, headDeco,
                customEyes, eyes, eyelid, mouth, customMouth, mouthMid, mouthSide,
                customEmotionBall, neutralBall, heroBall, darkBall, value);
    }

    public ChaoAppearanceState withColorType(ChaoColorType value) {
        return copy(type, age, alignment, swim, fly, run, power, value, monotone, reflectionType, animalParts, headDeco,
                customEyes, eyes, eyelid, mouth, customMouth, mouthMid, mouthSide,
                customEmotionBall, neutralBall, heroBall, darkBall, tiltedHalo);
    }

    public ChaoAppearanceState withMonotone(boolean value) {
        return copy(type, age, alignment, swim, fly, run, power, colorType, value, reflectionType, animalParts, headDeco,
                customEyes, eyes, eyelid, mouth, customMouth, mouthMid, mouthSide,
                customEmotionBall, neutralBall, heroBall, darkBall, tiltedHalo);
    }

    public ChaoAppearanceState withReflectionType(ChaoReflectionType value) {
        return copy(type, age, alignment, swim, fly, run, power, colorType, monotone, value, animalParts, headDeco,
                customEyes, eyes, eyelid, mouth, customMouth, mouthMid, mouthSide,
                customEmotionBall, neutralBall, heroBall, darkBall, tiltedHalo);
    }

    public ChaoAppearanceState withAnimalPart(Slot slot, ChaoAnimalType animal) {
        return withAnimalParts(animalParts.with(slot, animal));
    }

    public ChaoAppearanceState withAnimalParts(ChaoAnimalParts value) {
        return copy(type, age, alignment, swim, fly, run, power, colorType, monotone, reflectionType, value, headDeco,
                customEyes, eyes, eyelid, mouth, customMouth, mouthMid, mouthSide,
                customEmotionBall, neutralBall, heroBall, darkBall, tiltedHalo);
    }

    public ChaoAppearanceState clearAnimalParts() {
        return withAnimalParts(ChaoAnimalParts.NONE);
    }

    public ChaoAppearanceState withHeadDeco(ChaoHeadDecoType value) {
        return copy(type, age, alignment, swim, fly, run, power,
                colorType, monotone, reflectionType, animalParts,
                value == null ? ChaoHeadDecoType.NONE : value,
                customEyes, eyes, eyelid, mouth, customMouth, mouthMid, mouthSide,
                customEmotionBall, neutralBall, heroBall, darkBall, tiltedHalo);
    }

    public ChaoAppearanceState resetFace() {
        return copy(type, age, alignment, swim, fly, run, power, colorType, monotone, reflectionType, animalParts, headDeco,
                false, 0, 0, 0, false, 0, 0,
                customEmotionBall, neutralBall, heroBall, darkBall, tiltedHalo);
    }

    private static ChaoAppearanceState copy(
            ChaoVisualType type, float age, float alignment,
            float swim, float fly, float run, float power,
            ChaoColorType colorType, boolean monotone,
            ChaoReflectionType reflectionType, ChaoAnimalParts animalParts, ChaoHeadDecoType headDeco,
            boolean customEyes, int eyes, int eyelid,
            int mouth, boolean customMouth, int mouthMid, int mouthSide,
            boolean customEmotionBall, boolean neutralBall, boolean heroBall, boolean darkBall,
            boolean tiltedHalo
    ) {
        return new ChaoAppearanceState(type, age, alignment, swim, fly, run, power,
                colorType, monotone, reflectionType, animalParts, headDeco,
                customEyes, eyes, eyelid, mouth, customMouth, mouthMid, mouthSide,
                customEmotionBall, neutralBall, heroBall, darkBall, tiltedHalo);
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static float finiteClamp(float value, float min, float max, float fallback) {
        return Float.isFinite(value) ? clamp(value, min, max) : fallback;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public enum EvolutionChannel {
        SWIM,
        FLY,
        RUN,
        POWER
    }
}
