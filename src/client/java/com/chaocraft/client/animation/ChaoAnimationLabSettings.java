package com.chaocraft.client.animation;

import com.chaocraft.ChaoCraft;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;

/**
 * Tiny persistent tuning store used only by the F8 Animation Lab.
 *
 * <p>Speeds are intentionally kept outside the imported raw clips so animation
 * research never mutates source data. The resulting values can later be folded
 * into the production MotionTable/runtime contract once they are validated.</p>
 */
public final class ChaoAnimationLabSettings {
    private static final double DEFAULT_SPEED = 0.50D;
    private static final double MIN_SPEED = 0.05D;
    private static final double MAX_SPEED = 2.00D;
    private static final Properties SPEEDS = new Properties();
    private static boolean loaded;

    private ChaoAnimationLabSettings() {
    }

    public static double speed(ChaoAnimationClip clip) {
        ensureLoaded();
        if (clip == null) return DEFAULT_SPEED;
        String raw = SPEEDS.getProperty(key(clip));
        if (raw == null) return DEFAULT_SPEED;
        try {
            return clamp(Double.parseDouble(raw));
        } catch (NumberFormatException ignored) {
            return DEFAULT_SPEED;
        }
    }

    public static void setSpeed(ChaoAnimationClip clip, double speed) {
        if (clip == null) return;
        ensureLoaded();
        double resolved = Math.round(clamp(speed) * 100.0D) / 100.0D;
        SPEEDS.setProperty(key(clip), String.format(Locale.ROOT, "%.2f", resolved));
        save();
    }

    public static void resetSpeed(ChaoAnimationClip clip) {
        if (clip == null) return;
        ensureLoaded();
        SPEEDS.remove(key(clip));
        save();
    }

    private static String key(ChaoAnimationClip clip) {
        return String.format(Locale.ROOT, "%03d.%s", clip.exportIndex(), clip.name());
    }

    private static double clamp(double speed) {
        return Math.max(MIN_SPEED, Math.min(MAX_SPEED, speed));
    }

    private static void ensureLoaded() {
        if (loaded) return;
        loaded = true;
        Path file = settingsFile();
        if (!Files.isRegularFile(file)) return;
        try (InputStream input = Files.newInputStream(file)) {
            SPEEDS.load(input);
        } catch (IOException exception) {
            ChaoCraft.LOGGER.warn("Could not load Chao Animation Lab speeds from {}", file, exception);
        }
    }

    private static void save() {
        Path file = settingsFile();
        try {
            Files.createDirectories(file.getParent());
            try (OutputStream output = Files.newOutputStream(file)) {
                SPEEDS.store(output, "ChaoCraft F8 Animation Lab per-clip speed overrides");
            }
        } catch (IOException exception) {
            ChaoCraft.LOGGER.warn("Could not save Chao Animation Lab speeds to {}", file, exception);
        }
    }

    private static Path settingsFile() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.runDirectory.toPath().resolve("config").resolve("chaocraft-animation-lab.properties");
    }
}
