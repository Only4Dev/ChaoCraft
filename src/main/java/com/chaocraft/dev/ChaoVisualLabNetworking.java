package com.chaocraft.dev;

import com.chaocraft.ChaoCraft;
import com.chaocraft.entity.ChaoEntity;
import com.chaocraft.entity.ModEntities;
import com.chaocraft.visual.ChaoAppearanceState;
import com.chaocraft.visual.ChaoColorType;
import com.chaocraft.visual.ChaoReflectionType;
import com.chaocraft.visual.ChaoAnimalType;
import com.chaocraft.visual.ChaoAnimalParts;
import com.chaocraft.visual.ChaoHeadDecoType;
import com.chaocraft.visual.ChaoVisualType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Server-authoritative networking for the in-game Visual Lab.
 *
 * The Visual Lab edits only a temporary client-only preview. When the user
 * explicitly summons that draft (or a test matrix), creation happens on the
 * authoritative server and the resulting Chao uses normal DataTracker sync.
 */
public final class ChaoVisualLabNetworking {
    public static final Identifier SPAWN_DRAFT_CHAO = ChaoCraft.id("visual_lab/spawn_draft_chao");
    public static final Identifier SPAWN_ADULT_MATRIX = ChaoCraft.id("visual_lab/spawn_adult_matrix");
    public static final Identifier SPAWN_ADULT_EXTREMES = ChaoCraft.id("visual_lab/spawn_adult_extremes");
    public static final Identifier SPAWN_CHILD_EXTREMES = ChaoCraft.id("visual_lab/spawn_child_extremes");
    public static final Identifier SPAWN_CHAOS_MATRIX = ChaoCraft.id("visual_lab/spawn_chaos_matrix");
    public static final Identifier SPAWN_COLOR_MATRIX = ChaoCraft.id("visual_lab/spawn_color_matrix");
    public static final Identifier SPAWN_REFLECTION_MATRIX = ChaoCraft.id("visual_lab/spawn_reflection_matrix");
    public static final Identifier SPAWN_ANIMAL_MATRIX = ChaoCraft.id("visual_lab/spawn_animal_matrix");
    public static final Identifier CLEAR_ADULT_MATRIX = ChaoCraft.id("visual_lab/clear_adult_matrix");

    private static final String MATRIX_TAG = "chaocraft_visual_lab_matrix";
    private static final long MATRIX_ACTION_COOLDOWN_TICKS = 10L;
    private static final long DRAFT_SPAWN_COOLDOWN_TICKS = 4L;

    // Development endpoints are still treated as hostile network input. Weak
    // player keys keep this defensive rate limiter bounded across disconnects.
    // All access happens from server.execute() on the server thread.
    private static final Map<ServerPlayerEntity, RateState> RATE_LIMITS = new WeakHashMap<>();
    private static final Deque<PendingMatrixSpawn> MATRIX_SPAWN_QUEUE = new ArrayDeque<>();
    private static final int MATRIX_SPAWNS_PER_TICK = 2;

    private ChaoVisualLabNetworking() {
    }

    public static void registerServerReceivers() {
        ServerTickEvents.END_SERVER_TICK.register(ChaoVisualLabNetworking::drainMatrixQueue);

        ServerPlayNetworking.registerGlobalReceiver(SPAWN_DRAFT_CHAO, (server, player, handler, buf, responseSender) -> {
            ChaoAppearanceState state = readState(buf);
            server.execute(() -> spawnDraftChao(player, state));
        });

        ServerPlayNetworking.registerGlobalReceiver(SPAWN_ADULT_MATRIX, (server, player, handler, buf, responseSender) ->
                server.execute(() -> spawnAdultMatrix(player)));

        ServerPlayNetworking.registerGlobalReceiver(SPAWN_ADULT_EXTREMES, (server, player, handler, buf, responseSender) ->
                server.execute(() -> spawnAdultExtremes(player)));

        ServerPlayNetworking.registerGlobalReceiver(SPAWN_CHILD_EXTREMES, (server, player, handler, buf, responseSender) ->
                server.execute(() -> spawnChildExtremes(player)));

        ServerPlayNetworking.registerGlobalReceiver(SPAWN_CHAOS_MATRIX, (server, player, handler, buf, responseSender) ->
                server.execute(() -> spawnChaosMatrix(player)));

        ServerPlayNetworking.registerGlobalReceiver(SPAWN_COLOR_MATRIX, (server, player, handler, buf, responseSender) -> {
            ChaoAppearanceState baseState = readState(buf);
            server.execute(() -> spawnColorMatrix(player, baseState));
        });

        ServerPlayNetworking.registerGlobalReceiver(SPAWN_REFLECTION_MATRIX, (server, player, handler, buf, responseSender) -> {
            ChaoAppearanceState baseState = readState(buf);
            server.execute(() -> spawnReflectionMatrix(player, baseState));
        });

        ServerPlayNetworking.registerGlobalReceiver(SPAWN_ANIMAL_MATRIX, (server, player, handler, buf, responseSender) -> {
            ChaoAppearanceState baseState = readState(buf);
            server.execute(() -> spawnAnimalMatrix(player, baseState));
        });

        ServerPlayNetworking.registerGlobalReceiver(CLEAR_ADULT_MATRIX, (server, player, handler, buf, responseSender) ->
                server.execute(() -> clearAdultMatrix(player)));
    }

