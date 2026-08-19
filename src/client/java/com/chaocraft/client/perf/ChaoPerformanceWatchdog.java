package com.chaocraft.client.perf;

import com.chaocraft.ChaoCraft;
import com.chaocraft.client.render.debug.ChaoRenderMetrics;
import net.minecraft.client.MinecraftClient;

import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

/**
 * Always-on, allocation-light performance watchdog.
 *
 * <p>Samples once per second and stays silent unless a threshold is crossed.
 * Warnings are rate-limited by category so latest.log remains useful even
 * during 100+ Chao stress tests.</p>
 */
public final class ChaoPerformanceWatchdog {
    private static final long SAMPLE_NANOS = 1_000_000_000L;
    private static final long WARNING_COOLDOWN_NANOS = 10_000_000_000L;
    // CP11R intentionally allows a larger bounded GPU cache. Warn only once the
    // cache is genuinely approaching its 384 MiB budget, not during normal warm-up.
    private static final long GPU_CACHE_WARNING_BYTES = 320L * 1024L * 1024L;
    private static final double HEAP_WARNING_RATIO = 0.85D;
    private static final double RENDER_MICROS_WARNING = 500.0D;
    private static final int LOW_FPS_WARNING = 40;
    private static final int MIN_CACHED_CHAO_FOR_FPS_WARNING = 20;
    private static final int BUILDS_PER_SECOND_WARNING = 5;
    private static final int EVICTIONS_PER_SECOND_WARNING = 2;
    private static final int DEFERRED_PER_SECOND_WARNING = 20;

