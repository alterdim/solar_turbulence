package li.gerard.solarturbulence.generic;

import li.gerard.solarturbulence.block.ModBlocks;
import li.gerard.solarturbulence.block.solarabsorber.SolarAbsorberBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.List;

public class MetallicFrameBlock extends Block {

    private static final List<BlockPos> FILLER_OFFSETS = List.of(
            new BlockPos(1, -1, 0),
            new BlockPos(-1, -1, 0),
            new BlockPos(0, -1, 1),
            new BlockPos(0, -1, -1),
            new BlockPos(0, -1, 0)
    );

    public static final BooleanProperty ASSEMBLED = BooleanProperty.create("assembled");

    public MetallicFrameBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(ASSEMBLED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ASSEMBLED);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(ASSEMBLED) ? RenderShape.INVISIBLE : RenderShape.MODEL;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide && state.getValue(ASSEMBLED)) {
            for (BlockPos offset : FILLER_OFFSETS) {
                BlockPos candidate = pos.offset(offset);
                if (level.getBlockState(candidate).is(ModBlocks.SOLAR_ABSORBER_BLOCK.get())) {
                    if (level.getBlockEntity(candidate) instanceof SolarAbsorberBlockEntity be) {
                        be.disassemble();
                        break;
                    }
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}