package li.gerard.solarturbulence.block.solarmirror;

import li.gerard.solarturbulence.block.ModBlockEntities;
import li.gerard.solarturbulence.item.mirror.MirrorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MirrorFrameBlockEntity extends BlockEntity implements GeoBlockEntity {

    AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);


    public MirrorFrameBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MIRROR_FRAME_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    public ItemStack getMirrorStack() {
        return items.get(0);
    }

    public boolean hasMirror() {
        return !items.get(0).isEmpty();
    }

    /** Try to insert the player's held stack. Returns the leftover stack. */
    public ItemStack tryInsert(ItemStack incoming) {
        if (hasMirror() || incoming.isEmpty() || !(incoming.getItem() instanceof MirrorItem)) {
            return incoming;
        }
        ItemStack toStore = incoming.copyWithCount(1);
        items.set(0, toStore);
        ItemStack remainder = incoming.copy();
        remainder.shrink(1);
        setChangedAndSync();
        return remainder;
    }

    /** Removes and returns the inserted mirror, or EMPTY if none. */
    public ItemStack tryExtract() {
        if (!hasMirror()) return ItemStack.EMPTY;
        ItemStack out = items.get(0);
        items.set(0, ItemStack.EMPTY);
        setChangedAndSync();
        return out;
    }

    private void setChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    // --- save / load ---


    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items.clear();
        ContainerHelper.loadAllItems(tag, items, registries);
    }

    // --- client sync ---

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        items.clear();
        ContainerHelper.loadAllItems(tag, items, registries);
    }



    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet,
                             HolderLookup.Provider registries) {
        if (packet.getTag() != null) {
            handleUpdateTag(packet.getTag(), registries);
        }
    }



    public BlockPos getBeamTarget() {
        return getBlockPos().above(10);
    }

    public boolean shouldRenderBeam() {
        return hasMirror();
    }
}