    public static void writeState(PacketByteBuf buf, ChaoAppearanceState state) {
        buf.writeVarInt(state.type().ordinal());
        buf.writeFloat(state.age());
        buf.writeFloat(state.alignment());
        buf.writeFloat(state.swim());
        buf.writeFloat(state.fly());
        buf.writeFloat(state.run());
        buf.writeFloat(state.power());
        buf.writeVarInt(state.colorType().ordinal());
        buf.writeBoolean(state.monotone());
        buf.writeVarInt(state.reflectionType().ordinal());
        buf.writeVarInt(state.animalParts().arms().ordinal());
        buf.writeVarInt(state.animalParts().legs().ordinal());
        buf.writeVarInt(state.animalParts().tail().ordinal());
        buf.writeVarInt(state.animalParts().wings().ordinal());
        buf.writeVarInt(state.animalParts().face().ordinal());
        buf.writeVarInt(state.animalParts().horns().ordinal());
        buf.writeVarInt(state.animalParts().ears().ordinal());
        buf.writeVarInt(state.animalParts().forehead().ordinal());
        buf.writeVarInt(state.headDeco().ordinal());
        buf.writeBoolean(state.customEyes());
        buf.writeVarInt(state.eyes());
        buf.writeVarInt(state.eyelid());
        buf.writeVarInt(state.mouth());
        buf.writeBoolean(state.customMouth());
        buf.writeVarInt(state.mouthMid());
        buf.writeVarInt(state.mouthSide());
        buf.writeBoolean(state.customEmotionBall());
        buf.writeBoolean(state.neutralBall());
        buf.writeBoolean(state.heroBall());
        buf.writeBoolean(state.darkBall());
        buf.writeBoolean(state.tiltedHalo());
    }

