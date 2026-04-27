package li.gerard.solarturbulence.data;

import li.gerard.solarturbulence.SolarTurbulenceMod;
import li.gerard.solarturbulence.property.ModFluidProperties;
import li.gerard.solarturbulence.registry.ModRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = SolarTurbulenceMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DatapackRegistry {
    @SubscribeEvent
    public static void onNewRegistry(DataPackRegistryEvent.NewRegistry event) {

        event.dataPackRegistry(
                ModRegistries.FLUID_HEAT_PROPERTIES,
                ModFluidProperties.CODEC,
                ModFluidProperties.CODEC
        );
    }
}
