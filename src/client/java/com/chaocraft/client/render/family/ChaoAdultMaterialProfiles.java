package com.chaocraft.client.render.family;

import com.chaocraft.ChaoCraft;
import com.chaocraft.client.render.material.ChaoColor;
import com.chaocraft.client.render.material.ChaoPaletteState;
import net.minecraft.util.Identifier;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Generated from Chao Viewer scene material slots + Change*() assignments. */
public final class ChaoAdultMaterialProfiles {
    private static final Map<ChaoAdultFamily, FamilyProfile> PROFILES = new EnumMap<>(ChaoAdultFamily.class);

    static {
        PROFILES.put(ChaoAdultFamily.NN, new FamilyProfile(Map.of(
                "arms", List.of(new MaterialSpec("NN_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_body.png"), null, null, ColorRef.BASE, ColorRef.BODY, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "belly", List.of(new MaterialSpec("NN_Belly.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_belly.png"), null, null, ColorRef.BASE, ColorRef.BELLY, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER), new MaterialSpec("NN_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_body.png"), null, null, ColorRef.BASE, ColorRef.BODY, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "head", List.of(new MaterialSpec("NN_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_body.png"), null, null, ColorRef.BASE, ColorRef.BODY, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER), MaterialSpec.eye(), MaterialSpec.eyelid(), MaterialSpec.mouthMid(), MaterialSpec.mouthSide()),
                "legs", List.of(new MaterialSpec("NN_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_body.png"), null, null, ColorRef.BASE, ColorRef.BODY, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "tail", List.of(new MaterialSpec("NN_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_body.png"), null, null, ColorRef.BASE, ColorRef.BODY, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "wings", List.of(new MaterialSpec("NN_Wings.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_wings.png"), null, null, ColorRef.WINGS_BASE, ColorRef.WINGS, ColorRef.WHITE, ColorRef.WHITE, ColorRef.WINGS_COVER))
        )));
        PROFILES.put(ChaoAdultFamily.HN, new FamilyProfile(Map.of(
                "arms", List.of(new MaterialSpec("HN_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hn_body.png"), ChaoCraft.id("textures/entity/chao/material/all/hn_body2.png"), null, ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "belly", List.of(new MaterialSpec("HN_Belly.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hn_belly1.png"), null, null, ColorRef.BASE, ColorRef.BELLY, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER), new MaterialSpec("HN_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hn_body.png"), ChaoCraft.id("textures/entity/chao/material/all/hn_body2.png"), null, ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "head", List.of(new MaterialSpec("HN_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hn_body.png"), ChaoCraft.id("textures/entity/chao/material/all/hn_body2.png"), null, ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER), MaterialSpec.eye(), MaterialSpec.eyelid(), MaterialSpec.mouthMid(), MaterialSpec.mouthSide()),
                "legs", List.of(new MaterialSpec("HN_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hn_body.png"), ChaoCraft.id("textures/entity/chao/material/all/hn_body2.png"), null, ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "tail", List.of(new MaterialSpec("HN_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hn_body.png"), ChaoCraft.id("textures/entity/chao/material/all/hn_body2.png"), null, ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "wings", List.of(new MaterialSpec("HN_Wings.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hn_wings.png"), null, null, ColorRef.WINGS_BASE, ColorRef.WINGS, ColorRef.WHITE, ColorRef.WHITE, ColorRef.WINGS_COVER))
        )));
        PROFILES.put(ChaoAdultFamily.DN, new FamilyProfile(Map.of(
                "arms", List.of(new MaterialSpec("DN_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/dn_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/dn_body2.png"), null, ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "belly", List.of(new MaterialSpec("DN_Belly.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_belly_dark.png"), null, null, ColorRef.BASE, ColorRef.BELLY, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER), new MaterialSpec("DN_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/dn_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/dn_body2.png"), null, ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "head", List.of(new MaterialSpec("DN_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/dn_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/dn_body2.png"), null, ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER), MaterialSpec.eye(), MaterialSpec.eyelid(), new MaterialSpec("DN_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/dn_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/dn_body2.png"), null, ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER), MaterialSpec.mouthMid(), MaterialSpec.mouthSide()),
                "legs", List.of(new MaterialSpec("DN_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/dn_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/dn_body2.png"), null, ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "tail", List.of(new MaterialSpec("DN_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/dn_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/dn_body2.png"), null, ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "wings", List.of(new MaterialSpec("DN_Wings.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/dn_wings1.png"), ChaoCraft.id("textures/entity/chao/material/all/dn_wings2.png"), null, ColorRef.WINGS_BASE, ColorRef.WINGS, ColorRef.WINGS_EXTRA, ColorRef.WHITE, ColorRef.WINGS_COVER))
        )));
        PROFILES.put(ChaoAdultFamily.NS, new FamilyProfile(Map.of(
                "arms", List.of(new MaterialSpec("NS_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/ns_body.png"), null, null, ColorRef.BODY, ColorRef.BASE, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER), new MaterialSpec("NS_Arms.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/ns_arms2.png"), ChaoCraft.id("textures/entity/chao/material/all/ns_arms1.png"), null, ColorRef.BODY, ColorRef.BASE, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "belly", List.of(new MaterialSpec("NS_Belly.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/ns_belly.png"), null, null, ColorRef.BASE, ColorRef.BODY, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "head", List.of(new MaterialSpec("NS_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/ns_body.png"), null, null, ColorRef.BODY, ColorRef.BASE, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER), new MaterialSpec("NS_Head.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/ns_head.png"), null, null, ColorRef.BASE, ColorRef.HORNS, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER), MaterialSpec.eye(), MaterialSpec.eyelid(), new MaterialSpec("NS_Arms.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/ns_arms2.png"), ChaoCraft.id("textures/entity/chao/material/all/ns_arms1.png"), null, ColorRef.BODY, ColorRef.BASE, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER), MaterialSpec.mouthMid(), MaterialSpec.mouthSide()),
                "legs", List.of(new MaterialSpec("NS_Feet2.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_body.png"), null, null, ColorRef.BODY, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.WHITE, ColorRef.EXTRA2), new MaterialSpec("NS_Feet1.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/ns_belly.png"), null, null, ColorRef.BASE, ColorRef.BODY, ColorRef.WHITE, ColorRef.WHITE, ColorRef.EXTRA2)),
                "tail", List.of(new MaterialSpec("NS_tail.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_body.png"), null, null, ColorRef.BODY, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.WHITE, ColorRef.EXTRA3)),
                "wings", List.of(new MaterialSpec("NS_Wings.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_body.png"), null, null, ColorRef.WINGS_BASE, ColorRef.WINGS, ColorRef.WHITE, ColorRef.WHITE, ColorRef.WINGS_COVER))
        )));
        PROFILES.put(ChaoAdultFamily.HS, new FamilyProfile(Map.of(
                "arms", List.of(new MaterialSpec("HS_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hs_body3.png"), ChaoCraft.id("textures/entity/chao/material/all/hs_body2.png"), ChaoCraft.id("textures/entity/chao/material/all/hs_body1.png"), ColorRef.BASE, ColorRef.EXTRA3, ColorRef.EXTRA2, ColorRef.EXTRA, ColorRef.BODY_COVER)),
                "belly", List.of(new MaterialSpec("HS_Belly.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hs_belly2.png"), ChaoCraft.id("textures/entity/chao/material/all/hs_belly1.png"), ChaoCraft.id("textures/entity/chao/material/all/hs_belly3.png"), ColorRef.BASE, ColorRef.BELLY, ColorRef.BODY, ColorRef.HORNS, ColorRef.BODY_COVER)),
                "head", List.of(new MaterialSpec("HS_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hs_body3.png"), ChaoCraft.id("textures/entity/chao/material/all/hs_body2.png"), ChaoCraft.id("textures/entity/chao/material/all/hs_body1.png"), ColorRef.BASE, ColorRef.EXTRA3, ColorRef.EXTRA2, ColorRef.EXTRA, ColorRef.BODY_COVER), MaterialSpec.eye(), MaterialSpec.eyelid(), MaterialSpec.mouthMid(), MaterialSpec.mouthSide()),
                "legs", List.of(new MaterialSpec("HS_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hs_body3.png"), ChaoCraft.id("textures/entity/chao/material/all/hs_body2.png"), ChaoCraft.id("textures/entity/chao/material/all/hs_body1.png"), ColorRef.BASE, ColorRef.EXTRA3, ColorRef.EXTRA2, ColorRef.EXTRA, ColorRef.BODY_COVER)),
                "tail", List.of(new MaterialSpec("HS_Belly.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hs_belly2.png"), ChaoCraft.id("textures/entity/chao/material/all/hs_belly1.png"), ChaoCraft.id("textures/entity/chao/material/all/hs_belly3.png"), ColorRef.BASE, ColorRef.BELLY, ColorRef.BODY, ColorRef.HORNS, ColorRef.BODY_COVER)),
                "wings", List.of(new MaterialSpec("HS_Wings.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hs_wings2.png"), ChaoCraft.id("textures/entity/chao/material/all/hs_wings1.png"), null, ColorRef.WINGS_BASE, ColorRef.WINGS, ColorRef.WINGS_EXTRA, ColorRef.WHITE, ColorRef.WINGS_COVER))
        )));
        PROFILES.put(ChaoAdultFamily.DS, new FamilyProfile(Map.of(
                "arms", List.of(new MaterialSpec("DS_Body2.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/ds_body3.png"), ChaoCraft.id("textures/entity/chao/material/all/ds_body2.png"), ChaoCraft.id("textures/entity/chao/material/all/ds_body1.png"), ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA, ColorRef.EXTRA2, ColorRef.BODY_COVER)),
                "belly", List.of(new MaterialSpec("DS_Body1.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/ds_belly.png"), null, null, ColorRef.BASE, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "head", List.of(new MaterialSpec("DS_Body1.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/ds_belly.png"), null, null, ColorRef.BASE, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER), new MaterialSpec("DS_Body2.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/ds_body3.png"), ChaoCraft.id("textures/entity/chao/material/all/ds_body2.png"), ChaoCraft.id("textures/entity/chao/material/all/ds_body1.png"), ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA, ColorRef.EXTRA2, ColorRef.BODY_COVER), MaterialSpec.eye(), MaterialSpec.eyelid(), MaterialSpec.mouthMid(), MaterialSpec.mouthSide()),
                "legs", List.of(new MaterialSpec("DS_Body2.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/ds_body3.png"), ChaoCraft.id("textures/entity/chao/material/all/ds_body2.png"), ChaoCraft.id("textures/entity/chao/material/all/ds_body1.png"), ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA, ColorRef.EXTRA2, ColorRef.BODY_COVER)),
                "tail", List.of(new MaterialSpec("DS_Body2.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/ds_body3.png"), ChaoCraft.id("textures/entity/chao/material/all/ds_body2.png"), ChaoCraft.id("textures/entity/chao/material/all/ds_body1.png"), ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA, ColorRef.EXTRA2, ColorRef.BODY_COVER)),
                "wings", List.of(new MaterialSpec("DS_Wings.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/ds_wings2.png"), ChaoCraft.id("textures/entity/chao/material/all/ds_wings1.png"), null, ColorRef.WINGS_BASE, ColorRef.WINGS, ColorRef.WINGS_EXTRA, ColorRef.WHITE, ColorRef.WINGS_COVER))
        )));
        PROFILES.put(ChaoAdultFamily.NF, new FamilyProfile(Map.of(
                "arms", List.of(new MaterialSpec("NFBody2.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/nf_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/nf_body2.png"), ChaoCraft.id("textures/entity/chao/material/all/nf_body3.png"), ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA, ColorRef.EXTRA2, ColorRef.BODY_COVER)),
                "belly", List.of(new MaterialSpec("NFBack.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_belly.png"), null, null, ColorRef.BASE, ColorRef.EXTRA3, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER), new MaterialSpec("NFBelly.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_belly.png"), null, null, ColorRef.BASE, ColorRef.BELLY, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "head", List.of(new MaterialSpec("NFBody2.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/nf_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/nf_body2.png"), ChaoCraft.id("textures/entity/chao/material/all/nf_body3.png"), ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA, ColorRef.EXTRA2, ColorRef.BODY_COVER), MaterialSpec.eye(), MaterialSpec.eyelid(), new MaterialSpec("NFBody1.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_body.png"), null, null, ColorRef.BASE, ColorRef.BELLY, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER), MaterialSpec.mouthMid(), MaterialSpec.mouthSide()),
                "legs", List.of(new MaterialSpec("NFBody1.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_body.png"), null, null, ColorRef.BASE, ColorRef.BELLY, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "tail", List.of(new MaterialSpec("NFTail.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/nf_tail.png"), null, null, ColorRef.BASE, ColorRef.BODY, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "wings", List.of(new MaterialSpec("NFWings.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/nf_wings.png"), null, null, ColorRef.WINGS_BASE, ColorRef.WINGS, ColorRef.WHITE, ColorRef.WHITE, ColorRef.WINGS_COVER))
        )));
        PROFILES.put(ChaoAdultFamily.HF, new FamilyProfile(Map.of(
                "arms", List.of(new MaterialSpec("HFBody.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hf_body2.png"), ChaoCraft.id("textures/entity/chao/material/all/hf_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/hf_body3.png"), ColorRef.BASE, ColorRef.EXTRA, ColorRef.BODY, ColorRef.EXTRA2, ColorRef.BODY_COVER)),
                "belly", List.of(new MaterialSpec("HFBelly.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hf_belly1.png"), ChaoCraft.id("textures/entity/chao/material/all/hf_belly2.png"), null, ColorRef.BASE, ColorRef.BELLY, ColorRef.EXTRA3, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "head", List.of(new MaterialSpec("HFBody.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hf_body2.png"), ChaoCraft.id("textures/entity/chao/material/all/hf_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/hf_body3.png"), ColorRef.BASE, ColorRef.EXTRA, ColorRef.BODY, ColorRef.EXTRA2, ColorRef.BODY_COVER), MaterialSpec.eye(), MaterialSpec.eyelid(), MaterialSpec.mouthMid(), MaterialSpec.mouthSide()),
                "legs", List.of(new MaterialSpec("HFBody.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hf_body2.png"), ChaoCraft.id("textures/entity/chao/material/all/hf_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/hf_body3.png"), ColorRef.BASE, ColorRef.EXTRA, ColorRef.BODY, ColorRef.EXTRA2, ColorRef.BODY_COVER)),
                "tail", List.of(new MaterialSpec("HFBody.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hf_body2.png"), ChaoCraft.id("textures/entity/chao/material/all/hf_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/hf_body3.png"), ColorRef.BASE, ColorRef.EXTRA, ColorRef.BODY, ColorRef.EXTRA2, ColorRef.BODY_COVER)),
                "wings", List.of(new MaterialSpec("HFWings.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hf_wings1.png"), ChaoCraft.id("textures/entity/chao/material/all/hf_wings2.png"), null, ColorRef.WINGS_BASE, ColorRef.WINGS_EXTRA, ColorRef.WINGS, ColorRef.WHITE, ColorRef.WINGS_COVER))
        )));
        PROFILES.put(ChaoAdultFamily.DF, new FamilyProfile(Map.of(
                "arms", List.of(new MaterialSpec("DFBody.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/df_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/df_body2.png"), null, ColorRef.BODY, ColorRef.BASE, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "belly", List.of(new MaterialSpec("DFBelly.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/df_belly.png"), null, null, ColorRef.BASE, ColorRef.BELLY, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "head", List.of(new MaterialSpec("DFBody.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/df_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/df_body2.png"), null, ColorRef.BODY, ColorRef.BASE, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER), new MaterialSpec("DFBelly.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/df_belly.png"), null, null, ColorRef.BASE, ColorRef.BELLY, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER), MaterialSpec.eye(), MaterialSpec.eyelid(), MaterialSpec.hidden(), MaterialSpec.mouthMid(), MaterialSpec.mouthSide()),
                "legs", List.of(new MaterialSpec("DFBody.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/df_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/df_body2.png"), null, ColorRef.BODY, ColorRef.BASE, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "tail", List.of(new MaterialSpec("DFBody.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/df_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/df_body2.png"), null, ColorRef.BODY, ColorRef.BASE, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "wings", List.of(new MaterialSpec("DFWings.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/df_wings1.png"), ChaoCraft.id("textures/entity/chao/material/all/df_wings2.png"), null, ColorRef.WINGS_BASE, ColorRef.WINGS, ColorRef.WINGS_EXTRA, ColorRef.WHITE, ColorRef.WINGS_COVER))
        )));
        PROFILES.put(ChaoAdultFamily.NR, new FamilyProfile(Map.of(
                "arms", List.of(new MaterialSpec("NR_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_body.png"), ChaoCraft.id("textures/entity/chao/material/all/nr_bodyextra.png"), null, ColorRef.BASE, ColorRef.BELLY, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "belly", List.of(new MaterialSpec("NR_Belly.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_belly.png"), ChaoCraft.id("textures/entity/chao/material/all/hf_belly1.png"), null, ColorRef.BASE, ColorRef.BELLY, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER), new MaterialSpec("NR_Back.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_body.png"), null, null, ColorRef.BASE, ColorRef.BODY, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER), new MaterialSpec("NR_Head.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/nr_head2.png"), ChaoCraft.id("textures/entity/chao/material/all/nr_head1.png"), null, ColorRef.BASE, ColorRef.HORNS, ColorRef.EXTRA3, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "head", List.of(new MaterialSpec("NR_Head.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/nr_head2.png"), ChaoCraft.id("textures/entity/chao/material/all/nr_head1.png"), null, ColorRef.BASE, ColorRef.HORNS, ColorRef.EXTRA3, ColorRef.WHITE, ColorRef.BODY_COVER), MaterialSpec.eye(), MaterialSpec.eyelid(), new MaterialSpec("NR_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_body.png"), ChaoCraft.id("textures/entity/chao/material/all/nr_bodyextra.png"), null, ColorRef.BASE, ColorRef.BELLY, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER), MaterialSpec.mouthMid(), MaterialSpec.mouthSide()),
                "legs", List.of(new MaterialSpec("NR_Feet.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_body.png"), null, null, ColorRef.BASE, ColorRef.EXTRA2, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER), new MaterialSpec("NR_Belly.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_belly.png"), ChaoCraft.id("textures/entity/chao/material/all/hf_belly1.png"), null, ColorRef.BASE, ColorRef.BELLY, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "tail", List.of(new MaterialSpec("NR_Back.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_body.png"), null, null, ColorRef.BASE, ColorRef.BODY, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER), new MaterialSpec("NR_Head.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/nr_head2.png"), ChaoCraft.id("textures/entity/chao/material/all/nr_head1.png"), null, ColorRef.BASE, ColorRef.HORNS, ColorRef.EXTRA3, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "wings", List.of(new MaterialSpec("NR_Wings.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_body.png"), null, null, ColorRef.WINGS_BASE, ColorRef.WINGS, ColorRef.WHITE, ColorRef.WHITE, ColorRef.WINGS_COVER))
        )));
        PROFILES.put(ChaoAdultFamily.HR, new FamilyProfile(Map.of(
                "arms", List.of(new MaterialSpec("HR_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hr_body2.png"), ChaoCraft.id("textures/entity/chao/material/all/hr_body1.png"), null, ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA2, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "belly", List.of(new MaterialSpec("HR_Belly.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hr_belly1.png"), ChaoCraft.id("textures/entity/chao/material/all/hr_belly2.png"), null, ColorRef.BASE, ColorRef.BELLY, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "head", List.of(new MaterialSpec("HR_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hr_body2.png"), ChaoCraft.id("textures/entity/chao/material/all/hr_body1.png"), null, ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA2, ColorRef.WHITE, ColorRef.BODY_COVER), MaterialSpec.eye(), MaterialSpec.eyelid(), MaterialSpec.mouthMid(), MaterialSpec.mouthSide()),
                "legs", List.of(new MaterialSpec("HR_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hr_body2.png"), ChaoCraft.id("textures/entity/chao/material/all/hr_body1.png"), null, ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA2, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "tail", List.of(new MaterialSpec("HR_Belly.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hr_belly1.png"), ChaoCraft.id("textures/entity/chao/material/all/hr_belly2.png"), null, ColorRef.BASE, ColorRef.BELLY, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "wings", List.of(new MaterialSpec("HR_Wings.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_wings.png"), null, null, ColorRef.WINGS, ColorRef.WINGS_BASE, ColorRef.WHITE, ColorRef.WHITE, ColorRef.WINGS_COVER))
        )));
        PROFILES.put(ChaoAdultFamily.DR, new FamilyProfile(Map.of(
                "arms", List.of(new MaterialSpec("DR_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/dr_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/dr_body2.png"), ChaoCraft.id("textures/entity/chao/material/all/dr_body3.png"), ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA, ColorRef.EXTRA2, ColorRef.BODY_COVER)),
                "belly", List.of(new MaterialSpec("DR_Belly.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/dr_belly2.png"), ChaoCraft.id("textures/entity/chao/material/all/dr_belly1.png"), ChaoCraft.id("textures/entity/chao/material/all/dr_belly3.png"), ColorRef.BASE, ColorRef.EXTRA, ColorRef.BODY, ColorRef.EXTRA2, ColorRef.BODY_COVER)),
                "head", List.of(new MaterialSpec("DR_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/dr_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/dr_body2.png"), ChaoCraft.id("textures/entity/chao/material/all/dr_body3.png"), ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA, ColorRef.EXTRA2, ColorRef.BODY_COVER), MaterialSpec.eye(), MaterialSpec.eyelid(), MaterialSpec.mouthMid(), MaterialSpec.mouthSide()),
                "legs", List.of(new MaterialSpec("DR_Belly.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/dr_belly2.png"), ChaoCraft.id("textures/entity/chao/material/all/dr_belly1.png"), ChaoCraft.id("textures/entity/chao/material/all/dr_belly3.png"), ColorRef.BASE, ColorRef.EXTRA, ColorRef.BODY, ColorRef.EXTRA2, ColorRef.BODY_COVER)),
                "tail", List.of(new MaterialSpec("DR_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/dr_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/dr_body2.png"), ChaoCraft.id("textures/entity/chao/material/all/dr_body3.png"), ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA, ColorRef.EXTRA2, ColorRef.BODY_COVER)),
                "wings", List.of(new MaterialSpec("DR_Wings.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/dr_wings1.png"), ChaoCraft.id("textures/entity/chao/material/all/dr_wings2.png"), null, ColorRef.WINGS_BASE, ColorRef.WINGS, ColorRef.WINGS_EXTRA, ColorRef.WHITE, ColorRef.WINGS_COVER))
        )));
        PROFILES.put(ChaoAdultFamily.NP, new FamilyProfile(Map.of(
                "arms", List.of(new MaterialSpec("NP_Arms.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/np_arms2.png"), ChaoCraft.id("textures/entity/chao/material/all/np_arms1.png"), null, ColorRef.BASE, ColorRef.EXTRA2, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "belly", List.of(new MaterialSpec("NP_Back.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_body.png"), null, null, ColorRef.BASE, ColorRef.EXTRA3, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER), new MaterialSpec("NP_Belly.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_belly.png"), null, null, ColorRef.BASE, ColorRef.BELLY, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "head", List.of(new MaterialSpec("NP_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/np_body.png"), null, null, ColorRef.BASE, ColorRef.BODY, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER), MaterialSpec.eye(), MaterialSpec.eyelid(), MaterialSpec.mouthMid(), MaterialSpec.mouthSide()),
                "legs", List.of(new MaterialSpec("NP_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/np_body.png"), null, null, ColorRef.BASE, ColorRef.BODY, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "tail", List.of(new MaterialSpec("NP_Belly.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_belly.png"), null, null, ColorRef.BASE, ColorRef.BELLY, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER), new MaterialSpec("NP_Back.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/c_body.png"), null, null, ColorRef.BASE, ColorRef.EXTRA3, ColorRef.WHITE, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "wings", List.of(new MaterialSpec("NP_Wings.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/nf_wings.png"), null, null, ColorRef.WINGS, ColorRef.WINGS_BASE, ColorRef.WHITE, ColorRef.WHITE, ColorRef.WINGS_COVER))
        )));
        PROFILES.put(ChaoAdultFamily.HP, new FamilyProfile(Map.of(
                "arms", List.of(new MaterialSpec("HP_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hp_body2.png"), ChaoCraft.id("textures/entity/chao/material/all/hp_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/hp_body3.png"), ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA, ColorRef.BODY, ColorRef.BODY_COVER)),
                "belly", List.of(new MaterialSpec("HP_Belly.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hp_belly1.png"), ChaoCraft.id("textures/entity/chao/material/all/hp_belly2.png"), null, ColorRef.BASE, ColorRef.BELLY, ColorRef.EXTRA2, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "head", List.of(new MaterialSpec("HP_Belly.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hp_belly1.png"), ChaoCraft.id("textures/entity/chao/material/all/hp_belly2.png"), null, ColorRef.BASE, ColorRef.BELLY, ColorRef.EXTRA2, ColorRef.WHITE, ColorRef.BODY_COVER), new MaterialSpec("HP_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hp_body2.png"), ChaoCraft.id("textures/entity/chao/material/all/hp_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/hp_body3.png"), ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA, ColorRef.BODY, ColorRef.BODY_COVER), MaterialSpec.eye(), MaterialSpec.eyelid(), MaterialSpec.mouthMid(), MaterialSpec.mouthSide()),
                "legs", List.of(new MaterialSpec("HP_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hp_body2.png"), ChaoCraft.id("textures/entity/chao/material/all/hp_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/hp_body3.png"), ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA, ColorRef.BODY, ColorRef.BODY_COVER)),
                "tail", List.of(new MaterialSpec("HP_Belly.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hp_belly1.png"), ChaoCraft.id("textures/entity/chao/material/all/hp_belly2.png"), null, ColorRef.BASE, ColorRef.BELLY, ColorRef.EXTRA2, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "wings", List.of(new MaterialSpec("HP_Wings.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/hp_wings.png"), null, null, ColorRef.WINGS_BASE, ColorRef.WINGS, ColorRef.WHITE, ColorRef.WHITE, ColorRef.WINGS_COVER))
        )));
        PROFILES.put(ChaoAdultFamily.DP, new FamilyProfile(Map.of(
                "arms", List.of(new MaterialSpec("DP_Body2.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/dp_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/dp_body2.png"), null, ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA2, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "belly", List.of(new MaterialSpec("DP_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/dn_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/dn_body2.png"), null, ColorRef.BASE, ColorRef.BELLY, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "head", List.of(new MaterialSpec("DP_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/dn_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/dn_body2.png"), null, ColorRef.BASE, ColorRef.BELLY, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER), new MaterialSpec("DP_Body2.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/dp_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/dp_body2.png"), null, ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA2, ColorRef.WHITE, ColorRef.BODY_COVER), MaterialSpec.eye(), MaterialSpec.eyelid(), MaterialSpec.mouthMid(), MaterialSpec.mouthSide()),
                "legs", List.of(new MaterialSpec("DP_Body.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/dn_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/dn_body2.png"), null, ColorRef.BASE, ColorRef.BELLY, ColorRef.EXTRA, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "tail", List.of(new MaterialSpec("DP_Body2.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/dp_body1.png"), ChaoCraft.id("textures/entity/chao/material/all/dp_body2.png"), null, ColorRef.BASE, ColorRef.BODY, ColorRef.EXTRA2, ColorRef.WHITE, ColorRef.BODY_COVER)),
                "wings", List.of(new MaterialSpec("DP_Wings.mat", Kind.CHAO, ChaoCraft.id("textures/entity/chao/material/all/dp_wings1.png"), ChaoCraft.id("textures/entity/chao/material/all/dp_wings2.png"), null, ColorRef.WINGS_BASE, ColorRef.WINGS, ColorRef.WINGS_EXTRA, ColorRef.WHITE, ColorRef.WINGS_COVER))
        )));
    }

    private ChaoAdultMaterialProfiles() {}

    public static MaterialSpec resolve(ChaoAdultFamily family, String segmentName, int submeshIndex) {
        FamilyProfile profile = PROFILES.get(family);
        if (profile == null) return MaterialSpec.hidden();
        String part = partName(segmentName);
        List<MaterialSpec> materials = profile.parts().get(part);
        if (materials == null || submeshIndex < 0 || submeshIndex >= materials.size()) return MaterialSpec.hidden();
        return materials.get(submeshIndex);
    }

    public static ChaoColor color(ColorRef ref, ChaoPaletteState palette) {
        return switch (ref) {
            case BASE -> palette.base();
            case BODY -> palette.body();
            case BELLY -> palette.belly();
            case EXTRA -> palette.extra();
            case EXTRA2 -> palette.extra2();
            case EXTRA3 -> palette.extra3();
            case HORNS -> palette.horns();
            case WINGS -> palette.wings();
            case WINGS_BASE -> palette.wingsBase();
            case WINGS_EXTRA -> palette.wingsExtra();
            case BODY_COVER -> palette.bodyCover();
            case WINGS_COVER -> palette.wingsCover();
            case EMOTION_BALL -> palette.emotionBall();
            case WHITE -> ChaoColor.WHITE;
        };
    }

    private static String partName(String name) {
        String lower = name.toLowerCase();
        if (lower.contains("arms")) return "arms";
        if (lower.contains("belly")) return "belly";
        if (lower.contains("head")) return "head";
        if (lower.contains("legs")) return "legs";
        if (lower.contains("tail")) return "tail";
        if (lower.contains("wings")) return "wings";
        return lower;
    }

    public enum Kind { CHAO, EYE, EYELID, MOUTH_MID, MOUTH_SIDE, HIDDEN }
    public enum ColorRef { BASE, BODY, BELLY, EXTRA, EXTRA2, EXTRA3, HORNS, WINGS, WINGS_BASE, WINGS_EXTRA, BODY_COVER, WINGS_COVER, EMOTION_BALL, WHITE }

    public record MaterialSpec(String debugName, Kind kind, Identifier layer2, Identifier layer3, Identifier layer4,
            ColorRef color1, ColorRef color2, ColorRef color3, ColorRef color4, ColorRef cover) {
        static MaterialSpec eye() { return special(Kind.EYE); }
        static MaterialSpec eyelid() { return special(Kind.EYELID); }
        static MaterialSpec mouthMid() { return special(Kind.MOUTH_MID); }
        static MaterialSpec mouthSide() { return special(Kind.MOUTH_SIDE); }
        static MaterialSpec hidden() { return special(Kind.HIDDEN); }
        private static MaterialSpec special(Kind kind) {
            return new MaterialSpec(kind.name(), kind, null, null, null, ColorRef.WHITE, ColorRef.WHITE, ColorRef.WHITE, ColorRef.WHITE, ColorRef.WHITE);
        }
    }

    private record FamilyProfile(Map<String, List<MaterialSpec>> parts) {}
}
