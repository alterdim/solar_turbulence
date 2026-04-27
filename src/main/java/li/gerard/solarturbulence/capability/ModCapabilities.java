package li.gerard.solarturbulence.capability;

import li.gerard.solarturbulence.SolarTurbulenceMod;
import li.gerard.solarturbulence.block.ModBlockEntities;
import li.gerard.solarturbulence.capability.heat.IHeatStorage;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = SolarTurbulenceMod.MODID)
public class ModCapabilities {

    public static final BlockCapability<IHeatStorage, Direction> HEAT = BlockCapability.createSided(
            ResourceLocation.fromNamespaceAndPath(SolarTurbulenceMod.MODID, "heat"),
            IHeatStorage.class
    );

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.SOLAR_ABSORBER_BLOCK_ENTITY.get(),
                (be, side) -> be.energyStorage
        );
        event.registerBlockEntity(
                ModCapabilities.HEAT,
                ModBlockEntities.SOLAR_RECEIVER_BLOCK_ENTITY.get(),
                (be, side) -> be.getHeatStorage()
        );
    }
}