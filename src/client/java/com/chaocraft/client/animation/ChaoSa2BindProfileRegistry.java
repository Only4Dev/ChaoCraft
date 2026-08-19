package com.chaocraft.client.animation;

import com.chaocraft.ChaoCraft;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Process-lifetime registry of the original SA2 Chao bind profiles.
 *
 * <p>The source atlas was extracted directly from the user's original
 * {@code .sa2mdl} files. It contains 114 audited Chao models, each with the
 * universal 40-node topology. Loading is finite CPU work and happens once;
 * profiles then remain immutable and shared across every Chao/world.</p>
 */
public final class ChaoSa2BindProfileRegistry {
    public static final String CHILD_GOLDEN_MODEL_ID = "al_ncn";

    private static final String RESOURCE_PATH =
            "/assets/chaocraft/chao/rig/sa2_bind_profiles.json";
    private static final int FORMAT_VERSION = 1;
    private static final float CHILD_GOLDEN_TOLERANCE = 2.0E-6F;
    private static final double BAMS_TO_RADIANS = Math.PI * 2.0D / 65536.0D;

    private static final Object LOAD_LOCK = new Object();
    private static volatile boolean loadAttempted;
    private static volatile Map<String, ChaoSa2BindProfile> profiles = Map.of();

    private ChaoSa2BindProfileRegistry() {
    }

    /**
     * Loads all finite bind profiles once and returns the resident profile count.
     * Failure is non-fatal in CP12I.1 because the approved Child path still uses
     * {@link ChaoSa2RigDefinition} directly.
     */
    public static int preload() {
        ensureLoaded();
        return profiles.size();
    }

    public static int loadedProfileCount() {
        ensureLoaded();
        return profiles.size();
    }

    public static Optional<ChaoSa2BindProfile> find(String modelId) {
        ensureLoaded();
        return Optional.ofNullable(profiles.get(normalizeModelId(modelId)));
    }

    public static ChaoSa2BindProfile require(String modelId) {
        return find(modelId).orElseThrow(() -> new IllegalArgumentException(
                "No SA2 Chao bind profile registered for " + modelId));
    }

    public static Map<String, ChaoSa2BindProfile> profiles() {
        ensureLoaded();
        return profiles;
    }

    private static void ensureLoaded() {
        if (loadAttempted) {
            return;
        }
        synchronized (LOAD_LOCK) {
            if (loadAttempted) {
                return;
            }
            loadAttempted = true;

            long started = System.nanoTime();
            try {
                Map<String, ChaoSa2BindProfile> parsed = loadProfiles();
                validateChildGolden(parsed.get(CHILD_GOLDEN_MODEL_ID));
                profiles = Collections.unmodifiableMap(parsed);

                double elapsedMs = (System.nanoTime() - started) / 1_000_000.0D;
                ChaoCraft.LOGGER.info(
                        "[Performance] Preloaded {} universal SA2 Chao bind profiles in {} ms",
                        profiles.size(),
                        String.format(java.util.Locale.ROOT, "%.1f", elapsedMs));
            } catch (RuntimeException | IOException failure) {
                // CP12I.1 is architecture-only: never risk the existing Child
                // renderer if the new atlas is unavailable or malformed.
                profiles = Map.of();
                ChaoCraft.LOGGER.error(
                        "Failed to preload universal SA2 Chao bind profiles; "
                                + "keeping legacy Child rig path active",
                        failure);
            }
        }
    }

