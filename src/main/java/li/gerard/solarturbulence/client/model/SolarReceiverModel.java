package li.gerard.solarturbulence.client.model;

import li.gerard.solarturbulence.SolarTurbulenceMod;
import li.gerard.solarturbulence.block.solarabsorber.SolarAbsorberBlockEntity;
import li.gerard.solarturbulence.block.solarreceiver.SolarReceiverBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SolarReceiverModel extends GeoModel<SolarReceiverBlockEntity> {

    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(SolarTurbulenceMod.MODID, "geo/receiver.geo.json");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(SolarTurbulenceMod.MODID, "textures/block/receiver.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(SolarTurbulenceMod.MODID, "animations/receiver.animation.json");

    @Override
    public ResourceLocation getModelResource(SolarReceiverBlockEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(SolarReceiverBlockEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(SolarReceiverBlockEntity animatable) {
        return animations;
    }
}
