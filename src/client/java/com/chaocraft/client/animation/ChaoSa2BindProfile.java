package com.chaocraft.client.animation;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Immutable bind-pose profile for one original SA2 Chao model.
 *
 * <p>All audited Chao models share the same 40-node topology and animation
 * channel numbering. What varies per model is the bind transform (plus a few
 * Ninja evaluation flags). Those per-model values live here so ChaoCraft can
 * reuse one animation runtime without inventing separate Hero/Dark/Swim/etc.
 * rigs.</p>
 *
 * <p>Bind world and inverse-bind matrices are computed once when the profile is
 * loaded. They are never rebuilt per frame or per Chao instance.</p>
 */
public final class ChaoSa2BindProfile {
    public static final int NODE_COUNT = ChaoSa2RigDefinition.NODE_COUNT;

    private final String modelId;
    private final Node[] nodes;
    private final Matrix4f[] bindWorld = new Matrix4f[NODE_COUNT];
    private final Matrix4f[] inverseBindWorld = new Matrix4f[NODE_COUNT];

    public ChaoSa2BindProfile(String modelId, Node[] sourceNodes) {
        if (modelId == null || modelId.isBlank()) {
            throw new IllegalArgumentException("SA2 Chao bind profile requires a model id");
        }
        if (sourceNodes == null || sourceNodes.length != NODE_COUNT) {
            throw new IllegalArgumentException(
                    "SA2 Chao bind profile " + modelId + " must contain " + NODE_COUNT + " nodes");
        }

        this.modelId = modelId;
        this.nodes = sourceNodes.clone();

        for (int i = 0; i < NODE_COUNT; i++) {
            Node node = this.nodes[i];
            if (node == null) {
                throw new IllegalArgumentException(
                        "SA2 Chao bind profile " + modelId + " is missing node " + i);
            }

            Matrix4f local = composeLocal(node);
            int parent = ChaoSa2RigNodeRegistry.node(i).parent();
            bindWorld[i] = parent >= 0
                    ? new Matrix4f(bindWorld[parent]).mul(local)
                    : local;
            inverseBindWorld[i] = new Matrix4f(bindWorld[i]).invert();
        }
    }

    public String modelId() {
        return modelId;
    }

    public Node node(int index) {
        if (index < 0 || index >= NODE_COUNT) {
            throw new IndexOutOfBoundsException("SA2 Chao rig node " + index);
        }
        return nodes[index];
    }

    /** Defensive copy because JOML matrices are mutable. */
    public Matrix4f bindWorld(int index) {
        return new Matrix4f(bindWorld[index]);
    }

    /** Defensive copy because JOML matrices are mutable. */
    public Matrix4f inverseBindWorld(int index) {
        return new Matrix4f(inverseBindWorld[index]);
    }

    /**
     * Original Ninja object local-transform composition used by Chao models.
     *
     * <p>Every audited SA2 Chao profile currently has rotateZYX=false. The
     * alternate branch is retained because the original object flag is part of
     * the source data and this profile is intended to remain lossless.</p>
     */
    public static Matrix4f composeLocal(Node node) {
        return composeLocal(
                node.px(), node.py(), node.pz(),
                node.rx(), node.ry(), node.rz(),
                node.sx(), node.sy(), node.sz(),
                node.rotateZYX());
    }

    /**
     * Compose an animated local transform while retaining this model's bind
     * scale/evaluation convention. Used by the profile-aware pose sampler.
     */
    public static Matrix4f composeAnimatedLocal(
            Vector3f position, Vector3f rotation, Node bindNode) {
        return composeLocal(
                position.x, position.y, position.z,
                rotation.x, rotation.y, rotation.z,
                bindNode.sx(), bindNode.sy(), bindNode.sz(),
                bindNode.rotateZYX());
    }

    private static Matrix4f composeLocal(
            float px, float py, float pz,
            float rx, float ry, float rz,
            float sx, float sy, float sz,
            boolean rotateZYX) {
        Matrix4f result = new Matrix4f().translation(px, py, pz);
        if (rotateZYX) {
            result.rotateX(rx).rotateY(ry).rotateZ(rz);
        } else {
            // Verified Child convention: Translation * Rz * Ry * Rx * Scale.
            result.rotateZ(rz).rotateY(ry).rotateX(rx);
        }
        return result.scale(sx, sy, sz);
    }

    /**
     * Raw per-node source data from the original NJS_OBJECT.
     *
     * <p>IgnorePosition/Rotation/Scale are preserved as source metadata. CP12I.1
     * deliberately does not reinterpret the already-approved Child evaluator;
     * model-specific flag semantics can be activated only after an Adult golden
     * comparison proves they are required.</p>
     */
    public record Node(
            float px, float py, float pz,
            float rx, float ry, float rz,
            float sx, float sy, float sz,
            boolean ignorePosition,
            boolean ignoreRotation,
            boolean ignoreScale,
            boolean rotateZYX
    ) {
        public Vector3f position() {
            return new Vector3f(px, py, pz);
        }

        public Vector3f rotation() {
            return new Vector3f(rx, ry, rz);
        }

        public Vector3f scale() {
            return new Vector3f(sx, sy, sz);
        }
    }
}
