package com.chaocraft.client.render.animal;

import com.chaocraft.client.render.family.ChaoAdultFamily;
import com.chaocraft.visual.ChaoAnimalParts.Slot;
import com.chaocraft.visual.ChaoAppearanceState;
import org.joml.Vector3f;
import java.util.EnumMap;
import java.util.Map;

/** Adult animal attachment anchors generated from Palettes.cs / CalcPartsLocations(). */
public final class ChaoAnimalAnchorProfiles {
    private static final Map<ChaoAdultFamily, Profile> PROFILES = new EnumMap<>(ChaoAdultFamily.class);

    static {
        PROFILES.put(ChaoAdultFamily.NN, new Profile(false, false,
                new Anchors(v(0.0F,0.0F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.09F,-0.1F),v(0.0F,0.04F,-0.15F),v(0.0F,0.0F,0.0F),v(0.0F,0.03F,-0.36F)),
                new Anchors(v(0.0F,0.0F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.24F,-0.1F),v(0.0F,0.04F,-0.15F),v(0.0F,0.0F,0.0F),v(0.0F,0.03F,-0.36F)),
                new Anchors(v(0.0F,0.0F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.24F,-0.1F),v(0.0F,0.04F,-0.15F),v(0.0F,0.0F,0.0F),v(0.0F,0.03F,-0.36F)),
                new Anchors(v(0.0F,0.0F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.24F,-0.1F),v(0.0F,0.04F,-0.15F),v(0.0F,0.0F,0.0F),v(0.0F,0.03F,-0.36F)),
                new Anchors(v(0.0F,0.0F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.24F,-0.1F),v(0.0F,0.04F,-0.15F),v(0.0F,0.0F,0.0F),v(0.0F,0.03F,-0.36F)),
                new Anchors(v(0.0F,0.0F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.12F,-0.1F),v(0.0F,0.04F,-0.15F),v(0.0F,0.0F,0.0F),v(0.0F,0.03F,-0.36F))));
        PROFILES.put(ChaoAdultFamily.HN, new Profile(false, false,
                new Anchors(v(0.0F,0.0F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.12F,0.1F),v(0.0F,-0.03F,-0.07F),v(0.0F,0.0F,0.0F),v(0.0F,0.11F,-0.36F)),
                new Anchors(v(0.0F,0.11F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.15F,0.1F),v(0.0F,0.13F,-0.08F),v(0.0F,0.0F,0.0F),v(0.0F,0.2F,-0.36F)),
                new Anchors(v(0.0F,0.11F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.15F,0.1F),v(0.0F,0.13F,-0.08F),v(0.0F,0.0F,0.0F),v(0.0F,0.2F,-0.36F)),
                new Anchors(v(0.0F,0.11F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.15F,0.1F),v(0.0F,0.13F,-0.08F),v(0.0F,0.0F,0.0F),v(0.0F,0.2F,-0.36F)),
                new Anchors(v(0.0F,0.11F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.15F,0.1F),v(0.0F,0.13F,-0.08F),v(0.0F,0.0F,0.0F),v(0.0F,0.2F,-0.36F)),
                new Anchors(v(0.0F,0.11F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.15F,0.1F),v(0.0F,0.13F,-0.08F),v(0.0F,0.0F,0.0F),v(0.0F,0.2F,-0.36F))));
        PROFILES.put(ChaoAdultFamily.DN, new Profile(false, false,
                new Anchors(v(0.0F,0.0F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.21F,-0.1F),v(0.0F,0.04F,-0.15F),v(0.0F,0.0F,0.0F),v(0.0F,0.18F,-0.36F)),
                new Anchors(v(0.0F,0.13F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.21F,-0.1F),v(0.0F,0.24F,-0.12F),v(0.0F,0.0F,0.0F),v(0.0F,0.37F,-0.36F)),
                new Anchors(v(0.0F,0.13F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.21F,-0.1F),v(0.0F,0.24F,-0.12F),v(0.0F,0.0F,0.0F),v(0.0F,0.28F,-0.36F)),
                new Anchors(v(0.0F,0.13F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.21F,-0.25F),v(0.0F,0.24F,-0.12F),v(0.0F,0.0F,0.0F),v(0.0F,0.28F,-0.36F)),
                new Anchors(v(0.0F,0.13F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.21F,-0.1F),v(0.0F,0.24F,-0.12F),v(0.0F,0.0F,0.0F),v(0.0F,0.28F,-0.36F)),
                new Anchors(v(0.0F,0.13F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.21F,-0.1F),v(0.0F,0.11F,-0.12F),v(0.0F,0.0F,0.0F),v(0.0F,0.19F,-0.36F))));
        PROFILES.put(ChaoAdultFamily.NS, new Profile(false, true,
                new Anchors(v(0.0F,-0.13F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.15F,-0.25F),v(0.0F,-0.22F,-0.01F),v(0.0F,-0.09F,0.0F),v(0.0F,-0.07F,-0.29F)),
                new Anchors(v(0.0F,0.22F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.42F,-0.25F),v(0.0F,0.18F,0.06F),v(0.0F,0.25F,0.06F),v(0.0F,0.33F,-0.09F)),
                new Anchors(v(0.0F,0.06F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.11F,-0.59F),v(0.0F,0.04F,-0.12F),v(0.0F,0.14F,-0.12F),v(0.0F,0.11F,-0.34F)),
                new Anchors(v(0.0F,0.21F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.44F,-0.21F),v(0.0F,0.25F,0.19F),v(0.0F,0.33F,0.19F),v(0.0F,0.29F,0.0F)),
                new Anchors(v(0.0F,0.14F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.08F,-0.09F),v(0.0F,0.0F,-0.29F),v(0.0F,0.2F,-0.37F),v(0.0F,0.2F,-0.52F)),
                new Anchors(v(0.0F,0.3F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,-0.01F,-0.25F),v(0.0F,0.29F,-0.24F),v(0.0F,0.37F,-0.31F),v(0.0F,0.45F,-0.59F))));
        PROFILES.put(ChaoAdultFamily.HS, new Profile(false, false,
                new Anchors(v(0.0F,0.0F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.01F,0.15F),v(0.0F,0.0F,0.0F),v(0.0F,0.0F,0.0F)),
                new Anchors(v(0.0F,0.0F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.01F,0.05F),v(0.0F,0.0F,0.0F),v(0.0F,0.05F,-0.07F)),
                new Anchors(v(0.0F,0.0F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,-0.12F,-0.18F),v(0.0F,0.01F,0.12F),v(0.0F,0.0F,0.0F),v(0.0F,-0.05F,0.02F)),
                new Anchors(v(0.0F,0.11F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.16F,0.27F),v(0.0F,0.0F,0.0F),v(0.0F,0.26F,0.04F)),
                new Anchors(v(0.0F,0.15F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.14F,-0.06F),v(0.0F,0.16F,0.08F),v(0.0F,0.0F,0.0F),v(0.0F,0.24F,0.06F)),
                new Anchors(v(0.0F,0.31F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.31F,0.02F),v(0.0F,0.0F,0.0F),v(0.0F,0.29F,-0.08F))));
        PROFILES.put(ChaoAdultFamily.DS, new Profile(false, false,
                new Anchors(v(0.0F,0.08F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.18F,-0.08F),v(0.0F,0.1F,0.32F),v(0.0F,0.0F,0.0F),v(0.0F,0.2F,-0.01F)),
                new Anchors(v(0.0F,0.14F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.18F,-0.08F),v(0.0F,0.22F,0.26F),v(0.0F,0.0F,0.0F),v(0.0F,0.45F,-0.01F)),
                new Anchors(v(0.0F,0.14F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,-0.02F,-0.22F),v(0.0F,0.22F,0.26F),v(0.0F,0.0F,0.0F),v(0.0F,0.45F,-0.01F)),
                new Anchors(v(0.0F,0.09F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.18F,-0.08F),v(0.0F,0.08F,0.3F),v(0.0F,0.0F,0.0F),v(0.0F,0.2F,0.06F)),
                new Anchors(v(0.0F,0.14F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.18F,-0.08F),v(0.0F,0.18F,0.18F),v(0.0F,0.0F,0.0F),v(0.0F,0.33F,-0.14F)),
                new Anchors(v(0.0F,0.14F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.18F,-0.08F),v(0.0F,0.18F,0.18F),v(0.0F,0.0F,0.0F),v(0.0F,0.33F,-0.14F))));
        PROFILES.put(ChaoAdultFamily.NF, new Profile(true, false,
                new Anchors(v(0.0F,-0.16F,0.0F),v(0.0F,-0.08F,0.1F),v(0.0F,0.18F,-0.21F),v(0.0F,-0.24F,-0.12F),v(0.0F,0.0F,0.0F),v(0.0F,-0.07F,-0.2F)),
                new Anchors(v(0.0F,0.09F,0.0F),v(0.0F,0.19F,0.24F),v(0.0F,0.38F,-0.36F),v(0.0F,-0.02F,0.23F),v(0.0F,0.0F,0.0F),v(0.0F,0.07F,0.03F)),
                new Anchors(v(0.0F,0.09F,0.0F),v(0.0F,0.19F,0.24F),v(0.0F,0.33F,-0.39F),v(0.0F,-0.05F,0.38F),v(0.0F,0.0F,0.0F),v(0.0F,0.07F,0.22F)),
                new Anchors(v(0.0F,0.09F,0.0F),v(0.0F,0.19F,0.24F),v(0.0F,0.25F,-0.14F),v(0.0F,0.02F,0.29F),v(0.0F,0.0F,0.0F),v(0.0F,0.07F,0.22F)),
                new Anchors(v(0.0F,0.04F,0.0F),v(0.0F,0.19F,0.5F),v(0.0F,0.36F,-0.03F),v(0.0F,0.03F,0.49F),v(0.0F,0.0F,0.0F),v(0.0F,0.07F,0.31F)),
                new Anchors(v(0.0F,0.21F,0.0F),v(0.0F,0.19F,0.27F),v(0.0F,0.19F,-0.03F),v(0.0F,0.08F,0.43F),v(0.0F,0.0F,0.0F),v(0.0F,0.19F,0.3F))));
        PROFILES.put(ChaoAdultFamily.HF, new Profile(false, false,
                new Anchors(v(0.0F,-0.06F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.07F,-0.15F),v(0.0F,-0.1F,-0.13F),v(0.0F,0.0F,0.0F),v(0.0F,-0.12F,-0.27F)),
                new Anchors(v(0.0F,0.08F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.15F,-0.15F),v(0.0F,0.1F,-0.13F),v(0.0F,0.0F,0.0F),v(0.0F,-0.14F,-0.27F)),
                new Anchors(v(0.0F,-0.07F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,-0.11F,-0.34F),v(0.0F,-0.1F,-0.04F),v(0.0F,0.0F,0.0F),v(0.0F,-0.01F,-0.25F)),
                new Anchors(v(0.0F,0.11F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.15F,-0.15F),v(0.0F,0.08F,-0.13F),v(0.0F,0.0F,0.0F),v(0.0F,0.2F,-0.34F)),
                new Anchors(v(0.0F,0.06F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.13F,0.12F),v(0.0F,0.08F,-0.13F),v(0.0F,0.0F,0.0F),v(0.0F,0.11F,-0.3F)),
                new Anchors(v(0.0F,0.16F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.05F,0.11F),v(0.0F,0.15F,-0.11F),v(0.0F,0.0F,0.0F),v(0.0F,0.26F,-0.3F))));
        PROFILES.put(ChaoAdultFamily.DF, new Profile(false, false,
                new Anchors(v(0.0F,0.04F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.17F,0.06F),v(0.0F,0.04F,0.1F),v(0.0F,0.0F,0.0F),v(0.0F,0.1F,-0.11F)),
                new Anchors(v(0.0F,0.04F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.17F,0.06F),v(0.0F,0.13F,0.1F),v(0.0F,0.0F,0.0F),v(0.0F,0.2F,-0.09F)),
                new Anchors(v(0.0F,0.04F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.17F,0.06F),v(0.0F,0.04F,0.1F),v(0.0F,0.0F,0.0F),v(0.0F,0.05F,-0.03F)),
                new Anchors(v(0.0F,0.08F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.25F,0.06F),v(0.0F,0.13F,0.12F),v(0.0F,0.0F,0.0F),v(0.0F,0.16F,-0.03F)),
                new Anchors(v(0.0F,0.08F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.19F,0.03F),v(0.0F,0.13F,0.12F),v(0.0F,0.0F,0.0F),v(0.0F,0.15F,-0.03F)),
                new Anchors(v(0.0F,0.08F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.17F,0.06F),v(0.0F,0.13F,0.12F),v(0.0F,0.0F,0.0F),v(0.0F,0.16F,-0.03F))));
        PROFILES.put(ChaoAdultFamily.NR, new Profile(true, false,
                new Anchors(v(0.0F,0.16F,-0.18F),v(0.0F,0.09F,-0.05F),v(0.0F,0.05F,0.1F),v(0.0F,0.15F,-0.13F),v(0.0F,0.0F,0.0F),v(0.0F,0.28F,-0.25F)),
                new Anchors(v(0.0F,0.16F,-0.18F),v(0.0F,0.09F,-0.05F),v(0.0F,0.11F,0.1F),v(0.0F,0.15F,-0.13F),v(0.0F,0.0F,0.0F),v(0.0F,0.28F,-0.25F)),
                new Anchors(v(0.0F,0.26F,-0.18F),v(0.0F,0.17F,-0.52F),v(0.0F,0.95F,-1.06F),v(0.0F,0.11F,-0.07F),v(0.0F,0.0F,0.0F),v(0.0F,0.41F,-0.25F)),
                new Anchors(v(0.0F,0.21F,-0.18F),v(0.0F,0.25F,-0.27F),v(0.0F,0.11F,0.14F),v(0.0F,0.16F,-0.11F),v(0.0F,0.0F,0.0F),v(0.0F,0.34F,-0.31F)),
                new Anchors(v(0.0F,0.13F,-0.18F),v(0.0F,-0.09F,-0.27F),v(0.0F,-0.07F,0.14F),v(0.0F,0.16F,-0.09F),v(0.0F,0.0F,0.0F),v(0.0F,0.21F,-0.27F)),
                new Anchors(v(0.0F,0.29F,-0.18F),v(0.0F,0.25F,-0.62F),v(0.0F,0.11F,0.14F),v(0.0F,0.16F,-0.13F),v(0.0F,0.0F,0.0F),v(0.0F,0.32F,-0.25F))));
        PROFILES.put(ChaoAdultFamily.HR, new Profile(true, false,
                new Anchors(v(0.0F,-0.03F,-0.24F),v(0.0F,-0.03F,-0.01F),v(0.0F,0.1F,0.06F),v(0.0F,-0.1F,-0.12F),v(0.0F,0.0F,0.0F),v(0.0F,0.04F,-0.27F)),
                new Anchors(v(0.0F,0.04F,-0.24F),v(0.0F,0.04F,-0.01F),v(0.0F,0.1F,0.06F),v(0.0F,0.02F,-0.12F),v(0.0F,0.0F,0.0F),v(0.0F,0.04F,-0.27F)),
                new Anchors(v(0.0F,0.04F,-0.24F),v(0.0F,-0.01F,0.02F),v(0.0F,-0.23F,0.06F),v(0.0F,0.02F,-0.12F),v(0.0F,0.0F,0.0F),v(0.0F,0.04F,-0.27F)),
                new Anchors(v(0.0F,-0.06F,-0.24F),v(0.0F,-0.11F,0.08F),v(0.0F,0.12F,-0.08F),v(0.0F,0.02F,-0.12F),v(0.0F,0.0F,0.0F),v(0.0F,0.04F,-0.27F)),
                new Anchors(v(0.0F,0.24F,0.16F),v(0.0F,0.04F,0.02F),v(0.0F,0.22F,0.14F),v(0.0F,0.12F,-0.12F),v(0.0F,0.0F,0.0F),v(0.0F,0.08F,-0.27F)),
                new Anchors(v(0.0F,0.13F,-0.24F),v(0.0F,-0.06F,0.02F),v(0.0F,0.14F,0.08F),v(0.0F,0.12F,-0.12F),v(0.0F,0.0F,0.0F),v(0.0F,0.08F,-0.27F))));
        PROFILES.put(ChaoAdultFamily.DR, new Profile(false, false,
                new Anchors(v(0.0F,-0.07F,-0.2F),v(0.0F,0.0F,0.0F),v(0.0F,0.03F,-0.02F),v(0.0F,-0.11F,-0.06F),v(0.0F,0.0F,0.0F),v(0.0F,0.07F,-0.29F)),
                new Anchors(v(0.0F,-0.07F,-0.2F),v(0.0F,0.0F,0.0F),v(0.0F,0.03F,-0.02F),v(0.0F,-0.11F,-0.06F),v(0.0F,0.0F,0.0F),v(0.0F,0.04F,-0.25F)),
                new Anchors(v(0.0F,-0.07F,-0.2F),v(0.0F,0.0F,0.0F),v(0.0F,-0.03F,0.06F),v(0.0F,-0.11F,-0.06F),v(0.0F,0.0F,0.0F),v(0.0F,0.04F,-0.25F)),
                new Anchors(v(0.0F,-0.07F,-0.2F),v(0.0F,0.0F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,-0.11F,-0.06F),v(0.0F,0.0F,0.0F),v(0.0F,0.04F,-0.25F)),
                new Anchors(v(0.0F,0.16F,-0.2F),v(0.0F,0.0F,0.0F),v(0.0F,0.18F,0.0F),v(0.0F,0.14F,-0.06F),v(0.0F,0.0F,0.0F),v(0.0F,0.34F,-0.27F)),
                new Anchors(v(0.0F,-0.07F,-0.2F),v(0.0F,0.0F,0.0F),v(0.0F,-0.02F,0.06F),v(0.0F,-0.11F,-0.06F),v(0.0F,0.0F,0.0F),v(0.0F,0.04F,-0.25F))));
        PROFILES.put(ChaoAdultFamily.NP, new Profile(true, false,
                new Anchors(v(0.0F,0.13F,0.0F),v(0.0F,0.14F,0.01F),v(0.0F,0.16F,-0.02F),v(0.0F,0.07F,-0.11F),v(0.0F,0.0F,0.0F),v(0.0F,0.14F,-0.12F)),
                new Anchors(v(0.0F,0.21F,0.0F),v(0.0F,0.05F,0.01F),v(0.0F,0.16F,0.1F),v(0.0F,0.23F,-0.11F),v(0.0F,0.0F,0.0F),v(0.0F,0.22F,-0.12F)),
                new Anchors(v(0.0F,0.19F,0.0F),v(0.0F,0.05F,0.01F),v(0.0F,0.08F,0.08F),v(0.0F,0.19F,-0.04F),v(0.0F,0.0F,0.0F),v(0.0F,0.26F,-0.08F)),
                new Anchors(v(0.0F,0.21F,0.0F),v(0.0F,0.05F,0.19F),v(0.0F,0.22F,0.22F),v(0.0F,0.19F,-0.08F),v(0.0F,0.0F,0.0F),v(0.0F,0.22F,-0.12F)),
                new Anchors(v(0.0F,0.32F,0.0F),v(0.0F,0.28F,0.07F),v(0.0F,0.2F,0.12F),v(0.0F,0.33F,-0.26F),v(0.0F,0.0F,0.0F),v(0.0F,0.4F,-0.3F)),
                new Anchors(v(0.0F,0.29F,0.0F),v(0.0F,0.28F,-0.2F),v(0.0F,0.06F,0.08F),v(0.0F,0.23F,-0.04F),v(0.0F,0.0F,0.0F),v(0.0F,0.32F,-0.22F))));
        PROFILES.put(ChaoAdultFamily.HP, new Profile(true, false,
                new Anchors(v(0.0F,0.11F,-0.12F),v(0.0F,0.03F,0.04F),v(0.0F,0.16F,0.0F),v(0.0F,0.04F,0.02F),v(0.0F,0.0F,0.0F),v(0.0F,0.18F,-0.16F)),
                new Anchors(v(0.0F,0.2F,-0.12F),v(0.0F,0.14F,0.04F),v(0.0F,0.16F,0.0F),v(0.0F,0.22F,0.0F),v(0.0F,0.0F,0.0F),v(0.0F,0.3F,-0.16F)),
                new Anchors(v(0.0F,0.07F,-0.12F),v(0.0F,0.0F,0.04F),v(0.0F,0.16F,0.0F),v(0.0F,0.04F,-0.02F),v(0.0F,0.0F,0.0F),v(0.0F,0.12F,-0.16F)),
                new Anchors(v(0.0F,0.11F,-0.12F),v(0.0F,0.0F,0.04F),v(0.0F,0.16F,0.0F),v(0.0F,0.04F,-0.02F),v(0.0F,0.0F,0.0F),v(0.0F,0.12F,-0.16F)),
                new Anchors(v(0.0F,0.18F,-0.12F),v(0.0F,0.18F,0.1F),v(0.0F,0.16F,0.0F),v(0.0F,0.22F,-0.02F),v(0.0F,0.0F,0.0F),v(0.0F,0.24F,-0.16F)),
                new Anchors(v(0.0F,0.2F,-0.12F),v(0.0F,0.11F,0.02F),v(0.0F,0.16F,0.0F),v(0.0F,0.22F,-0.02F),v(0.0F,0.0F,0.0F),v(0.0F,0.24F,-0.16F))));
        PROFILES.put(ChaoAdultFamily.DP, new Profile(true, false,
                new Anchors(v(0.0F,0.02F,-0.12F),v(0.0F,0.02F,0.13F),v(0.0F,0.26F,0.1F),v(0.0F,0.02F,-0.12F),v(0.0F,0.0F,0.0F),v(0.0F,0.08F,-0.22F)),
                new Anchors(v(0.0F,0.09F,-0.12F),v(0.0F,0.07F,0.03F),v(0.0F,0.26F,0.0F),v(0.0F,0.11F,-0.12F),v(0.0F,0.0F,0.0F),v(0.0F,0.18F,-0.22F)),
                new Anchors(v(0.0F,0.09F,-0.12F),v(0.0F,0.05F,0.07F),v(0.0F,0.16F,0.12F),v(0.0F,0.11F,-0.12F),v(0.0F,0.0F,0.0F),v(0.0F,0.16F,-0.22F)),
                new Anchors(v(0.0F,0.09F,-0.12F),v(0.0F,0.05F,0.11F),v(0.0F,0.16F,0.18F),v(0.0F,0.11F,-0.12F),v(0.0F,0.0F,0.0F),v(0.0F,0.18F,-0.22F)),
                new Anchors(v(0.0F,0.19F,-0.17F),v(0.0F,0.12F,-0.01F),v(0.0F,0.26F,-0.04F),v(0.0F,0.29F,-0.12F),v(0.0F,0.0F,0.0F),v(0.0F,0.32F,-0.22F)),
                new Anchors(v(0.0F,0.01F,-0.12F),v(0.0F,-0.1F,-0.08F),v(0.0F,0.26F,-0.04F),v(0.0F,0.03F,-0.12F),v(0.0F,0.0F,0.0F),v(0.0F,0.08F,-0.22F))));
    }