    private static final Map<Category, Long> lastWarnings = new EnumMap<>(Category.class);
    private static long lastSampleNanos = Long.MIN_VALUE;
    private static ChaoRenderMetrics.Snapshot previous;
    private static final BufferPoolMXBean DIRECT_BUFFER_POOL = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class)
            .stream().filter(pool -> "direct".equalsIgnoreCase(pool.getName())).findFirst().orElse(null);

    private ChaoPerformanceWatchdog() {
    }

    public static void tick(MinecraftClient client) {
        long now = System.nanoTime();
        if (lastSampleNanos != Long.MIN_VALUE && now - lastSampleNanos < SAMPLE_NANOS) {
            return;
        }
        lastSampleNanos = now;

        ChaoRenderMetrics.Snapshot current = ChaoRenderMetrics.snapshot();
        checkGpuPressure(current, now);
        checkBuildPressure(current, now);
        checkHeap(now);
        checkFps(client, current, now);
        checkRenderCpu(current, now);
        emitAuditSample(client, current);
        previous = current;
    }

    public static void reset() {
        lastSampleNanos = Long.MIN_VALUE;
        previous = null;
        lastWarnings.clear();
    }

    public static void event(String event) {
        ChaoCraft.LOGGER.debug("[Performance] {}", event);
    }

    private static void checkGpuPressure(ChaoRenderMetrics.Snapshot m, long now) {
        if (m.cachedEstimatedBytes() >= GPU_CACHE_WARNING_BYTES) {
            warn(Category.GPU_CACHE, now,
                    "GPU cache pressure: %.1f MiB, %d shared entries, %d entity bindings",
                    mib(m.cachedEstimatedBytes()), m.sharedEntries(), m.cachedEntities());
        }
    }

    private static void checkBuildPressure(ChaoRenderMetrics.Snapshot m, long now) {
        if (m.buildsPerSecond() >= BUILDS_PER_SECOND_WARNING) {
            warn(Category.BUILDS, now,
                    "VBO build pressure: %d builds/s, %d deferred/s, %d shared entries",
                    m.buildsPerSecond(), m.deferredPerSecond(), m.sharedEntries());
        }
        if (m.evictionsPerSecond() >= EVICTIONS_PER_SECOND_WARNING) {
            warn(Category.EVICTIONS, now,
                    "GPU cache churn: %d evictions/s with %.1f MiB cached",
                    m.evictionsPerSecond(), mib(m.cachedEstimatedBytes()));
        }
        if (m.deferredPerSecond() >= DEFERRED_PER_SECOND_WARNING) {
            warn(Category.DEFERRED, now,
                    "Render builds are backing up: %d deferred attempts/s, %d builds/s",
                    m.deferredPerSecond(), m.buildsPerSecond());
        }
    }

    private static void checkHeap(long now) {
        Runtime runtime = Runtime.getRuntime();
        long used = runtime.totalMemory() - runtime.freeMemory();
        long max = runtime.maxMemory();
        if (max > 0L && used >= 512L * 1024L * 1024L && (double) used / max >= HEAP_WARNING_RATIO) {
            warn(Category.HEAP, now, "JVM heap pressure: %.1f / %.1f MiB (%.0f%%)",
                    mib(used), mib(max), used * 100.0D / max);
        }
    }

    private static void checkFps(MinecraftClient client, ChaoRenderMetrics.Snapshot m, long now) {
        if (client.world != null && m.cachedEntities() >= MIN_CACHED_CHAO_FOR_FPS_WARNING
                && client.getCurrentFps() > 0 && client.getCurrentFps() < LOW_FPS_WARNING) {
            warn(Category.FPS, now,
                    "Low FPS while rendering many Chao: %d FPS, %d cached Chao, %d shared VBO states",
                    client.getCurrentFps(), m.cachedEntities(), m.sharedEntries());
        }
    }

    private static void checkRenderCpu(ChaoRenderMetrics.Snapshot current, long now) {
        if (previous == null) {
            return;
        }
        long rendered = current.totalRenderedChao() - previous.totalRenderedChao();
        long nanos = current.totalRenderNanos() - previous.totalRenderNanos();
        if (rendered <= 0L || nanos <= 0L) {
            return;
        }
        double microsPerCall = (nanos / 1000.0D) / rendered;
        if (rendered >= 100L && microsPerCall >= RENDER_MICROS_WARNING) {
            warn(Category.RENDER_CPU, now,
                    "Chao renderer CPU spike: %.1f us/call across %d render calls in the last sample",
                    microsPerCall, rendered);
        }
    }


    /**
     * CP12J.2 diagnostic sampler. It is intentionally arithmetic-only and runs once
     * per second. Hot draw paths merely increment primitive counters, so the audit
     * does not introduce per-batch allocations or logging.
     */
    private static void emitAuditSample(MinecraftClient client, ChaoRenderMetrics.Snapshot current) {
        if (previous == null || client.world == null) {
            return;
        }

        long rendered = current.totalRenderedChao() - previous.totalRenderedChao();
        long draws = current.totalGpuBatchDraws() - previous.totalGpuBatchDraws();
        long skinnedDraws = current.totalSkinnedBatchDraws() - previous.totalSkinnedBatchDraws();
        long reflectedDraws = current.totalReflectionBatchDraws() - previous.totalReflectionBatchDraws();
        long paletteUploads = current.totalSkinPaletteUploads() - previous.totalSkinPaletteUploads();
        long builds = current.totalBuilds() - previous.totalBuilds();
        long uploadedBytes = current.totalBytesUploaded() - previous.totalBytesUploaded();
        long hits = current.cacheHits() - previous.cacheHits();
        long misses = current.cacheMisses() - previous.cacheMisses();
        long faceChanges = current.totalPreviewFaceChanges() - previous.totalPreviewFaceChanges();

        // Stay silent during ordinary gameplay. 50/100-Chao matrices easily exceed
        // this threshold, while Face tab interaction explicitly opts into sampling.
        if (faceChanges <= 0L && rendered < 100L) {
            return;
        }

        int fps = Math.max(1, client.getCurrentFps());
        double visibleChaoPerFrame = rendered / (double) fps;
        double drawsPerFrame = draws / (double) fps;
        double drawsPerChao = rendered > 0L ? draws / (double) rendered : 0.0D;
        double palettesPerChao = rendered > 0L ? paletteUploads / (double) rendered : 0.0D;
        long cacheLookups = hits + misses;
        double hitRate = cacheLookups > 0L ? hits * 100.0D / cacheLookups : 100.0D;
        long directBytes = DIRECT_BUFFER_POOL == null ? -1L : DIRECT_BUFFER_POOL.getMemoryUsed();

        ChaoCraft.LOGGER.warn(
                "[Performance][Audit] fps={} visible~{}/frame draws={}/frame ({}/Chao) "
                        + "skinned={} reflected={} palettes={}/Chao cacheHit={}% "
                        + "builds={} uploaded={}MiB shared={} bindings={} gpuCache={}MiB direct={}MiB",
                fps, format1(visibleChaoPerFrame), format1(drawsPerFrame), format1(drawsPerChao),
                skinnedDraws, reflectedDraws, format2(palettesPerChao), format1(hitRate),
                builds, format1(mib(uploadedBytes)), current.sharedEntries(), current.cachedEntities(),
                format1(mib(current.cachedEstimatedBytes())),
                directBytes < 0L ? "n/a" : format1(mib(directBytes)));

        if (faceChanges > 0L) {
            ChaoCraft.LOGGER.warn(
                    "[Performance][FaceAudit] changes={} builds={} misses={} hits={} uploaded={}MiB "
                            + "shared={} gpuCache={}MiB",
                    faceChanges, builds, misses, hits, format1(mib(uploadedBytes)),
                    current.sharedEntries(), format1(mib(current.cachedEstimatedBytes())));
        }
    }

    private static String format1(double value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }

    private static String format2(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    private static void warn(Category category, long now, String format, Object... args) {
        long last = lastWarnings.getOrDefault(category, Long.MIN_VALUE);
        if (last != Long.MIN_VALUE && now - last < WARNING_COOLDOWN_NANOS) {
            return;
        }
        lastWarnings.put(category, now);
        ChaoCraft.LOGGER.warn("[Performance] " + String.format(Locale.ROOT, format, args));
    }

    private static double mib(long bytes) {
        return bytes / (1024.0D * 1024.0D);
    }

    private enum Category {
        GPU_CACHE,
        BUILDS,
        EVICTIONS,
        DEFERRED,
        HEAP,
        FPS,
        RENDER_CPU
    }
}
