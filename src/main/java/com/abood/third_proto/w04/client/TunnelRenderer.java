package com.abood.third_proto.w04.client;

import net.minecraft.client.gui.GuiGraphics;

/** One way of drawing the tunnel. Implementations: cinematic shader (step 2) and plain GUI mask (step 1). */
interface TunnelRenderer {
    /** Draws this frame's effect. Returns false if it could not draw, so the caller can fall back. */
    boolean render(GuiGraphics graphics, TunnelFrame frame);
}
