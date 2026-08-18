package com.chaocraft.client.render.material;

import java.util.Map;
import java.util.Set;

/**
 * Exact material-object membership of ChaoMorphController.ReflMaterials after
 * SetMaterials()/material sharing. Names come from the Viewer's source .mat data.
 */
public final class ChaoReflectionMaterialRules {
    private static final String EYELID = "__EYELID__";
    private static final Map<String, Set<String>> MATERIALS = Map.ofEntries(
            Map.entry("NN", Set.of("NN_Belly.mat", "NN_Body.mat", EYELID)),
            Map.entry("HN", Set.of("HN_Belly.mat", "HN_Body.mat", EYELID)),
            Map.entry("DN", Set.of("DN_Belly.mat", "DN_Body.mat", EYELID)),
            Map.entry("NS", Set.of("NS_Arms.mat", "NS_Belly.mat", "NS_Body.mat", "NS_Feet1.mat", "NS_Feet2.mat", "NS_Head.mat", "NS_tail.mat", EYELID)),
            Map.entry("HS", Set.of("HS_Belly.mat", "HS_Body.mat", EYELID)),
            Map.entry("DS", Set.of("DS_Body1.mat", "DS_Body2.mat", EYELID)),
            Map.entry("NF", Set.of("NFBack.mat", "NFBelly.mat", "NFBody1.mat", "NFBody2.mat", "NFTail.mat", EYELID)),
            Map.entry("HF", Set.of("HFBelly.mat", "HFBody.mat", EYELID)),
            Map.entry("DF", Set.of("DFBelly.mat", "DFBody.mat", EYELID)),
            Map.entry("NR", Set.of("NR_Back.mat", "NR_Belly.mat", "NR_Body.mat", "NR_Feet.mat", "NR_Head.mat", EYELID)),
            Map.entry("HR", Set.of("HR_Belly.mat", "HR_Body.mat", EYELID)),
            Map.entry("DR", Set.of("DR_Belly.mat", "DR_Body.mat", EYELID)),
            Map.entry("NP", Set.of("NP_Arms.mat", "NP_Back.mat", "NP_Belly.mat", "NP_Body.mat", EYELID)),
            Map.entry("HP", Set.of("HP_Belly.mat", "HP_Body.mat", EYELID)),
            Map.entry("DP", Set.of("DP_Body.mat", "DP_Body2.mat", EYELID))
    );

    private ChaoReflectionMaterialRules() {}

    public static boolean isReflectiveAdult(String family, String materialName, boolean eyelid) {
        String key = eyelid ? EYELID : materialName;
        return MATERIALS.getOrDefault(family, Set.of()).contains(key);
    }
}
