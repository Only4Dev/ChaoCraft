package com.chaocraft.client;

import com.chaocraft.client.render.ChaoPlaceholderRenderer;
import com.chaocraft.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ChaoCraftClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(ModEntities.CHAO, ChaoPlaceholderRenderer::new);
	}
}
