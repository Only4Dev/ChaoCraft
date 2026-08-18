package com.chaocraft.client.render.cache;

import com.chaocraft.visual.ChaoAppearanceState;
import com.chaocraft.visual.ChaoVisualType;

/**
 * Quantizes continuous authoritative Chao state into visually meaningful steps.
 *
 * <p>The server may evolve biological values at arbitrary precision, but the
 * renderer must not rebuild large GPU buffers for imperceptible sub-step changes.
 * Viewer-facing controls already expose alignment/evolution as integer values
 * and age at 0.01 precision, so these buckets preserve the current visual QA
 * fidelity while preventing future per-tick simulation drift from thrashing the
 * render cache.</p>
 */
public final class ChaoRenderStateQuantizer {
    private ChaoRenderStateQuantizer() {
    }

    public static ChaoAppearanceState quantize(ChaoAppearanceState state) {
        float age = Math.round(state.age() * 100.0F) / 100.0F;
        float alignment = Math.round(state.alignment());
        float swim = Math.round(state.swim());
        float fly = Math.round(state.fly());
        float run = Math.round(state.run());
        float power = Math.round(state.power());

        // Chaos Chao has a fixed final mesh (SizeDown only in the source); age
        // and second-evolution sliders are not visual inputs. Canonicalizing
        // ignored fields prevents harmless stat/debug changes from rebuilding VBOs.
        if (state.type() == ChaoVisualType.CHAOS) {
            age = 1.0F;
            swim = 0.0F;
            fly = 0.0F;
            run = 0.0F;
            power = 0.0F;
        }

        return new ChaoAppearanceState(
                state.type(), age, alignment, swim, fly, run, power,
                state.colorType(), state.monotone(), state.reflectionType(), state.animalParts(),
                state.customEyes(), state.eyes(), state.eyelid(),
                state.mouth(), state.customMouth(), state.mouthMid(), state.mouthSide(),
                state.customEmotionBall(), state.neutralBall(), state.heroBall(), state.darkBall(),
                state.tiltedHalo()
        );
    }
}
