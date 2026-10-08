package com.abood.third_proto.w04.client;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/** Where the clear hole sits on screen this frame, in GUI units. Radius 0 means no hole at all. */
record TunnelHole(float x, float y, float radius) {

    enum OffscreenMode { EDGE_HINT, DARK }

    /** All values are fractions of the screen height. */
    record Sizing(float minRadius, float maxRadius, float heightToRadius) {}

    static TunnelHole none(int width, int height) {
        return new TunnelHole(width * 0.5F, height * 0.5F, 0.0F);
    }

    static TunnelHole compute(Mob target, float partialTick, int width, int height, Sizing sizing, OffscreenMode mode) {
        Vec3 feet = target.getPosition(partialTick);
        Vec3 head = feet.add(0.0, target.getBbHeight(), 0.0);
        Vec3 middle = feet.add(0.0, target.getBbHeight() * 0.5, 0.0);
        FrameCamera.Projection center = FrameCamera.project(middle);
        float minRadius = sizing.minRadius() * height;

        if (center.onScreen()) {
            return new TunnelHole(toGuiX(center.ndcX(), width), toGuiY(center.ndcY(), height),
                    radiusFromHeight(feet, head, minRadius, sizing, height));
        }
        if (mode == OffscreenMode.DARK) {
            return none(width, height);
        }
        if (center.inFront()) {
            // In front but outside the frame: slide the hole along the nearest edge
            float x = Mth.clamp(toGuiX(center.ndcX(), width), 0.0F, width);
            float y = Mth.clamp(toGuiY(center.ndcY(), height), 0.0F, height);
            return new TunnelHole(x, y, minRadius);
        }
        // Behind the camera: put the hole on the edge the player has to turn toward
        return edgeHint(center.viewX(), -center.viewY(), minRadius, width, height);
    }

    private static float radiusFromHeight(Vec3 feet, Vec3 head, float minRadius, Sizing sizing, int height) {
        FrameCamera.Projection bottom = FrameCamera.project(feet);
        FrameCamera.Projection top = FrameCamera.project(head);
        if (!bottom.inFront() || !top.inFront()) {
            return minRadius;
        }
        float projectedHeight = Math.abs(toGuiY(top.ndcY(), height) - toGuiY(bottom.ndcY(), height));
        return Mth.clamp(projectedHeight * sizing.heightToRadius(), minRadius, sizing.maxRadius() * height);
    }

    private static TunnelHole edgeHint(float dirX, float dirY, float radius, int width, int height) {
        float length = Mth.sqrt(dirX * dirX + dirY * dirY);
        if (length < 1.0E-4F) {
            return none(width, height); // exactly behind: no side to point at
        }
        float halfW = width * 0.5F;
        float halfH = height * 0.5F;
        float dx = dirX / length;
        float dy = dirY / length;
        float toEdgeX = Math.abs(dx) > 1.0E-4F ? halfW / Math.abs(dx) : Float.MAX_VALUE;
        float toEdgeY = Math.abs(dy) > 1.0E-4F ? halfH / Math.abs(dy) : Float.MAX_VALUE;
        float scale = Math.min(toEdgeX, toEdgeY);
        return new TunnelHole(halfW + dx * scale, halfH + dy * scale, radius);
    }

    private static float toGuiX(float ndcX, int width) {
        return (ndcX * 0.5F + 0.5F) * width;
    }

    private static float toGuiY(float ndcY, int height) {
        return (0.5F - ndcY * 0.5F) * height; // NDC y points up, GUI y points down
    }
}
