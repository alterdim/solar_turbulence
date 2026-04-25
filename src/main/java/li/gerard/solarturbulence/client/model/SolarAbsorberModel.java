package li.gerard.solarturbulence.client.model;

import li.gerard.solarturbulence.SolarTurbulenceMod;
import li.gerard.solarturbulence.block.solarabsorber.SolarAbsorberBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class SolarAbsorberModel extends GeoModel<SolarAbsorberBlockEntity> {

    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(SolarTurbulenceMod.MODID, "geo/solar_absorber.geo.json");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(SolarTurbulenceMod.MODID, "textures/block/solar_absorber_multiblock.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(SolarTurbulenceMod.MODID, "animations/example.animation.json");

    @Override
    public ResourceLocation getModelResource(SolarAbsorberBlockEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(SolarAbsorberBlockEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(SolarAbsorberBlockEntity animatable) {
        return animations;
    }
}
