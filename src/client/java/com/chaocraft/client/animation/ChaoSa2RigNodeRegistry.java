package com.chaocraft.client.animation;

import java.util.List;

/**
 * Chao Garden / SA2 40-node rig bible.
 *
 * <p>Node indices, hierarchy and object ids come directly from the SA Tools
 * Chao model. Semantic labels are based on the in-tool visual audit and are
 * intentionally kept separate from deformation policy: pivots and attachments
 * remain first-class animated nodes even when no body vertices are weighted to
 * them.</p>
 */
public final class ChaoSa2RigNodeRegistry {
    public static final int ROOT = 0;
    public static final int CORE = 1;

    public static final int LEFT_ARM = 3;
    public static final int LEFT_HAND_ATTACHMENT = 4;
    public static final int LEFT_LEG = 6;
    public static final int TAIL = 8;

    public static final int RIGHT_ARM = 10;
    public static final int RIGHT_HAND_ATTACHMENT = 11;
    public static final int RIGHT_LEG = 13;

    public static final int HEAD = 16;
    public static final int LEFT_EYE = 18;
    public static final int LEFT_EYELID = 19;
    public static final int RIGHT_EYE = 21;
    public static final int RIGHT_EYELID = 22;
    public static final int LEFT_UPPER_HEAD = 23;
    public static final int RIGHT_UPPER_HEAD = 25;
    public static final int MOUTH = 27;
    public static final int MOUTH_ATTACHMENT = 28;
    public static final int FOREHEAD_ATTACHMENT = 29;
    public static final int EMOTION = 33;
    public static final int BACK_ATTACHMENT = 34;
    public static final int CHEST_MEDAL_ATTACHMENT = 35;
    public static final int LEFT_WING = 37;
    public static final int RIGHT_WING = 39;

    private static final List<NodeInfo> NODES = List.of(
        new NodeInfo(0, -1, "object_00016DB4", "Root / floor origin", Role.ROOT, false),
        new NodeInfo(1, 0, "object_00016D7C", "Core / belly center", Role.DEFORM, true),
        new NodeInfo(2, 1, "object_00016804", "Left arm pivot/helper", Role.PIVOT, false),
        new NodeInfo(3, 2, "object_000167CC", "Left visible arm/shoulder", Role.DEFORM, true),
        new NodeInfo(4, 3, "object_000163A4", "Left hand held-object attachment", Role.ATTACHMENT, false),
        new NodeInfo(5, 1, "object_0001636C", "Left leg pivot/helper", Role.PIVOT, false),
        new NodeInfo(6, 5, "object_00016334", "Left visible leg", Role.DEFORM, true),
        new NodeInfo(7, 1, "object_00015F54", "Tail pivot/helper", Role.PIVOT, false),
        new NodeInfo(8, 7, "object_00015F1C", "Visible tail", Role.DEFORM, true),
        new NodeInfo(9, 1, "object_00015B84", "Right arm pivot/helper", Role.PIVOT, false),
        new NodeInfo(10, 9, "object_00015B4C", "Right visible arm/shoulder", Role.DEFORM, true),
        new NodeInfo(11, 10, "object_00015724", "Right hand held-object attachment", Role.ATTACHMENT, false),
        new NodeInfo(12, 1, "object_000156EC", "Right leg pivot/helper", Role.PIVOT, false),
        new NodeInfo(13, 12, "object_000156B4", "Right visible leg", Role.DEFORM, true),
        new NodeInfo(14, 1, "object_000152E4", "Head chain pivot A", Role.PIVOT, false),
        new NodeInfo(15, 14, "object_000152AC", "Head chain pivot B", Role.PIVOT, false),
        new NodeInfo(16, 15, "object_00015274", "Visible head", Role.DEFORM, true),
        new NodeInfo(17, 16, "object_00014554", "Left eye pivot/helper", Role.PIVOT, false),
        new NodeInfo(18, 17, "object_0001451C", "Visible left eye", Role.DEFORM, true),
        new NodeInfo(19, 17, "object_00013FAC", "Visible left eyelid", Role.DEFORM, true),
        new NodeInfo(20, 16, "object_00013B74", "Right eye pivot/helper", Role.PIVOT, false),
        new NodeInfo(21, 20, "object_00013B3C", "Visible right eye", Role.DEFORM, true),
        new NodeInfo(22, 20, "object_00013604", "Visible right eyelid", Role.DEFORM, true),
        new NodeInfo(23, 16, "object_000131CC", "Upper-head left visible part / horn-ear base", Role.DEFORM, true),
        new NodeInfo(24, 16, "object_00012F14", "Upper-head left helper / tip attachment", Role.ATTACHMENT, false),
        new NodeInfo(25, 16, "object_00012EDC", "Upper-head right visible part / horn-ear base", Role.DEFORM, true),
        new NodeInfo(26, 16, "object_00012C14", "Upper-head right helper / tip attachment", Role.ATTACHMENT, false),
        new NodeInfo(27, 16, "object_00012BDC", "Visible mouth region", Role.DEFORM, true),
        new NodeInfo(28, 27, "object_000128BC", "Mouth item attachment (food/pacifier candidate)", Role.ATTACHMENT, false),
        new NodeInfo(29, 16, "object_00012884", "Forehead attachment/helper", Role.ATTACHMENT, false),
        new NodeInfo(30, 16, "object_0001284C", "Upper-head left rear attachment/helper", Role.ATTACHMENT, false),
        new NodeInfo(31, 16, "object_00012814", "Upper-head right rear attachment/helper", Role.ATTACHMENT, false),
        new NodeInfo(32, 16, "object_000127DC", "Emotion parent/pivot", Role.PIVOT, false),
        new NodeInfo(33, 32, "object_000127A4", "Emotion attachment/controller", Role.ATTACHMENT, false),
        new NodeInfo(34, 1, "object_0001276C", "Back-center attachment between wings", Role.ATTACHMENT, false),
        new NodeInfo(35, 1, "object_00012734", "Chest / medal attachment", Role.ATTACHMENT, false),
        new NodeInfo(36, 1, "object_000126FC", "Left wing pivot/helper", Role.PIVOT, false),
        new NodeInfo(37, 36, "object_000126C4", "Visible left wing", Role.DEFORM, true),
        new NodeInfo(38, 1, "object_000123FC", "Right wing pivot/helper", Role.PIVOT, false),
        new NodeInfo(39, 38, "object_000123C4", "Visible right wing", Role.DEFORM, true)
    );

    private ChaoSa2RigNodeRegistry() {}

    public static NodeInfo node(int index) {
        if (index < 0 || index >= NODES.size()) {
            throw new IndexOutOfBoundsException("SA2 Chao rig node " + index);
        }
        return NODES.get(index);
    }

    public static List<NodeInfo> nodes() {
        return NODES;
    }

    public static boolean isVisibleDeformNode(int index) {
        return node(index).visibleDeform();
    }

    public enum Role {
        ROOT,
        DEFORM,
        PIVOT,
        ATTACHMENT
    }

    public record NodeInfo(
            int index,
            int parent,
            String objectId,
            String semantic,
            Role role,
            boolean visibleDeform
    ) {}
}
