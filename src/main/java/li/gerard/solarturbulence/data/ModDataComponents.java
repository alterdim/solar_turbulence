package li.gerard.solarturbulence.data;

import com.mojang.serialization.Codec;
import li.gerard.solarturbulence.SolarTurbulenceMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDataComponents {

    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, SolarTurbulenceMod.MODID);

    public static final Supplier<DataComponentType<BlockPos>> LINKED_ABSORBER =
            COMPONENTS.registerComponentType("linked_absorber", builder -> builder
                    .persistent(BlockPos.CODEC)
                    .networkSynchronized(BlockPos.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> HEAT =
            COMPONENTS.register("heat", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .build()
            );
}