package li.gerard.solarturbulence.block.heatbeam;

import li.gerard.solarturbulence.block.ModBlockEntities;
import li.gerard.solarturbulence.capability.heat.HeatStorage;
import li.gerard.solarturbulence.block.generic.RayEmittingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class HeatBeamEmitterBlockEntity extends RayEmittingBlockEntity {


    public HeatBeamEmitterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.HEAT_BEAM_EMITTER_BLOCK_ENTITY.get(), pos, blockState, 3000, 10, 10);
    }

    @Override
    public Direction getEmitDirection() {
        return getBlockState().getValue(HeatBeamEmitterBlock.FACING);
    }

    @Override
    protected int getMaxRange() {
        return 16;
    }


    public HeatStorage getHeatStorage() {
        return heatStorage;
    }
}
