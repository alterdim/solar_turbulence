package li.gerard.solarturbulence.capability;

import li.gerard.solarturbulence.SolarTurbulenceMod;
import li.gerard.solarturbulence.block.ModBlockEntities;
import li.gerard.solarturbulence.capability.heat.HeatStorage;
import li.gerard.solarturbulence.capability.heat.IHeatStorage;
import li.gerard.solarturbulence.capability.heat.ItemStackHeatStorage;
import li.gerard.solarturbulence.item.ModItems;
import li.gerard.solarturbulence.item.heatrod.HeatRodItem;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import static li.gerard.solarturbulence.SolarTurbulenceMod.MODID;

@EventBusSubscriber(modid = MODID)
public class ModCapabilities {

    public static final BlockCapability<IHeatStorage, Direction> HEAT = BlockCapability.createSided(
            ResourceLocation.fromNamespaceAndPath(MODID, "heat"),
            IHeatStorage.class
    );

    public static final ItemCapability<IHeatStorage, Void> HEAT_ITEM =
            ItemCapability.createVoid(ResourceLocation.fromNamespaceAndPath(SolarTurbulenceMod.MODID, "heat"), IHeatStorage.class);

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
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.SOLAR_RECEIVER_BLOCK_ENTITY.get(),
                (be, side) -> be.getEnergyStorage()
        );

        event.registerBlockEntity(
                ModCapabilities.HEAT,
                ModBlockEntities.HEAT_BEAM_EMITTER_BLOCK_ENTITY.get(),
                (be, side) -> be.getHeatStorage()
        );

        event.registerBlockEntity(
                ModCapabilities.HEAT,
                ModBlockEntities.BLAZE_THERMAL_STORAGE_BLOCK_ENTITY.get(),
                (be, side) -> be.getHeatStorage()
        );

        event.registerItem(
                ModCapabilities.HEAT_ITEM,
                (stack, ctx) -> {
                    HeatRodItem rod = (HeatRodItem) stack.getItem();
                    return new ItemStackHeatStorage(stack, rod.getCapacity(), rod.getMaxReceive(), rod.getMaxExtract());
                },
                ModItems.BLAZE_HEAT_RODS.get()
        );

    }
}