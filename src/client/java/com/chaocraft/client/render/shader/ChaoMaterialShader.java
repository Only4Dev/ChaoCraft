package com.chaocraft.client.render.shader;

import com.chaocraft.ChaoCraft;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormats;

/** Base Chao material shader; lighting is supplied every draw, never baked into cache identity. */
public final class ChaoMaterialShader {
    private static ShaderProgram program;
    private static boolean loadLogged;

    private ChaoMaterialShader() {}

    public static void register() {
        CoreShaderRegistrationCallback.EVENT.register(context ->
                context.register(
                        ChaoCraft.id("chao_material"),
                        VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                        loaded -> {
                            program = loaded;
                            if (!loadLogged) {
                                loadLogged = true;
                                ChaoCraft.LOGGER.info("Loaded Chao draw-time material shader.");
                            }
                        }
                )
        );
    }

    public static boolean isAvailable() { return program != null; }
    public static ShaderProgram get() { return program; }
}
