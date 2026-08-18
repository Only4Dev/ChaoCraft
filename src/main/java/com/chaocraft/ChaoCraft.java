package com.chaocraft;

import com.chaocraft.command.ChaoCommands;
import com.chaocraft.config.ChaoServerConfig;
import com.chaocraft.entity.ModEntities;
import com.chaocraft.dev.ChaoVisualLabNetworking;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChaoCraft implements ModInitializer {
	public static final String MOD_ID = "chaocraft";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// Load server-authoritative performance distances before ModEntities is initialized.
		ChaoServerConfig.load();
		ModEntities.register();
		ChaoCommands.register();
		ChaoVisualLabNetworking.registerServerReceivers();
		LOGGER.info("ChaoCraft initialized.");
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
