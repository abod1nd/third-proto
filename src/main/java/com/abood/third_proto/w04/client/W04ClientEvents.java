package com.abood.third_proto.w04.client;

import com.abood.third_proto.w04.W04Registry;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.FrameGraphSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/** Client wiring. FML routes each method to the mod bus or the game bus from its event type. */
@EventBusSubscriber(modid = W04Registry.MODID, value = Dist.CLIENT)
public final class W04ClientEvents {
    private W04ClientEvents() {}

    // Above vanilla camera overlays (vignette, pumpkin), below crosshair, hotbar and chat.
    // Not tied to hideGui, so F1 does not remove it.
    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CAMERA_OVERLAYS,
                Identifier.fromNamespaceAndPath(W04Registry.MODID, "tunnel_vision"),
                new TunnelVisionLayer());
    }

    // Fires once per frame with the camera and matrices the level is about to be rendered with
    @SubscribeEvent
    public static void captureCamera(FrameGraphSetupEvent event) {
        FrameCamera.capture(event.getCamera(), event.getModelViewMatrix(), event.getProjectionMatrix());
    }
}
