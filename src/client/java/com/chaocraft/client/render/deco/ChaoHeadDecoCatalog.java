package com.chaocraft.client.render.deco;

import com.chaocraft.ChaoCraft;
import com.chaocraft.visual.ChaoHeadDecoType;
import net.minecraft.util.Identifier;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Exact Viewer HeadDeco mesh/submesh material assignments. */
public final class ChaoHeadDecoCatalog {
    private static final Map<ChaoHeadDecoType, HeadSpec> SPECS = new EnumMap<>(ChaoHeadDecoType.class);

    static {
        SPECS.put(ChaoHeadDecoType.EGGSHELL, new HeadSpec(ChaoCraft.id("models/chao/deco/head/eggshell.cmesh"), List.of(m("chaocraft:textures/entity/chao/deco/head/eggshell.png", 1F, 1F, 1F, 1F), m("chaocraft:textures/entity/chao/deco/head/eggshell_eye.png", 1F, 1F, 1F, 1F))));
        SPECS.put(ChaoHeadDecoType.COOKING_POT, new HeadSpec(
                ChaoCraft.id("models/chao/deco/head/cooking_pot.cmesh"),
                List.of(
                        m("chaocraft:textures/entity/chao/deco/head/cookinpot.png", 1F, 1F, 1F, 1F),
                        // Viewer Pot.mat: ChaosMaterial.shader + _Cube=CookinPot 1,
                        // _Ref=1, _Emission=0, no _MainTex.
                        r("chaocraft:textures/entity/chao/deco/head/cooking_pot_reflection.png", 1.0F, 0.0F),
                        m("chaocraft:textures/entity/chao/deco/head/eye.png", 1F, 1F, 1F, 1F)
                )));
        SPECS.put(ChaoHeadDecoType.WOOL_1, new HeadSpec(ChaoCraft.id("models/chao/deco/head/wool_1.cmesh"), List.of(m("chaocraft:textures/entity/chao/deco/head/woolhat1_ball.png", 1F, 1F, 1F, 1F), m("chaocraft:textures/entity/chao/deco/head/woolhat1_eye.png", 1F, 1F, 1F, 1F), m("chaocraft:textures/entity/chao/deco/head/woolhat1.png", 1F, 1F, 1F, 1F))));
        SPECS.put(ChaoHeadDecoType.WOOL_2, new HeadSpec(ChaoCraft.id("models/chao/deco/head/wool_2.cmesh"), List.of(m("chaocraft:textures/entity/chao/deco/head/woolhat2_ball.png", 1F, 1F, 1F, 1F), m("chaocraft:textures/entity/chao/deco/head/woolhat2_eye.png", 1F, 1F, 1F, 1F), m("chaocraft:textures/entity/chao/deco/head/woolhat2.png", 1F, 1F, 1F, 1F))));
        SPECS.put(ChaoHeadDecoType.WOOL_3, new HeadSpec(ChaoCraft.id("models/chao/deco/head/wool_3.cmesh"), List.of(m("chaocraft:textures/entity/chao/deco/head/woolhat3_ball.png", 1F, 1F, 1F, 1F), m("chaocraft:textures/entity/chao/deco/head/woolhat3_eye.png", 1F, 1F, 1F, 1F), m("chaocraft:textures/entity/chao/deco/head/woolhat3.png", 1F, 1F, 1F, 1F))));
        SPECS.put(ChaoHeadDecoType.APPLE, new HeadSpec(ChaoCraft.id("models/chao/deco/head/apple.cmesh"), List.of(m("chaocraft:textures/entity/chao/deco/head/eye.png", 1F, 1F, 1F, 1F), m("chaocraft:textures/entity/chao/deco/head/apple.png", 0.9F, 0.9F, 0.9F, 1F), m("chaocraft:textures/entity/chao/deco/head/apple_leaf.png", 1F, 1F, 1F, 1F), m("chaocraft:textures/entity/chao/deco/head/stump_top.png", 1F, 1F, 1F, 1F))));
        SPECS.put(ChaoHeadDecoType.PAPER_BAG, new HeadSpec(ChaoCraft.id("models/chao/deco/head/paper_bag.cmesh"), List.of(m("chaocraft:textures/entity/chao/deco/head/paperbag.png", 0.9F, 0.9F, 0.9F, 1F), m("chaocraft:textures/entity/chao/deco/head/eye.png", 1F, 1F, 1F, 1F))));
        SPECS.put(ChaoHeadDecoType.CARDBOARD, new HeadSpec(ChaoCraft.id("models/chao/deco/head/cardboard.cmesh"), List.of(m("chaocraft:textures/entity/chao/deco/head/cardboard1.png", 0.9F, 0.9F, 0.9F, 1F), m("chaocraft:textures/entity/chao/deco/head/cardboard2.png", 0.9F, 0.9F, 0.9F, 1F), m("chaocraft:textures/entity/chao/deco/head/eye.png", 1F, 1F, 1F, 1F))));
        SPECS.put(ChaoHeadDecoType.BUCKET, new HeadSpec(ChaoCraft.id("models/chao/deco/head/bucket.cmesh"), List.of(m("chaocraft:textures/entity/chao/deco/head/bucket2.png", 0.9F, 0.9F, 0.9F, 1F), m("chaocraft:textures/entity/chao/deco/head/eye.png", 1F, 1F, 1F, 1F), m("chaocraft:textures/entity/chao/deco/head/bucket.png", 0.9F, 0.9F, 0.9F, 1F))));
        SPECS.put(ChaoHeadDecoType.PUMPKIN, new HeadSpec(ChaoCraft.id("models/chao/deco/head/pumpkin.cmesh"), List.of(m("chaocraft:textures/entity/chao/deco/head/pumpkin.png", 0.9F, 0.9F, 0.9F, 1F), m("chaocraft:textures/entity/chao/deco/head/pumpkin_mouth.png", 1F, 1F, 1F, 1F), m("chaocraft:textures/entity/chao/deco/head/pumpkin_eye.png", 1F, 1F, 1F, 1F))));
        SPECS.put(ChaoHeadDecoType.POT, new HeadSpec(ChaoCraft.id("models/chao/deco/head/pot.cmesh"), List.of(m("chaocraft:textures/entity/chao/deco/head/flowerpot2.png", 0.9F, 0.9F, 0.9F, 1F), m("chaocraft:textures/entity/chao/deco/head/eye.png", 1F, 1F, 1F, 1F), m("chaocraft:textures/entity/chao/deco/head/flowerpot1.png", 0.9F, 0.9F, 0.9F, 1F))));
        SPECS.put(ChaoHeadDecoType.CAN, new HeadSpec(ChaoCraft.id("models/chao/deco/head/can.cmesh"), List.of(m("chaocraft:textures/entity/chao/deco/head/can_top.png", 1F, 1F, 1F, 1F), m("chaocraft:textures/entity/chao/deco/head/can.png", 0.9F, 0.9F, 0.9F, 1F), m("chaocraft:textures/entity/chao/deco/head/eye.png", 1F, 1F, 1F, 1F))));
        SPECS.put(ChaoHeadDecoType.MELON, new HeadSpec(ChaoCraft.id("models/chao/deco/head/melon.cmesh"), List.of(m("chaocraft:textures/entity/chao/deco/head/melon_mouth.png", 0.9F, 0.9F, 0.9F, 1F), m("chaocraft:textures/entity/chao/deco/head/eye.png", 1F, 1F, 1F, 1F), m("chaocraft:textures/entity/chao/deco/head/melon.png", 0.9F, 0.9F, 0.9F, 1F))));
        SPECS.put(ChaoHeadDecoType.TREE, new HeadSpec(ChaoCraft.id("models/chao/deco/head/tree.cmesh"), List.of(m("chaocraft:textures/entity/chao/deco/head/stump.png", 0.9F, 0.9F, 0.9F, 1F), m("chaocraft:textures/entity/chao/deco/head/stump_top.png", 1F, 1F, 1F, 1F), m("chaocraft:textures/entity/chao/deco/head/apple_leaf.png", 1F, 1F, 1F, 1F), m("chaocraft:textures/entity/chao/deco/head/eye.png", 1F, 1F, 1F, 1F))));
        SPECS.put(ChaoHeadDecoType.SKULL, new HeadSpec(ChaoCraft.id("models/chao/deco/head/skull.cmesh"), List.of(m("chaocraft:textures/entity/chao/deco/head/skull.png", 1F, 1F, 1F, 1F), m("chaocraft:textures/entity/chao/deco/head/skull_teeth.png", 1F, 1F, 1F, 1F), m("chaocraft:textures/entity/chao/deco/head/skull_eye.png", 1F, 1F, 1F, 1F))));
    }

    private ChaoHeadDecoCatalog() {}

    public static HeadSpec resolve(ChaoHeadDecoType type) {
        return SPECS.get(type);
    }

    private static MaterialSpec m(String texture, float r, float g, float b, float a) {
        return new MaterialSpec(new Identifier(texture), r, g, b, a, false, 0.0F);
    }

    private static MaterialSpec r(String cubemap, float ref, float emission) {
        return new MaterialSpec(new Identifier(cubemap), 1F, 1F, 1F, ref, true, emission);
    }

    public record HeadSpec(Identifier model, List<MaterialSpec> materials) {}

    /**
     * Head Deco material. reflective=true means texture is a Viewer cubemap strip
     * and alpha carries the original _Ref amount into ChaoReflectionShader.
     */
    public record MaterialSpec(
            Identifier texture, float r, float g, float b, float a,
            boolean reflective, float emission
    ) {}
}
