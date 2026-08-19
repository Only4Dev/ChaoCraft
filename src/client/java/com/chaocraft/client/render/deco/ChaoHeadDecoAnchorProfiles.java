package com.chaocraft.client.render.deco;

import com.chaocraft.client.render.family.ChaoAdultFamily;
import com.chaocraft.visual.ChaoAppearanceState;
import org.joml.Vector3f;

import java.util.EnumMap;
import java.util.Map;

/** Viewer Palette.HatPosition interpolation for the independent HeadDeco slot. */
public final class ChaoHeadDecoAnchorProfiles {
    private static final Map<ChaoAdultFamily, Profile> PROFILES = new EnumMap<>(ChaoAdultFamily.class);

    static {
        PROFILES.put(ChaoAdultFamily.NN, new Profile(v(0F,-0.07F,0F), v(0F,-0.07F,0F), v(0F,-0.07F,0F), v(0F,-0.07F,0F), v(0F,-0.07F,0F), v(0F,-0.07F,0F)));
        PROFILES.put(ChaoAdultFamily.HN, new Profile(v(0F,-0.07F,0F), v(0F,0.02F,0F), v(0F,0.02F,0F), v(0F,0.02F,0F), v(0F,0.02F,0F), v(0F,0F,0F)));
        PROFILES.put(ChaoAdultFamily.DN, new Profile(v(0F,-0.02F,0F), v(0F,0.1F,0F), v(0F,0.1F,0F), v(0F,0.1F,0F), v(0F,0.1F,0F), v(0F,0.05F,0F)));
        PROFILES.put(ChaoAdultFamily.NS, new Profile(v(0F,-0.2F,0F), v(0F,0.13F,0F), v(0F,0.01F,0F), v(0F,0.22F,0F), v(0F,0.15F,0F), v(0F,0.29F,0F)));
        PROFILES.put(ChaoAdultFamily.HS, new Profile(v(0F,0F,0F), v(0F,0F,0F), v(0F,0F,0F), v(0F,0.14F,0.23F), v(0F,0.14F,0.04F), v(0F,0.26F,0.02F)));
        PROFILES.put(ChaoAdultFamily.DS, new Profile(v(0F,0.06F,0.38F), v(0F,0.18F,0.21F), v(0F,0.13F,0.21F), v(0F,0.01F,0.38F), v(0F,0.11F,0.21F), v(0F,0.11F,0.21F)));
        PROFILES.put(ChaoAdultFamily.NF, new Profile(v(0F,-0.21F,0F), v(0F,0F,0.32F), v(0F,-0.03F,0.32F), v(0F,0.02F,0.32F), v(0F,0.02F,0.37F), v(0F,0.07F,0.47F)));
        PROFILES.put(ChaoAdultFamily.HF, new Profile(v(0F,-0.13F,-0.08F), v(0F,0.08F,-0.08F), v(0F,-0.06F,0.06F), v(0F,0.1F,-0.06F), v(0F,0.06F,-0.06F), v(0F,0.14F,-0.06F)));
        PROFILES.put(ChaoAdultFamily.DF, new Profile(v(0F,0.01F,0.16F), v(0F,0.03F,0.16F), v(0F,-0.01F,0.16F), v(0F,0.05F,0.12F), v(0F,0.05F,0.12F), v(0F,0.05F,0.12F)));
        PROFILES.put(ChaoAdultFamily.NR, new Profile(v(0F,0.14F,-0.22F), v(0F,0.14F,-0.22F), v(0F,0.08F,0.08F), v(0F,0.2F,-0.22F), v(0F,0.14F,-0.22F), v(0F,0.14F,-0.22F)));
        PROFILES.put(ChaoAdultFamily.HR, new Profile(v(0F,-0.14F,-0.06F), v(0F,-0.04F,-0.06F), v(0F,-0.04F,-0.06F), v(0F,-0.04F,-0.06F), v(0F,0.08F,-0.06F), v(0F,0.04F,-0.06F)));
        PROFILES.put(ChaoAdultFamily.DR, new Profile(v(0F,-0.12F,-0.04F), v(0F,-0.1F,-0.04F), v(0F,-0.1F,-0.04F), v(0F,-0.1F,-0.04F), v(0F,0.1F,-0.06F), v(0F,-0.1F,-0.04F)));
        PROFILES.put(ChaoAdultFamily.NP, new Profile(v(0F,0.06F,-0.06F), v(0F,0.16F,-0.06F), v(0F,0.16F,0.06F), v(0F,0.12F,0.06F), v(0F,0.3F,-0.02F), v(0F,0.19F,-0.04F)));
        PROFILES.put(ChaoAdultFamily.HP, new Profile(v(0F,0.02F,0F), v(0F,0.18F,0F), v(0F,0.04F,0F), v(0F,0.04F,0F), v(0F,0.18F,0F), v(0F,0.18F,0F)));
        PROFILES.put(ChaoAdultFamily.DP, new Profile(v(0F,-0.01F,-0.04F), v(0F,0.09F,-0.04F), v(0F,0.09F,-0.04F), v(0F,0.1F,-0.04F), v(0F,0.22F,-0.04F), v(0F,-0.01F,-0.04F)));
    }

    private ChaoHeadDecoAnchorProfiles() {}

    public static Vector3f resolve(ChaoAdultFamily family, ChaoAppearanceState state) {
        Profile p = PROFILES.get(family);
        if (p == null) return new Vector3f();

        float age = state.age();
        return new Vector3f(p.young()).mul(Math.max(0F, 1F - age))
                .fma((state.normal() / 100F) * age, p.normal())
                .fma((state.swim() / 100F) * age, p.swim())
                .fma((state.fly() / 100F) * age, p.fly())
                .fma((state.run() / 100F) * age, p.run())
                .fma((state.power() / 100F) * age, p.power());
    }

    private static Vector3f v(float x, float y, float z) {
        return new Vector3f(x, y, z);
    }

    private record Profile(Vector3f young, Vector3f normal, Vector3f swim,
                           Vector3f fly, Vector3f run, Vector3f power) {}
}
