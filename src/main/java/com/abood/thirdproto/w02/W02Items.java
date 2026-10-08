package com.abood.thirdproto.w02;

import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.component.PiercingWeapon;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class W02Items {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("third_proto");

    public static final DeferredItem<ProtoSpearItem> PROTO_SPEAR = ITEMS.registerItem(
            "proto_spear",
            ProtoSpearItem::new,
            props -> props.durability(250)
                    .attributes(TridentItem.createAttributes())
                    .component(DataComponents.PIERCING_WEAPON, new PiercingWeapon(
                            true, false,
                            Optional.of(SoundEvents.SPEAR_ATTACK),
                            Optional.of(SoundEvents.SPEAR_HIT))));

    private W02Items() {}

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}