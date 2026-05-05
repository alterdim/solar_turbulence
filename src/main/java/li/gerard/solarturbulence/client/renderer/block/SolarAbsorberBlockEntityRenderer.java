package li.gerard.solarturbulence.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import li.gerard.solarturbulence.block.solarabsorber.SolarAbsorberBlockEntity;
import li.gerard.solarturbulence.client.model.block.SolarAbsorberModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SolarAbsorberBlockEntityRenderer extends GeoBlockRenderer<SolarAbsorberBlockEntity> {
    public SolarAbsorberBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new SolarAbsorberModel());
    }

    @Override
    public void render(SolarAbsorberBlockEntity animatable, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (animatable.isMultiFormed()) {
            super.render(animatable, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }

    @Override
    public AABB getRenderBoundingBox(SolarAbsorberBlockEntity blockEntity) {
        return super.getRenderBoundingBox(blockEntity).expandTowards(1, 1, 1).expandTowards(-1, -1, -1);
    }
}