    private static Map<String, ChaoSa2BindProfile> loadProfiles() throws IOException {
        try (InputStream stream = ChaoSa2BindProfileRegistry.class.getResourceAsStream(RESOURCE_PATH)) {
            if (stream == null) {
                throw new IOException("Missing classpath resource " + RESOURCE_PATH);
            }

            JsonObject root;
            try (InputStreamReader reader =
                         new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                root = JsonParser.parseReader(reader).getAsJsonObject();
            }

            int version = root.get("formatVersion").getAsInt();
            int nodeCount = root.get("nodeCount").getAsInt();
            String referenceModel = root.get("referenceModel").getAsString();
            if (version != FORMAT_VERSION) {
                throw new IOException("Unsupported SA2 bind atlas format " + version);
            }
            if (nodeCount != ChaoSa2RigDefinition.NODE_COUNT) {
                throw new IOException("SA2 bind atlas nodeCount=" + nodeCount
                        + ", expected " + ChaoSa2RigDefinition.NODE_COUNT);
            }
            if (!CHILD_GOLDEN_MODEL_ID.equals(referenceModel)) {
                throw new IOException("Unexpected SA2 bind atlas reference model " + referenceModel);
            }

            validateUniversalTopology(root.getAsJsonArray("universalTopology"));

            JsonObject models = root.getAsJsonObject("models");
            Map<String, ChaoSa2BindProfile> result = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> entry : models.entrySet()) {
                String modelId = normalizeModelId(entry.getKey());
                JsonArray sourceNodes = entry.getValue().getAsJsonObject().getAsJsonArray("nodes");
                if (sourceNodes.size() != ChaoSa2RigDefinition.NODE_COUNT) {
                    throw new IOException(modelId + " has " + sourceNodes.size()
                            + " nodes, expected " + ChaoSa2RigDefinition.NODE_COUNT);
                }

                ChaoSa2BindProfile.Node[] nodes =
                        new ChaoSa2BindProfile.Node[ChaoSa2RigDefinition.NODE_COUNT];
                for (JsonElement sourceNode : sourceNodes) {
                    JsonObject node = sourceNode.getAsJsonObject();
                    int index = node.get("index").getAsInt();
                    if (index < 0 || index >= nodes.length || nodes[index] != null) {
                        throw new IOException(modelId + " has invalid/duplicate node index " + index);
                    }

                    JsonArray p = node.getAsJsonArray("position");
                    JsonArray r = node.getAsJsonArray("rotationBams");
                    JsonArray s = node.getAsJsonArray("scale");
                    nodes[index] = new ChaoSa2BindProfile.Node(
                            p.get(0).getAsFloat(), p.get(1).getAsFloat(), p.get(2).getAsFloat(),
                            bamsToRadians(r.get(0).getAsInt()),
                            bamsToRadians(r.get(1).getAsInt()),
                            bamsToRadians(r.get(2).getAsInt()),
                            s.get(0).getAsFloat(), s.get(1).getAsFloat(), s.get(2).getAsFloat(),
                            node.get("ignorePosition").getAsBoolean(),
                            node.get("ignoreRotation").getAsBoolean(),
                            node.get("ignoreScale").getAsBoolean(),
                            node.get("rotateZYX").getAsBoolean());
                }

                result.put(modelId, new ChaoSa2BindProfile(modelId, nodes));
            }

            if (!result.containsKey(CHILD_GOLDEN_MODEL_ID)) {
                throw new IOException("SA2 bind atlas does not contain " + CHILD_GOLDEN_MODEL_ID);
            }
            return result;
        }
    }

    private static void validateUniversalTopology(JsonArray topology) throws IOException {
        if (topology == null || topology.size() != ChaoSa2RigDefinition.NODE_COUNT) {
            throw new IOException("SA2 bind atlas universal topology is missing/incomplete");
        }

        for (int i = 0; i < topology.size(); i++) {
            JsonObject source = topology.get(i).getAsJsonObject();
            int index = source.get("index").getAsInt();
            int parent = source.get("parent").getAsInt();
            if (index != i || parent != ChaoSa2RigNodeRegistry.node(i).parent()) {
                throw new IOException("SA2 bind atlas topology mismatch at node " + i);
            }
        }
    }

    /**
     * Proves that the binary-extracted al_ncn profile agrees with the exact DAE
     * Child rig already validated in ChaoCraft before we trust the atlas.
     */
    private static void validateChildGolden(ChaoSa2BindProfile parsedChild) throws IOException {
        if (parsedChild == null) {
            throw new IOException("Missing parsed Child golden profile");
        }

        float maxDifference = 0.0F;
        int maxNode = -1;
        for (int i = 0; i < ChaoSa2RigDefinition.NODE_COUNT; i++) {
            ChaoSa2RigDefinition.Node legacy = ChaoSa2RigDefinition.node(i);
            ChaoSa2BindProfile.Node parsed = parsedChild.node(i);

            float difference = maxAbs(
                    legacy.position().x, legacy.position().y, legacy.position().z,
                    legacy.rotation().x, legacy.rotation().y, legacy.rotation().z,
                    legacy.scale().x, legacy.scale().y, legacy.scale().z,
                    parsed.px(), parsed.py(), parsed.pz(),
                    parsed.rx(), parsed.ry(), parsed.rz(),
                    parsed.sx(), parsed.sy(), parsed.sz());
            if (difference > maxDifference) {
                maxDifference = difference;
                maxNode = i;
            }
        }

        if (maxDifference > CHILD_GOLDEN_TOLERANCE) {
            throw new IOException("Parsed al_ncn differs from approved Child golden at node "
                    + maxNode + " by " + maxDifference);
        }

        ChaoCraft.LOGGER.info(
                "[Animation] Universal SA2 bind atlas validated against al_ncn golden "
                        + "(max component delta={})",
                maxDifference);
    }

    private static float maxAbs(float... values) {
        float max = 0.0F;
        for (int i = 0; i < 9; i++) {
            max = Math.max(max, Math.abs(values[i] - values[i + 9]));
        }
        return max;
    }

    private static float bamsToRadians(int rawValue) {
        int signed = rawValue & 0xFFFF;
        if (signed >= 0x8000) {
            signed -= 0x10000;
        }
        return (float) (signed * BAMS_TO_RADIANS);
    }

    private static String normalizeModelId(String modelId) {
        if (modelId == null) {
            return "";
        }
        String normalized = modelId.trim().toLowerCase(java.util.Locale.ROOT);
        if (normalized.endsWith(".sa2mdl")) {
            normalized = normalized.substring(0, normalized.length() - ".sa2mdl".length());
        }
        return normalized;
    }
}