    private ChaoAnimalAnchorProfiles() {}

    private static Vector3f v(float x,float y,float z){ return new Vector3f(x,y,z); }

    public static Vector3f resolve(ChaoAdultFamily family, ChaoAppearanceState state, Slot slot) {
        Profile p = PROFILES.get(family);
        if (p == null || slot == Slot.LEGS) return new Vector3f();
        Anchors mixed = mix(p, state);
        return switch (slot) {
            case ARMS -> new Vector3f(mixed.arms());
            case WINGS -> new Vector3f(p.owp() ? mixed.wings() : mixed.arms());
            case TAIL -> new Vector3f(mixed.tail());
            case FACE -> new Vector3f(p.ofp() ? mixed.face() : mixed.mouth());
            case HORNS, EARS -> new Vector3f(mixed.ears());
            case FOREHEAD -> new Vector3f(mixed.mouth());
            case LEGS -> new Vector3f();
        };
    }

    private static Anchors mix(Profile p, ChaoAppearanceState s) {
        float young = Math.max(0F, 1F - s.age());
        float age = s.age();
        float normal = (s.normal() / 100F) * age;
        float swim = (s.swim() / 100F) * age;
        float fly = (s.fly() / 100F) * age;
        float run = (s.run() / 100F) * age;
        float power = (s.power() / 100F) * age;
        return weighted(p.young(), young, p.normal(), normal, p.swim(), swim, p.fly(), fly, p.run(), run, p.power(), power);
    }

