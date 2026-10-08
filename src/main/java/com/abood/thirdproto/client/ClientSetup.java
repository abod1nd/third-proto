package com.abood.thirdproto.client;

import com.abood.thirdproto.ThirdPrototypes;
import com.abood.thirdproto.client.renderer.EchoTestRenderer;
import com.abood.thirdproto.registry.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = ThirdPrototypes.MODID, value = Dist.CLIENT)
public final class ClientSetup {

    private ClientSetup() {
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.ECHO_TEST.get(), EchoTestRenderer::new);
    }
}