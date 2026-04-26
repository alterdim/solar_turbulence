package li.gerard.solarturbulence.data;

import li.gerard.solarturbulence.SolarTurbulenceMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDataComponents {

    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, SolarTurbulenceMod.MODID);

    public static final Supplier<DataComponentType<BlockPos>> LINKED_ABSORBER =
            COMPONENTS.registerComponentType("linked_absorber", builder -> builder
                    .persistent(BlockPos.CODEC)
                    .networkSynchronized(BlockPos.STREAM_CODEC));
}