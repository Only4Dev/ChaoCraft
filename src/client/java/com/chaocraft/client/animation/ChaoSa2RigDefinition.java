package com.chaocraft.client.animation;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Exact 40-node SA2 Chao bind rig used by the original AL_RootObject/al_ncn.
 *
 * <p>This definition was generated from the user-exported SA Tools Collada
 * ChaoSaTool.dae and cross-checked against the original NJS_OBJECT
 * position/rotation values. The local transform convention was mathematically
 * verified against every DAE joint matrix:</p>
 *
 * <pre>
 * local = Translation * Rz * Ry * Rx * Scale
 * </pre>
 *
 * <p>Maximum bind-matrix reconstruction error during generation:
 * 9.436943762e-08. Bind-world inverse matrices are computed once at class load,
 * never once per rendered frame.</p>
 */
public final class ChaoSa2RigDefinition {
    public static final int NODE_COUNT = 40;
    public static final float BIND_RECONSTRUCTION_MAX_ERROR = 9.43694376E-08F;

    private static final Node[] NODES = {
            new Node(-1, v(0F, 0F, 0F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(0, v(0F, 1F, -9.99999997E-07F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(1, v(0.548399985F, 0.683363974F, 0.0896859989F), v(-0.101146858F, 0F, 0.401423597F), v(1.00000001F, 0.999999997F, 1.00000001F)),
            new Node(2, v(0F, 0F, 0F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(3, v(-0.0950189978F, -1.09773803F, 0.0502279997F), v(0.0931893329F, 0.0394041315F, -0.399506121F), v(1.00000003F, 0.999999969F, 1.00000005F)),
            new Node(1, v(0.348432988F, 0.0473879986F, -0.0239819996F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(5, v(0F, 0F, 0F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(1, v(0F, -0.0255399998F, -0.800000012F), v(1.39621014F, 0F, 0F), v(1F, 0.999999978F, 0.999999978F)),
            new Node(7, v(0F, 0F, 0F), v(-0.261735472F, 0F, 0F), v(1F, 1.00000001F, 1.00000001F)),
            new Node(1, v(-0.548430979F, 0.683364987F, 0.0896859989F), v(0.101146858F, -3.14159265F, -0.401423597F), v(1.00000001F, 0.999999997F, 1.00000001F)),
            new Node(9, v(0F, 0F, 0F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(10, v(-0.0725810006F, -1.10720801F, -0.0492659993F), v(0.0931893329F, 3.10209265F, -0.399506121F), v(0.999999992F, 0.999999972F, 0.999999992F)),
            new Node(1, v(-0.348432004F, 0.0473890007F, -0.0239819996F), v(0F, -3.14159265F, 0F), v(1F, 1F, 1F)),
            new Node(12, v(0F, 0F, 0F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(1, v(0F, 0.5F, 0F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(14, v(0F, 0F, 0F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(15, v(0F, 0F, 0F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(16, v(0.572955012F, 0.970260024F, 1.250471F), v(-0.174490315F, 0.331531598F, -0.0348980629F), v(1.00320005F, 1.38020014F, 0.300000002F)),
            new Node(17, v(0F, 0F, 0F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(17, v(0F, 0F, 0F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(16, v(-0.572977006F, 0.970260024F, 1.25048304F), v(-0.174490315F, -0.331531598F, 0.0348980629F), v(1.00320005F, 1.38020014F, 0.300000002F)),
            new Node(20, v(1.70000003E-05F, 1.99999999E-06F, 0F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(20, v(1.70000003E-05F, 1.99999999E-06F, 0F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(16, v(0.59999299F, 1.84087205F, 0.0500009991F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(16, v(0.699999988F, 2F, -0.0999990031F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(16, v(-0.600009978F, 1.84087205F, 0.0500000007F), v(0F, -3.14159265F, 0F), v(1F, 1F, 1F)),
            new Node(16, v(-0.700001001F, 2F, -0.0999990031F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(16, v(0F, 0F, 0.0188059993F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(27, v(0F, 0.570258975F, 1.38120604F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(16, v(0F, 1.97026002F, 0.899999976F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(16, v(0.870998979F, 1.97026002F, -0.300000012F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(16, v(-0.871001005F, 1.97026002F, -0.300000012F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(16, v(0F, 3.29999995F, 0F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(32, v(0F, 0F, 0F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(1, v(0F, 0.470259994F, -0.5F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(1, v(0F, 0.0702600032F, 1F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(1, v(0.200000003F, 0.470259994F, -0.402768999F), v(0F, 0.523566818F, 0F), v(0.999999993F, 1F, 0.999999993F)),
            new Node(36, v(0F, 0F, 0F), v(0F, 0F, 0F), v(1F, 1F, 1F)),
            new Node(1, v(-0.200000003F, 0.470259994F, -0.402767986F), v(0F, 2.61792996F, 0F), v(1.00000001F, 1F, 1.00000001F)),
            new Node(38, v(0F, 0F, 0F), v(0F, 0F, 0F), v(1F, 1F, 1F))
    };

    private static final Matrix4f[] BIND_WORLD = new Matrix4f[NODE_COUNT];
    private static final Matrix4f[] INVERSE_BIND_WORLD = new Matrix4f[NODE_COUNT];

    static {
        for (int i = 0; i < NODE_COUNT; i++) {
            Node node = NODES[i];
            Matrix4f local = composeLocal(node.position(), node.rotation(), node.scale());
            BIND_WORLD[i] = node.parent() >= 0
                    ? new Matrix4f(BIND_WORLD[node.parent()]).mul(local)
                    : local;
            INVERSE_BIND_WORLD[i] = new Matrix4f(BIND_WORLD[i]).invert();
        }
    }

    private ChaoSa2RigDefinition() {
    }

    public static Node node(int index) {
        if (index < 0 || index >= NODE_COUNT) {
            throw new IndexOutOfBoundsException("SA2 Chao rig node " + index);
        }
        return NODES[index];
    }

    /** Returns a defensive copy because JOML matrices are mutable. */
    public static Matrix4f inverseBindWorld(int index) {
        return new Matrix4f(INVERSE_BIND_WORLD[index]);
    }

    /**
     * Column-vector/JOML equivalent of SA Tools' Ninja RotateXYZ path.
     *
     * <p>SA Tools MatrixFunctions.RotateXYZ calls Z, then Y, then X using its
     * row-vector convention. The equivalent JOML composition is
     * Translation * Rz * Ry * Rx * Scale; this exact order was validated
     * against the exported DAE matrices.</p>
     */
    public static Matrix4f composeLocal(Vector3f position, Vector3f rotation, Vector3f scale) {
        return new Matrix4f()
                .translation(position)
                .rotateZ(rotation.z)
                .rotateY(rotation.y)
                .rotateX(rotation.x)
                .scale(scale);
    }

    private static Vector3f v(float x, float y, float z) {
        return new Vector3f(x, y, z);
    }

    public record Node(int parent, Vector3f position, Vector3f rotation, Vector3f scale) {
    }
}
