package li.gerard.solarturbulence.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import li.gerard.solarturbulence.block.solarmirror.MirrorFrameBlockEntity;
import li.gerard.solarturbulence.client.model.MirrorFrameModel;
import li.gerard.solarturbulence.item.mirror.MirrorItem;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MirrorFrameBlockEntityRenderer extends GeoBlockRenderer<MirrorFrameBlockEntity> {

    private static final float BEAM_RADIUS = 0.15f;
    private static final int CYLINDER_SIDES = 16;
    private static final float PULSE_SPEED = 0.04f;
    private static final float PULSE_MIN_ALPHA = 0.55f;
    private static final float PULSE_MAX_ALPHA = 1.0f;

    public MirrorFrameBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new MirrorFrameModel());
    }

    @Override
    public void render(MirrorFrameBlockEntity frame, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        // Render the GeckoLib model first
        super.render(frame, partialTick, poseStack, bufferSource, packedLight, packedOverlay);

        // Then render the beam if a mirror is inserted
        if (!frame.shouldRenderBeam()) return;
        ItemStack stack = frame.getMirrorStack();
        if (!(stack.getItem() instanceof MirrorItem mirror)) return;
        BlockPos target = frame.getBeamTarget();
        if (target == null) return;

        renderBeam(frame, mirror, target, poseStack, partialTick);
    }

    private void renderBeam(MirrorFrameBlockEntity frame, MirrorItem mirror, BlockPos target,
                            PoseStack poseStack, float partialTick) {
        poseStack.pushPose();


        poseStack.translate(0.5, 0.5, 0.5);

        BlockPos source = frame.getBlockPos();
        Vector3f dir = new Vector3f(
                target.getX() - source.getX(),
                target.getY() - source.getY(),
                target.getZ() - source.getZ()
        );
        float length = dir.length();
        if (length < 0.001f) {
            poseStack.popPose();
            return;
        }
        dir.div(length);

        long gameTime = frame.getLevel() != null ? frame.getLevel().getGameTime() : 0;
        float pulse = (float) (Math.sin((gameTime + partialTick) * PULSE_SPEED * Math.PI) * 0.5 + 0.5);
        float pulseAlpha = PULSE_MIN_ALPHA + (PULSE_MAX_ALPHA - PULSE_MIN_ALPHA) * pulse;

        // coord stuff
        Vector3f up = Math.abs(dir.y) > 0.99f ? new Vector3f(1, 0, 0) : new Vector3f(0, 1, 0);
        Vector3f right = new Vector3f(dir).cross(up).normalize();
        Vector3f localUp = new Vector3f(right).cross(dir).normalize();

        int[] colors = mirror.getBeamColors();

        // glow
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA,
                com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Matrix4f pose = poseStack.last().pose();
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        // pass
        int colorSource = colors[0];
        int colorTarget = colors[colors.length - 1];
        emitCylinder(buffer, pose, dir, length, right, localUp, BEAM_RADIUS,
                colorSource, colorTarget, pulseAlpha);

        // 3 colors
        if (colors.length == 3) {
            emitMidSegment(buffer, pose, dir, length, right, localUp, colors[1], pulseAlpha);
        }

        MeshData mesh = buffer.build();
        if (mesh != null) {
            BufferUploader.drawWithShader(mesh);
        }

        //
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
            float a0 = (float) (i * 2 * Math.PI / CYLINDER_SIDES);
            float a1 = (float) ((i + 1) * 2 * Math.PI / CYLINDER_SIDES);
            float c0x = (float) Math.cos(a0), c0y = (float) Math.sin(a0);
            float c1x = (float) Math.cos(a1), c1y = (float) Math.sin(a1);

            Vector3f r0a = ringPoint(right, localUp, c0x, c0y, radius);
            Vector3f r1a = ringPoint(right, localUp, c1x, c1y, radius);
            Vector3f r0b = new Vector3f(r0a).add(dir.x * length, dir.y * length, dir.z * length);
            Vector3f r1b = new Vector3f(r1a).add(dir.x * length, dir.y * length, dir.z * length);

            addVertex(buffer, pose, r0a, colorStart, pulseAlpha);
            addVertex(buffer, pose, r1a, colorStart, pulseAlpha);
            addVertex(buffer, pose, r1b, colorEnd, pulseAlpha);
            addVertex(buffer, pose, r0b, colorEnd, pulseAlpha);
        }
    }

    private static void emitMidSegment(BufferBuilder buffer, Matrix4f pose, Vector3f dir, float length,
                                       Vector3f right, Vector3f localUp, int midColor, float pulseAlpha) {
        float segStart = length * 0.25f;
        float segEnd = length * 0.75f;
        float midRadius = BEAM_RADIUS * 1.05f;
        int faded = midColor & 0x00FFFFFF;

        for (int i = 0; i < CYLINDER_SIDES; i++) {
            float a0 = (float) (i * 2 * Math.PI / CYLINDER_SIDES);
            float a1 = (float) ((i + 1) * 2 * Math.PI / CYLINDER_SIDES);
            float c0x = (float) Math.cos(a0), c0y = (float) Math.sin(a0);
            float c1x = (float) Math.cos(a1), c1y = (float) Math.sin(a1);

            Vector3f r0a = ringPoint(right, localUp, c0x, c0y, midRadius)
                    .add(dir.x * segStart, dir.y * segStart, dir.z * segStart);
            Vector3f r1a = ringPoint(right, localUp, c1x, c1y, midRadius)
                    .add(dir.x * segStart, dir.y * segStart, dir.z * segStart);
            Vector3f r0b = ringPoint(right, localUp, c0x, c0y, midRadius)
                    .add(dir.x * segEnd, dir.y * segEnd, dir.z * segEnd);
            Vector3f r1b = ringPoint(right, localUp, c1x, c1y, midRadius)
                    .add(dir.x * segEnd, dir.y * segEnd, dir.z * segEnd);

            addVertex(buffer, pose, r0a, faded, pulseAlpha);
            addVertex(buffer, pose, r1a, faded, pulseAlpha);
            addVertex(buffer, pose, r1b, midColor, pulseAlpha);
            addVertex(buffer, pose, r0b, midColor, pulseAlpha);
        }
    }

    private static Vector3f ringPoint(Vector3f right, Vector3f localUp, float cx, float cy, float radius) {
        return new Vector3f(
                right.x * cx * radius + localUp.x * cy * radius,
                right.y * cx * radius + localUp.y * cy * radius,
                right.z * cx * radius + localUp.z * cy * radius
        );
    }

    private static void addVertex(BufferBuilder buffer, Matrix4f pose, Vector3f v,
                                  int argb, float pulseAlpha) {
        int a = (int) (((argb >> 24) & 0xFF) * pulseAlpha);
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        buffer.addVertex(pose, v.x, v.y, v.z).setColor(r, g, b, a);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, MirrorFrameBlockEntity animatable, GeoBone bone,
                                  RenderType renderType, MultiBufferSource bufferSource,
                                  VertexConsumer buffer, boolean isReRender, float partialTick,
                                  int packedLight, int packedOverlay, int colour) {
        if (bone.getName().equals("mirror")) {
            ItemStack stack = animatable.getMirrorStack();

            if (stack.isEmpty() || !(stack.getItem() instanceof MirrorItem mirror)) {
                return;
            }

            ResourceLocation faceTex = mirror.getMirrorFaceTexture();
            RenderType faceType = RenderType.entityCutout(faceTex);
            VertexConsumer faceBuffer = bufferSource.getBuffer(faceType);

            super.renderRecursively(poseStack, animatable, bone, faceType, bufferSource,
                    faceBuffer, isReRender, partialTick, packedLight, packedOverlay, colour);
            return;
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource,
                buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}