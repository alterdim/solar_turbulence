package li.gerard.solarturbulence.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import li.gerard.solarturbulence.block.solarabsorber.SolarAbsorberBlockEntity;
import li.gerard.solarturbulence.block.solarreceiver.SolarReceiverBlockEntity;
import li.gerard.solarturbulence.client.model.SolarAbsorberModel;
import li.gerard.solarturbulence.client.model.SolarReceiverModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SolarReceiverBlockEntityRenderer extends GeoBlockRenderer<SolarReceiverBlockEntity> {
    public SolarReceiverBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new SolarReceiverModel());
    }

    @Override
    public void render(SolarReceiverBlockEntity animatable, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        super.render(animatable, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(SolarReceiverBlockEntity blockEntity) {
        return super.getRenderBoundingBox(blockEntity).expandTowards(1, 1, 1).expandTowards(-1, -1, -1);
    }
}
