package com.abood.third_proto.w04.client;

import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

/** Snapshot of the exact camera matrices the current frame is rendered with (bobbing and FOV changes included). */
public final class FrameCamera {
    private static final Matrix4f VIEW = new Matrix4f();
    private static final Matrix4f VIEW_PROJECTION = new Matrix4f();
    private static Vec3 cameraPos = Vec3.ZERO;
    private static boolean captured;

    private FrameCamera() {}

    /** Called once per frame from FrameGraphSetupEvent. The event's own matrices are only read, never modified. */
    public static void capture(Camera camera, Matrix4fc modelView, Matrix4fc projection) {
        cameraPos = camera.position();
        VIEW.set(modelView);
        projection.mul(modelView, VIEW_PROJECTION);
        captured = true;
    }

    public static boolean isCaptured() {
        return captured;
    }

    /** Projects a world position. The NDC values are only meaningful when {@code inFront} is true. */
    public static Projection project(Vec3 worldPos) {
        Vector4f relative = new Vector4f(
                (float) (worldPos.x - cameraPos.x),
                (float) (worldPos.y - cameraPos.y),
                (float) (worldPos.z - cameraPos.z),
                1.0F);
        Vector4f view = VIEW.transform(new Vector4f(relative));
        Vector4f clip = VIEW_PROJECTION.transform(relative);
        boolean inFront = clip.w > 1.0E-4F; // w <= 0 means behind the camera: dividing by it mirrors the point
        return new Projection(clip.x / clip.w, clip.y / clip.w, inFront, view.x, view.y);
    }

    public record Projection(float ndcX, float ndcY, boolean inFront, float viewX, float viewY) {
        public boolean onScreen() {
            return inFront && Math.abs(ndcX) <= 1.0F && Math.abs(ndcY) <= 1.0F;
        }
    }
}
