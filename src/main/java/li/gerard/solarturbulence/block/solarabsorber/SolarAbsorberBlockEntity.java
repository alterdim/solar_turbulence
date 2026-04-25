package li.gerard.solarturbulence.block.solarabsorber;

import li.gerard.solarturbulence.block.ModBlockEntities;
import li.gerard.solarturbulence.block.ModBlocks;
import li.gerard.solarturbulence.block.generic.MetallicFrameBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

import static li.gerard.solarturbulence.block.solarabsorber.SolarAbsorberBlock.ASSEMBLED;

public class SolarAbsorberBlockEntity extends BlockEntity implements GeoBlockEntity {

    AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    private static final List<BlockPos> FILLER_OFFSETS = List.of(
            new BlockPos(1, 1, 0),
            new BlockPos(-1, 1, 0),
            new BlockPos(0, 1, 1),
            new BlockPos(0, 1, -1),
            new BlockPos(0, 1, 0)
    );

    public SolarAbsorberBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.SOLAR_ABSORBER_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    public boolean isMultiFormed() {
        return getBlockState().getValue(ASSEMBLED);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    public void tryAssemble() {
        if (isStructureValid()) {
            assemble();
        } else {
            // optionally send feedback to player
        }
    }

    private boolean isStructureValid() {
        for (BlockPos offset : FILLER_OFFSETS) {
            BlockPos target = worldPosition.offset(offset);
            BlockState state = level.getBlockState(target);

            // Check it's the right block AND not already part of another multiblock
            if (!state.is(ModBlocks.METALLIC_FRAME_BLOCK.get()) || state.getValue(MetallicFrameBlock.ASSEMBLED)) {
                return false;
            }
        }
        return true;
    }

    private void assemble() {
        for (BlockPos offset : FILLER_OFFSETS) {
            BlockPos target = worldPosition.offset(offset);
            level.setBlock(target, level.getBlockState(target)
                    .setValue(MetallicFrameBlock.ASSEMBLED, true), 3);
        }
        level.setBlock(worldPosition, getBlockState()
                .setValue(SolarAbsorberBlock.ASSEMBLED, true), 3);
    }

    public void disassemble() {
        for (BlockPos offset : FILLER_OFFSETS) {
            BlockPos target = worldPosition.offset(offset);
            BlockState state = level.getBlockState(target);

            if (state.is(ModBlocks.METALLIC_FRAME_BLOCK.get())) {
                level.setBlock(target, state.setValue(MetallicFrameBlock.ASSEMBLED, false), 3);
            }
        }
        level.setBlock(worldPosition, getBlockState()
                .setValue(SolarAbsorberBlock.ASSEMBLED, false), 3);
    }

}
