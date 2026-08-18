package com.chaocraft.entity;

import com.chaocraft.ChaoCraft;
import com.chaocraft.config.ChaoServerConfig;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

/** Central registry for ChaoCraft entity types and their vanilla attributes. */
public final class ModEntities {
	public static final EntityType<ChaoEntity> CHAO = Registry.register(
			Registries.ENTITY_TYPE,
			ChaoCraft.id("chao"),
			FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ChaoEntity::new)
					.dimensions(EntityDimensions.fixed(0.70F, 0.85F))
					// Server-side tracking/view distance. The default remains the validated
					// 80-block vanilla-like baseline, but server owners can tune it before
					// startup without adding any client-side render-distance hacks.
					.trackRangeBlocks(ChaoServerConfig.get().viewDistanceBlocks())
					.trackedUpdateRate(3)
					.build()
	);

	private ModEntities() {
	}

	public static void register() {
		FabricDefaultAttributeRegistry.register(CHAO, ChaoEntity.createAttributes());
	}
}
