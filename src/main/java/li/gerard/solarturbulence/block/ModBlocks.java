package li.gerard.solarturbulence.block;

import li.gerard.solarturbulence.SolarTurbulenceMod;
import li.gerard.solarturbulence.block.heatbeam.HeatBeamEmitterBlock;
import li.gerard.solarturbulence.generic.MetallicFrameBlock;
import li.gerard.solarturbulence.block.solarabsorber.SolarAbsorberBlock;
import li.gerard.solarturbulence.block.solarmirror.MirrorFrameBlock;
import li.gerard.solarturbulence.block.solarreceiver.SolarReceiverBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(
            BuiltInRegistries.BLOCK,
            SolarTurbulenceMod.MODID
    );

    public static final DeferredHolder<Block, Block> EXAMPLE_BLOCK = BLOCKS.register(
            "example_block", // Our registry name.
            () -> new Block(BlockBehaviour.Properties.of()));

    public static final DeferredHolder<Block, Block> SOLAR_ABSORBER_BLOCK = BLOCKS.register(
            "solar_absorber", // Our registry name.
            () -> new SolarAbsorberBlock(BlockBehaviour.Properties.of().noOcclusion()));

    public static final DeferredHolder<Block, Block> METALLIC_FRAME_BLOCK = BLOCKS.register(
            "metallic_frame", // Our registry name.
            () -> new MetallicFrameBlock(BlockBehaviour.Properties.of()));

    public static final DeferredHolder<Block, Block> MIRROR_FRAME_BLOCK = BLOCKS.register(
            "mirror_frame", // Our registry name.
            () -> new MirrorFrameBlock(BlockBehaviour.Properties.of().noOcclusion()));

    public static final DeferredHolder<Block, Block> SOLAR_RECEIVER_BLOCK = BLOCKS.register(
            "solar_receiver",
            () -> new SolarReceiverBlock(BlockBehaviour.Properties.of().noOcclusion()));

    public static final DeferredHolder<Block, Block> HEAT_BEAM_EMITTER_BLOCK = BLOCKS.register(
            "heat_beam_emitter",
            () -> new HeatBeamEmitterBlock(BlockBehaviour.Properties.of().noOcclusion()));
}
