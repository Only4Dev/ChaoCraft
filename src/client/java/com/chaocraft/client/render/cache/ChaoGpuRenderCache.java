package com.chaocraft.client.render.cache;

import com.chaocraft.client.render.debug.ChaoRenderMetrics;
import com.chaocraft.client.render.mesh.ChaoMeshModel;
import com.chaocraft.entity.ChaoEntity;
import com.chaocraft.visual.ChaoAppearanceState;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.RenderLayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Shared GPU cache for complete Chao render states.
 *
 * <p>The cache is keyed by visual state instead of entity UUID so identical
 * Chao reuse the same native/GPU buffers. Each entity only owns a lightweight
 * binding to a shared entry. When its appearance or light changes, the old
 * binding is released before a replacement is allocated, preventing the
 * two-full-VBO light spike that previously made torch updates dangerous.</p>
 *
 * <p>Builds are also rate-limited. A crowded garden may show a Chao one or two
 * frames late while its VBO is queued, but rendering is never allowed to burst
 * dozens of multi-megabyte native allocations in one frame.</p>
 */
public final class ChaoGpuRenderCache {
    private static final long STALE_TICKS = 100L;
    private static final long PRUNE_INTERVAL = 40L;
    private static final int MAX_SHARED_ENTRIES = 160;
    private static final long MAX_ESTIMATED_CACHE_BYTES = 64L * 1024L * 1024L;
    private static final long BUILD_RESERVE_BYTES = 16L * 1024L * 1024L;

    // At most one expensive cache miss every 50 ms. Existing/shared VBO hits are unlimited.
    private static final long BUILD_WINDOW_NANOS = 50_000_000L;

    private final LinkedHashMap<CacheKey, SharedEntry> shared = new LinkedHashMap<>(64, 0.75F, true);
    private final Map<UUID, CacheKey> bindings = new HashMap<>();
    private long lastPruneTick = Long.MIN_VALUE;
    private long lastBuildNanos = Long.MIN_VALUE;

    public Entry get(ChaoEntity entity, ChaoAppearanceState state, ChaoMeshModel model,
            int packedLight, long worldTick, Supplier<List<DrawBatch>> batchBuilder) {
        prepareForWorldTick(worldTick);
        UUID entityId = entity.getUuid();
        CacheKey wanted = new CacheKey(state, model, packedLight);
        CacheKey previousKey = bindings.get(entityId);

        SharedEntry cached = shared.get(wanted);
        if (cached != null && !cached.entry.isClosed()) {
            if (previousKey != null && !previousKey.equals(wanted)) {
                unlink(entityId, previousKey);
            }
            bind(entityId, wanted, cached, worldTick);
            ChaoRenderMetrics.onGpuCacheHit();
            prune(worldTick);
            updateMetrics();
            return cached.entry;
        }
        if (cached != null) {
            shared.remove(wanted);
        }

        // Keep the last stable appearance/light while a crowded scene waits for
        // its build slot. This is preferable to a native-allocation burst.
        if (!acquireBuildSlot()) {
            ChaoRenderMetrics.onGpuBuildDeferred();
            SharedEntry previous = previousKey == null ? null : shared.get(previousKey);
            if (previous != null && !previous.entry.isClosed()) {
                previous.lastSeenTick = worldTick;
                return previous.entry;
            }
            return null;
        }

        ChaoRenderMetrics.onGpuCacheMiss();

        // Release this entity's obsolete unique resource before reserving native
        // memory for the replacement. Shared resources remain alive for other users.
        if (previousKey != null && !previousKey.equals(wanted)) {
            unlink(entityId, previousKey);
        }

        trimForReserve(wanted);
        List<DrawBatch> builtBatches = batchBuilder.get();
        Entry entry = new Entry(packedLight, builtBatches);
        SharedEntry rebuilt = new SharedEntry(entry, worldTick);
        shared.put(wanted, rebuilt);
        bind(entityId, wanted, rebuilt, worldTick);
        ChaoRenderMetrics.onGpuBuild(entry.batches().size(), entry.estimatedBytes());

        prune(worldTick);
        trimGlobalBudget(wanted);
        updateMetrics();
        return entry;
    }

    /** Releases one entity binding immediately, used by client-only preview entities. */
    public void remove(UUID entityId) {
        CacheKey key = bindings.remove(entityId);
        if (key != null) {
            SharedEntry entry = shared.get(key);
            if (entry != null) {
                entry.users.remove(entityId);
                if (entry.users.isEmpty()) {
                    shared.remove(key);
                    closeEntry(entry, "last-user");
                }
            }
        }
        updateMetrics();
    }

    public void clear() {
        for (SharedEntry entry : shared.values()) {
            entry.entry.close();
        }
        shared.clear();
        bindings.clear();
        lastPruneTick = Long.MIN_VALUE;
        lastBuildNanos = Long.MIN_VALUE;
        updateMetrics();
        ChaoRenderMetrics.onGpuCacheClear();
    }

