package com.chaocraft.client.animation;

import java.util.Map;

/**
 * Known SA2 Chao node indices recovered from the original attachment contracts.
 *
 * <p>This is intentionally incomplete. CP12A does not guess unknown node names;
 * the remaining hierarchy will be imported from AL_RootObject before animation
 * transforms are connected to the production renderer.</p>
 */
public final class ChaoRigContract {
    private static final Map<Integer, String> KNOWN_NODES = Map.ofEntries(
            Map.entry(1, "Body"),
            Map.entry(3, "Left Arm"),
            Map.entry(6, "Left Leg"),
            Map.entry(8, "Tail"),
            Map.entry(10, "Right Arm"),
            Map.entry(13, "Right Leg"),
            Map.entry(16, "Head"),
            Map.entry(24, "Left Ear"),
            Map.entry(26, "Right Ear"),
            Map.entry(28, "Face/Tongue"),
            Map.entry(29, "Forehead"),
            Map.entry(30, "Left Horn"),
            Map.entry(31, "Right Horn"),
            Map.entry(37, "Left Wing"),
            Map.entry(39, "Right Wing")
    );

    private ChaoRigContract() {
    }

    public static String label(int nodeIndex) {
        return KNOWN_NODES.getOrDefault(nodeIndex, "Node " + nodeIndex);
    }

    public static boolean isKnown(int nodeIndex) {
        return KNOWN_NODES.containsKey(nodeIndex);
    }
}
