package li.gerard.solarturbulence.block;

import li.gerard.solarturbulence.SolarTurbulenceMod;
import li.gerard.solarturbulence.block.blazethermalstorage.BlazeThermalStorageBlockEntity;
import li.gerard.solarturbulence.block.heatbeam.HeatBeamEmitterBlockEntity;
import li.gerard.solarturbulence.block.raysplitter.RaySplitterBlockEntity;
import li.gerard.solarturbulence.block.solarabsorber.SolarAbsorberBlockEntity;
import li.gerard.solarturbulence.block.solarmirror.MirrorFrameBlockEntity;
import li.gerard.solarturbulence.block.solarreceiver.SolarReceiverBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SolarTurbulenceMod.MODID);

    public static final Supplier<BlockEntityType<SolarAbsorberBlockEntity>> SOLAR_ABSORBER_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "solar_absorber_block_entity",
            // The block entity type, created using a builder.
            () -> BlockEntityType.Builder.of(
                            SolarAbsorberBlockEntity::new,
                            ModBlocks.SOLAR_ABSORBER_BLOCK.get()
                    )
                    // Build using null; vanilla does some datafixer shenanigans with the parameter that we don't need.
                    .build(null)
    );

    public static final Supplier<BlockEntityType<MirrorFrameBlockEntity>> MIRROR_FRAME_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "mirror_frame_block_entity",
            () -> BlockEntityType.Builder.of(
                            MirrorFrameBlockEntity::new,
                            ModBlocks.MIRROR_FRAME_BLOCK.get()
                    )
                    .build(null)
    );

    public static final Supplier<BlockEntityType<SolarReceiverBlockEntity>> SOLAR_RECEIVER_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "solar_receiver_block_entity",
            () -> BlockEntityType.Builder.of(
                            SolarReceiverBlockEntity::new,
                            ModBlocks.SOLAR_RECEIVER_BLOCK.get()
                    )
                    .build(null)
    );

    public static final Supplier<BlockEntityType<HeatBeamEmitterBlockEntity>> HEAT_BEAM_EMITTER_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "heat_beam_emitter_block_entity",
            () -> BlockEntityType.Builder.of(
                            HeatBeamEmitterBlockEntity::new,
                            ModBlocks.HEAT_BEAM_EMITTER_BLOCK.get()
                    )
                    .build(null)
    );

    public static final Supplier<BlockEntityType<RaySplitterBlockEntity>> RAY_SPLITTER_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "ray_splitter_block_entity",
            () -> BlockEntityType.Builder.of(
                            RaySplitterBlockEntity::new,
                            ModBlocks.RAY_SPLITTER_BLOCK.get()
                    )
                    .build(null)
    );

    public static final Supplier<BlockEntityType<BlazeThermalStorageBlockEntity>> BLAZE_THERMAL_STORAGE_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "blaze_thermal_storage_block_entity",
            () -> BlockEntityType.Builder.of(
                            BlazeThermalStorageBlockEntity::new,
                            ModBlocks.BLAZE_THERMAL_STORAGE_BLOCK.get()
                    )
                    .build(null)
    );

}
