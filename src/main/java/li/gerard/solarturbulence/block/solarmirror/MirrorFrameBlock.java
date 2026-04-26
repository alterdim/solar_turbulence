package li.gerard.solarturbulence.block.solarmirror;

import li.gerard.solarturbulence.item.mirror.MirrorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class MirrorFrameBlock extends Block implements EntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public MirrorFrameBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MirrorFrameBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    /** Right-click with an item in hand — try to insert if it's a MirrorItem. */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                              BlockPos pos, Player player, InteractionHand hand,
                                              BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof MirrorFrameBlockEntity frame)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!(stack.getItem() instanceof MirrorItem)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (frame.hasMirror()) {
            // Already has one — let the empty-hand path handle extraction instead
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!level.isClientSide) {
            ItemStack remainder = frame.tryInsert(stack);
            if (!player.getAbilities().instabuild) {
                player.setItemInHand(hand, remainder);
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    /** Right-click with empty hand (or non-mirror item already handled above) — extract. */
    @Override
    protected net.minecraft.world.InteractionResult useWithoutItem(BlockState state, Level level,
                                                                   BlockPos pos, Player player,
                                                                   BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof MirrorFrameBlockEntity frame)) {
            return net.minecraft.world.InteractionResult.PASS;
        }
        if (!frame.hasMirror()) {
            return net.minecraft.world.InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            ItemStack extracted = frame.tryExtract();
            if (!player.getInventory().add(extracted)) {
                player.drop(extracted, false);
            }
        }
        return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);
    }

    /** Drop the inserted mirror when the frame is broken. */
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof MirrorFrameBlockEntity frame && frame.hasMirror()) {
                net.minecraft.world.Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(),
                        frame.getMirrorStack());
            }
            super.onRemove(state, level, pos, newState, moved);
        }
    }
}