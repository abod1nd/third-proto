package com.abood.thirdproto.w03; // عدّلها لتطابق حزمة w02 عندك + .w03

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.util.Optional;

import static com.abood.thirdproto.w03.IslandConstants.*; // عدّل الحزمة هون كمان

/** W0-3: وضع القطع مرة وحدة + تثبيت الـ world spawn بالجزيرة. */
public final class IslandPlacer {
    private static final Logger LOG = LogUtils.getLogger();

    private IslandPlacer() {}

    /** بتنادى عند ServerStartedEvent. بتحط القطع إذا ما انحطت قبل. */
    public static void placeOnce(MinecraftServer server) {

        ServerLevel island = server.getLevel(ISLAND);
        if (island == null) {
            LOG.error("[W0-3] island dimension NOT FOUND — check data/third_proto/dimension/island.json");
            return;
        }
        if (island.getBlockState(PLACED_MARKER).is(Blocks.BEDROCK)) {
            LOG.info("[W0-3] already placed — skipping");
            return;
        }

        long start = System.nanoTime();
        boolean okA = placePiece(island, PIECE_A, PIECE_A_ORIGIN);
        boolean okB = placePiece(island, PIECE_B, PIECE_B_ORIGIN);
        boolean okC = placePiece(island, PIECE_C, PIECE_C_ORIGIN);
        LOG.info("[W0-3] total placement: {} ms", (System.nanoTime() - start) / 1_000_000);

        if (okA && okB &&okC) {
            island.setBlock(PLACED_MARKER, Blocks.BEDROCK.defaultBlockState(), Block.UPDATE_ALL);
            setWorldSpawn(server, island);
        }
    }

    private static boolean placePiece(ServerLevel level, Identifier id, BlockPos origin) {
        Optional<StructureTemplate> template = level.getStructureManager().get(id);
        if (template.isEmpty()) {
            LOG.error("[W0-3] template NOT FOUND: {}", id);
            return false;
        }
        long start = System.nanoTime();
        boolean ok = template.get().placeInWorld(
                level, origin, origin, new StructurePlaceSettings(), level.getRandom(), Block.UPDATE_CLIENTS);
        LOG.info("[W0-3] placed {} at {} size {} ok={} in {} ms",
                id, origin, template.get().getSize(), ok, (System.nanoTime() - start) / 1_000_000);
        return ok;
    }

    /** الانتقال الإجباري بالطريق الفانيلا (1.21.9+): world spawn جوّا البُعد. */
    private static void setWorldSpawn(MinecraftServer server, ServerLevel island) {
        island.getGameRules().set(GameRules.RESPAWN_RADIUS, 0, server);
        server.setRespawnData(LevelData.RespawnData.of(ISLAND, SPAWN, 0f, 0f));
        LOG.info("[W0-3] world spawn set to {} {}", ISLAND.identifier(), SPAWN);
    }

    /** نقطة الـ spawn كـ TeleportTransition — للحراس بس. */
    public static TeleportTransition spawnTransition(MinecraftServer server) {
        ServerLevel island = server.getLevel(ISLAND);
        if (island == null) return null;
        return new TeleportTransition(island, Vec3.atBottomCenterOf(SPAWN), Vec3.ZERO, 0f, 0f,
                TeleportTransition.DO_NOTHING);
    }
}
