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
 * CPU-side morph preparation cache used only while constructing a GPU state.
 *
 * <p>CP11R.18 makes GPU VBOs the persistent representation. Prepared position/
 * normal arrays are scratch data and are released immediately after upload, so
 * the client does not retain a second morphed CPU copy for every visible Chao.</p>
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

    /** One-shot preparation for startup warmup; caller does not retain the arrays. */
    public static Entry prepare(ChaoAppearanceState state, ChaoMeshModel model,
            float[] morphWeights, ChaoPaletteState palette) {
        return build(state, model, morphWeights, palette);
    }

    private static Entry build(ChaoAppearanceState state, ChaoMeshModel model,
            float[] morphWeights, ChaoPaletteState palette) {
        Map<ChaoMeshModel.Segment, PreparedSegment> segments = new IdentityHashMap<>();
        int sizeDownIndex = model.morphIndex("SizeDown");
        for (ChaoMeshModel.Segment segment : model.segments()) {
            float[] segmentWeights = morphWeights;
            if (sizeDownIndex >= 0 && isBaseSegmentReplaced(segment.name(), state)) {
                // Viewer SetBlendShapeWeights(): replaced renderers clear their
                // active family morph and use SizeDown=100% as the replacement shape.
                segmentWeights = new float[morphWeights.length];
                segmentWeights[sizeDownIndex] = 1.0F;
            }
            segments.put(segment, prepareSegment(segment, segmentWeights));
        }
        return new Entry(state, model, palette, segments);
    }

    private static boolean isBaseSegmentReplaced(String segmentName, ChaoAppearanceState state) {
        String name = segmentName.toLowerCase(java.util.Locale.ROOT);
        if (name.contains("arm")) return hasPart(state, Slot.ARMS);
        if (name.contains("leg") || name.contains("feet")) return hasPart(state, Slot.LEGS);
        if (name.contains("tail")) return hasPart(state, Slot.TAIL);
        if (name.contains("wing")) return hasPart(state, Slot.WINGS);

        // Viewer HeadDeco is the only decoration category that retracts the
        // base Head renderer. Ordinary Face/Forehead/Horns/Ears are attachments.
        if (name.contains("head")) {
            return state.headDeco() != com.chaocraft.visual.ChaoHeadDecoType.NONE;
        }
        return false;
    }

    private static boolean hasPart(ChaoAppearanceState state, Slot slot) {
        return state.animalParts().get(slot) != ChaoAnimalType.NONE;
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
