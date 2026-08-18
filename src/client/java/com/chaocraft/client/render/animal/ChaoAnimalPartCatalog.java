package com.chaocraft.client.render.animal;

import com.chaocraft.ChaoCraft;
import com.chaocraft.visual.ChaoAnimalParts.Slot;
import com.chaocraft.visual.ChaoAnimalType;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Generated directly from Chao Viewer AnimalObject scene data. Do not hand-edit mappings. */
public final class ChaoAnimalPartCatalog {
    private static final Map<Key, PartSpec> PARTS = new LinkedHashMap<>();

    static {
        put(true, 14, Slot.ARMS, "chaocraft:models/chao/animal/adult/14_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_shep_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 14, Slot.ARMS, "chaocraft:models/chao/animal/child/14_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_shep_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 15, Slot.FOREHEAD, "chaocraft:models/chao/animal/adult/15_forehead.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_ska_maegami.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 14, Slot.EARS, "chaocraft:models/chao/animal/child/14_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_shep_mimi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 12, Slot.ARMS, "chaocraft:models/chao/animal/adult/12_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_ara_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 28, Slot.EARS, "chaocraft:models/chao/animal/adult/28_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gwb02.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gwb01.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 6, Slot.LEGS, "chaocraft:models/chao/animal/adult/06_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gori_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gori_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gori_asib.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 23, Slot.TAIL, "chaocraft:models/chao/animal/child/23_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kj05.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kj04.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.14F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 17, Slot.TAIL, "chaocraft:models/chao/animal/child/17_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_uni_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 21, Slot.TAIL, "chaocraft:models/chao/animal/adult/21_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gpe03.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 11, Slot.WINGS, "chaocraft:models/chao/animal/adult/11_wings.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_phen_hanea.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_phen_haneb.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 18, Slot.EARS, "chaocraft:models/chao/animal/child/18_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_ino_mimi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 24, Slot.TAIL, "chaocraft:models/chao/animal/child/24_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_om04.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_om03.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.14F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 23, Slot.WINGS, "chaocraft:models/chao/animal/adult/23_wings.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gkj01.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 5, Slot.LEGS, "chaocraft:models/chao/animal/child/05_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_dra_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 2, Slot.LEGS, "chaocraft:models/chao/animal/adult/02_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_usa_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 5, Slot.TAIL, "chaocraft:models/chao/animal/adult/05_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_dra_sippob.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_dra_sippoa.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 12, Slot.LEGS, "chaocraft:models/chao/animal/child/12_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_ara_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 25, Slot.LEGS, "chaocraft:models/chao/animal/child/25_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_tu04.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_tu03.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 30, Slot.TAIL, "chaocraft:models/chao/animal/child/30_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_leo04.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.14F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 26, Slot.LEGS, "chaocraft:models/chao/animal/child/26_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_usa03.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 9, Slot.FOREHEAD, "chaocraft:models/chao/animal/child/09_forehead.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kuja_kanmuri.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 2, Slot.EARS, "chaocraft:models/chao/animal/adult/02_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_usa_mimi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 2, Slot.EARS, "chaocraft:models/chao/animal/child/02_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_usa_mimi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 30, Slot.LEGS, "chaocraft:models/chao/animal/adult/30_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_leo02.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gle06.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_leo03.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                -1.388535e-05F, 0.007850886F, 0.27884814F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 9, Slot.TAIL, "chaocraft:models/chao/animal/adult/09_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kuja_sippob.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kuja_sippoa.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 22, Slot.EARS, "chaocraft:models/chao/animal/child/22_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_rk07.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 23, Slot.WINGS, "chaocraft:models/chao/animal/child/23_wings.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kj03.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 24, Slot.WINGS, "chaocraft:models/chao/animal/adult/24_wings.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gom06.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 31, Slot.LEGS, "chaocraft:models/chao/animal/child/31_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_zo03.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_zo02.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_zo01.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 32, Slot.LEGS, "chaocraft:models/chao/animal/adult/32_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gsk06.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_sk05.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_sk03.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 25, Slot.WINGS, "chaocraft:models/chao/animal/adult/25_wings.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gtu01.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 9, Slot.FOREHEAD, "chaocraft:models/chao/animal/adult/09_forehead.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kuja_kanmuri.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 23, Slot.FOREHEAD, "chaocraft:models/chao/animal/adult/23_forehead.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gkji03.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 30, Slot.ARMS, "chaocraft:models/chao/animal/child/30_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_leo01.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_leo11.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_leo11.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_leo10.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 8, Slot.WINGS, "chaocraft:models/chao/animal/adult/08_wings.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_oum_hane.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 14, Slot.EARS, "chaocraft:models/chao/animal/adult/14_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_shep_mimi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 32, Slot.FOREHEAD, "chaocraft:models/chao/animal/child/32_forehead.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_sk08.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 32, Slot.FOREHEAD, "chaocraft:models/chao/animal/adult/32_forehead.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gsk03.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 6, Slot.EARS, "chaocraft:models/chao/animal/child/06_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_gori_mimib.png"), 0.8F, 0.8F, 0.8F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_gori_mimia.png"), 0.8F, 0.8F, 0.8F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 11, Slot.TAIL, "chaocraft:models/chao/animal/adult/11_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_phen_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 13, Slot.ARMS, "chaocraft:models/chao/animal/adult/13_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_goma_te.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 12, Slot.LEGS, "chaocraft:models/chao/animal/adult/12_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_ara_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 4, Slot.WINGS, "chaocraft:models/chao/animal/adult/04_wings.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kon_hane.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 26, Slot.LEGS, "chaocraft:models/chao/animal/adult/26_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gusa05.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 28, Slot.LEGS, "chaocraft:models/chao/animal/child/28_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_wb04.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_wb02.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_wb03.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 18, Slot.TAIL, "chaocraft:models/chao/animal/adult/18_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_ino_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_ino_sippob16.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 33, Slot.TAIL, "chaocraft:models/chao/animal/child/33_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_mo01.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.14F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 13, Slot.TAIL, "chaocraft:models/chao/animal/child/13_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_goma_te.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 14, Slot.LEGS, "chaocraft:models/chao/animal/adult/14_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_shep_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 15, Slot.FOREHEAD, "chaocraft:models/chao/animal/child/15_forehead.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_ska_maegami.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 33, Slot.LEGS, "chaocraft:models/chao/animal/adult/33_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gmo03.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gmo04.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gmo05.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 6, Slot.FOREHEAD, "chaocraft:models/chao/animal/child/06_forehead.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_gori_mayu.png"), 0.8F, 0.8F, 0.8F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 31, Slot.TAIL, "chaocraft:models/chao/animal/adult/31_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gzo05.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_zo02.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 18, Slot.LEGS, "chaocraft:models/chao/animal/adult/18_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_ino_hizumea.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_ino_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_ino_hizumeb16.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 7, Slot.TAIL, "chaocraft:models/chao/animal/child/07_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_rako_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.006351154F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 16, Slot.LEGS, "chaocraft:models/chao/animal/child/16_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_tora_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_tora_tume16.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_tora_asib.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 18, Slot.LEGS, "chaocraft:models/chao/animal/child/18_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_ino_hizumea.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_ino_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_ino_hizumeb16.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 29, Slot.LEGS, "chaocraft:models/chao/animal/adult/29_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gori04.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 24, Slot.WINGS, "chaocraft:models/chao/animal/child/24_wings.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_om01.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 27, Slot.ARMS, "chaocraft:models/chao/animal/adult/27_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_bb05.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gbb03.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 16, Slot.EARS, "chaocraft:models/chao/animal/adult/16_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_tora_mimia.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_tora_mimib.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 34, Slot.LEGS, "chaocraft:models/chao/animal/child/34_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kal03.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kal04.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kal04.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 2, Slot.TAIL, "chaocraft:models/chao/animal/adult/02_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_usa_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 8, Slot.TAIL, "chaocraft:models/chao/animal/child/08_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_oum_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 4, Slot.EARS, "chaocraft:models/chao/animal/adult/04_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kon_kami.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 3, Slot.ARMS, "chaocraft:models/chao/animal/child/03_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_chit_teb.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_chit_tea.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 4, Slot.LEGS, "chaocraft:models/chao/animal/adult/04_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kon_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kon_tume16.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 12, Slot.EARS, "chaocraft:models/chao/animal/child/12_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_ara_mimi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 10, Slot.LEGS, "chaocraft:models/chao/animal/child/10_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_pen_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 5, Slot.WINGS, "chaocraft:models/chao/animal/adult/05_wings.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_dra_haneb16.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_dra_hanea.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 20, Slot.TAIL, "chaocraft:models/chao/animal/adult/20_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gaz02.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 28, Slot.ARMS, "chaocraft:models/chao/animal/adult/28_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gwb05.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gwb04.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 7, Slot.TAIL, "chaocraft:models/chao/animal/adult/07_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_rako_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 22, Slot.EARS, "chaocraft:models/chao/animal/adult/22_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_grk05.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 30, Slot.LEGS, "chaocraft:models/chao/animal/child/30_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_leo10.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_leo02.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_leo03.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_leo02.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 9, Slot.WINGS, "chaocraft:models/chao/animal/adult/09_wings.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kuja_hane.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 5, Slot.LEGS, "chaocraft:models/chao/animal/adult/05_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_dra_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_dra_tume16.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 28, Slot.TAIL, "chaocraft:models/chao/animal/adult/28_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gwb04.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 31, Slot.TAIL, "chaocraft:models/chao/animal/child/31_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_zo01.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_zo02.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.14F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 33, Slot.ARMS, "chaocraft:models/chao/animal/adult/33_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gmo01.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gmo05.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 17, Slot.ARMS, "chaocraft:models/chao/animal/adult/17_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_uni_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_uni_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_uni_hidume.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_uni_asinoura16.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 10, Slot.ARMS, "chaocraft:models/chao/animal/adult/10_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_pen_tea16.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_pen_teb16.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 31, Slot.LEGS, "chaocraft:models/chao/animal/adult/31_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gzo05.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gzo06.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_zo02.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 30, Slot.ARMS, "chaocraft:models/chao/animal/adult/30_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gle06.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_leo01.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_leo11.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                -1.388535e-05F, 0.007850886F, 0.27884814F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 5, Slot.EARS, "chaocraft:models/chao/animal/child/05_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_dra_antenna.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 29, Slot.EARS, "chaocraft:models/chao/animal/adult/29_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gori05.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gori06.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 21, Slot.ARMS, "chaocraft:models/chao/animal/child/21_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/c_penguin_arms_2.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/c_penguin_arms_1.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 24, Slot.LEGS, "chaocraft:models/chao/animal/adult/24_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_om06.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gom05.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_om02.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 6, Slot.EARS, "chaocraft:models/chao/animal/adult/06_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_gori_mimia.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_gori_mimib.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 1, Slot.ARMS, "chaocraft:models/chao/animal/adult/01_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kuma_tume16.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kuma_te.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kuma_asib.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 25, Slot.TAIL, "chaocraft:models/chao/animal/child/25_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_tu02.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.14F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 11, Slot.WINGS, "chaocraft:models/chao/animal/child/11_wings.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_phen_hanea.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_phen_haneb.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 15, Slot.LEGS, "chaocraft:models/chao/animal/adult/15_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_ska_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_ska_asib.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 23, Slot.TAIL, "chaocraft:models/chao/animal/adult/23_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kj05.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gkji02.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 7, Slot.FACE, "chaocraft:models/chao/animal/adult/07_face.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_rako_hige16.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 5, Slot.TAIL, "chaocraft:models/chao/animal/child/05_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_dra_sippob.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_dra_sippoa.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 7, Slot.EARS, "chaocraft:models/chao/animal/child/07_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_rako_mimi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.006351154F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 15, Slot.ARMS, "chaocraft:models/chao/animal/adult/15_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_ska_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 26, Slot.ARMS, "chaocraft:models/chao/animal/adult/26_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gusa01.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 27, Slot.LEGS, "chaocraft:models/chao/animal/child/27_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_bb05.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_bb01.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 26, Slot.EARS, "chaocraft:models/chao/animal/child/26_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_usa04.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_usa05.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 7, Slot.EARS, "chaocraft:models/chao/animal/adult/07_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_rako_mimi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 20, Slot.ARMS, "chaocraft:models/chao/animal/adult/20_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gaz01.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 18, Slot.FACE, "chaocraft:models/chao/animal/adult/18_face.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_ino_kiba16.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 18, Slot.ARMS, "chaocraft:models/chao/animal/child/18_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_ino_hizumea.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_ino_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_ino_hizumeb16.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 23, Slot.LEGS, "chaocraft:models/chao/animal/adult/23_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kj01.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kj02.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kj02.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 6, Slot.FOREHEAD, "chaocraft:models/chao/animal/adult/06_forehead.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gori_mayu.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 24, Slot.FOREHEAD, "chaocraft:models/chao/animal/child/24_forehead.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_om05.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 4, Slot.TAIL, "chaocraft:models/chao/animal/child/04_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kon_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 5, Slot.EARS, "chaocraft:models/chao/animal/adult/05_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_dra_antenna.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 30, Slot.FOREHEAD, "chaocraft:models/chao/animal/child/30_forehead.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_leo08.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_leo09.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 22, Slot.ARMS, "chaocraft:models/chao/animal/adult/22_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_grk01.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_rk01.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_grk04.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 17, Slot.TAIL, "chaocraft:models/chao/animal/adult/17_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_uni_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 22, Slot.LEGS, "chaocraft:models/chao/animal/adult/22_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_grk02.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_grk04.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 7, Slot.ARMS, "chaocraft:models/chao/animal/adult/07_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_rako_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 32, Slot.TAIL, "chaocraft:models/chao/animal/child/32_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_sk07.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.14F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 1, Slot.ARMS, "chaocraft:models/chao/animal/child/01_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kuma_te.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kuma_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kuma_tume16.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0063289483F, 0.09899483F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 1, Slot.LEGS, "chaocraft:models/chao/animal/child/01_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kuma_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kuma_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kuma_asib.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kuma_tume16.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 21, Slot.ARMS, "chaocraft:models/chao/animal/adult/21_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gpe02.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gpe03.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 28, Slot.LEGS, "chaocraft:models/chao/animal/adult/28_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_wb04.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gwb05.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_wb02.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 8, Slot.LEGS, "chaocraft:models/chao/animal/adult/08_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_oum_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_oum_asib.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_oum_tume16.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 17, Slot.ARMS, "chaocraft:models/chao/animal/child/17_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_uni_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_uni_kamia.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_uni_hidume.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_uni_asinoura16.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 18, Slot.ARMS, "chaocraft:models/chao/animal/adult/18_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_ino_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_ino_hizumeb16.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_ino_hizumea.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 16, Slot.ARMS, "chaocraft:models/chao/animal/child/16_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_tora_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_tora_asib.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_tora_tume16.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 10, Slot.FOREHEAD, "chaocraft:models/chao/animal/child/10_forehead.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_pen_mayu.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 34, Slot.TAIL, "chaocraft:models/chao/animal/adult/34_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gkl01.png"), 0.8F, 0.8F, 0.8F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 15, Slot.TAIL, "chaocraft:models/chao/animal/adult/15_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_ska_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 17, Slot.FOREHEAD, "chaocraft:models/chao/animal/child/17_forehead.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_uni_kamia.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_uni_tuno16.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_uni_kamib.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 32, Slot.TAIL, "chaocraft:models/chao/animal/adult/32_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gsk02.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 17, Slot.LEGS, "chaocraft:models/chao/animal/adult/17_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_uni_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_uni_asinoura16.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_uni_hidume.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 1, Slot.EARS, "chaocraft:models/chao/animal/child/01_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kuma_mimi.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kuma_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 27, Slot.TAIL, "chaocraft:models/chao/animal/adult/27_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gbb03.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 14, Slot.TAIL, "chaocraft:models/chao/animal/adult/14_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_shep_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 9, Slot.WINGS, "chaocraft:models/chao/animal/child/09_wings.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kuja_hane.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 29, Slot.LEGS, "chaocraft:models/chao/animal/child/29_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gori04.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 14, Slot.TAIL, "chaocraft:models/chao/animal/child/14_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_shep_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 28, Slot.EARS, "chaocraft:models/chao/animal/child/28_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_wb07.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_wb08.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 29, Slot.ARMS, "chaocraft:models/chao/animal/child/29_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gori01.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gori02.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 21, Slot.FOREHEAD, "chaocraft:models/chao/animal/child/21_forehead.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/c_penguin_eyebrows.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 18, Slot.EARS, "chaocraft:models/chao/animal/adult/18_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_ino_mimi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 34, Slot.ARMS, "chaocraft:models/chao/animal/adult/34_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kal02.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gkl01.png"), 0.8F, 0.8F, 0.8F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 27, Slot.EARS, "chaocraft:models/chao/animal/adult/27_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gbb02.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gbb01.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 3, Slot.EARS, "chaocraft:models/chao/animal/adult/03_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_chit_mimi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 14, Slot.HORNS, "chaocraft:models/chao/animal/adult/14_horns.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_shep_tuno.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 15, Slot.TAIL, "chaocraft:models/chao/animal/child/15_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_ska_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 23, Slot.FOREHEAD, "chaocraft:models/chao/animal/child/23_forehead.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kj06.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 32, Slot.LEGS, "chaocraft:models/chao/animal/child/32_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_sk05.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_sk03.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_sk04.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_sk04.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 18, Slot.TAIL, "chaocraft:models/chao/animal/child/18_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_ino_sippob16.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_ino_sippoa16.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 8, Slot.TAIL, "chaocraft:models/chao/animal/adult/08_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_oum_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 10, Slot.LEGS, "chaocraft:models/chao/animal/adult/10_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_pen_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_pen_asi16.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 34, Slot.LEGS, "chaocraft:models/chao/animal/adult/34_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gkl02.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kal04.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 16, Slot.TAIL, "chaocraft:models/chao/animal/child/16_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_tora_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 27, Slot.LEGS, "chaocraft:models/chao/animal/adult/27_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gbb03.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_bb05.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 7, Slot.ARMS, "chaocraft:models/chao/animal/child/07_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_rako_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.006351154F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 11, Slot.LEGS, "chaocraft:models/chao/animal/adult/11_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_phen_tume16.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_phen_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_phen_asib.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 27, Slot.TAIL, "chaocraft:models/chao/animal/child/27_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_bb01.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.14F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 7, Slot.LEGS, "chaocraft:models/chao/animal/adult/07_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_rako_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 31, Slot.ARMS, "chaocraft:models/chao/animal/adult/31_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gzo05.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_zo02.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gzo06.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 6, Slot.ARMS, "chaocraft:models/chao/animal/child/06_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_gori_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_gori_asib.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 27, Slot.EARS, "chaocraft:models/chao/animal/child/27_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_bb03.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_bb04.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 8, Slot.LEGS, "chaocraft:models/chao/animal/child/08_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_oum_tume16.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_oum_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_oum_asib.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 21, Slot.LEGS, "chaocraft:models/chao/animal/adult/21_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_pen03.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 11, Slot.LEGS, "chaocraft:models/chao/animal/child/11_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_phen_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_phen_tume16.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_phen_asib.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 25, Slot.TAIL, "chaocraft:models/chao/animal/adult/25_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gtu02.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 17, Slot.FOREHEAD, "chaocraft:models/chao/animal/adult/17_forehead.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_uni_kamia.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_dra_antenna.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 24, Slot.LEGS, "chaocraft:models/chao/animal/child/24_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_om07.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_om06.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_om02.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 3, Slot.EARS, "chaocraft:models/chao/animal/child/03_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_chit_mimi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 22, Slot.LEGS, "chaocraft:models/chao/animal/child/22_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_rk04.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_rk03.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 30, Slot.EARS, "chaocraft:models/chao/animal/adult/30_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_leo04.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_leo05.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                -1.388535e-05F, 0.007850886F, 0.27884814F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 5, Slot.ARMS, "chaocraft:models/chao/animal/adult/05_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_dra_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_dra_tume16.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 9, Slot.LEGS, "chaocraft:models/chao/animal/adult/09_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kuja_asib.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kuja_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 10, Slot.FOREHEAD, "chaocraft:models/chao/animal/adult/10_forehead.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_pen_mayu.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 24, Slot.TAIL, "chaocraft:models/chao/animal/adult/24_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gom07.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gom02.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 33, Slot.TAIL, "chaocraft:models/chao/animal/adult/33_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gmo01.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 6, Slot.LEGS, "chaocraft:models/chao/animal/child/06_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_gori_asib.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_gori_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 30, Slot.TAIL, "chaocraft:models/chao/animal/adult/30_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_leo04.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gle05.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                -1.388535e-05F, 0.007850886F, 0.27884814F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 27, Slot.HORNS, "chaocraft:models/chao/animal/adult/27_horns.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gbb04.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 22, Slot.ARMS, "chaocraft:models/chao/animal/child/22_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_rk01.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_rk02.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_rk02.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_rk03.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 28, Slot.TAIL, "chaocraft:models/chao/animal/child/28_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_wb04.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.14F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 30, Slot.EARS, "chaocraft:models/chao/animal/child/30_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_leo05.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_leo04.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 21, Slot.LEGS, "chaocraft:models/chao/animal/child/21_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/c_penguin_feet.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 11, Slot.FOREHEAD, "chaocraft:models/chao/animal/adult/11_forehead.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_phen_maegami.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                -0.0F, -0.03F, -0.14F, -0.69753665F, -0.0F, -0.0F, 0.71654916F, 1.0F, 1.0F, 1.0F);
        put(true, 21, Slot.FOREHEAD, "chaocraft:models/chao/animal/adult/21_forehead.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gpe01.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 11, Slot.TAIL, "chaocraft:models/chao/animal/child/11_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_phen_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 29, Slot.ARMS, "chaocraft:models/chao/animal/adult/29_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gori02.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_ggr01.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 30, Slot.FOREHEAD, "chaocraft:models/chao/animal/adult/30_forehead.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gle04.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gle05.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                -1.388535e-05F, 0.007850886F, 0.27884814F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 32, Slot.ARMS, "chaocraft:models/chao/animal/child/32_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_sk03.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_sk02.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_sk01.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 1, Slot.EARS, "chaocraft:models/chao/animal/adult/01_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kuma_mimi.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kuma_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 13, Slot.ARMS, "chaocraft:models/chao/animal/child/13_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_goma_te.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 23, Slot.LEGS, "chaocraft:models/chao/animal/child/23_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kj02.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kj01.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 26, Slot.TAIL, "chaocraft:models/chao/animal/adult/26_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gusa05.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 2, Slot.ARMS, "chaocraft:models/chao/animal/child/02_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_usa_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 29, Slot.EARS, "chaocraft:models/chao/animal/child/29_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gori06.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gori05.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 5, Slot.ARMS, "chaocraft:models/chao/animal/child/05_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_dra_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 34, Slot.EARS, "chaocraft:models/chao/animal/child/34_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kal05.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kal06.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 16, Slot.LEGS, "chaocraft:models/chao/animal/adult/16_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_tora_tume16.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_tora_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_tora_asib.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 24, Slot.FOREHEAD, "chaocraft:models/chao/animal/adult/24_forehead.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gom04.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 15, Slot.ARMS, "chaocraft:models/chao/animal/child/15_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_ska_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 7, Slot.LEGS, "chaocraft:models/chao/animal/child/07_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_rako_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.006351154F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 14, Slot.HORNS, "chaocraft:models/chao/animal/child/14_horns.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_shep_tuno.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 26, Slot.EARS, "chaocraft:models/chao/animal/adult/26_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gusa03.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gusa04.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 31, Slot.EARS, "chaocraft:models/chao/animal/child/31_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_zo05.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_zo04.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 4, Slot.EARS, "chaocraft:models/chao/animal/child/04_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kon_kami.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 13, Slot.TAIL, "chaocraft:models/chao/animal/adult/13_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_goma_te.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 11, Slot.FOREHEAD, "chaocraft:models/chao/animal/child/11_forehead.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_phen_maegami.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 31, Slot.EARS, "chaocraft:models/chao/animal/adult/31_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gzo04.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gzo03.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 20, Slot.TAIL, "chaocraft:models/chao/animal/child/20_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/c_seal_tail.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.14F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 4, Slot.TAIL, "chaocraft:models/chao/animal/adult/04_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kon_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 17, Slot.EARS, "chaocraft:models/chao/animal/adult/17_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_uni_mimi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 28, Slot.ARMS, "chaocraft:models/chao/animal/child/28_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_wb04.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_wb03.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 26, Slot.ARMS, "chaocraft:models/chao/animal/child/26_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_usa01.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 16, Slot.EARS, "chaocraft:models/chao/animal/child/16_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_tora_mimib.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_tora_mimia.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 3, Slot.TAIL, "chaocraft:models/chao/animal/adult/03_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_chit_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 34, Slot.ARMS, "chaocraft:models/chao/animal/child/34_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kal02.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kal01.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 14, Slot.LEGS, "chaocraft:models/chao/animal/child/14_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_shep_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 9, Slot.LEGS, "chaocraft:models/chao/animal/child/09_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kuja_asib.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kuja_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 8, Slot.FOREHEAD, "chaocraft:models/chao/animal/adult/08_forehead.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_oum_maegami.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 32, Slot.ARMS, "chaocraft:models/chao/animal/adult/32_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_sk03.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gsk01.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_sk02.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 21, Slot.TAIL, "chaocraft:models/chao/animal/child/21_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/c_penguin_arms_1.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/c_penguin_arms_2.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.14F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 3, Slot.ARMS, "chaocraft:models/chao/animal/adult/03_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_chit_tea.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_chit_tume16.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_chit_teb.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 5, Slot.WINGS, "chaocraft:models/chao/animal/child/05_wings.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_dra_haneb.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_dra_hanea.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 17, Slot.LEGS, "chaocraft:models/chao/animal/child/17_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_uni_hidume.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_uni_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_uni_asinoura16.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 4, Slot.LEGS, "chaocraft:models/chao/animal/child/04_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kon_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kon_tume.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 4, Slot.WINGS, "chaocraft:models/chao/animal/child/04_wings.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kon_hane.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 26, Slot.TAIL, "chaocraft:models/chao/animal/child/26_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_usa04.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.14F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 34, Slot.EARS, "chaocraft:models/chao/animal/adult/34_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gkl03.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kal05.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 17, Slot.EARS, "chaocraft:models/chao/animal/child/17_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_uni_mimi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 27, Slot.HORNS, "chaocraft:models/chao/animal/child/27_horns.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_bb06.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 3, Slot.LEGS, "chaocraft:models/chao/animal/child/03_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_chit_asib.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_chit_tume16.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_chit_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 25, Slot.LEGS, "chaocraft:models/chao/animal/adult/25_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_tu03.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gtu03.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 20, Slot.ARMS, "chaocraft:models/chao/animal/child/20_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/c_seal_arms.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 34, Slot.TAIL, "chaocraft:models/chao/animal/child/34_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kal01.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.14F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 3, Slot.TAIL, "chaocraft:models/chao/animal/child/03_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_chit_sippo.png"), 0.8F, 0.8F, 0.8F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 18, Slot.FACE, "chaocraft:models/chao/animal/child/18_face.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_ino_kiba16.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 16, Slot.ARMS, "chaocraft:models/chao/animal/adult/16_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_tora_tume16.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_tora_asib.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_tora_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 5, Slot.HORNS, "chaocraft:models/chao/animal/child/05_horns.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_dra_tuno.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 8, Slot.WINGS, "chaocraft:models/chao/animal/child/08_wings.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_oum_hane.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 5, Slot.HORNS, "chaocraft:models/chao/animal/adult/05_horns.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_dra_tuno.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 25, Slot.WINGS, "chaocraft:models/chao/animal/child/25_wings.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_tu01.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 12, Slot.ARMS, "chaocraft:models/chao/animal/child/12_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_ara_asi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 22, Slot.TAIL, "chaocraft:models/chao/animal/child/22_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_rk04.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.14F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 33, Slot.LEGS, "chaocraft:models/chao/animal/child/33_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_mo03.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_mo04.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_mo02.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 1, Slot.LEGS, "chaocraft:models/chao/animal/adult/01_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kuma_tume16.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kuma_asib.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_kuma_te.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 3, Slot.LEGS, "chaocraft:models/chao/animal/adult/03_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_chit_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_chit_tume16.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_chit_asib.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 33, Slot.ARMS, "chaocraft:models/chao/animal/child/33_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_mo01.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_mo04.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 10, Slot.ARMS, "chaocraft:models/chao/animal/child/10_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_pen_te16.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 31, Slot.ARMS, "chaocraft:models/chao/animal/child/31_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_zo01.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_zo02.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_zo03.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 27, Slot.ARMS, "chaocraft:models/chao/animal/child/27_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_bb01.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_bb05.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 12, Slot.EARS, "chaocraft:models/chao/animal/adult/12_ears.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_ara_mimi.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 9, Slot.TAIL, "chaocraft:models/chao/animal/child/09_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kuja_sippob.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_kuja_sippoa.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 22, Slot.TAIL, "chaocraft:models/chao/animal/adult/22_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_grk02.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 6, Slot.ARMS, "chaocraft:models/chao/animal/adult/06_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gori_asib.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_gori_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 2, Slot.TAIL, "chaocraft:models/chao/animal/child/02_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_usa_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 8, Slot.FOREHEAD, "chaocraft:models/chao/animal/child/08_forehead.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_oum_maegami.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 16, Slot.TAIL, "chaocraft:models/chao/animal/adult/16_tail.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_tora_mimia.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 15, Slot.LEGS, "chaocraft:models/chao/animal/child/15_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_ska_asib.png"), 1.0F, 1.0F, 1.0F, 1.0F), new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_ska_asia.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(false, 2, Slot.LEGS, "chaocraft:models/chao/animal/child/02_legs.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alpc_usa_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
        put(true, 2, Slot.ARMS, "chaocraft:models/chao/animal/adult/02_arms.cmesh", List.of(new MaterialSpec(new Identifier("chaocraft:textures/entity/chao/animal/alp_usa_sippo.png"), 1.0F, 1.0F, 1.0F, 1.0F)),
                0.0F, 0.0F, 0.0F, -0.7071068F, -0.0F, -0.0F, 0.7071068F, 1.0F, 1.0F, 1.0F);
    }

    private ChaoAnimalPartCatalog() {}

    private static void put(boolean adult, int animalId, Slot slot, String model, List<MaterialSpec> materials,
            float px, float py, float pz, float qx, float qy, float qz, float qw, float sx, float sy, float sz) {
        PARTS.put(new Key(adult, ChaoAnimalType.fromOrdinal(animalId), slot),
                new PartSpec(new Identifier(model), materials, new Vector3f(px, py, pz),
                        new Quaternionf(qx, qy, qz, qw), new Vector3f(sx, sy, sz)));
    }

    public static PartSpec resolve(boolean adult, ChaoAnimalType animal, Slot slot) {
        if (animal == null || animal == ChaoAnimalType.NONE) return null;
        return PARTS.get(new Key(adult, animal, slot));
    }

    public static List<ChaoAnimalType> available(boolean adult, Slot slot) {
        List<ChaoAnimalType> result = new ArrayList<>();
        result.add(ChaoAnimalType.NONE);
        for (ChaoAnimalType animal : ChaoAnimalType.values()) {
            if (animal != ChaoAnimalType.NONE && PARTS.containsKey(new Key(adult, animal, slot))) result.add(animal);
        }
        return List.copyOf(result);
    }

    public record Key(boolean adult, ChaoAnimalType animal, Slot slot) {}

    public record MaterialSpec(Identifier texture, float r, float g, float b, float a) {}

    public record PartSpec(Identifier model, List<MaterialSpec> materials, Vector3f position, Quaternionf rotation, Vector3f scale) {}
}