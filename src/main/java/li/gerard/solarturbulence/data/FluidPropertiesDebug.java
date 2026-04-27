package li.gerard.solarturbulence.data;

import com.mojang.logging.LogUtils;
import li.gerard.solarturbulence.SolarTurbulenceMod;
import li.gerard.solarturbulence.property.FluidPropertyHelper;
import li.gerard.solarturbulence.registry.ModRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import org.slf4j.Logger;

@EventBusSubscriber(modid = SolarTurbulenceMod.MODID)
public class FluidPropertiesDebug {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        var registryAccess = event.getPlayerList().getServer().registryAccess();
        var registry = registryAccess.registry(ModRegistries.FLUID_HEAT_PROPERTIES).orElse(null);
        LOGGER.error("THIS IS A TEST");
        if (registry == null) {
            LOGGER.error("[SolarTurbulence] FLUID_HEAT_PROPERTIES registry is missing!");
            return;
        }

        LOGGER.info("[SolarTurbulence] FLUID_HEAT_PROPERTIES loaded with {} entries:", registry.size());
        registry.entrySet().forEach(entry -> {
            var id = entry.getKey().location();
            var props = entry.getValue();
            LOGGER.info("  - {} : conductivity={}", id, props.conductivity());
        });

        // Specific test: does minecraft:water resolve?
        var waterProps = FluidPropertyHelper.get(registryAccess, net.minecraft.world.level.material.Fluids.WATER);
        if (waterProps.isPresent()) {
            LOGGER.info("[SolarTurbulence] minecraft:water lookup OK: {}", waterProps.get());
        } else {
            LOGGER.warn("[SolarTurbulence] minecraft:water lookup FAILED — entry not found in registry");
        }
    }
}
