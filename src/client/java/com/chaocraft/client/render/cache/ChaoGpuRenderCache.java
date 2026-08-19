package com.chaocraft.client.render.cache;

import com.chaocraft.ChaoCraft;
import com.chaocraft.client.render.debug.ChaoRenderMetrics;
import com.chaocraft.client.render.mesh.ChaoMeshModel;
import com.chaocraft.entity.ChaoEntity;
import com.chaocraft.visual.ChaoAppearanceState;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.RenderLayer;

import java.util.ArrayDeque;
import java.util.Deque;
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
    private final boolean publishCacheSizeMetrics;

    /** Production cache constructor. */
    public ChaoGpuRenderCache() {
        this(true);
    }

    /**
     * @param publishCacheSizeMetrics false for isolated debug caches such as F8,
     *                                so they cannot overwrite production cache telemetry.
     */
    public ChaoGpuRenderCache(boolean publishCacheSizeMetrics) {
        this.publishCacheSizeMetrics = publishCacheSizeMetrics;
    }

    // Idle visual states are useful for short camera moves, but holding them for
    // a full minute left ~378 MiB resident after a large matrix was removed.
    // Active entries are never evicted; only unused warm states use this timeout.
    private static final long STALE_TICKS = 200L;
    private static final long PRUNE_INTERVAL = 40L;
    private static final long MAX_IDLE_CACHE_BYTES = 96L * 1024L * 1024L;
    // Native/driver deletes are also work. Release old warm entries gradually
    // instead of issuing a large glDeleteBuffers burst after a world transition.
    private static final int MAX_BACKGROUND_EVICTIONS_PER_PRUNE = 8;
    // Entry count is only a secondary guard. The byte budget is the real safety
    // limit; 256 entries caused needless churn at ~160 MiB during the 109-Chao
    // stress test even though the 384 MiB cache budget still had ample headroom.
    private static final int MAX_SHARED_ENTRIES = 512;

    // CP11's 64 MiB budget plus 16 MiB reserve made the stress scene start
    // evicting around 48 MiB. Keep enough room for many distinct real Chao
    // states while remaining conservative on the current 4 GiB target GPU.
    private static final long MAX_ESTIMATED_CACHE_BYTES = 384L * 1024L * 1024L;
    private static final long BUILD_RESERVE_BYTES = 8L * 1024L * 1024L;

    // At most one expensive cache miss every 50 ms. Existing/shared VBO hits are unlimited.
    private static final long BUILD_WINDOW_NANOS = 50_000_000L;
    private static final long PENDING_BUILD_STALE_TICKS = 40L;

    private final LinkedHashMap<CacheKey, SharedEntry> shared = new LinkedHashMap<>(64, 0.75F, true);
    private final Map<UUID, CacheKey> bindings = new HashMap<>();

    // Deduplicated first-time build queue. A visual state waits once instead of
    // every entity retrying the 50 ms gate every rendered frame.
    private final Deque<CacheKey> buildQueue = new ArrayDeque<>();
    private final Set<CacheKey> queuedBuilds = new HashSet<>();
    private final Map<CacheKey, Long> pendingLastRequestedTick = new HashMap<>();

    private static final long NATIVE_OOM_COOLDOWN_NANOS = 5_000_000_000L;

    private long lastPruneTick = Long.MIN_VALUE;
    private long lastBuildNanos = Long.MIN_VALUE;
    private long nativeOomCooldownUntil;
    private long estimatedCacheBytes;

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
            removePendingBuild(wanted);
            ChaoRenderMetrics.onGpuCacheHit();
            prune(worldTick);
            updateMetrics();
            return cached.entry;
        }
        if (cached != null) {
            shared.remove(wanted);
            estimatedCacheBytes = Math.max(0L, estimatedCacheBytes - cached.entry.estimatedBytes());
        }

        // Keep the last stable appearance/light while a crowded scene waits for
        // its FIFO build slot. Queue admission is counted once per unique state,
        // eliminating thousands of repeated deferred attempts after save/rejoin.
        if (!acquireQueuedBuildSlot(wanted, worldTick)) {
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

        if (System.nanoTime() < nativeOomCooldownUntil) {
            return previousEntry(previousKey, worldTick);
        }

        trimForReserve(wanted);
        List<DrawBatch> builtBatches;
        try {
            builtBatches = batchBuilder.get();
        } catch (OutOfMemoryError | ChaoGpuMemoryException allocationFailure) {
            trimAllIdleEntries();
            nativeOomCooldownUntil = System.nanoTime() + NATIVE_OOM_COOLDOWN_NANOS;
            ChaoRenderMetrics.onGpuBuildDeferred();
            ChaoCraft.LOGGER.warn(
                    "[Performance] Chao VBO allocation pressure detected; released idle GPU states and paused builds for 5s ({})",
                    allocationFailure.getClass().getSimpleName()
            );
            return previousEntry(previousKey, worldTick);
        }
        Entry entry = new Entry(packedLight, builtBatches);
        SharedEntry rebuilt = new SharedEntry(entry, worldTick);
        shared.put(wanted, rebuilt);
        estimatedCacheBytes += entry.estimatedBytes();
        bind(entityId, wanted, rebuilt, worldTick);
        ChaoRenderMetrics.onGpuBuild(entry.batches().size(), entry.estimatedBytes());

        prune(worldTick);
        trimGlobalBudget(wanted);
        updateMetrics();
        return entry;
    }

    /** Releases one entity binding while retaining its shared VBO warm. */
    public void remove(UUID entityId) {
        CacheKey key = bindings.remove(entityId);
        if (key != null) {
            SharedEntry entry = shared.get(key);
            if (entry != null) {
                entry.users.remove(entityId);
            }
        }
        updateMetrics();
    }

    /**
     * Releases a debug-preview binding and immediately closes its now-unused VBO.
     *
     * <p>Production scenes benefit from a warm immutable cache; slider scrubbing
     * does not. Without this path, one F8 drag can retain dozens of multi-MiB
     * intermediate states for STALE_TICKS and create avoidable native-memory churn.</p>
     */
    public void removeAndEvict(UUID entityId) {
        CacheKey key = bindings.remove(entityId);
        if (key == null) {
            updateMetrics();
            return;
        }

        SharedEntry entry = shared.get(key);
        if (entry == null) {
            updateMetrics();
            return;
        }

        entry.users.remove(entityId);
        if (entry.users.isEmpty()) {
            shared.remove(key);
            removePendingBuild(key);
            closeEntry(entry, "preview-state-replaced");
            ChaoRenderMetrics.onGpuEviction();
        }
        updateMetrics();
    }

    /** Returns the currently bound open entry for an entity, if any. */
    public Entry getBound(UUID entityId) {
        CacheKey key = bindings.get(entityId);
        if (key == null) return null;
        SharedEntry entry = shared.get(key);
        return entry == null || entry.entry.isClosed() ? null : entry.entry;
    }

    /** True when the entity is already bound to this exact visual state. */
    public boolean isBoundTo(UUID entityId, ChaoAppearanceState state, ChaoMeshModel model, int packedLight) {
        CacheKey key = bindings.get(entityId);
        return key != null
                && key.state().equals(state)
                && key.model() == model
                && key.packedLight() == packedLight
                && getBound(entityId) != null;
    }

    /**
     * World transition policy: remove entity UUID bindings but keep immutable
     * shared VBOs warm. Resource reload is the only normal hard-clear path.
     */
    public void detachWorldBindingsKeepWarm() {
        bindings.clear();
        for (SharedEntry entry : shared.values()) {
            entry.users.clear();
        }
        buildQueue.clear();
        queuedBuilds.clear();
        pendingLastRequestedTick.clear();
        lastPruneTick = Long.MIN_VALUE;
        updateMetrics();
    }

    /**
     * Rebase retained warm states to the new ClientWorld clock.
     *
     * <p>World times are unrelated across saves/servers. Rebasing prevents the
     * old tick value from either clearing valid warm VBOs or keeping them stale
     * forever after a Save/Quit/Rejoin.</p>
     */
    public void beginWorld(long worldTick) {
        bindings.clear();
        for (SharedEntry entry : shared.values()) {
            entry.users.clear();
            entry.lastSeenTick = worldTick;
        }
        buildQueue.clear();
        queuedBuilds.clear();
        pendingLastRequestedTick.clear();
        lastPruneTick = worldTick;
        nativeOomCooldownUntil = 0L;
        updateMetrics();
    }

    /**
     * Closes a small number of currently unused entries.
     *
     * <p>Used by the F8 preview after a debounced state replacement. Production
     * never calls this; its old visual states remain available to other Chao.</p>
     */
    public void evictIdleEntries(int maxEntries, boolean countAsEviction) {
        int remaining = Math.max(0, maxEntries);
        if (remaining == 0) return;

        Iterator<Map.Entry<CacheKey, SharedEntry>> iterator = shared.entrySet().iterator();
        while (iterator.hasNext() && remaining > 0) {
            Map.Entry<CacheKey, SharedEntry> candidate = iterator.next();
            SharedEntry entry = candidate.getValue();
            if (!entry.users.isEmpty()) continue;

            iterator.remove();
            removePendingBuild(candidate.getKey());
            closeEntry(entry, "explicit-idle");
            if (countAsEviction) {
                ChaoRenderMetrics.onGpuEviction();
            }
            remaining--;
        }
        updateMetrics();
    }

    /** Background pruning even when no Chao is currently being rendered. */
    public void maintenance(long worldTick) {
        prepareForWorldTick(worldTick);
        prune(worldTick);
        updateMetrics();
    }

    public void clear() {
        for (SharedEntry entry : shared.values()) {
            entry.entry.close();
        }
        shared.clear();
        bindings.clear();
        buildQueue.clear();
        queuedBuilds.clear();
        pendingLastRequestedTick.clear();
        estimatedCacheBytes = 0L;
        lastPruneTick = Long.MIN_VALUE;
        lastBuildNanos = Long.MIN_VALUE;
        nativeOomCooldownUntil = 0L;
        updateMetrics();
        ChaoRenderMetrics.onGpuCacheClear();
    }

    private boolean acquireQueuedBuildSlot(CacheKey key, long worldTick) {
        prunePendingBuilds(worldTick);

        boolean newlyQueued = queuedBuilds.add(key);
        pendingLastRequestedTick.put(key, worldTick);
        if (newlyQueued) {
            buildQueue.addLast(key);
        }

        CacheKey head = buildQueue.peekFirst();
        if (!key.equals(head) || !acquireBuildSlot()) {
            if (newlyQueued) {
                // Count one queued visual state, not one denial per frame/entity.
                ChaoRenderMetrics.onGpuBuildDeferred();
            }
            return false;
        }

        buildQueue.removeFirst();
        queuedBuilds.remove(key);
        pendingLastRequestedTick.remove(key);
        return true;
    }

    private void prunePendingBuilds(long worldTick) {
        Iterator<CacheKey> iterator = buildQueue.iterator();
        while (iterator.hasNext()) {
            CacheKey key = iterator.next();
            long lastRequested = pendingLastRequestedTick.getOrDefault(key, Long.MIN_VALUE);
            if (lastRequested != Long.MIN_VALUE
                    && worldTick - lastRequested <= PENDING_BUILD_STALE_TICKS) {
                continue;
            }
            iterator.remove();
            queuedBuilds.remove(key);
            pendingLastRequestedTick.remove(key);
        }
    }

    private void removePendingBuild(CacheKey key) {
        if (!queuedBuilds.remove(key)) {
            return;
        }
        buildQueue.remove(key);
        pendingLastRequestedTick.remove(key);
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
        // State changes only detach the entity. The old immutable VBO remains
        // reusable until normal stale/LRU budget eviction.
        old.users.remove(entityId);
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

        int evicted = 0;
        Iterator<Map.Entry<CacheKey, SharedEntry>> iterator = shared.entrySet().iterator();
        while (iterator.hasNext() && evicted < MAX_BACKGROUND_EVICTIONS_PER_PRUNE) {
            Map.Entry<CacheKey, SharedEntry> mapEntry = iterator.next();
            SharedEntry entry = mapEntry.getValue();
            if (!entry.users.isEmpty() || worldTick - entry.lastSeenTick <= STALE_TICKS) continue;
            iterator.remove();
            bindings.entrySet().removeIf(binding -> binding.getValue().equals(mapEntry.getKey()));
            closeEntry(entry, "stale");
            ChaoRenderMetrics.onGpuEviction();
            evicted++;
        }

        trimIdleBudget(MAX_BACKGROUND_EVICTIONS_PER_PRUNE - evicted);
    }

    /**
     * Keep a modest reusable warm pool after a stress scene collapses.
     *
     * <p>The global 384 MiB ceiling still protects large ACTIVE gardens. This
     * secondary budget applies only to entries with no entity users, so dropping
     * from hundreds of Chao to a small scene releases old GPU memory quickly
     * without forcing currently visible Chao to rebuild.</p>
     */
    private void trimIdleBudget(int maxEvictions) {
        if (maxEvictions <= 0) return;

        long idleBytes = 0L;
        for (SharedEntry entry : shared.values()) {
            if (entry.users.isEmpty()) {
                idleBytes += entry.entry.estimatedBytes();
            }
        }

        if (idleBytes <= MAX_IDLE_CACHE_BYTES) {
            return;
        }

        int evicted = 0;
        Iterator<Map.Entry<CacheKey, SharedEntry>> iterator = shared.entrySet().iterator();
        while (iterator.hasNext() && idleBytes > MAX_IDLE_CACHE_BYTES && evicted < maxEvictions) {
            Map.Entry<CacheKey, SharedEntry> candidate = iterator.next();
            SharedEntry entry = candidate.getValue();
            if (!entry.users.isEmpty()) {
                continue;
            }

            long bytes = entry.entry.estimatedBytes();
            iterator.remove();
            closeEntry(entry, "idle-budget");
            ChaoRenderMetrics.onGpuEviction();
            idleBytes = Math.max(0L, idleBytes - bytes);
            evicted++;
        }
    }

    private Entry previousEntry(CacheKey previousKey, long worldTick) {
        SharedEntry previous = previousKey == null ? null : shared.get(previousKey);
        if (previous == null || previous.entry.isClosed()) return null;
        previous.lastSeenTick = worldTick;
        return previous.entry;
    }

    private void trimAllIdleEntries() {
        Iterator<Map.Entry<CacheKey, SharedEntry>> iterator = shared.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<CacheKey, SharedEntry> candidate = iterator.next();
            if (!candidate.getValue().users.isEmpty()) continue;
            iterator.remove();
            closeEntry(candidate.getValue(), "native-oom-recovery");
            ChaoRenderMetrics.onGpuEviction();
        }
        updateMetrics();
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

            // Never evict a VBO still referenced by an active Chao. Otherwise the
            // next frame immediately rebuilds the same state and creates the
            // build -> evict -> rebuild churn seen during the CP11R stress test.
            if (!candidate.getValue().users.isEmpty()) continue;

            iterator.remove();
            closeEntry(candidate.getValue(), reason);
            ChaoRenderMetrics.onGpuEviction();
            return true;
        }
        return false;
    }

    private void closeEntry(SharedEntry entry, String reason) {
        long bytes = entry.entry.estimatedBytes();
        estimatedCacheBytes = Math.max(0L, estimatedCacheBytes - bytes);
        entry.entry.close();
        ChaoRenderMetrics.onGpuEntryClosed(bytes, reason);
    }

    private long estimatedBytes() {
        return estimatedCacheBytes;
    }

    private void updateMetrics() {
        if (publishCacheSizeMetrics) {
            ChaoRenderMetrics.updateCacheSize(bindings.size(), shared.size(), estimatedBytes());
        }
    }

    /**
     * Immutable visual identity only. Future animation pose/time must NEVER enter
     * this key: animation belongs to draw-time GPU transforms so a walking Chao
     * does not rebuild geometry every frame.
     */
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

    public record DrawBatch(
            RenderLayer layer,
            VertexBuffer vertexBuffer,
            int estimatedBytes,
            boolean reflection,
            float reflectionEmission,
            int rigNode,
            boolean skinned
    ) implements AutoCloseable {
        /** Ordinary/static compatibility constructor. */
        public DrawBatch(RenderLayer layer, VertexBuffer vertexBuffer, int estimatedBytes) {
            this(layer, vertexBuffer, estimatedBytes, false, 0.0F, -1, false);
        }

        /** Reflection/static compatibility constructor. */
        public DrawBatch(RenderLayer layer, VertexBuffer vertexBuffer, int estimatedBytes,
                boolean reflection, float reflectionEmission) {
            this(layer, vertexBuffer, estimatedBytes, reflection, reflectionEmission, -1, false);
        }

        /** CP12B rigid-preview compatibility constructor. */
        public DrawBatch(RenderLayer layer, VertexBuffer vertexBuffer, int estimatedBytes,
                boolean reflection, float reflectionEmission, int rigNode) {
            this(layer, vertexBuffer, estimatedBytes, reflection, reflectionEmission, rigNode, false);
        }

        @Override
        public void close() {
            if (!vertexBuffer.isClosed()) vertexBuffer.close();
        }
    }
}
