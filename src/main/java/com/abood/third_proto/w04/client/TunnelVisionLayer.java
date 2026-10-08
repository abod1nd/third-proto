package com.abood.third_proto.w04.client;

import com.abood.third_proto.w04.W04Registry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.client.gui.GuiLayer;

/** Decides whether the tunnel is on, how strong it is and where the hole is, then hands drawing to a renderer. */
public final class TunnelVisionLayer implements GuiLayer {
    // true = cinematic shader (options 3 + 4), false = the plain GUI mask from step 1
    private static final boolean USE_SHADER = true;

    // Keyhole sizing: the hole follows the target's size, with a small floor so a far target stays a small spot
    private static final TunnelHole.Sizing SIZING = new TunnelHole.Sizing(0.05F, 0.45F, 0.8F);
    private static final TunnelHole.OffscreenMode OFFSCREEN_MODE = TunnelHole.OffscreenMode.EDGE_HINT;
    private static final float FADE_IN_SECONDS = 0.8F;
    private static final float FADE_OUT_SECONDS = 0.5F;

    private final TunnelRenderer shader = new ShaderTunnelRenderer();
    private final TunnelRenderer mask = new MaskTunnelRenderer();
    private float intensity;

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        boolean active = mc.player != null && mc.player.hasEffect(W04Registry.TUNNEL_VISION);
        updateIntensity(active, deltaTracker.getRealtimeDeltaTicks() / 20.0F);
        if (intensity <= 0.0F) {
            TunnelTarget.clear();
            return;
        }
        if (mc.player == null || mc.level == null || !FrameCamera.isCaptured()) {
            return;
        }

        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        Mob target = TunnelTarget.resolve(mc.level, mc.player);
        TunnelHole hole = target == null
                ? TunnelHole.none(width, height)
                : TunnelHole.compute(target, deltaTracker.getGameTimeDeltaPartialTick(true), width, height, SIZING, OFFSCREEN_MODE);
        float timeSeconds = (Util.getMillis() % 3_600_000L) / 1000.0F;
        TunnelFrame frame = new TunnelFrame(hole, intensity, timeSeconds, width, height);

        if (!USE_SHADER || !shader.render(graphics, frame)) {
            mask.render(graphics, frame);
        }
    }

    private void updateIntensity(boolean active, float deltaSeconds) {
        float step = deltaSeconds / (active ? FADE_IN_SECONDS : FADE_OUT_SECONDS);
        intensity = Mth.clamp(intensity + (active ? step : -step), 0.0F, 1.0F);
    }
}
