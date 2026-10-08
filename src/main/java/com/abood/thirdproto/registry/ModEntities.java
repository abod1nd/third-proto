package com.abood.thirdproto.registry;

import com.abood.thirdproto.ThirdPrototypes;
import com.abood.thirdproto.entity.EchoTestEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {

    private ModEntities() {
    }

    private static final DeferredRegister.Entities ENTITY_TYPES =
            DeferredRegister.createEntities(ThirdPrototypes.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<EchoTestEntity>> ECHO_TEST =
            ENTITY_TYPES.registerEntityType(
                    "echo_test",
                    EchoTestEntity::new,
                    MobCategory.MONSTER,
                    builder -> builder
                            .sized(0.6F, 1.8F)
                            .eyeHeight(1.62F)
                            .clientTrackingRange(8)
            );

    public static void register(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(ModEntities::onAttributeCreation);
    }

    private static void onAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ECHO_TEST.get(), EchoTestEntity.createAttributes().build());
    }
}
