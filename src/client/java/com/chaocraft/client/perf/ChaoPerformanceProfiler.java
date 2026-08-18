package com.chaocraft.client.perf;

import com.chaocraft.ChaoCraft;
import com.chaocraft.client.render.debug.ChaoRenderMetrics;
import com.chaocraft.entity.ChaoEntity;
import com.chaocraft.dev.ChaoVisualLabNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Low-overhead ChaoCraft profiler for reproducible QA reports.
 *
 * <p>Samples once per second instead of continuously profiling stacks, so it is
 * safe to leave running during matrix stress tests and heavy modpacks. A session
 * produces metrics.csv, events.log and summary.txt under logs/chaocraft-profiler.</p>
 */
public final class ChaoPerformanceProfiler {
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final long SAMPLE_NANOS = 1_000_000_000L;

    private static boolean running;
    private static long startedNanos;
    private static long lastSampleNanos;
    private static long samples;
    private static Path sessionDir;
    private static BufferedWriter csv;
    private static BufferedWriter events;
    private static ChaoRenderMetrics.Snapshot startSnapshot;

    private ChaoPerformanceProfiler() {}

    public static synchronized boolean isRunning() { return running; }
    public static synchronized Path sessionDir() { return sessionDir; }

    public static synchronized void start(MinecraftClient client) {
        if (running) return;
        try {
            Path root = FabricLoader.getInstance().getGameDir().resolve("logs").resolve("chaocraft-profiler");
            Files.createDirectories(root);
            sessionDir = root.resolve("session-" + LocalDateTime.now().format(STAMP));
            Files.createDirectories(sessionDir);
            csv = Files.newBufferedWriter(sessionDir.resolve("metrics.csv"), StandardCharsets.UTF_8);
            events = Files.newBufferedWriter(sessionDir.resolve("events.log"), StandardCharsets.UTF_8);
            csv.write("elapsed_s,fps,client_chao,vbo_builds_s,vbo_deferred_s,vbo_evictions_s,cache_bindings,shared_vbo_entries,cache_mb,cache_hits,cache_misses,avg_chao_render_us,total_batches,total_upload_mb,reflection_renders,heap_used_mb,heap_committed_mb,heap_max_mb,direct_used_mb,direct_capacity_mb,direct_buffers,matrix_pending\n");
            csv.flush();
            running = true;
            samples = 0L;
            startedNanos = System.nanoTime();
            lastSampleNanos = Long.MIN_VALUE;
            startSnapshot = ChaoRenderMetrics.snapshot();
            event("PROFILER_START");
            sample(client, true);
        } catch (IOException exception) {
            closeQuietly();
            ChaoCraft.LOGGER.error("Could not start ChaoCraft profiler", exception);
        }
    }

    public static synchronized void stop(MinecraftClient client) {
        if (!running) return;
        sample(client, true);
        event("PROFILER_STOP");
        try {
            writeSummary(client);
        } catch (IOException exception) {
            ChaoCraft.LOGGER.error("Could not write ChaoCraft profiler summary", exception);
        } finally {
            closeQuietly();
        }
    }

    public static synchronized void toggle(MinecraftClient client) {
        if (running) stop(client); else start(client);
    }

    public static synchronized void snapshotNow(MinecraftClient client) {
        if (!running) start(client);
        sample(client, true);
        event("MANUAL_SNAPSHOT");
    }

    public static synchronized void tick(MinecraftClient client) {
        if (!running) return;
        sample(client, false);
    }

    public static synchronized void event(String name) {
        if (!running || events == null) return;
        try {
            double elapsed = (System.nanoTime() - startedNanos) / 1_000_000_000.0;
            events.write(String.format(java.util.Locale.ROOT, "%.3f\t%s%n", elapsed, name));
            events.flush();
        } catch (IOException exception) {
            ChaoCraft.LOGGER.warn("Could not append ChaoCraft profiler event {}", name, exception);
        }
    }

