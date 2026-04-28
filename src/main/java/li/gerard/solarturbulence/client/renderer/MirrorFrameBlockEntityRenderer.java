package li.gerard.solarturbulence.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import li.gerard.solarturbulence.block.solarmirror.MirrorFrameBlockEntity;
import li.gerard.solarturbulence.client.model.MirrorFrameModel;
import li.gerard.solarturbulence.item.mirror.MirrorItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MirrorFrameBlockEntityRenderer extends GeoBlockRenderer<MirrorFrameBlockEntity> {

    public MirrorFrameBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new MirrorFrameModel());
    }

    @Override
    public void render(MirrorFrameBlockEntity frame, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        super.render(frame, partialTick, poseStack, bufferSource, packedLight, packedOverlay);

        Direction outDir = frame.getOutDirection();
        if (outDir == null) return;
        int outBeam    = frame.getOutBeamLength();
        int outPointer = frame.getOutPointerLength();
        if (outBeam <= 0 && outPointer <= 0) return;

        long gameTime = frame.getLevel() != null ? frame.getLevel().getGameTime() : 0;
        RayRenderer.renderSegment(outDir, outBeam, outPointer, partialTick, gameTime, poseStack,
                frame.getBeamColors());
    }

    @Override
    public boolean shouldRenderOffScreen(MirrorFrameBlockEntity be) {
        return be.hasActiveRayOutput();
    }

    /** Swap the mirror face bone's texture to the inserted MirrorItem's face texture. */
    @Override
    public void renderRecursively(PoseStack poseStack, MirrorFrameBlockEntity animatable, GeoBone bone,
                                  RenderType renderType, MultiBufferSource bufferSource,
                                  VertexConsumer buffer, boolean isReRender, float partialTick,
                                  int packedLight, int packedOverlay, int colour) {
        if (bone.getName().equals("mirror")) {
            ItemStack stack = animatable.getMirrorStack();
            if (stack.isEmpty() || !(stack.getItem() instanceof MirrorItem mirror)) return;

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
