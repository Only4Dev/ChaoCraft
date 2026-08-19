package com.chaocraft.client.render.debug;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Allocation-light telemetry used by the Visual Lab and performance logger.
 * All counters are client-only and deliberately cheap enough to leave enabled
 * during stress tests.
 */
public final class ChaoRenderMetrics {
    private static final long WINDOW_NANOS = 1_000_000_000L;
    private static final int MAX_RECENT = 8192;
    private static final Deque<Long> recentBuilds = new ArrayDeque<>();
    private static final Deque<Long> recentEvictions = new ArrayDeque<>();
    private static final Deque<Long> recentDeferred = new ArrayDeque<>();

    private static long totalBuilds;
    private static long totalBatchesUploaded;
    private static long totalBytesUploaded;
    private static long cacheHits;
    private static long cacheMisses;
    private static long totalEvictions;
    private static long totalDeferred;
    private static long totalRenderNanos;
    private static long totalRenderedChao;
    private static long reflectionPasses;
    private static long cacheClears;
    private static long totalGpuBatchDraws;
    private static long totalSkinnedBatchDraws;
    private static long totalReflectionBatchDraws;
    private static long totalSkinPaletteUploads;
    private static long totalPreviewFaceChanges;
    private static int boundEntities;
    private static int sharedEntries;
    private static long cachedEstimatedBytes;

    private ChaoRenderMetrics() {}

    public static synchronized void onGpuBuild(int uploadedBatches, long uploadedBytes) {
        long now = System.nanoTime();
        recentBuilds.addLast(now);
        cap(recentBuilds);
        pruneRecent(now);
        totalBuilds++;
        totalBatchesUploaded += Math.max(0, uploadedBatches);
        totalBytesUploaded += Math.max(0L, uploadedBytes);
    }

    public static synchronized void onGpuCacheHit() { cacheHits++; }
    public static synchronized void onGpuCacheMiss() { cacheMisses++; }

    public static synchronized void onGpuBuildDeferred() {
        long now = System.nanoTime();
        recentDeferred.addLast(now);
        cap(recentDeferred);
        pruneRecent(now);
        totalDeferred++;
    }

    public static synchronized void onGpuEviction() {
        long now = System.nanoTime();
        recentEvictions.addLast(now);
        cap(recentEvictions);
        pruneRecent(now);
        totalEvictions++;
    }

    public static synchronized void onGpuEntryClosed(long bytes, String reason) {
        // Hook kept for event/logger integration without allocating per close.
    }

    public static synchronized void onGpuCacheClear() { cacheClears++; }

    /** Render-thread-only hot counters: intentionally unsynchronized to avoid profiling the profiler. */
    public static void onGpuBatchDraw(boolean skinned, boolean reflected) {
        totalGpuBatchDraws++;
        if (skinned) totalSkinnedBatchDraws++;
        if (reflected) totalReflectionBatchDraws++;
    }

    /** One upload means the complete 40-node palette was pushed to a skinning shader. */
    public static void onSkinPaletteUpload() { totalSkinPaletteUploads++; }

    /** Counts actual Visual Lab state changes made while the Face tab is active. */
    public static void onPreviewFaceChange() { totalPreviewFaceChanges++; }

    public static synchronized void onRender(long nanos, boolean reflected) {
        totalRenderNanos += Math.max(0L, nanos);
        totalRenderedChao++;
        if (reflected) reflectionPasses++;
    }

    public static synchronized void updateCacheSize(int entities, int shared, long estimatedBytes) {
        boundEntities = Math.max(0, entities);
        sharedEntries = Math.max(0, shared);
        cachedEstimatedBytes = Math.max(0L, estimatedBytes);
    }

    public static synchronized Snapshot snapshot() {
        long now = System.nanoTime();
        pruneRecent(now);
        return new Snapshot(totalBuilds, recentBuilds.size(), totalBatchesUploaded, totalBytesUploaded,
                cacheHits, cacheMisses, totalEvictions, recentEvictions.size(), totalDeferred,
                recentDeferred.size(), totalRenderNanos, totalRenderedChao, reflectionPasses, cacheClears,
                boundEntities, sharedEntries, cachedEstimatedBytes, totalGpuBatchDraws,
                totalSkinnedBatchDraws, totalReflectionBatchDraws, totalSkinPaletteUploads,
                totalPreviewFaceChanges);
    }

    public static synchronized void reset() {
        recentBuilds.clear();
        recentEvictions.clear();
        recentDeferred.clear();
        totalBuilds = totalBatchesUploaded = totalBytesUploaded = 0L;
        cacheHits = cacheMisses = totalEvictions = totalDeferred = 0L;
        totalRenderNanos = totalRenderedChao = reflectionPasses = cacheClears = 0L;
        totalGpuBatchDraws = totalSkinnedBatchDraws = totalReflectionBatchDraws = 0L;
        totalSkinPaletteUploads = totalPreviewFaceChanges = 0L;
        boundEntities = sharedEntries = 0;
        cachedEstimatedBytes = 0L;
    }

    private static void cap(Deque<Long> deque) {
        while (deque.size() > MAX_RECENT) deque.removeFirst();
    }

    private static void pruneRecent(long now) {
        long cutoff = now - WINDOW_NANOS;
        prune(recentBuilds, cutoff);
        prune(recentEvictions, cutoff);
        prune(recentDeferred, cutoff);
    }

    private static void prune(Deque<Long> deque, long cutoff) {
        while (!deque.isEmpty() && deque.peekFirst() < cutoff) deque.removeFirst();
    }

    public record Snapshot(
            long totalBuilds,
            int buildsPerSecond,
            long totalBatchesUploaded,
            long totalBytesUploaded,
            long cacheHits,
            long cacheMisses,
            long totalEvictions,
            int evictionsPerSecond,
            long totalDeferredBuilds,
            int deferredPerSecond,
            long totalRenderNanos,
            long totalRenderedChao,
            long reflectionPasses,
            long cacheClears,
            int cachedEntities,
            int sharedEntries,
            long cachedEstimatedBytes,
            long totalGpuBatchDraws,
            long totalSkinnedBatchDraws,
            long totalReflectionBatchDraws,
            long totalSkinPaletteUploads,
            long totalPreviewFaceChanges
    ) {
        public double averageRenderMicros() {
            return totalRenderedChao == 0 ? 0.0 : (totalRenderNanos / 1000.0) / totalRenderedChao;
        }
    }
}