    private static void sample(MinecraftClient client, boolean force) {
        long now = System.nanoTime();
        if (!force && lastSampleNanos != Long.MIN_VALUE && now - lastSampleNanos < SAMPLE_NANOS) return;
        lastSampleNanos = now;
        if (csv == null) return;

        try {
            ChaoRenderMetrics.Snapshot m = ChaoRenderMetrics.snapshot();
            Runtime runtime = Runtime.getRuntime();
            long heapUsed = runtime.totalMemory() - runtime.freeMemory();
            DirectStats direct = directStats();
            int clientChao = 0;
            if (client.world != null && client.player != null) {
                clientChao = client.world.getEntitiesByClass(ChaoEntity.class,
                        client.player.getBoundingBox().expand(2048.0), entity -> true).size();
            }
            double elapsed = (now - startedNanos) / 1_000_000_000.0;
            csv.write(String.format(java.util.Locale.ROOT,
                    "%.3f,%d,%d,%d,%d,%d,%d,%d,%.3f,%d,%d,%.3f,%d,%.3f,%d,%.3f,%.3f,%.3f,%.3f,%.3f,%d,%d%n",
                    elapsed,
                    client.getCurrentFps(), clientChao,
                    m.buildsPerSecond(), m.deferredPerSecond(), m.evictionsPerSecond(),
                    m.cachedEntities(), m.sharedEntries(), mb(m.cachedEstimatedBytes()),
                    m.cacheHits(), m.cacheMisses(), m.averageRenderMicros(),
                    m.totalBatchesUploaded(), mb(m.totalBytesUploaded()), m.reflectionPasses(),
                    mb(heapUsed), mb(runtime.totalMemory()), mb(runtime.maxMemory()),
                    mb(direct.used), mb(direct.capacity), direct.count,
                    ChaoVisualLabNetworking.pendingMatrixSpawns()));
            csv.flush();
            samples++;
        } catch (IOException exception) {
            ChaoCraft.LOGGER.error("ChaoCraft profiler sample failed", exception);
            stop(client);
        }
    }

    private static void writeSummary(MinecraftClient client) throws IOException {
        ChaoRenderMetrics.Snapshot end = ChaoRenderMetrics.snapshot();
        try (BufferedWriter out = Files.newBufferedWriter(sessionDir.resolve("summary.txt"), StandardCharsets.UTF_8)) {
            out.write("ChaoCraft performance session\n");
            out.write("Duration seconds: " + String.format(java.util.Locale.ROOT, "%.2f", (System.nanoTime()-startedNanos)/1e9) + "\n");
            out.write("Samples: " + samples + "\n");
            out.write("Final FPS: " + client.getCurrentFps() + "\n");
            out.write("VBO builds during session: " + (end.totalBuilds() - startSnapshot.totalBuilds()) + "\n");
            out.write("Deferred build attempts: " + (end.totalDeferredBuilds() - startSnapshot.totalDeferredBuilds()) + "\n");
            out.write("Evictions: " + (end.totalEvictions() - startSnapshot.totalEvictions()) + "\n");
            out.write("GPU bytes uploaded MB: " + String.format(java.util.Locale.ROOT, "%.2f", mb(end.totalBytesUploaded() - startSnapshot.totalBytesUploaded())) + "\n");
            out.write("Final shared VBO entries: " + end.sharedEntries() + "\n");
            out.write("Final cache MB: " + String.format(java.util.Locale.ROOT, "%.2f", mb(end.cachedEstimatedBytes())) + "\n");
            out.write("Average Chao renderer CPU us/call: " + String.format(java.util.Locale.ROOT, "%.2f", end.averageRenderMicros()) + "\n");
            out.write("Upload this whole session folder (metrics.csv + events.log + summary.txt) for analysis.\n");
        }
    }

    private static DirectStats directStats() {
        long used = 0L, capacity = 0L, count = 0L;
        List<BufferPoolMXBean> pools = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
        for (BufferPoolMXBean pool : pools) {
            if (!"direct".equalsIgnoreCase(pool.getName())) continue;
            used += Math.max(0L, pool.getMemoryUsed());
            capacity += Math.max(0L, pool.getTotalCapacity());
            count += Math.max(0L, pool.getCount());
        }
        return new DirectStats(used, capacity, count);
    }

    private static double mb(long bytes) { return bytes / (1024.0 * 1024.0); }

    private static void closeQuietly() {
        running = false;
        try { if (csv != null) csv.close(); } catch (IOException ignored) {}
        try { if (events != null) events.close(); } catch (IOException ignored) {}
        csv = null;
        events = null;
    }

    private record DirectStats(long used, long capacity, long count) {}
}
