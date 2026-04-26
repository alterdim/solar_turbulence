package li.gerard.solarturbulence.client.model;

import li.gerard.solarturbulence.SolarTurbulenceMod;
import li.gerard.solarturbulence.block.solarabsorber.SolarAbsorberBlockEntity;
import li.gerard.solarturbulence.block.solarmirror.MirrorFrameBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MirrorFrameModel extends GeoModel<MirrorFrameBlockEntity> {

    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(SolarTurbulenceMod.MODID, "geo/mirror_frame.geo.json");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(SolarTurbulenceMod.MODID, "textures/block/mirror_frame.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(SolarTurbulenceMod.MODID, "animations/example.animation.json");

    @Override
    public ResourceLocation getModelResource(MirrorFrameBlockEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(MirrorFrameBlockEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(MirrorFrameBlockEntity animatable) {
        return animations;
    }
}
