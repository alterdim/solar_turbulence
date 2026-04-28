package li.gerard.solarturbulence.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import li.gerard.solarturbulence.generic.RayEmittingBlockEntity;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class RayRenderer {

    private static final float BEAM_RADIUS    = 0.12f;
    private static final float POINTER_RADIUS = 0.018f;
    private static final int   CYLINDER_SIDES = 12;
    private static final float PULSE_SPEED    = 0.05f;
    private static final float PULSE_MIN_ALPHA = 0.5f;
    private static final float PULSE_MAX_ALPHA = 1.0f;

    private static final int COLOR_START   = 0xFFFFFFAA;
    private static final int COLOR_MID     = 0xFFFFCC44;
    private static final int COLOR_END     = 0xFFFF6600;
    private static final int COLOR_POINTER = 0xFFFF1100;

    private RayRenderer() {}

    public static boolean shouldRenderOffScreen(RayEmittingBlockEntity be) {
        return be.getBeamLength() > 0 || be.getPointerLength() > 0;
    }

    public static void render(RayEmittingBlockEntity be, float partialTick, PoseStack poseStack) {
        int beamLength    = be.getBeamLength();
        int pointerLength = be.getPointerLength();
        if (beamLength <= 0 && pointerLength <= 0) return;

        Direction facing = be.getEmitDirection();
        Vector3f dir = new Vector3f(facing.getStepX(), facing.getStepY(), facing.getStepZ());

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        long gameTime = be.getLevel() != null ? be.getLevel().getGameTime() : 0;
        float pulse = (float) (Math.sin((gameTime + partialTick) * PULSE_SPEED * Math.PI) * 0.5 + 0.5);
        float pulseAlpha = PULSE_MIN_ALPHA + (PULSE_MAX_ALPHA - PULSE_MIN_ALPHA) * pulse;

        Vector3f up      = Math.abs(dir.y) > 0.99f ? new Vector3f(1, 0, 0) : new Vector3f(0, 1, 0);
        Vector3f right   = new Vector3f(dir).cross(up).normalize();
        Vector3f localUp = new Vector3f(right).cross(dir).normalize();

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Matrix4f pose = poseStack.last().pose();
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        if (beamLength > 0) {
            emitCylinder(buffer, pose, dir, beamLength, right, localUp, BEAM_RADIUS, COLOR_START, COLOR_END, pulseAlpha);
            emitMidSegment(buffer, pose, dir, beamLength, right, localUp, COLOR_MID, pulseAlpha);
        } else {
            emitCylinder(buffer, pose, dir, pointerLength, right, localUp, POINTER_RADIUS, COLOR_POINTER, COLOR_POINTER, pulseAlpha);
        }

        MeshData mesh = buffer.build();
        if (mesh != null) BufferUploader.drawWithShader(mesh);

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();

        poseStack.popPose();
    }

    private static void emitCylinder(BufferBuilder buffer, Matrix4f pose, Vector3f dir, float length,
                                     Vector3f right, Vector3f localUp, float radius,
                                     int colorStart, int colorEnd, float pulseAlpha) {
        for (int i = 0; i < CYLINDER_SIDES; i++) {
            float a0 = (float) (i       * 2 * Math.PI / CYLINDER_SIDES);
            float a1 = (float) ((i + 1) * 2 * Math.PI / CYLINDER_SIDES);
            float c0x = (float) Math.cos(a0), c0y = (float) Math.sin(a0);
            float c1x = (float) Math.cos(a1), c1y = (float) Math.sin(a1);

            Vector3f r0a = ringPoint(right, localUp, c0x, c0y, radius);
            Vector3f r1a = ringPoint(right, localUp, c1x, c1y, radius);
            Vector3f r0b = new Vector3f(r0a).add(dir.x * length, dir.y * length, dir.z * length);
            Vector3f r1b = new Vector3f(r1a).add(dir.x * length, dir.y * length, dir.z * length);

            addVertex(buffer, pose, r0a, colorStart, pulseAlpha);
            addVertex(buffer, pose, r1a, colorStart, pulseAlpha);
            addVertex(buffer, pose, r1b, colorEnd,   pulseAlpha);
            addVertex(buffer, pose, r0b, colorEnd,   pulseAlpha);
        }
    }

    private static void emitMidSegment(BufferBuilder buffer, Matrix4f pose, Vector3f dir, float length,
                                       Vector3f right, Vector3f localUp, int midColor, float pulseAlpha) {
        float segStart  = length * 0.25f;
        float segEnd    = length * 0.75f;
        float midRadius = BEAM_RADIUS * 1.1f;
        int   faded     = midColor & 0x00FFFFFF;

        for (int i = 0; i < CYLINDER_SIDES; i++) {
            float a0 = (float) (i       * 2 * Math.PI / CYLINDER_SIDES);
            float a1 = (float) ((i + 1) * 2 * Math.PI / CYLINDER_SIDES);
            float c0x = (float) Math.cos(a0), c0y = (float) Math.sin(a0);
            float c1x = (float) Math.cos(a1), c1y = (float) Math.sin(a1);

            Vector3f r0a = ringPoint(right, localUp, c0x, c0y, midRadius).add(dir.x * segStart, dir.y * segStart, dir.z * segStart);
            Vector3f r1a = ringPoint(right, localUp, c1x, c1y, midRadius).add(dir.x * segStart, dir.y * segStart, dir.z * segStart);
            Vector3f r0b = ringPoint(right, localUp, c0x, c0y, midRadius).add(dir.x * segEnd,   dir.y * segEnd,   dir.z * segEnd);
            Vector3f r1b = ringPoint(right, localUp, c1x, c1y, midRadius).add(dir.x * segEnd,   dir.y * segEnd,   dir.z * segEnd);

            addVertex(buffer, pose, r0a, faded,    pulseAlpha);
            addVertex(buffer, pose, r1a, faded,    pulseAlpha);
            addVertex(buffer, pose, r1b, midColor, pulseAlpha);
            addVertex(buffer, pose, r0b, midColor, pulseAlpha);
        }
    }

    private static Vector3f ringPoint(Vector3f right, Vector3f localUp, float cx, float cy, float radius) {
        return new Vector3f(
                right.x * cx * radius + localUp.x * cy * radius,
                right.y * cx * radius + localUp.y * cy * radius,
                right.z * cx * radius + localUp.z * cy * radius);
    }

    private static void addVertex(BufferBuilder buffer, Matrix4f pose, Vector3f v, int argb, float pulseAlpha) {
        int a = (int) (((argb >> 24) & 0xFF) * pulseAlpha);
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8)  & 0xFF;
        int b =  argb        & 0xFF;
        buffer.addVertex(pose, v.x, v.y, v.z).setColor(r, g, b, a);
    }
}
