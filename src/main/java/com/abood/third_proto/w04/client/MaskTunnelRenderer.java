package com.abood.third_proto.w04.client;

import net.minecraft.client.gui.GuiGraphics;

/** Step 1 look: black GUI mask with a soft round hole. Also the fallback if the shader fails to compile. */
final class MaskTunnelRenderer implements TunnelRenderer {
    private static final float FEATHER_RATIO = 0.5F; // soft edge width relative to the hole radius
    private static final int MASK_ALPHA = 245;       // 255 = pitch black

    @Override
    public boolean render(GuiGraphics graphics, TunnelFrame frame) {
        TunnelHole hole = frame.hole();
        int alpha = Math.round(MASK_ALPHA * frame.intensity());
        graphics.submitGuiElementRenderState(TunnelMaskRenderState.create(
                hole.x(), hole.y(), hole.radius(), FEATHER_RATIO, alpha, frame.guiWidth(), frame.guiHeight()));
        return true;
    }
}
