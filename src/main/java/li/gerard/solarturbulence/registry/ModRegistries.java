package li.gerard.solarturbulence.registry;

import li.gerard.solarturbulence.SolarTurbulenceMod;
import li.gerard.solarturbulence.property.ModFluidProperties;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;


public class ModRegistries {
    public static final ResourceKey<Registry<ModFluidProperties>> FLUID_HEAT_PROPERTIES =
            ResourceKey.createRegistryKey(
                    ResourceLocation.fromNamespaceAndPath(SolarTurbulenceMod.MODID, "fluid_properties")
            );
}