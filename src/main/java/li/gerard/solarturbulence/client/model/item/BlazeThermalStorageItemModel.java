package li.gerard.solarturbulence.client.model.item;

import li.gerard.solarturbulence.SolarTurbulenceMod;
import li.gerard.solarturbulence.block.blazethermalstorage.BlazeThermalStorageBlockEntity;
import li.gerard.solarturbulence.item.block.BlazeThermalStorageBlockItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlazeThermalStorageItemModel extends GeoModel<BlazeThermalStorageBlockItem> {

    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(SolarTurbulenceMod.MODID, "geo/blaze_thermal_storage.geo.json");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(SolarTurbulenceMod.MODID, "textures/block/blaze_thermal_storage.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(SolarTurbulenceMod.MODID, "animations/blaze_thermal_storage.json");

    @Override
    public ResourceLocation getModelResource(BlazeThermalStorageBlockItem animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(BlazeThermalStorageBlockItem animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(BlazeThermalStorageBlockItem animatable) {
        return animations;
    }


}