    public static ChaoAppearanceState readState(PacketByteBuf buf) {
        return new ChaoAppearanceState(
                ChaoVisualType.fromOrdinal(buf.readVarInt()),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                ChaoColorType.fromOrdinal(buf.readVarInt()),
                buf.readBoolean(),
                ChaoReflectionType.fromOrdinal(buf.readVarInt()),
                new ChaoAnimalParts(
                        ChaoAnimalType.fromOrdinal(buf.readVarInt()),
                        ChaoAnimalType.fromOrdinal(buf.readVarInt()),
                        ChaoAnimalType.fromOrdinal(buf.readVarInt()),
                        ChaoAnimalType.fromOrdinal(buf.readVarInt()),
                        ChaoAnimalType.fromOrdinal(buf.readVarInt()),
                        ChaoAnimalType.fromOrdinal(buf.readVarInt()),
                        ChaoAnimalType.fromOrdinal(buf.readVarInt()),
                        ChaoAnimalType.fromOrdinal(buf.readVarInt())
                ),
                ChaoHeadDecoType.fromOrdinal(buf.readVarInt()),
                buf.readBoolean(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readBoolean(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean()
        );
    }

    private static void spawnDraftChao(ServerPlayerEntity player, ChaoAppearanceState state) {
        if (!canUseLab(player) || !allowDraftSpawn(player)) {
            return;
        }

        ServerWorld world = player.getServerWorld();
        Vec3d forward = horizontalUnit(player.getRotationVec(1.0F));
        Vec3d position = player.getPos().add(forward.multiply(2.75D));

        ChaoEntity chao = ModEntities.CHAO.create(world);
        if (chao == null) {
            return;
        }
        chao.refreshPositionAndAngles(position.x, position.y, position.z, player.getYaw() + 180.0F, 0.0F);
        chao.setAppearanceState(state);
        chao.setCustomName(Text.literal("Chao [VIS prototype]"));
        chao.setCustomNameVisible(false);
        world.spawnEntity(chao);
    }

    private static void spawnAdultMatrix(ServerPlayerEntity player) {
        if (!canUseLab(player) || !allowMatrixAction(player)) {
            return;
        }

        clearAdultMatrixInternal(player);

        ServerWorld world = player.getServerWorld();
        Vec3d forward = horizontalUnit(player.getRotationVec(1.0F));
        Vec3d right = new Vec3d(-forward.z, 0.0D, forward.x);
        Vec3d base = player.getPos().add(forward.multiply(5.5D));

        ChaoVisualType[] types = {
                ChaoVisualType.NORMAL,
                ChaoVisualType.SWIM,
                ChaoVisualType.FLY,
                ChaoVisualType.RUN,
                ChaoVisualType.POWER
        };
        float[] alignments = {0.0F, 100.0F, -100.0F};
        String[] alignmentCodes = {"N", "H", "D"};

        for (int row = 0; row < alignments.length; row++) {
            for (int column = 0; column < types.length; column++) {
                ChaoEntity chao = ModEntities.CHAO.create(world);
                if (chao == null) {
                    continue;
                }

                double horizontalOffset = (column - 2.0D) * 2.15D;
                double depthOffset = row * 2.65D;
                Vec3d position = base
                        .add(right.multiply(horizontalOffset))
                        .add(forward.multiply(depthOffset));

                ChaoVisualType type = types[column];
                ChaoAppearanceState state = ChaoAppearanceState.DEFAULT
                        .withType(type)
                        .withAge(1.0F)
                        .withAlignment(alignments[row]);

                chao.refreshPositionAndAngles(
                        position.x,
                        position.y,
                        position.z,
                        player.getYaw() + 180.0F,
                        0.0F
                );
                chao.setAppearanceState(state);
                chao.addCommandTag(MATRIX_TAG);
                chao.setCustomName(Text.literal(alignmentCodes[row] + shortType(type)));
                chao.setCustomNameVisible(false);
                world.spawnEntity(chao);
            }
        }
    }


    private static void spawnChaosMatrix(ServerPlayerEntity player) {
        if (!canUseLab(player) || !allowMatrixAction(player)) {
            return;
        }
        clearAdultMatrixInternal(player);

        ServerWorld world = player.getServerWorld();
        Vec3d forward = horizontalUnit(player.getRotationVec(1.0F));
        Vec3d right = new Vec3d(-forward.z, 0.0D, forward.x);
        Vec3d base = player.getPos().add(forward.multiply(5.5D));
        float[] alignments = {0.0F, 100.0F, -100.0F};
        String[] names = {"Neutral Chaos", "Hero Chaos", "Dark Chaos"};

        for (int i = 0; i < alignments.length; i++) {
            ChaoAppearanceState state = ChaoAppearanceState.DEFAULT
                    .withType(ChaoVisualType.CHAOS)
                    .withAge(1.0F)
                    .withAlignment(alignments[i]);
            Vec3d position = base.add(right.multiply((i - 1.0D) * 2.5D));
            spawnMatrixChao(world, player, position, state, names[i]);
        }
    }

    /**
     * Compact audit grid for the real SA2 Color + monotone save fields:
     * fourteen colors in Two-Tone and the same fourteen in Monotone.
     */
    private static void spawnColorMatrix(ServerPlayerEntity player, ChaoAppearanceState baseState) {
        if (!canUseLab(player) || !allowMatrixAction(player)) {
            return;
        }
        clearAdultMatrixInternal(player);

        ServerWorld world = player.getServerWorld();
        Vec3d forward = horizontalUnit(player.getRotationVec(1.0F));
        Vec3d right = new Vec3d(-forward.z, 0.0D, forward.x);
        Vec3d base = player.getPos().add(forward.multiply(5.5D));
        ChaoColorType[] colors = ChaoColorType.values();

        // Seven columns x four rows keeps all 28 cases comfortably visible.
        for (int tone = 0; tone < 2; tone++) {
            for (int colorIndex = 0; colorIndex < colors.length; colorIndex++) {
                int localRow = colorIndex / 7;
                int column = colorIndex % 7;
                int row = tone * 2 + localRow;
                ChaoColorType color = colors[colorIndex];
                // Preserve the Visual Lab draft's family/alignment/evolution so the
                // same 28-case audit can be reused for Child, every adult family,
                // and Chaos instead of hard-coding Neutral Normal. Face/emotion
                // settings are preserved as well because this is a visual audit tool.
                ChaoAppearanceState state = baseState
                        .withHeadDeco(ChaoHeadDecoType.NONE)
                        .withColorType(color)
                        .withMonotone(tone == 1);
                Vec3d position = base
                        .add(right.multiply((column - 3.0D) * 2.15D))
                        .add(forward.multiply(row * 2.5D));
                spawnMatrixChao(world, player, position, state,
                        (tone == 1 ? "M-" : "2T-") + color.name());
            }
        }
    }

    private static void spawnReflectionMatrix(ServerPlayerEntity player, ChaoAppearanceState baseState) {
        if (!canUseLab(player) || !allowMatrixAction(player)) return;
        clearAdultMatrixInternal(player);
        ServerWorld world = player.getServerWorld();
        Vec3d forward = horizontalUnit(player.getRotationVec(1.0F));
        Vec3d right = new Vec3d(-forward.z, 0.0D, forward.x);
        Vec3d base = player.getPos().add(forward.multiply(5.5D));
        ChaoReflectionType[] values = ChaoReflectionType.values();
        for (int i = 0; i < values.length; i++) {
            int col = i % 6, row = i / 6;
            ChaoAppearanceState state = baseState
                    .withHeadDeco(ChaoHeadDecoType.NONE)
                    .withReflectionType(values[i]);
            Vec3d position = base.add(right.multiply((col - 2.5D) * 2.15D)).add(forward.multiply(row * 2.5D));
            spawnMatrixChao(world, player, position, state, "REF-" + values[i].name());
        }
    }

    private static void spawnAnimalMatrix(ServerPlayerEntity player, ChaoAppearanceState baseState) {
        if (!canUseLab(player) || !allowMatrixAction(player)) return;
        clearAdultMatrixInternal(player);
        ServerWorld world = player.getServerWorld();
        Vec3d forward = horizontalUnit(player.getRotationVec(1.0F));
        Vec3d right = new Vec3d(-forward.z, 0.0D, forward.x);
        Vec3d base = player.getPos().add(forward.multiply(5.5D));
        ChaoAnimalType[] values = ChaoAnimalType.values();
        int n = 0;
        for (ChaoAnimalType animal : values) {
            if (animal == ChaoAnimalType.NONE) continue;
            int col = n % 7, row = n / 7; n++;
            ChaoAnimalParts all = new ChaoAnimalParts(animal, animal, animal, animal, animal, animal, animal, animal);
            // Animal Matrix isolates Animal Parts. Carrying the preview's HeadDeco
            // duplicated an unrelated attachment across every matrix entity and
            // obscured both visual QA and GPU-cache stress results.
            ChaoAppearanceState state = baseState
                    .withHeadDeco(ChaoHeadDecoType.NONE)
                    .withAnimalParts(all);
            Vec3d position = base.add(right.multiply((col - 3.0D) * 2.15D)).add(forward.multiply(row * 2.5D));
            spawnMatrixChao(world, player, position, state, "ANI-" + animal.name());
        }
    }

    private static void spawnAdultExtremes(ServerPlayerEntity player) {
        if (!canUseLab(player) || !allowMatrixAction(player)) {
            return;
        }
        clearAdultMatrixInternal(player);

        ServerWorld world = player.getServerWorld();
        Vec3d forward = horizontalUnit(player.getRotationVec(1.0F));
        Vec3d right = new Vec3d(-forward.z, 0.0D, forward.x);
        Vec3d base = player.getPos().add(forward.multiply(5.5D));

        ChaoVisualType[] types = {
                ChaoVisualType.NORMAL, ChaoVisualType.SWIM, ChaoVisualType.FLY,
                ChaoVisualType.RUN, ChaoVisualType.POWER
        };
        float[] alignments = {0.0F, 100.0F, -100.0F};
        String[] alignmentCodes = {"N", "H", "D"};

        // Five canonical second-evolution endpoints: Normal remainder, then each
        // evolution channel at 100. Continuous intermediate states are covered by
        // the sliders, while these 75 cases expose every adult family endpoint.
        for (int alignmentIndex = 0; alignmentIndex < alignments.length; alignmentIndex++) {
            for (int evolutionIndex = 0; evolutionIndex < 5; evolutionIndex++) {
                int row = alignmentIndex * 5 + evolutionIndex;
                for (int typeIndex = 0; typeIndex < types.length; typeIndex++) {
                    ChaoVisualType type = types[typeIndex];
                    ChaoAppearanceState state = ChaoAppearanceState.DEFAULT
                            .withType(type)
                            .withAge(1.0F)
                            .withAlignment(alignments[alignmentIndex]);
                    state = applyEvolutionExtreme(state, evolutionIndex);

                    Vec3d position = base
                            .add(right.multiply((typeIndex - 2.0D) * 2.15D))
                            .add(forward.multiply(row * 2.35D));
                    spawnMatrixChao(world, player, position, state,
                            alignmentCodes[alignmentIndex] + shortType(type) + "-" + evolutionCode(evolutionIndex));
                }
            }
        }
    }

    private static void spawnChildExtremes(ServerPlayerEntity player) {
        if (!canUseLab(player) || !allowMatrixAction(player)) {
            return;
        }
        clearAdultMatrixInternal(player);

        ServerWorld world = player.getServerWorld();
        Vec3d forward = horizontalUnit(player.getRotationVec(1.0F));
        Vec3d right = new Vec3d(-forward.z, 0.0D, forward.x);
        Vec3d base = player.getPos().add(forward.multiply(5.5D));
        float[] alignments = {0.0F, 100.0F, -100.0F};
        String[] alignmentCodes = {"N", "H", "D"};

        for (int alignmentIndex = 0; alignmentIndex < alignments.length; alignmentIndex++) {
            for (int evolutionIndex = 0; evolutionIndex < 5; evolutionIndex++) {
                ChaoAppearanceState state = ChaoAppearanceState.DEFAULT
                        .withType(ChaoVisualType.CHILD)
                        .withAge(1.0F)
                        .withAlignment(alignments[alignmentIndex]);
                state = applyEvolutionExtreme(state, evolutionIndex);

                Vec3d position = base
                        .add(right.multiply((evolutionIndex - 2.0D) * 2.15D))
                        .add(forward.multiply(alignmentIndex * 2.65D));
                spawnMatrixChao(world, player, position, state,
                        "C" + alignmentCodes[alignmentIndex] + "-" + evolutionCode(evolutionIndex));
            }
        }
    }

    private static ChaoAppearanceState applyEvolutionExtreme(ChaoAppearanceState state, int evolutionIndex) {
        return switch (evolutionIndex) {
            case 1 -> state.withEvolution(ChaoAppearanceState.EvolutionChannel.SWIM, 100.0F);
            case 2 -> state.withEvolution(ChaoAppearanceState.EvolutionChannel.FLY, 100.0F);
            case 3 -> state.withEvolution(ChaoAppearanceState.EvolutionChannel.RUN, 100.0F);
            case 4 -> state.withEvolution(ChaoAppearanceState.EvolutionChannel.POWER, 100.0F);
            default -> state;
        };
    }

    private static String evolutionCode(int evolutionIndex) {
        return switch (evolutionIndex) {
            case 1 -> "S";
            case 2 -> "F";
            case 3 -> "R";
            case 4 -> "P";
            default -> "N";
        };
    }

    private static void spawnMatrixChao(ServerWorld world, ServerPlayerEntity player, Vec3d position,
            ChaoAppearanceState state, String name) {
        // Matrix tools are stress/QA helpers, not gameplay. Stagger their creation
        // so 34/75 entities do not all enter tracking and trigger client VBO work
        // in the same server tick. Final matrix contents are unchanged.
        MATRIX_SPAWN_QUEUE.addLast(new PendingMatrixSpawn(
                player.getUuid(), world, position, state, name, player.getYaw() + 180.0F));
    }

    private static void drainMatrixQueue(net.minecraft.server.MinecraftServer server) {
        for (int i = 0; i < MATRIX_SPAWNS_PER_TICK && !MATRIX_SPAWN_QUEUE.isEmpty(); i++) {
            PendingMatrixSpawn pending = MATRIX_SPAWN_QUEUE.removeFirst();
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(pending.playerId());
            if (player == null || player.getServerWorld() != pending.world()) continue;
            ChaoEntity chao = ModEntities.CHAO.create(pending.world());
            if (chao == null) continue;
            Vec3d position = pending.position();
            chao.refreshPositionAndAngles(position.x, position.y, position.z, pending.yaw(), 0.0F);
            chao.setAppearanceState(pending.state());
            chao.addCommandTag(MATRIX_TAG);
            chao.setCustomName(Text.literal(pending.name()));
            chao.setCustomNameVisible(false);
            pending.world().spawnEntity(chao);
        }
    }

    public static int pendingMatrixSpawns() {
        return MATRIX_SPAWN_QUEUE.size();
    }

    private static void clearAdultMatrix(ServerPlayerEntity player) {
        if (!canUseLab(player) || !allowMatrixAction(player)) {
            return;
        }

        clearAdultMatrixInternal(player);
    }

    private static void clearAdultMatrixInternal(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        MATRIX_SPAWN_QUEUE.removeIf(pending -> pending.playerId().equals(playerId));

        Box searchBox = player.getBoundingBox().expand(64.0D, 32.0D, 64.0D);
        List<ChaoEntity> matrixChaos = player.getServerWorld().getEntitiesByClass(
                ChaoEntity.class,
                searchBox,
                chao -> chao.getCommandTags().contains(MATRIX_TAG)
        );
        matrixChaos.forEach(ChaoEntity::discard);
    }


    private static boolean allowDraftSpawn(ServerPlayerEntity player) {
        long tick = player.getServerWorld().getTime();
        RateState rate = RATE_LIMITS.computeIfAbsent(player, ignored -> new RateState());
        if (rate.lastDraftSpawnTick != Long.MIN_VALUE && tick >= rate.lastDraftSpawnTick
                && tick - rate.lastDraftSpawnTick < DRAFT_SPAWN_COOLDOWN_TICKS) {
            return false;
        }
        rate.lastDraftSpawnTick = tick;
        return true;
    }

    private static boolean allowMatrixAction(ServerPlayerEntity player) {
        long tick = player.getServerWorld().getTime();
        RateState rate = RATE_LIMITS.computeIfAbsent(player, ignored -> new RateState());
        if (rate.lastMatrixTick != Long.MIN_VALUE && tick >= rate.lastMatrixTick
                && tick - rate.lastMatrixTick < MATRIX_ACTION_COOLDOWN_TICKS) {
            return false;
        }
        rate.lastMatrixTick = tick;
        return true;
    }

    private static boolean canUseLab(ServerPlayerEntity player) {
        // This is a development tool. Keep remote survival players from editing
        // arbitrary Chao while still making it convenient in creative test worlds.
        return player.hasPermissionLevel(2) || player.isCreative();
    }

    private static Vec3d horizontalUnit(Vec3d vector) {
        Vec3d horizontal = new Vec3d(vector.x, 0.0D, vector.z);
        return horizontal.lengthSquared() < 1.0E-6D ? new Vec3d(0.0D, 0.0D, 1.0D) : horizontal.normalize();
    }

    private static String shortType(ChaoVisualType type) {
        return switch (type) {
            case NORMAL -> "N";
            case SWIM -> "S";
            case FLY -> "F";
            case RUN -> "R";
            case POWER -> "P";
            case CHAOS -> "C";
            case CHILD -> "C";
        };
    }

    private record PendingMatrixSpawn(UUID playerId, ServerWorld world, Vec3d position,
            ChaoAppearanceState state, String name, float yaw) {
    }

    private static final class RateState {
        private long lastDraftSpawnTick = Long.MIN_VALUE;
        private long lastMatrixTick = Long.MIN_VALUE;
    }
}
