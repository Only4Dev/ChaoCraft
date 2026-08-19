package com.chaocraft.client.render.mesh;

import com.chaocraft.ChaoCraft;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Process-lifetime CPU repository for immutable Chao mesh resources.
 *
 * <p>All .cmesh files are parsed during the client resource reload instead of
 * on the first frame that happens to need a specific Chao/animal part. Renderer
 * instances only retain references to these immutable models. F3+T atomically
 * replaces the repository, so resource packs remain fully supported.</p>
 */
public final class ChaoMeshRepository {
    private static final long LARGE_REPOSITORY_BYTES = 256L * 1024L * 1024L;
    private static volatile Map<Identifier, ChaoMeshModel> models = Map.of();

    private ChaoMeshRepository() {
    }

    public static ChaoMeshModel get(Identifier id) {
        return models.get(id);
    }

    public static void reload(ResourceManager manager) {
        long started = System.nanoTime();
        Map<Identifier, Resource> resources = manager.findResources(
                "models/chao",
                id -> id.getPath().endsWith(".cmesh")
        );
        Map<Identifier, ChaoMeshModel> loaded = new LinkedHashMap<>(resources.size());
        long estimatedBytes = 0L;

        for (Map.Entry<Identifier, Resource> resourceEntry : resources.entrySet()) {
            try (InputStream input = resourceEntry.getValue().getInputStream()) {
                ChaoMeshModel model = ChaoMeshLoader.load(input);
                loaded.put(resourceEntry.getKey(), model);
                estimatedBytes += estimateModelBytes(model);
            } catch (IOException | RuntimeException exception) {
                ChaoCraft.LOGGER.error("Failed to preload Chao mesh: {}", resourceEntry.getKey(), exception);
            }
        }

        models = Map.copyOf(loaded);
        double millis = (System.nanoTime() - started) / 1_000_000.0D;
        ChaoCraft.LOGGER.info(
                "Preloaded {} Chao mesh resources ({} MiB estimated CPU arrays) in {} ms",
                loaded.size(),
                String.format(Locale.ROOT, "%.1f", estimatedBytes / (1024.0D * 1024.0D)),
                String.format(Locale.ROOT, "%.1f", millis)
        );
        if (estimatedBytes >= LARGE_REPOSITORY_BYTES) {
            ChaoCraft.LOGGER.warn(
                    "[Performance] Chao mesh repository is unusually large: {} MiB across {} resources",
                    String.format(Locale.ROOT, "%.1f", estimatedBytes / (1024.0D * 1024.0D)),
                    loaded.size()
            );
        }
    }

    private static long estimateModelBytes(ChaoMeshModel model) {
        long bytes = 0L;
        for (ChaoMeshModel.Segment segment : model.segments()) {
            bytes += (long) segment.positions().length * Float.BYTES;
            bytes += (long) segment.normals().length * Float.BYTES;
            bytes += (long) segment.uvs().length * Float.BYTES;
            bytes += (long) segment.indices().length * Integer.BYTES;
            for (float[] morph : segment.morphPositionDeltas()) {
                bytes += (long) morph.length * Float.BYTES;
            }
            for (float[] morph : segment.morphNormalDeltas()) {
                bytes += (long) morph.length * Float.BYTES;
            }
        }
        return bytes;
    }
}
