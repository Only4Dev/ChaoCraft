package com.chaocraft.command;

import com.chaocraft.entity.ChaoEntity;
import com.chaocraft.visual.ChaoAppearanceState;
import com.chaocraft.visual.ChaoAppearanceState.EvolutionChannel;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/** Developer controls for validating Chao Viewer morph parity in-game. */
public final class ChaoCommands {
	private static final double SEARCH_RADIUS = 16.0D;

	private ChaoCommands() {
	}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
				literal("chao")
						.requires(source -> source.hasPermissionLevel(2))
						.then(literal("age")
								.then(argument("value", FloatArgumentType.floatArg(0.0F, 1.0F))
										.executes(context -> updateAge(context.getSource(), FloatArgumentType.getFloat(context, "value")))))
						.then(literal("alignment")
								.then(argument("value", FloatArgumentType.floatArg(-100.0F, 100.0F))
										.executes(context -> updateAlignment(context.getSource(), FloatArgumentType.getFloat(context, "value")))))
						.then(evolutionCommand("swim", EvolutionChannel.SWIM))
						.then(evolutionCommand("fly", EvolutionChannel.FLY))
						.then(evolutionCommand("run", EvolutionChannel.RUN))
						.then(evolutionCommand("power", EvolutionChannel.POWER))
						.then(literal("reset").executes(context -> reset(context.getSource())))
						.then(literal("state").executes(context -> showState(context.getSource())))
		));
	}

	private static com.mojang.brigadier.builder.LiteralArgumentBuilder<ServerCommandSource> evolutionCommand(
			String name, EvolutionChannel channel) {
		return literal(name).then(argument("value", FloatArgumentType.floatArg(0.0F, 100.0F))
				.executes(context -> updateEvolution(
						context.getSource(), channel, FloatArgumentType.getFloat(context, "value")
				)));
	}

	private static int updateAge(ServerCommandSource source, float value) {
		ChaoEntity chao = nearestChao(source);
		if (chao == null) {
			return 0;
		}
		chao.setAppearanceState(chao.getAppearanceState().withAge(value));
		return feedback(source, chao);
	}

	private static int updateAlignment(ServerCommandSource source, float value) {
		ChaoEntity chao = nearestChao(source);
		if (chao == null) {
			return 0;
		}
		chao.setAppearanceState(chao.getAppearanceState().withAlignment(value));
		return feedback(source, chao);
	}

	private static int updateEvolution(ServerCommandSource source, EvolutionChannel channel, float value) {
		ChaoEntity chao = nearestChao(source);
		if (chao == null) {
			return 0;
		}
		chao.setAppearanceState(chao.getAppearanceState().withEvolution(channel, value));
		return feedback(source, chao);
	}

	private static int reset(ServerCommandSource source) {
		ChaoEntity chao = nearestChao(source);
		if (chao == null) {
			return 0;
		}
		chao.setAppearanceState(ChaoAppearanceState.DEFAULT);
		return feedback(source, chao);
	}

	private static int showState(ServerCommandSource source) {
		ChaoEntity chao = nearestChao(source);
		return chao == null ? 0 : feedback(source, chao);
	}

	private static int feedback(ServerCommandSource source, ChaoEntity chao) {
		ChaoAppearanceState state = chao.getAppearanceState();
		source.sendFeedback(() -> Text.literal(String.format(
				"Chao VIS: age=%.2f alignment=%.1f normal=%.1f swim=%.1f fly=%.1f run=%.1f power=%.1f",
				state.age(), state.alignment(), state.normal(), state.swim(), state.fly(), state.run(), state.power()
		)), false);
		return 1;
	}

	private static ChaoEntity nearestChao(ServerCommandSource source) {
		Vec3d center = source.getPosition();
		Box searchBox = Box.of(center, SEARCH_RADIUS * 2.0D, SEARCH_RADIUS * 2.0D, SEARCH_RADIUS * 2.0D);
		List<ChaoEntity> chaos = source.getWorld().getEntitiesByClass(ChaoEntity.class, searchBox, ChaoEntity::isAlive);
		ChaoEntity nearest = chaos.stream()
				.min(Comparator.comparingDouble(chao -> chao.getPos().squaredDistanceTo(center)))
				.orElse(null);
		if (nearest == null) {
			source.sendError(Text.literal("No Chao found within 16 blocks."));
		}
		return nearest;
	}
}
