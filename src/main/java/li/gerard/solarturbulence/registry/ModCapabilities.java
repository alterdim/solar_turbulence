package li.gerard.solarturbulence.registry;

import li.gerard.solarturbulence.SolarTurbulenceMod;
import li.gerard.solarturbulence.block.ModBlockEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = SolarTurbulenceMod.MODID)
public class ModCapabilities {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.SOLAR_ABSORBER_BLOCK_ENTITY.get(),
                (be, side) -> be.energyStorage
        );
    }
}