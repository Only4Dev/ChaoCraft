package com.chaocraft.client.animation;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Map;

/**
 * Samples original SA2 Chao node motions as draw-time rigid transforms.
 *
 * <p>The exact bind hierarchy/scales live in {@link ChaoSa2RigDefinition}.
 * ChaoCraft geometry is already baked in model space, so each animated batch
 * receives animatedWorld * inverse(bindWorld). Animation frame/time never
 * participates in VBO cache identity.</p>
 */
public final class ChaoAnimationPose {
    public static final int NODE_COUNT = ChaoSa2RigDefinition.NODE_COUNT;

    private static final Matrix4f CHILD_COORDINATE =
            new Matrix4f().rotateX((float) Math.toRadians(90.0D));
    private static final Matrix4f CHILD_COORDINATE_INVERSE =
            new Matrix4f(CHILD_COORDINATE).invert();

    private final Matrix4f[] deltas;

    private ChaoAnimationPose(Matrix4f[] deltas) {
        this.deltas = deltas;
    }

    public static ChaoAnimationPose sample(
            ChaoAnimationClip clip, double frame, boolean childCoordinateSpace) {
        if (clip == null) return identity();

        Matrix4f[] animatedWorld = new Matrix4f[NODE_COUNT];
        Matrix4f[] deltas = new Matrix4f[NODE_COUNT];

        for (int i = 0; i < NODE_COUNT; i++) {
            ChaoSa2RigDefinition.Node bind = ChaoSa2RigDefinition.node(i);
            ChaoAnimationClip.NodeTrack track = clip.nodes().get(i);

            Vector3f position = track == null || track.positions().isEmpty()
                    ? new Vector3f(bind.position())
                    : samplePosition(track.positions(), frame);
            Vector3f rotation = track == null || track.rotations().isEmpty()
                    ? new Vector3f(bind.rotation())
                    : sampleRotation(track.rotations(), frame);

            Matrix4f animatedLocal = ChaoSa2RigDefinition.composeLocal(
                    position, rotation, bind.scale());

            if (bind.parent() >= 0) {
                animatedWorld[i] = new Matrix4f(animatedWorld[bind.parent()])
                        .mul(animatedLocal);
            } else {
                animatedWorld[i] = animatedLocal;
            }

            Matrix4f delta = new Matrix4f(animatedWorld[i])
                    .mul(ChaoSa2RigDefinition.inverseBindWorld(i));

            if (childCoordinateSpace) {
                delta = new Matrix4f(CHILD_COORDINATE)
                        .mul(delta)
                        .mul(CHILD_COORDINATE_INVERSE);
            }
            deltas[i] = delta;
        }

        return new ChaoAnimationPose(deltas);
    }

    public static ChaoAnimationPose identity() {
        Matrix4f[] deltas = new Matrix4f[NODE_COUNT];
        for (int i = 0; i < NODE_COUNT; i++) {
            deltas[i] = new Matrix4f();
        }
        return new ChaoAnimationPose(deltas);
    }

    public Matrix4f delta(int nodeIndex) {
        if (nodeIndex < 0 || nodeIndex >= deltas.length) return new Matrix4f();
        return deltas[nodeIndex];
    }

    private static Vector3f samplePosition(Map<Integer, Vector3f> keys, double frame) {
        KeyPair<Vector3f> pair = enclosing(keys, frame);
        if (pair == null) return new Vector3f();
        if (pair.leftFrame == pair.rightFrame) return new Vector3f(pair.left);
        float t = (float) ((frame - pair.leftFrame) / (pair.rightFrame - pair.leftFrame));
        return new Vector3f(pair.left).lerp(pair.right, t);
    }

    private static Vector3f sampleRotation(
            Map<Integer, ChaoAnimationClip.RotationKey> keys, double frame) {
        KeyPair<ChaoAnimationClip.RotationKey> pair = enclosing(keys, frame);
        if (pair == null) return new Vector3f();

        Vector3f left = radians(pair.left);
        if (pair.leftFrame == pair.rightFrame) return left;

        Vector3f right = radians(pair.right);
        float t = (float) ((frame - pair.leftFrame) / (pair.rightFrame - pair.leftFrame));
        return new Vector3f(
                lerpAngle(left.x, right.x, t),
                lerpAngle(left.y, right.y, t),
                lerpAngle(left.z, right.z, t));
    }

    private static Vector3f radians(ChaoAnimationClip.RotationKey key) {
        return new Vector3f(key.xRadians(), key.yRadians(), key.zRadians());
    }

    private static float lerpAngle(float a, float b, float t) {
        float delta = (float) Math.atan2(Math.sin(b - a), Math.cos(b - a));
        return a + delta * t;
    }

    private static <T> KeyPair<T> enclosing(Map<Integer, T> keys, double frame) {
        if (keys.isEmpty()) return null;

        int leftFrame = Integer.MIN_VALUE;
        int rightFrame = Integer.MAX_VALUE;
        T left = null;
        T right = null;

        for (Map.Entry<Integer, T> entry : keys.entrySet()) {
            int keyFrame = entry.getKey();
            if (keyFrame <= frame && keyFrame > leftFrame) {
                leftFrame = keyFrame;
                left = entry.getValue();
            }
            if (keyFrame >= frame && keyFrame < rightFrame) {
                rightFrame = keyFrame;
                right = entry.getValue();
            }
        }

        if (left == null) {
            Map.Entry<Integer, T> first = keys.entrySet().iterator().next();
            return new KeyPair<>(
                    first.getKey(), first.getValue(),
                    first.getKey(), first.getValue());
        }
        if (right == null) {
            return new KeyPair<>(leftFrame, left, leftFrame, left);
        }
        return new KeyPair<>(leftFrame, left, rightFrame, right);
    }

    private record KeyPair<T>(int leftFrame, T left, int rightFrame, T right) {
    }
}
