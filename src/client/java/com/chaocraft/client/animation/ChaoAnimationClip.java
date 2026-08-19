package com.chaocraft.client.animation;

import org.joml.Vector3f;

import java.util.Collections;
import java.util.Map;

/** Immutable raw SA2 Chao animation clip imported from AnimJSONConverter output. */
public record ChaoAnimationClip(
        int exportIndex,
        String name,
        int frames,
        int modelParts,
        int interpolationMode,
        boolean shortRot,
        Map<Integer, NodeTrack> nodes
) {
    public ChaoAnimationClip {
        nodes = Collections.unmodifiableMap(nodes);
    }

    public int animatedNodeCount() {
        return nodes.size();
    }

    /** Sparse original key data for one SA2 model/node index. */
    public record NodeTrack(
            Map<Integer, Vector3f> positions,
            Map<Integer, RotationKey> rotations,
            int keyframeCount
    ) {
        public NodeTrack {
            positions = Collections.unmodifiableMap(positions);
            rotations = Collections.unmodifiableMap(rotations);
        }
    }

    /** Raw Ninja binary-angle rotation values. One full revolution = 0x10000. */
    public record RotationKey(int x, int y, int z) {
        public float xRadians() { return binaryAngleToRadians(x); }
        public float yRadians() { return binaryAngleToRadians(y); }
        public float zRadians() { return binaryAngleToRadians(z); }

        private static float binaryAngleToRadians(int value) {
            int signed16 = (short) (value & 0xFFFF);
            return (float) (signed16 * (Math.PI * 2.0D / 65536.0D));
        }
    }
}
