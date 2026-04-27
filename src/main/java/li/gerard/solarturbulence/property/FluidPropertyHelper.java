package li.gerard.solarturbulence.property;

import li.gerard.solarturbulence.registry.ModRegistries;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import java.util.Optional;

public class FluidPropertyHelper {
    public static Optional<ModFluidProperties> get(RegistryAccess registries, Fluid fluid) {
        ResourceLocation fluidId = net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(fluid);
        if (fluidId == null) return Optional.empty();

        return registries.registry(ModRegistries.FLUID_HEAT_PROPERTIES)
                .flatMap(reg -> reg.getOptional(fluidId));
    }
}
