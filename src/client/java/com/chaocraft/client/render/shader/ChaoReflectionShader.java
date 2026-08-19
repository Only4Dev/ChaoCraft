package com.chaocraft.client.render.shader;

import com.chaocraft.ChaoCraft;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormats;

/**
 * Registers the Chao Viewer's cubemap-material approximation as a Fabric core shader.
 *
 * <p>The shader samples ChaoCraft's static Viewer cubemap strips. It never captures
 * or reflects the Minecraft world. The VBO stays reusable; camera-relative reflection
 * math happens at draw time on the GPU.</p>
 */
public final class ChaoReflectionShader {
    private static ShaderProgram program;
    private static boolean loadLogged;

    private ChaoReflectionShader() {
    }

    public static void register() {
        CoreShaderRegistrationCallback.EVENT.register(context ->
                context.register(
                        ChaoCraft.id("chao_reflection"),
                        VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                        loaded -> {
                            program = loaded;
                            if (!loadLogged) {
                                loadLogged = true;
                                ChaoCraft.LOGGER.info("Loaded Chao Viewer-style reflection shader.");
                            }
                        }
                )
        );
    }

    public static boolean isAvailable() {
        return program != null;
    }

    public static ShaderProgram get() {
        return program;
    }
}
