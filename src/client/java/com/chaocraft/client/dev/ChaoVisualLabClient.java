package com.chaocraft.client.dev;

import com.chaocraft.client.render.ChaoRenderer;
import com.chaocraft.client.render.ChaoClientResourcePreloader;
import com.chaocraft.client.render.debug.ChaoRenderMetrics;
import com.chaocraft.client.perf.ChaoPerformanceWatchdog;
import com.chaocraft.dev.ChaoVisualLabNetworking;
import com.chaocraft.visual.ChaoAppearanceState;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;

/** Client entry point for the isolated in-game Chao visual QA tool. */
public final class ChaoVisualLabClient {
    private static KeyBinding openLabKey;

    private ChaoVisualLabClient() {
    }

    public static void register() {
        openLabKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.chaocraft.visual_lab",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F8,
                "category.chaocraft"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Resource reload only schedules the warm-up. Upload atomic Chao
            // textures here after Minecraft's own TextureManager reload is complete.
            ChaoClientResourcePreloader.preloadPending(client);
            if (client.world != null) {
                ChaoRenderer.maintainCaches(client.world.getTime());
            }
            ChaoPerformanceWatchdog.tick(client);
            while (openLabKey.wasPressed()) {
                if (client.currentScreen instanceof ChaoVisualLabScreen) {
                    client.setScreen(null);
                } else if (client.currentScreen == null && client.player != null && client.world != null) {
                    client.setScreen(new ChaoVisualLabScreen());
                }
            }
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ChaoRenderMetrics.reset();
            ChaoPerformanceWatchdog.reset();

            long worldTick = client.world == null ? 0L : client.world.getTime();
            ChaoRenderer.beginWorldCaches(worldTick);
            ChaoPerformanceWatchdog.event("WORLD_JOIN_WARM_CACHE");
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ChaoPerformanceWatchdog.event("WORLD_DISCONNECT_KEEP_WARM");

            // Server/world objects are gone, but immutable visual VBOs are not
            // world data. Detach UUID bindings and keep the bounded shared cache.
            ChaoRenderMetrics.reset();
            ChaoRenderer.detachWorldCaches();
            ChaoPerformanceWatchdog.reset();
        });
    }

    /**
     * Spawns a real server-authoritative Chao from the Visual Lab's isolated
     * client-only draft. The preview itself never edits an existing world Chao.
     */
    public static void summonDraft(ChaoAppearanceState state) {
        PacketByteBuf buf = PacketByteBufs.create();
        ChaoVisualLabNetworking.writeState(buf, state);
        ClientPlayNetworking.send(ChaoVisualLabNetworking.SPAWN_DRAFT_CHAO, buf);
    }

    public static void spawnAdultMatrix() {
        ChaoPerformanceWatchdog.event("MATRIX_BASE_REQUEST");
        ClientPlayNetworking.send(ChaoVisualLabNetworking.SPAWN_ADULT_MATRIX, PacketByteBufs.empty());
    }

    public static void spawnChaosMatrix() {
        ChaoPerformanceWatchdog.event("MATRIX_CHAOS_REQUEST");
        ClientPlayNetworking.send(ChaoVisualLabNetworking.SPAWN_CHAOS_MATRIX, PacketByteBufs.empty());
    }

    public static void spawnColorMatrix(ChaoAppearanceState baseState) {
        ChaoPerformanceWatchdog.event("MATRIX_COLOR_REQUEST");
        PacketByteBuf buf = PacketByteBufs.create();
        ChaoVisualLabNetworking.writeState(buf, baseState);
        ClientPlayNetworking.send(ChaoVisualLabNetworking.SPAWN_COLOR_MATRIX, buf);
    }

    public static void spawnReflectionMatrix(ChaoAppearanceState baseState) {
        ChaoPerformanceWatchdog.event("MATRIX_REFLECTION_REQUEST");
        PacketByteBuf buf = PacketByteBufs.create();
        ChaoVisualLabNetworking.writeState(buf, baseState);
        ClientPlayNetworking.send(ChaoVisualLabNetworking.SPAWN_REFLECTION_MATRIX, buf);
    }

    public static void spawnAnimalMatrix(ChaoAppearanceState baseState) {
        ChaoPerformanceWatchdog.event("MATRIX_ANIMAL_REQUEST");
        PacketByteBuf buf = PacketByteBufs.create();
        ChaoVisualLabNetworking.writeState(buf, baseState);
        ClientPlayNetworking.send(ChaoVisualLabNetworking.SPAWN_ANIMAL_MATRIX, buf);
    }

    public static void spawnAdultExtremes() {
        ChaoPerformanceWatchdog.event("MATRIX_ADULT_75_REQUEST");
        ClientPlayNetworking.send(ChaoVisualLabNetworking.SPAWN_ADULT_EXTREMES, PacketByteBufs.empty());
    }

    public static void spawnChildExtremes() {
        ChaoPerformanceWatchdog.event("MATRIX_CHILD_REQUEST");
        ClientPlayNetworking.send(ChaoVisualLabNetworking.SPAWN_CHILD_EXTREMES, PacketByteBufs.empty());
    }

    public static void clearAdultMatrix() {
        ChaoPerformanceWatchdog.event("MATRIX_CLEAR_REQUEST");
        ClientPlayNetworking.send(ChaoVisualLabNetworking.CLEAR_ADULT_MATRIX, PacketByteBufs.empty());
    }
}
