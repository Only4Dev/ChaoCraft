package com.chaocraft.client.render.shader;

import com.chaocraft.ChaoCraft;
import com.chaocraft.client.render.mesh.ChaoSkinnedVertexFormat;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.gl.ShaderProgram;

/** Reflection variant of the native SA2 skinning shader. */
public final class ChaoReflectionSkinningShader {
    private static ShaderProgram program;
    private static boolean loadLogged;

    private ChaoReflectionSkinningShader() {}

    public static void register() {
        CoreShaderRegistrationCallback.EVENT.register(context ->
                context.register(
                        ChaoCraft.id("chao_reflection_skinning"),
                        ChaoSkinnedVertexFormat.FORMAT,
                        loaded -> {
                            program = loaded;
                            if (!loadLogged) {
                                loadLogged = true;
                                ChaoCraft.LOGGER.info("Loaded Chao skinned reflection shader.");
                            }
                        }
                )
        );
    }

    public static boolean isAvailable() { return program != null; }
    public static ShaderProgram get() { return program; }
}
