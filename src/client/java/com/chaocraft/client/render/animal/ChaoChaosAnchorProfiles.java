package com.chaocraft.client.render.animal;

import com.chaocraft.client.render.family.ChaoChaosFamily;
import com.chaocraft.visual.ChaoAnimalParts.Slot;
import org.joml.Vector3f;

/**
 * Fixed decoration anchors used by Viewer SetDecoLoc() for Chaos Chao.
 *
 * <p>Unlike ordinary adults, Chaos types never interpolate PaletteGroup
 * Young/Normal/Swim/Fly/Run/Power attachment locations. Each final Chaos body
 * uses one fixed Palette: NChaos, HChaos or DChaos.</p>
 */
public final class ChaoChaosAnchorProfiles {
    private static final Profile NEUTRAL = new Profile(
            v(0F, 0.21F, -0.17F), // ArmsPosition
            v(0F, 0.35F, -0.04F), // TailPosition
            v(0F, 0.04F, 0F),     // WingsPosition
            v(0F, 0.37F, -0.15F), // mouthPosition
            v(0F, 0.34F, -0.13F), // FacePosition
            v(0F, 0.5F, -0.59F),  // EarsPosition
            v(0F, 0.31F, -0.05F)  // HatPosition
    );

    // Viewer HChaos uses the same decoration anchors as NChaos.
    private static final Profile HERO = NEUTRAL;

    private static final Profile DARK = new Profile(
            v(0F, 0.26F, -0.17F), // ArmsPosition
            v(0F, 0.35F, -0.04F), // TailPosition
            v(0F, 0.04F, 0F),     // WingsPosition
            v(0F, 0.37F, -0.15F), // mouthPosition
            v(0F, 0.34F, -0.13F), // FacePosition
            v(0F, 0.5F, -0.59F),  // EarsPosition
            v(0F, 0.24F, -0.03F)  // HatPosition
    );

    private ChaoChaosAnchorProfiles() {}

    public static Vector3f resolve(ChaoChaosFamily family, Slot slot) {
        Profile p = profile(family);
        return switch (slot) {
            case ARMS -> new Vector3f(p.arms());
            case LEGS -> new Vector3f(); // Viewer has no separate LegsPosition anchor.
            case TAIL -> new Vector3f(p.tail());
            case WINGS -> new Vector3f(p.wings());
            // Viewer SetDecoLoc always uses mouthPosition for Face on Chaos.
            case FACE -> new Vector3f(p.mouth());
            case FOREHEAD -> new Vector3f(p.face());
            case HORNS, EARS -> new Vector3f(p.ears());
        };
    }

    public static Vector3f resolveHat(ChaoChaosFamily family) {
        return new Vector3f(profile(family).hat());
    }

    private static Profile profile(ChaoChaosFamily family) {
        return switch (family) {
            case HERO -> HERO;
            case DARK -> DARK;
            case NEUTRAL -> NEUTRAL;
        };
    }

    private static Vector3f v(float x, float y, float z) {
        return new Vector3f(x, y, z);
    }

    private record Profile(
            Vector3f arms,
            Vector3f tail,
            Vector3f wings,
            Vector3f mouth,
            Vector3f face,
            Vector3f ears,
            Vector3f hat
    ) {}
}
