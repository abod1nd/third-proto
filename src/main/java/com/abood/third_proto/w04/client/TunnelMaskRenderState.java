package com.abood.third_proto.w04.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.renderer.RenderPipelines;

/** Full-screen black mask with a soft round hole, built as two rings of quads. */
record TunnelMaskRenderState(
        float centerX,
        float centerY,
        float holeRadius,
        float featherWidth,
        float outerRadius,
        int darkColor,
        ScreenRectangle bounds) implements GuiElementRenderState {

    private static final int SEGMENTS = 64;
    private static final int CLEAR = 0x00000000;

    static TunnelMaskRenderState create(float centerX, float centerY, float holeRadius, float featherRatio,
                                        int maskAlpha, int guiWidth, int guiHeight) {
        float outerRadius = (guiWidth + guiHeight) * 2.0F; // always past every screen corner
        int darkColor = maskAlpha << 24;                    // black, alpha in the top byte
        // bounds must not be null, otherwise GuiRenderState silently drops the element
        return new TunnelMaskRenderState(centerX, centerY, holeRadius, holeRadius * featherRatio, outerRadius,
                darkColor, new ScreenRectangle(0, 0, guiWidth, guiHeight));
    }

    @Override
    public void buildVertices(VertexConsumer consumer) {
        float featherEnd = holeRadius + featherWidth;
        addRing(consumer, holeRadius, featherEnd, CLEAR, darkColor);       // soft edge
        addRing(consumer, featherEnd, outerRadius, darkColor, darkColor);   // solid darkness
    }

    // Vertex order (inner a0, inner a1, outer a1, outer a0) has the same winding as vanilla GUI rectangles.
    // RenderPipelines.GUI culls back faces, so the reversed order would draw nothing.
    private void addRing(VertexConsumer consumer, float innerR, float outerR, int innerColor, int outerColor) {
        for (int i = 0; i < SEGMENTS; i++) {
            double a0 = Math.PI * 2.0 * i / SEGMENTS;
            double a1 = Math.PI * 2.0 * (i + 1) / SEGMENTS;
            float cos0 = (float) Math.cos(a0);
            float sin0 = (float) Math.sin(a0);
            float cos1 = (float) Math.cos(a1);
            float sin1 = (float) Math.sin(a1);
            consumer.addVertex(centerX + innerR * cos0, centerY + innerR * sin0, 0.0F).setColor(innerColor);
            consumer.addVertex(centerX + innerR * cos1, centerY + innerR * sin1, 0.0F).setColor(innerColor);
            consumer.addVertex(centerX + outerR * cos1, centerY + outerR * sin1, 0.0F).setColor(outerColor);
            consumer.addVertex(centerX + outerR * cos0, centerY + outerR * sin0, 0.0F).setColor(outerColor);
        }
    }

    @Override
    public RenderPipeline pipeline() {
        return RenderPipelines.GUI; // position + color, translucent blending, no depth test
    }

    @Override
    public TextureSetup textureSetup() {
        return TextureSetup.noTexture();
    }

    @Override
    public ScreenRectangle scissorArea() {
        return null;
    }
}
