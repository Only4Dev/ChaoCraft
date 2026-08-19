package com.chaocraft.client.animation;

import com.chaocraft.ChaoCraft;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Client-only immutable library of original SA2 Chao motions.
 *
 * <p>The source JSONs are generated offline by SA Tools AnimJSONConverter and are
 * parsed once during client resource reload. Gameplay never reads SA2 files and
 * the server has no dependency on animation assets.</p>
 */
public final class ChaoAnimationRepository {
    private static volatile List<ChaoAnimationClip> clips = List.of();

    private ChaoAnimationRepository() {
    }

    public static void reload(ResourceManager manager) {
        long started = System.nanoTime();
        Map<Identifier, Resource> resources = manager.findResources(
                "animations/chao/original",
                id -> id.getPath().endsWith(".json")
        );

        List<ChaoAnimationClip> loaded = new ArrayList<>(resources.size());
        int failed = 0;
        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    entry.getValue().getInputStream(), StandardCharsets.UTF_8))) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                loaded.add(parse(entry.getKey(), root));
            } catch (IOException | RuntimeException exception) {
                failed++;
                ChaoCraft.LOGGER.warn("Failed to load Chao animation {}", entry.getKey(), exception);
            }
        }

        loaded.sort(Comparator.comparingInt(ChaoAnimationClip::exportIndex));
        clips = List.copyOf(loaded);

        double millis = (System.nanoTime() - started) / 1_000_000.0D;
        ChaoCraft.LOGGER.info(
                "Preloaded {} original SA2 Chao animation clips in {} ms{}",
                loaded.size(),
                String.format(Locale.ROOT, "%.1f", millis),
                failed == 0 ? "" : " (" + failed + " failed)"
        );
    }

    public static List<ChaoAnimationClip> clips() {
        return clips;
    }

    public static ChaoAnimationClip clip(int index) {
        List<ChaoAnimationClip> snapshot = clips;
        if (snapshot.isEmpty()) return null;
        int wrapped = Math.floorMod(index, snapshot.size());
        return snapshot.get(wrapped);
    }

    private static ChaoAnimationClip parse(Identifier id, JsonObject root) {
        int exportIndex = parseExportIndex(id.getPath());
        String name = string(root, "Name", id.getPath());
        int frames = integer(root, "Frames", 1);
        int modelParts = integer(root, "ModelParts", 0);
        int interpolationMode = integer(root, "InterpolationMode", 0);
        boolean shortRot = bool(root, "ShortRot", false);

        Map<Integer, ChaoAnimationClip.NodeTrack> nodes = new TreeMap<>();
        JsonObject models = root.getAsJsonObject("Models");
        if (models != null) {
            for (Map.Entry<String, JsonElement> nodeEntry : models.entrySet()) {
                int nodeIndex = Integer.parseInt(nodeEntry.getKey());
                JsonObject node = nodeEntry.getValue().getAsJsonObject();
                Map<Integer, Vector3f> positions = parseVectors(node.getAsJsonObject("Position"));
                Map<Integer, ChaoAnimationClip.RotationKey> rotations = parseRotations(node.getAsJsonObject("Rotation"));
                int keyframes = integer(node, "NbKeyframes", Math.max(positions.size(), rotations.size()));
                nodes.put(nodeIndex, new ChaoAnimationClip.NodeTrack(positions, rotations, keyframes));
            }
        }

        return new ChaoAnimationClip(exportIndex, name, frames, modelParts, interpolationMode, shortRot, nodes);
    }

    private static Map<Integer, Vector3f> parseVectors(JsonObject object) {
        Map<Integer, Vector3f> result = new LinkedHashMap<>();
        if (object == null) return result;
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            String[] xyz = entry.getValue().getAsString().split(",");
            if (xyz.length != 3) continue;
            result.put(Integer.parseInt(entry.getKey()), new Vector3f(
                    Float.parseFloat(xyz[0].trim()),
                    Float.parseFloat(xyz[1].trim()),
                    Float.parseFloat(xyz[2].trim())
            ));
        }
        return result;
    }

    private static Map<Integer, ChaoAnimationClip.RotationKey> parseRotations(JsonObject object) {
        Map<Integer, ChaoAnimationClip.RotationKey> result = new LinkedHashMap<>();
        if (object == null) return result;
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            String[] xyz = entry.getValue().getAsString().split(",");
            if (xyz.length != 3) continue;
            result.put(Integer.parseInt(entry.getKey()), new ChaoAnimationClip.RotationKey(
                    parseHexAngle(xyz[0]), parseHexAngle(xyz[1]), parseHexAngle(xyz[2])
            ));
        }
        return result;
    }

    private static int parseHexAngle(String value) {
        String text = value.trim();
        if (text.isEmpty()) return 0;
        long raw = Long.parseUnsignedLong(text, 16);
        return (int) raw;
    }

    private static int parseExportIndex(String path) {
        String filename = path.substring(path.lastIndexOf('/') + 1);
        int first = filename.indexOf('_');
        int second = filename.indexOf('_', first + 1);
        if (first < 0 || second < 0) return Integer.MAX_VALUE;
        try {
            return Integer.parseInt(filename.substring(first + 1, second));
        } catch (NumberFormatException ignored) {
            return Integer.MAX_VALUE;
        }
    }

    private static String string(JsonObject object, String key, String fallback) {
        JsonElement element = object.get(key);
        return element == null || element.isJsonNull() ? fallback : element.getAsString();
    }

    private static int integer(JsonObject object, String key, int fallback) {
        JsonElement element = object.get(key);
        return element == null || element.isJsonNull() ? fallback : element.getAsInt();
    }

    private static boolean bool(JsonObject object, String key, boolean fallback) {
        JsonElement element = object.get(key);
        return element == null || element.isJsonNull() ? fallback : element.getAsBoolean();
    }
}
