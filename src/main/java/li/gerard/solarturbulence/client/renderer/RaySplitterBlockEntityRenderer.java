package li.gerard.solarturbulence.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import li.gerard.solarturbulence.block.raysplitter.RaySplitterBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class RaySplitterBlockEntityRenderer implements BlockEntityRenderer<RaySplitterBlockEntity> {

    public RaySplitterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(RaySplitterBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        long gameTime = be.getLevel() != null ? be.getLevel().getGameTime() : 0;
        int[] colors = be.getBeamColors();

        Direction dir0 = be.getOutDir0();
        if (dir0 != null && (be.getOutBeam0() > 0 || be.getOutPointer0() > 0)) {
            RayRenderer.renderSegment(dir0, be.getOutBeam0(), be.getOutPointer0(), partialTick, gameTime, poseStack, colors);
        }

        Direction dir1 = be.getOutDir1();
        if (dir1 != null && (be.getOutBeam1() > 0 || be.getOutPointer1() > 0)) {
            RayRenderer.renderSegment(dir1, be.getOutBeam1(), be.getOutPointer1(), partialTick, gameTime, poseStack, colors);
        }
    }

    @Override
    public boolean shouldRenderOffScreen(RaySplitterBlockEntity be) {
        return be.hasActiveRayOutput();
    }
}
