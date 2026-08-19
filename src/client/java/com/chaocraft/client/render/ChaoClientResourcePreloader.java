package com.chaocraft.client.render;

import com.chaocraft.ChaoCraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Locale;

/**
 * One-time atomic Chao visual resource warm-up.
 *
 * <p>.cmesh parsing is handled by {@code ChaoMeshRepository}. Core shaders are
 * loaded by Fabric's shader registration. This class eagerly resolves and uploads
 * every Chao entity texture after a client-resource reload so first sight of a
 * Chao never pays texture decode/upload cost in the middle of gameplay.</p>
 *
 * <p>It deliberately preloads atomic resources only. Complete Chao appearance
 * combinations remain demand-built and shared through {@code ChaoGpuRenderCache}.</p>
 */
public final class ChaoClientResourcePreloader {
    private static volatile List<Identifier> pendingTextures = List.of();

    private ChaoClientResourcePreloader() {
    }

    /** Collects the merged resource-pack view without touching OpenGL. */
    public static void prepare(ResourceManager manager) {
        Map<Identifier, Resource> resources = manager.findResources(
                "textures/entity/chao",
                id -> {
                    String path = id.getPath().toLowerCase(Locale.ROOT);
                    return path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(".jpeg");
                }
        );

        List<Identifier> textures = new ArrayList<>(resources.keySet());
        textures.sort(Comparator.comparing(Identifier::toString));
        pendingTextures = List.copyOf(textures);
    }

    /**
     * Runs once on the client/render thread after reload completion.
     *
     * <p>The deliberate startup stall is preferable to hundreds of small first-use
     * stalls later. TextureManager keeps the uploaded textures resident until the
     * next resource reload/client shutdown.</p>
     */
    public static void preloadPending(MinecraftClient client) {
        List<Identifier> textures = pendingTextures;
        if (textures.isEmpty()) {
            return;
        }
        pendingTextures = List.of();

        long started = System.nanoTime();
        int loaded = 0;
        int failed = 0;

        for (Identifier texture : textures) {
            try {
                client.getTextureManager().bindTexture(texture);
                loaded++;
            } catch (RuntimeException exception) {
                failed++;
                ChaoCraft.LOGGER.warn("Failed to preload Chao texture {}", texture, exception);
            }
        }

        double millis = (System.nanoTime() - started) / 1_000_000.0D;
        ChaoCraft.LOGGER.info(
                "Preloaded {} Chao textures to the client texture manager in {} ms{}",
                loaded,
                String.format(Locale.ROOT, "%.1f", millis),
                failed == 0 ? "" : " (" + failed + " failed)"
        );
    }

    public static void clearPending() {
        pendingTextures = List.of();
    }
}
