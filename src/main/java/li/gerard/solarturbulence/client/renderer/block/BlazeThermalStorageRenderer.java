package li.gerard.solarturbulence.client.renderer.block;

import li.gerard.solarturbulence.block.blazethermalstorage.BlazeThermalStorageBlockEntity;
import li.gerard.solarturbulence.client.model.block.BlazeThermalStorageModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BlazeThermalStorageRenderer extends GeoBlockRenderer<BlazeThermalStorageBlockEntity> {
    public BlazeThermalStorageRenderer(BlockEntityRendererProvider.Context context) {
        super(new BlazeThermalStorageModel());
    }
}
