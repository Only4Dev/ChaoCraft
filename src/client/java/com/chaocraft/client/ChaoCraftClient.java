package com.chaocraft.client;

import com.chaocraft.ChaoCraft;
import com.chaocraft.client.dev.ChaoVisualLabClient;
import com.chaocraft.client.animation.ChaoAnimationRepository;
import com.chaocraft.client.render.ChaoRenderer;
import com.chaocraft.client.render.ChaoClientResourcePreloader;
import com.chaocraft.client.render.debug.ChaoRenderMetrics;
import com.chaocraft.client.perf.ChaoPerformanceWatchdog;
import com.chaocraft.client.render.mesh.ChaoMeshRepository;
import com.chaocraft.client.render.shader.ChaoMaterialShader;
import com.chaocraft.client.render.shader.ChaoReflectionShader;
import com.chaocraft.client.render.shader.ChaoReflectionSkinningShader;
import com.chaocraft.client.render.shader.ChaoSkinningShader;
import com.chaocraft.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class ChaoCraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ChaoMaterialShader.register();
        ChaoReflectionShader.register();
        ChaoReflectionSkinningShader.register();
        ChaoSkinningShader.register();
        EntityRendererRegistry.register(ModEntities.CHAO, ChaoRenderer::new);
        ChaoVisualLabClient.register();

        // .cmesh/VBO resources are renderer-owned native resources. Explicitly
        // invalidate them on F3+T/resource-pack reload instead of relying on GC.
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(
                new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public Identifier getFabricId() {
                        return ChaoCraft.id("renderer_cache");
                    }

                    @Override
                    public void reload(ResourceManager manager) {
                        ChaoPerformanceWatchdog.event("RESOURCE_RELOAD");
                        ChaoRenderer.clearAllCaches(true);
                        ChaoMeshRepository.reload(manager);
                        ChaoAnimationRepository.reload(manager);
                        ChaoClientResourcePreloader.prepare(manager);
                        ChaoRenderMetrics.reset();
                        ChaoPerformanceWatchdog.reset();
                    }
                }
        );
    }
}
