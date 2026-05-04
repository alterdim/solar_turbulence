package li.gerard.solarturbulence.client.renderer;

import li.gerard.solarturbulence.block.blazethermalstorage.BlazeThermalStorageBlockEntity;
import li.gerard.solarturbulence.client.model.BlazeThermalStorageModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BlazeThermalStorageRenderer extends GeoBlockRenderer<BlazeThermalStorageBlockEntity> {
    public BlazeThermalStorageRenderer(BlockEntityRendererProvider.Context context) {
        super(new BlazeThermalStorageModel());
    }
}
