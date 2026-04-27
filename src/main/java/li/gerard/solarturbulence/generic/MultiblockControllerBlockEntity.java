package li.gerard.solarturbulence.generic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public abstract class MultiblockControllerBlockEntity extends BlockEntity {

    int syncTicker = 0;

    protected MultiblockControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    protected abstract void onTick(Level level, BlockPos pos, BlockState state);

    public abstract List<Component> getHudLines();

    protected static <T extends MultiblockControllerBlockEntity> void tick(
            Level level, BlockPos pos, BlockState state, T be) {
        if (level.isClientSide) return;
        be.onTick(level, pos, state);
        if (++be.syncTicker >= 20) {
            be.syncTicker = 0;
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }
}