    private boolean acquireBuildSlot() {
        long now = System.nanoTime();
        if (lastBuildNanos != Long.MIN_VALUE && now - lastBuildNanos < BUILD_WINDOW_NANOS) {
            return false;
        }
        lastBuildNanos = now;
        return true;
    }

    private void bind(UUID entityId, CacheKey key, SharedEntry entry, long worldTick) {
        bindings.put(entityId, key);
        entry.users.add(entityId);
        entry.lastSeenTick = worldTick;
    }

    private void unlink(UUID entityId, CacheKey key) {
        bindings.remove(entityId, key);
        SharedEntry old = shared.get(key);
        if (old == null) return;
        old.users.remove(entityId);
        if (old.users.isEmpty()) {
            shared.remove(key);
            closeEntry(old, "state-replaced");
        }
    }

    private void prepareForWorldTick(long worldTick) {
        if (lastPruneTick != Long.MIN_VALUE && worldTick < lastPruneTick) {
            clear();
            lastPruneTick = worldTick;
        }
    }

    private void prune(long worldTick) {
        if (lastPruneTick != Long.MIN_VALUE && worldTick - lastPruneTick < PRUNE_INTERVAL) return;
        lastPruneTick = worldTick;

        Iterator<Map.Entry<CacheKey, SharedEntry>> iterator = shared.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<CacheKey, SharedEntry> mapEntry = iterator.next();
            SharedEntry entry = mapEntry.getValue();
            if (worldTick - entry.lastSeenTick <= STALE_TICKS) continue;
            iterator.remove();
            bindings.entrySet().removeIf(binding -> binding.getValue().equals(mapEntry.getKey()));
            closeEntry(entry, "stale");
        }
    }

    /** Evict before a cache miss allocates its largest possible single batch. */
    private void trimForReserve(CacheKey protectedKey) {
        while (!shared.isEmpty() && estimatedBytes() > MAX_ESTIMATED_CACHE_BYTES - BUILD_RESERVE_BYTES) {
            if (!evictEldest(protectedKey, "preflight")) break;
        }
    }

    private void trimGlobalBudget(CacheKey protectedKey) {
        while (shared.size() > MAX_SHARED_ENTRIES || estimatedBytes() > MAX_ESTIMATED_CACHE_BYTES) {
            if (!evictEldest(protectedKey, "budget")) break;
        }
    }

    private boolean evictEldest(CacheKey protectedKey, String reason) {
        Iterator<Map.Entry<CacheKey, SharedEntry>> iterator = shared.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<CacheKey, SharedEntry> candidate = iterator.next();
            if (candidate.getKey().equals(protectedKey)) continue;
            iterator.remove();
            bindings.entrySet().removeIf(binding -> binding.getValue().equals(candidate.getKey()));
            closeEntry(candidate.getValue(), reason);
            ChaoRenderMetrics.onGpuEviction();
            return true;
        }
        return false;
    }

    private void closeEntry(SharedEntry entry, String reason) {
        long bytes = entry.entry.estimatedBytes();
        entry.entry.close();
        ChaoRenderMetrics.onGpuEntryClosed(bytes, reason);
    }

    private long estimatedBytes() {
        long total = 0L;
        for (SharedEntry entry : shared.values()) total += entry.entry.estimatedBytes();
        return total;
    }

    private void updateMetrics() {
        ChaoRenderMetrics.updateCacheSize(bindings.size(), shared.size(), estimatedBytes());
    }

    private record CacheKey(ChaoAppearanceState state, ChaoMeshModel model, int packedLight) {
    }

    private static final class SharedEntry {
        private final Entry entry;
        private final Set<UUID> users = new HashSet<>();
        private long lastSeenTick;

        private SharedEntry(Entry entry, long lastSeenTick) {
            this.entry = entry;
            this.lastSeenTick = lastSeenTick;
        }
    }

    public static final class Entry implements AutoCloseable {
        private final int packedLight;
        private final List<DrawBatch> batches;
        private boolean closed;

        private Entry(int packedLight, List<DrawBatch> batches) {
            this.packedLight = packedLight;
            this.batches = List.copyOf(batches);
        }

        public int packedLight() { return packedLight; }
        public List<DrawBatch> batches() { return batches; }
        public boolean isClosed() { return closed; }

        public long estimatedBytes() {
            long total = 0L;
            for (DrawBatch batch : batches) total += batch.estimatedBytes();
            return total;
        }

        @Override
        public void close() {
            if (closed) return;
            closed = true;
            for (DrawBatch batch : batches) batch.close();
        }
    }

    public record DrawBatch(RenderLayer layer, VertexBuffer vertexBuffer, int estimatedBytes) implements AutoCloseable {
        @Override
        public void close() {
            if (!vertexBuffer.isClosed()) vertexBuffer.close();
        }
    }
}
