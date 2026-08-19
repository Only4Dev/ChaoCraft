package com.chaocraft.client.render.shader;

import com.chaocraft.ChaoCraft;
import com.chaocraft.client.render.mesh.ChaoSkinnedVertexFormat;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.gl.ShaderProgram;

/**
 * SA2 40-node Child skinning shader for the stable high-poly VBO.
 *
 * <p>Its fragment path intentionally mirrors Minecraft's ordinary entity
 * preview shader; only the vertex position/normal stage is extended.</p>
 */
public final class ChaoSkinningShader {
    private static ShaderProgram program;
    private static boolean loadLogged;

    private ChaoSkinningShader() {}

    public static void register() {
        CoreShaderRegistrationCallback.EVENT.register(context ->
                context.register(
                        ChaoCraft.id("chao_skinning"),
                        ChaoSkinnedVertexFormat.FORMAT,
                        loaded -> {
                            program = loaded;
                            if (!loadLogged) {
                                loadLogged = true;
                                ChaoCraft.LOGGER.info("Loaded Chao native SA2 skinning shader.");
                            }
                        }
                )
        );
    }

    public static boolean isAvailable() { return program != null; }
    public static ShaderProgram get() { return program; }
}