    private static Anchors weighted(Anchors a,float aw,Anchors b,float bw,Anchors c,float cw,Anchors d,float dw,Anchors e,float ew,Anchors f,float fw){
        return new Anchors(w(a.arms(),aw,b.arms(),bw,c.arms(),cw,d.arms(),dw,e.arms(),ew,f.arms(),fw),
                w(a.wings(),aw,b.wings(),bw,c.wings(),cw,d.wings(),dw,e.wings(),ew,f.wings(),fw),
                w(a.tail(),aw,b.tail(),bw,c.tail(),cw,d.tail(),dw,e.tail(),ew,f.tail(),fw),
                w(a.face(),aw,b.face(),bw,c.face(),cw,d.face(),dw,e.face(),ew,f.face(),fw),
                w(a.mouth(),aw,b.mouth(),bw,c.mouth(),cw,d.mouth(),dw,e.mouth(),ew,f.mouth(),fw),
                w(a.ears(),aw,b.ears(),bw,c.ears(),cw,d.ears(),dw,e.ears(),ew,f.ears(),fw));
    }

    private static Vector3f w(Vector3f a,float aw,Vector3f b,float bw,Vector3f c,float cw,Vector3f d,float dw,Vector3f e,float ew,Vector3f f,float fw){
        return new Vector3f(a).mul(aw).fma(bw,b).fma(cw,c).fma(dw,d).fma(ew,e).fma(fw,f);
    }

    private record Anchors(Vector3f arms,Vector3f wings,Vector3f tail,Vector3f face,Vector3f mouth,Vector3f ears) {}

    private record Profile(boolean owp, boolean ofp, Anchors young, Anchors normal, Anchors swim, Anchors fly, Anchors run, Anchors power) {}
}