package com.abood.third_proto.w04;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * W0-4: registers the "tunnel vision" effect. It has no behaviour of its own —
 * having it on a player is the on/off switch the client reads to draw the tunnel.
 * Vanilla already syncs effects to the affected player, so no custom packet is needed.
 */
public final class W04Registry {
    public static final String MODID = "third_proto";

    private static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, MODID);

    public static final DeferredHolder<MobEffect, MobEffect> TUNNEL_VISION =
            MOB_EFFECTS.register("tunnel_vision", () -> new MobEffect(MobEffectCategory.HARMFUL, 0x000000) {});

    private W04Registry() {}

    /** Call once from the main mod class constructor. */
    public static void register(IEventBus modEventBus) {
        MOB_EFFECTS.register(modEventBus);
    }
}
