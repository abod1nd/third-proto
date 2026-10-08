package com.abood.third_proto.w04.client;

import java.util.Comparator;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

/** Stand-in for Echo: locks onto the nearest mob and keeps it until it dies or unloads. */
final class TunnelTarget {
    private static final double SEARCH_RADIUS = 64.0;
    private static int targetId = -1;

    private TunnelTarget() {}

    static Mob resolve(ClientLevel level, LocalPlayer player) {
        Entity current = targetId >= 0 ? level.getEntity(targetId) : null;
        if (current instanceof Mob mob && mob.isAlive()) {
            return mob;
        }
        Mob nearest = findNearest(level, player);
        targetId = nearest != null ? nearest.getId() : -1;
        return nearest;
    }

    static void clear() {
        targetId = -1;
    }

    private static Mob findNearest(ClientLevel level, LocalPlayer player) {
        return level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(SEARCH_RADIUS), Mob::isAlive)
                .stream()
                .min(Comparator.comparingDouble(mob -> mob.distanceToSqr(player)))
                .orElse(null);
    }
}
