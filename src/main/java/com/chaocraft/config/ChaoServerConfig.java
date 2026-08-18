package com.chaocraft.config;

import com.chaocraft.ChaoCraft;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Server-authoritative ChaoCraft performance distances.
 *
 * <p>The view distance is consumed while the Chao EntityType is registered, so
 * it is intentionally startup-only. The simulation distance is read by each
 * Chao's server-side simulation gate. Keeping both values in the common/server
 * source set means a dedicated server never depends on client classes.</p>
 */
public final class ChaoServerConfig {
    private static final String FILE_NAME = "chaocraft-server.properties";
    private static final int DEFAULT_SIMULATION_DISTANCE_BLOCKS = 80;
    private static final int DEFAULT_VIEW_DISTANCE_BLOCKS = 80;
    private static final int MAX_DISTANCE_BLOCKS = 1024;

    private static ChaoServerConfig INSTANCE = new ChaoServerConfig(
            DEFAULT_SIMULATION_DISTANCE_BLOCKS,
            DEFAULT_VIEW_DISTANCE_BLOCKS
    );

    private final int simulationDistanceBlocks;
    private final int viewDistanceBlocks;

    private ChaoServerConfig(int simulationDistanceBlocks, int viewDistanceBlocks) {
        this.simulationDistanceBlocks = simulationDistanceBlocks;
        this.viewDistanceBlocks = viewDistanceBlocks;
    }

    /** Loads the server config before ModEntities is initialized. */
    public static synchronized void load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        createDefaultFileIfMissing(path);

        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException exception) {
            ChaoCraft.LOGGER.warn("Could not read {}; using safe defaults", path, exception);
            INSTANCE = new ChaoServerConfig(
                    DEFAULT_SIMULATION_DISTANCE_BLOCKS,
                    DEFAULT_VIEW_DISTANCE_BLOCKS
            );
            return;
        }

        int simulation = readDistance(
                properties,
                "simulation_distance_blocks",
                DEFAULT_SIMULATION_DISTANCE_BLOCKS,
                0
        );
        int view = readDistance(
                properties,
                "view_distance_blocks",
                DEFAULT_VIEW_DISTANCE_BLOCKS,
                16
        );
        INSTANCE = new ChaoServerConfig(simulation, view);
        ChaoCraft.LOGGER.info(
                "Chao server distances: simulation={} blocks, view/tracking={} blocks",
                simulation == 0 ? "vanilla" : simulation,
                view
        );
    }

    public static ChaoServerConfig get() {
        return INSTANCE;
    }

    /**
     * Additional ChaoCraft simulation radius around a non-spectator player.
     * Zero disables the extra radius and leaves loaded/ticking-chunk behavior to
     * Minecraft itself.
     */
    public int simulationDistanceBlocks() {
        return simulationDistanceBlocks;
    }

    /** Server tracking radius used by FabricEntityTypeBuilder at startup. */
    public int viewDistanceBlocks() {
        return viewDistanceBlocks;
    }

    private static int readDistance(Properties properties, String key, int fallback, int minimum) {
        String raw = properties.getProperty(key);
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            int parsed = Integer.parseInt(raw.trim());
            if (parsed < minimum || parsed > MAX_DISTANCE_BLOCKS) {
                ChaoCraft.LOGGER.warn(
                        "Ignoring {}={} (allowed {}..{}); using {}",
                        key, parsed, minimum, MAX_DISTANCE_BLOCKS, fallback
                );
                return fallback;
            }
            return parsed;
        } catch (NumberFormatException exception) {
            ChaoCraft.LOGGER.warn("Ignoring invalid {}='{}'; using {}", key, raw, fallback);
            return fallback;
        }
    }

    private static void createDefaultFileIfMissing(Path path) {
        if (Files.exists(path)) {
            return;
        }
        try {
            Files.createDirectories(path.getParent());
            String template = """
                    # ChaoCraft server configuration
                    # Distances are measured in blocks and are authoritative on the server.
                    #
                    # Additional radius in which ChaoCraft-owned AI/lifecycle/simulation work runs.
                    # 0 = no additional ChaoCraft cap; obey Minecraft's normal loaded/ticking chunks.
                    simulation_distance_blocks=80
                    #
                    # How far the server tracks/sends Chao entities to players.
                    # 80 blocks = the current vanilla-like 5-chunk baseline used by ChaoCraft.
                    # This value is read when the entity type is registered and requires a restart.
                    view_distance_blocks=80
                    """;
            Files.writeString(path, template, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            ChaoCraft.LOGGER.warn("Could not create default ChaoCraft server config at {}", path, exception);
        }
    }
}
