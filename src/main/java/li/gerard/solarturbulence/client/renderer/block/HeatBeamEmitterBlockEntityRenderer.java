package li.gerard.solarturbulence.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import li.gerard.solarturbulence.block.heatbeam.HeatBeamEmitterBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class HeatBeamEmitterBlockEntityRenderer implements BlockEntityRenderer<HeatBeamEmitterBlockEntity> {

    public HeatBeamEmitterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(HeatBeamEmitterBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        RayRenderer.render(be, partialTick, poseStack);
    }

    @Override
    public boolean shouldRenderOffScreen(HeatBeamEmitterBlockEntity be) {
        return RayRenderer.shouldRenderOffScreen(be);
    }
}
