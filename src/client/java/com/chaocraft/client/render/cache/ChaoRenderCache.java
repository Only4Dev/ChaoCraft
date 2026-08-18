package com.chaocraft.client.render.cache;

import com.chaocraft.client.render.material.ChaoPaletteState;
import com.chaocraft.client.render.mesh.ChaoMeshModel;
import com.chaocraft.entity.ChaoEntity;
import com.chaocraft.visual.ChaoAppearanceState;
import com.chaocraft.visual.ChaoAnimalParts.Slot;
import com.chaocraft.visual.ChaoAnimalType;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Per-entity client render cache.
 *
 * <p>The extracted Child model is large enough that recalculating every blend
 * shape for every material pass on every frame is prohibitively expensive.
 * This cache keeps the already-morphed geometry until the visual state changes.
 * Weak entity keys ensure unloaded/dead Chao do not keep client memory alive.</p>
 */
public final class ChaoRenderCache {
    private final Map<ChaoEntity, Entry> entries = new WeakHashMap<>();

    public Entry get(ChaoEntity entity, ChaoAppearanceState state, ChaoMeshModel model,
            float[] morphWeights, ChaoPaletteState palette) {
        Entry cached = entries.get(entity);
        if (cached != null && cached.matches(state, model)) {
            return cached;
        }

        Entry rebuilt = build(state, model, morphWeights, palette);
        entries.put(entity, rebuilt);
        return rebuilt;
    }

    /** Releases one entity's prepared CPU morph arrays immediately. */
    public void remove(UUID entityId) {
        entries.entrySet().removeIf(entry -> entry.getKey() == null || entry.getKey().getUuid().equals(entityId));
    }

    public void clear() {
        entries.clear();
    }

    private static Entry build(ChaoAppearanceState state, ChaoMeshModel model,
            float[] morphWeights, ChaoPaletteState palette) {
        Map<ChaoMeshModel.Segment, PreparedSegment> segments = new IdentityHashMap<>();
        int sizeDownIndex = model.morphIndex("SizeDown");
        for (ChaoMeshModel.Segment segment : model.segments()) {
            float[] segmentWeights = morphWeights;
            if (sizeDownIndex >= 0 && isBaseSegmentReplaced(segment.name(), state)) {
                // Chao Viewer SetBlendShapeWeights() retracts the original body
                // segment through its SizeDown key when an animal part occupies
                // Arms/Legs/Tail/Wings. It does not simply hide the mesh.
                segmentWeights = new float[morphWeights.length];
                segmentWeights[sizeDownIndex] = 1.0F;
            }
            segments.put(segment, prepareSegment(segment, segmentWeights));
        }
        return new Entry(state, model, palette, segments);
    }

    private static boolean isBaseSegmentReplaced(String segmentName, ChaoAppearanceState state) {
        String name = segmentName.toLowerCase(java.util.Locale.ROOT);
        if (name.contains("arm")) return state.animalParts().get(Slot.ARMS) != ChaoAnimalType.NONE;
        if (name.contains("leg") || name.contains("feet")) return state.animalParts().get(Slot.LEGS) != ChaoAnimalType.NONE;
        if (name.contains("tail")) return state.animalParts().get(Slot.TAIL) != ChaoAnimalType.NONE;
        if (name.contains("wing")) return state.animalParts().get(Slot.WINGS) != ChaoAnimalType.NONE;
        return false;
    }

    private static PreparedSegment prepareSegment(ChaoMeshModel.Segment segment, float[] weights) {
        float[] positions = new float[segment.positions().length];
        float[] normals = new float[segment.normals().length];
        int morphCount = Math.min(weights.length, segment.morphPositionDeltas().length);

        for (int vertex = 0; vertex < segment.vertexCount(); vertex++) {
            int p = vertex * 3;
            float x = segment.positions()[p];
            float y = segment.positions()[p + 1];
            float z = segment.positions()[p + 2];
            float nx = segment.normals()[p];
            float ny = segment.normals()[p + 1];
            float nz = segment.normals()[p + 2];

            for (int morph = 0; morph < morphCount; morph++) {
                float weight = weights[morph];
                if (weight == 0.0F) {
                    continue;
                }
                float[] positionDelta = segment.morphPositionDeltas()[morph];
                float[] normalDelta = segment.morphNormalDeltas()[morph];
                x += positionDelta[p] * weight;
                y += positionDelta[p + 1] * weight;
                z += positionDelta[p + 2] * weight;
                nx += normalDelta[p] * weight;
                ny += normalDelta[p + 1] * weight;
                nz += normalDelta[p + 2] * weight;
            }

            // Unity -> Minecraft handedness conversion. This used to happen for
            // every emitted material-pass vertex; now it is baked once per state.
            z = -z;
            nz = -nz;

            float lengthSquared = nx * nx + ny * ny + nz * nz;
            if (lengthSquared > 0.000001F) {
                float inverseLength = (float) (1.0D / Math.sqrt(lengthSquared));
                nx *= inverseLength;
                ny *= inverseLength;
                nz *= inverseLength;
            } else {
                nx = 0.0F;
                ny = 1.0F;
                nz = 0.0F;
            }

            positions[p] = x;
            positions[p + 1] = y;
            positions[p + 2] = z;
            normals[p] = nx;
            normals[p + 1] = ny;
            normals[p + 2] = nz;
        }

        return new PreparedSegment(positions, normals);
    }

    public record Entry(
            ChaoAppearanceState state,
            ChaoMeshModel model,
            ChaoPaletteState palette,
            Map<ChaoMeshModel.Segment, PreparedSegment> segments
    ) {
        public boolean matches(ChaoAppearanceState otherState, ChaoMeshModel otherModel) {
            return model == otherModel && state.equals(otherState);
        }

        public PreparedSegment segment(ChaoMeshModel.Segment segment) {
            return segments.get(segment);
        }
    }

    public record PreparedSegment(float[] positions, float[] normals) {
    }
}
