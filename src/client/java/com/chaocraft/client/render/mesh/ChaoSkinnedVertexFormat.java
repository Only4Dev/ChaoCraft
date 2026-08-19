package com.chaocraft.client.render.mesh;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.render.VertexFormats;

/**
 * Stable Child skinning VBO layout.
 *
 * <p>The vanilla entity fields are preserved byte-for-byte in meaning:
 * UV1 remains Overlay and UV2 remains Light. Two extra SHORT2 UV channels are
 * reserved exclusively for Chao skinning so animation never corrupts Minecraft
 * presentation state.</p>
 *
 * <p>UV3.x and UV4.x each contain one packed 16-bit influence:
 * low 6 bits = SA2 bone index, next 10 bits = normalized weight.</p>
 */
public final class ChaoSkinnedVertexFormat {
    public static final VertexFormatElement SKIN0 =
            new VertexFormatElement(
                    3,
                    VertexFormatElement.ComponentType.SHORT,
                    VertexFormatElement.Type.UV,
                    2
            );

    public static final VertexFormatElement SKIN1 =
            new VertexFormatElement(
                    4,
                    VertexFormatElement.ComponentType.SHORT,
                    VertexFormatElement.Type.UV,
                    2
            );

    public static final VertexFormat FORMAT = new VertexFormat(
            ImmutableMap.<String, VertexFormatElement>builder()
                    .put("Position", VertexFormats.POSITION_ELEMENT)
                    .put("Color", VertexFormats.COLOR_ELEMENT)
                    .put("UV0", VertexFormats.TEXTURE_ELEMENT)
                    .put("UV1", VertexFormats.OVERLAY_ELEMENT)
                    .put("UV2", VertexFormats.LIGHT_ELEMENT)
                    .put("UV3", SKIN0)
                    .put("UV4", SKIN1)
                    .put("Normal", VertexFormats.NORMAL_ELEMENT)
                    .put("Padding", VertexFormats.PADDING_ELEMENT)
                    .build()
    );

    private ChaoSkinnedVertexFormat() {}
}
