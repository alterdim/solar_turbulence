package li.gerard.solarturbulence.client.renderer.item;

import li.gerard.solarturbulence.client.model.item.BlazeThermalStorageItemModel;
import li.gerard.solarturbulence.item.block.BlazeThermalStorageBlockItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class BlazeThermalStorageItemRenderer extends GeoItemRenderer<BlazeThermalStorageBlockItem> {
    public BlazeThermalStorageItemRenderer() {
        super(new BlazeThermalStorageItemModel());
    }
}
