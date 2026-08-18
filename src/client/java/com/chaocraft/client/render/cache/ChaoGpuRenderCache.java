package com.chaocraft.client.render.cache;

import com.chaocraft.client.render.mesh.ChaoMeshModel;
import com.chaocraft.entity.ChaoEntity;
import com.chaocraft.visual.ChaoAppearanceState;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.RenderLayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * GPU-side render cache for Chao geometry.
 *
 * <p>Each Chao keeps static VBOs until an input that changes the actual rendered
 * mesh changes. This removes BufferBuilder/VertexConsumer work from the normal
 * per-frame path. Entries are also aged out so unloaded Chao release GPU memory.</p>
 */
public final class ChaoGpuRenderCache {
    private static final long STALE_TICKS = 100L;
    private static final long PRUNE_INTERVAL = 40L;

    private final Map<UUID, Entry> entries = new HashMap<>();
    private long lastPruneTick = Long.MIN_VALUE;

    public Entry get(ChaoEntity entity, ChaoAppearanceState state, ChaoMeshModel model,
            int packedLight, float yaw, long worldTick, Supplier<List<DrawBatch>> batchBuilder) {
        int yawKey = Math.round(yaw);
        UUID id = entity.getUuid();
        Entry cached = entries.get(id);
        if (cached != null && cached.matches(state, model, packedLight, yawKey)) {
            cached.lastSeenTick = worldTick;
            prune(worldTick);
            return cached;
        }

        if (cached != null) {
            cached.close();
        }

        Entry rebuilt = new Entry(state, model, packedLight, yawKey, batchBuilder.get(), worldTick);
        entries.put(id, rebuilt);
        prune(worldTick);
        return rebuilt;
    }

    public void clear() {
        for (Entry entry : entries.values()) {
            entry.close();
        }
        entries.clear();
        lastPruneTick = Long.MIN_VALUE;
    }

    private void prune(long worldTick) {
        // World time can move backwards after changing/reloading worlds.
        if (lastPruneTick != Long.MIN_VALUE && worldTick < lastPruneTick) {
            clear();
            lastPruneTick = worldTick;
            return;
        }
        if (lastPruneTick != Long.MIN_VALUE && worldTick - lastPruneTick < PRUNE_INTERVAL) {
            return;
        }
        lastPruneTick = worldTick;
        entries.entrySet().removeIf(entry -> {
            if (worldTick - entry.getValue().lastSeenTick <= STALE_TICKS) {
                return false;
            }
            entry.getValue().close();
            return true;
        });
    }

    public static final class Entry implements AutoCloseable {
        private final ChaoAppearanceState state;
        private final ChaoMeshModel model;
        private final int packedLight;
        private final int yawKey;
        private final List<DrawBatch> batches;
        private long lastSeenTick;

        private Entry(ChaoAppearanceState state, ChaoMeshModel model, int packedLight,
                int yawKey, List<DrawBatch> batches, long lastSeenTick) {
            this.state = state;
            this.model = model;
            this.packedLight = packedLight;
            this.yawKey = yawKey;
            this.batches = List.copyOf(batches);
            this.lastSeenTick = lastSeenTick;
        }

        public List<DrawBatch> batches() {
            return batches;
        }

        private boolean matches(ChaoAppearanceState otherState, ChaoMeshModel otherModel,
                int otherPackedLight, int otherYawKey) {
            return model == otherModel
                    && packedLight == otherPackedLight
                    && yawKey == otherYawKey
                    && state.equals(otherState);
        }

        @Override
        public void close() {
            for (DrawBatch batch : batches) {
                batch.close();
            }
        }
    }

    public record DrawBatch(RenderLayer layer, VertexBuffer vertexBuffer) implements AutoCloseable {
        @Override
        public void close() {
            if (!vertexBuffer.isClosed()) {
                vertexBuffer.close();
            }
        }
    }
}
