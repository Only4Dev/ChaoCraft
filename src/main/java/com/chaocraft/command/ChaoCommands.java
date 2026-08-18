package com.chaocraft.command;

import com.chaocraft.entity.ChaoEntity;
import com.chaocraft.visual.ChaoAppearanceState;
import com.chaocraft.visual.ChaoAppearanceState.EvolutionChannel;
import com.chaocraft.visual.ChaoColorType;
import com.chaocraft.visual.ChaoVisualType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/** Developer controls for validating Chao Viewer visual parity in-game. */
public final class ChaoCommands {
    private static final double SEARCH_RADIUS = 16.0D;

    private ChaoCommands() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                literal("chao")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(literal("type")
                                .then(literal("child").executes(context -> updateType(context.getSource(), ChaoVisualType.CHILD)))
                                .then(literal("normal").executes(context -> updateType(context.getSource(), ChaoVisualType.NORMAL)))
                                .then(literal("swim").executes(context -> updateType(context.getSource(), ChaoVisualType.SWIM)))
                                .then(literal("fly").executes(context -> updateType(context.getSource(), ChaoVisualType.FLY)))
                                .then(literal("run").executes(context -> updateType(context.getSource(), ChaoVisualType.RUN)))
                                .then(literal("power").executes(context -> updateType(context.getSource(), ChaoVisualType.POWER)))
                                .then(literal("chaos").executes(context -> updateType(context.getSource(), ChaoVisualType.CHAOS))))
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
                        .then(colorCommands())
                        .then(literal("monotone")
                                .then(argument("value", BoolArgumentType.bool())
                                        .executes(context -> update(context.getSource(), state -> state.withMonotone(
                                                BoolArgumentType.getBool(context, "value"))))))
                        .then(faceCommands())
                        .then(ballCommands())
                        .then(literal("halo")
                                .then(literal("default").executes(context -> updateHalo(context.getSource(), false)))
                                .then(literal("tilted").executes(context -> updateHalo(context.getSource(), true))))
                        .then(literal("reset").executes(context -> reset(context.getSource())))
                        .then(literal("state").executes(context -> showState(context.getSource())))
        ));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<ServerCommandSource> colorCommands() {
        var root = literal("color");
        for (ChaoColorType color : ChaoColorType.values()) {
            String name = color.name().toLowerCase(java.util.Locale.ROOT);
            root.then(literal(name).executes(context -> update(context.getSource(), state -> state.withColorType(color))));
        }
        return root;
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<ServerCommandSource> faceCommands() {
        return literal("face")
                .then(literal("auto").executes(context -> update(context.getSource(), ChaoAppearanceState::withAutoEyes)))
                .then(literal("eyes")
                        .then(argument("value", IntegerArgumentType.integer(0, 12))
                                .executes(context -> update(context.getSource(), state -> state.withEyes(
                                        IntegerArgumentType.getInteger(context, "value"))))))
                .then(literal("eyelid")
                        .then(argument("value", IntegerArgumentType.integer(0, 2))
                                .executes(context -> update(context.getSource(), state -> state.withEyelid(
                                        IntegerArgumentType.getInteger(context, "value"))))))
                .then(literal("mouth")
                        .then(argument("value", IntegerArgumentType.integer(0, 12))
                                .executes(context -> update(context.getSource(), state -> state.withMouth(
                                        IntegerArgumentType.getInteger(context, "value"))))))
                .then(literal("mouthadv")
                        .then(argument("mid", IntegerArgumentType.integer(0, 18))
                                .then(argument("side", IntegerArgumentType.integer(0, 18))
                                        .executes(context -> update(context.getSource(), state -> state.withAdvancedMouth(
                                                IntegerArgumentType.getInteger(context, "mid"),
                                                IntegerArgumentType.getInteger(context, "side")))))))
                .then(literal("mouthstandard")
                        .executes(context -> update(context.getSource(), ChaoAppearanceState::withStandardMouthMode)))
                .then(literal("reset")
                        .executes(context -> update(context.getSource(), ChaoAppearanceState::resetFace)));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<ServerCommandSource> ballCommands() {
        return literal("ball")
                .then(literal("auto")
                        .executes(context -> update(context.getSource(), ChaoAppearanceState::withAutoEmotionBall)))
                .then(literal("neutral")
                        .executes(context -> update(context.getSource(), state -> state.withCustomEmotionBalls(true, false, false))))
                .then(literal("hero")
                        .executes(context -> update(context.getSource(), state -> state.withCustomEmotionBalls(false, true, false))))
                .then(literal("dark")
                        .executes(context -> update(context.getSource(), state -> state.withCustomEmotionBalls(false, false, true))))
                .then(literal("none")
                        .executes(context -> update(context.getSource(), state -> state.withCustomEmotionBalls(false, false, false))))
                .then(literal("custom")
                        .then(argument("neutral", BoolArgumentType.bool())
                                .then(argument("hero", BoolArgumentType.bool())
                                        .then(argument("dark", BoolArgumentType.bool())
                                                .executes(context -> update(context.getSource(), state -> state.withCustomEmotionBalls(
                                                        BoolArgumentType.getBool(context, "neutral"),
                                                        BoolArgumentType.getBool(context, "hero"),
                                                        BoolArgumentType.getBool(context, "dark"))))))));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<ServerCommandSource> evolutionCommand(
            String name, EvolutionChannel channel) {
        return literal(name).then(argument("value", FloatArgumentType.floatArg(0.0F, 100.0F))
                .executes(context -> updateEvolution(
                        context.getSource(), channel, FloatArgumentType.getFloat(context, "value")
                )));
    }

    private static int updateType(ServerCommandSource source, ChaoVisualType type) {
        return update(source, state -> state.withType(type));
    }

    private static int updateAge(ServerCommandSource source, float value) {
        return update(source, state -> state.withAge(value));
    }

    private static int updateAlignment(ServerCommandSource source, float value) {
        return update(source, state -> state.withAlignment(value));
    }

    private static int updateEvolution(ServerCommandSource source, EvolutionChannel channel, float value) {
        return update(source, state -> state.withEvolution(channel, value));
    }

    private static int updateHalo(ServerCommandSource source, boolean tilted) {
        return update(source, state -> state.withTiltedHalo(tilted));
    }

    private static int update(ServerCommandSource source, java.util.function.UnaryOperator<ChaoAppearanceState> operation) {
        ChaoEntity chao = nearestChao(source);
        if (chao == null) {
            return 0;
        }
        chao.setAppearanceState(operation.apply(chao.getAppearanceState()));
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
        String eyes = state.customEyes()
                ? state.eyes() + "/" + state.eyelid()
                : "auto(" + state.resolvedEyes() + "/" + state.resolvedEyelid() + ")";
        String mouth = state.customMouth()
                ? "adv(" + state.resolvedMouthMid() + "," + state.resolvedMouthSide() + ")"
                : Integer.toString(state.mouth());
        String ball = state.customEmotionBall()
                ? String.format("custom(N=%s,H=%s,D=%s)", state.neutralBall(), state.heroBall(), state.darkBall())
                : "auto";

        source.sendFeedback(() -> Text.literal(String.format(
                "Chao VIS: type=%s age=%.2f alignment=%.1f normal=%.1f swim=%.1f fly=%.1f run=%.1f power=%.1f color=%s tone=%s face=%s mouth=%s ball=%s halo=%s",
                state.type().name().toLowerCase(), state.age(), state.alignment(), state.normal(),
                state.swim(), state.fly(), state.run(), state.power(),
                state.colorType().name().toLowerCase(), state.monotone() ? "monotone" : "two-tone", eyes, mouth, ball,
                state.tiltedHalo() ? "tilted" : "default"
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
