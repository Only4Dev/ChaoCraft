package com.chaocraft.client.render;

import com.chaocraft.entity.ChaoEntity;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/**
 * Temporary renderer used only while the morph-target Chao renderer is built.
 * It deliberately renders a slime block instead of inheriting SlimeEntity, so
 * the server-side entity architecture remains final from the beginning.
 */
public final class ChaoPlaceholderRenderer extends EntityRenderer<ChaoEntity> {
	public ChaoPlaceholderRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.shadowRadius = 0.28F;
	}

	@Override
	public void render(ChaoEntity entity, float yaw, float tickDelta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light) {
		matrices.push();
		matrices.scale(0.58F, 0.58F, 0.58F);
		matrices.translate(-0.5D, 0.0D, -0.5D);
		MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(
				Blocks.SLIME_BLOCK.getDefaultState(),
				matrices,
				vertexConsumers,
				light,
				OverlayTexture.DEFAULT_UV
		);
		matrices.pop();

		// Keeps vanilla nameplate/debug label rendering intact.
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
	}

	@Override
	public Identifier getTexture(ChaoEntity entity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}
}
