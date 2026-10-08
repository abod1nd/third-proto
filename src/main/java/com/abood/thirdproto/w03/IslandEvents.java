package com.abood.thirdproto.w03; // عدّلها لتطابق حزمة w02 عندك + .w03

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerRespawnPositionEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;

/**
 * W0-3: ربط الأحداث بس. الحراس التلاتة شبكة أمان + أداة قياس:
 * اللوغ بيقول إذا الفانيلا عملت الشغل لحالها ولا الحارس تدخّل.
 */
@EventBusSubscriber(modid = IslandConstants.MOD_ID)
public final class IslandEvents {
    private static final Logger LOG = LogUtils.getLogger();

    private IslandEvents() {}

    private static boolean isIsland(ResourceKey<Level> dim) {
        return dim.equals(IslandConstants.ISLAND);
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        IslandPlacer.placeOnce(event.getServer());
    }

    /** T4: أول دخول. */
    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (isIsland(player.level().dimension())) {
            LOG.info("[W0-3] login: {} already in island at {} (vanilla did it)",
                    player.getName().getString(), player.blockPosition());
            return;
        }
        LOG.warn("[W0-3] login: {} in {} -> GUARD teleporting to island",
                player.getName().getString(), player.level().dimension().identifier());
        TeleportTransition target = IslandPlacer.spawnTransition(player.level().getServer());
        if (target != null) player.teleport(target);
    }

    /** T5: الإحياء بعد الموت. */
    @SubscribeEvent
    public static void onRespawnPosition(PlayerRespawnPositionEvent event) {
        TeleportTransition target = event.getTeleportTransition();
        if (isIsland(target.newLevel().dimension())) {
            LOG.info("[W0-3] respawn: vanilla target already island at {}", target.position());
            return;
        }
        LOG.warn("[W0-3] respawn: vanilla target {} -> GUARD redirecting to island",
                target.newLevel().dimension().identifier());
        TeleportTransition redirect = IslandPlacer.spawnTransition(target.newLevel().getServer());
        if (redirect != null) event.setTeleportTransition(redirect);
    }

    /** T6: أي سفر للاعب لبُعد غير الجزيرة بينمنع. */
    @SubscribeEvent
    public static void onTravel(EntityTravelToDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (isIsland(event.getDimension())) return;
        event.setCanceled(true);
        LOG.warn("[W0-3] travel BLOCKED: {} -> {}",
                player.getName().getString(), event.getDimension().identifier());
    }
}
