package li.gerard.solarturbulence.block.solarmirror;

import com.lowdragmc.lowdraglib2.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib2.syncdata.holder.blockentity.ISyncPersistRPCBlockEntity;
import com.lowdragmc.lowdraglib2.syncdata.storage.FieldManagedStorage;
import com.lowdragmc.lowdraglib2.syncdata.storage.IManagedStorage;
import li.gerard.solarturbulence.block.ModBlockEntities;
import li.gerard.solarturbulence.item.mirror.MirrorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class MirrorFrameBlockEntity extends BlockEntity implements GeoBlockEntity, ISyncPersistRPCBlockEntity {

    AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    @Persisted @DescSynced
    private ItemStack mirrorStack = ItemStack.EMPTY;

    @Persisted @DescSynced
    @Nullable private BlockPos linkedAbsorber;


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
        return mirrorStack;
    }

    public boolean hasMirror() {
        return !mirrorStack.isEmpty();
    }

    /** Try to insert the player's held stack. Returns the leftover stack. */
    public ItemStack tryInsert(ItemStack incoming) {
        if (hasMirror() || incoming.isEmpty() || !(incoming.getItem() instanceof MirrorItem)) {
            return incoming;
        }
        mirrorStack = incoming.copyWithCount(1);
        ItemStack remainder = incoming.copy();
        remainder.shrink(1);
        return remainder;
    }

    /** Removes and returns the inserted mirror, or EMPTY if none. */
    public ItemStack tryExtract() {
        if (!hasMirror()) return ItemStack.EMPTY;
        ItemStack out = mirrorStack;
        mirrorStack = ItemStack.EMPTY;
        return out;
    }

    public BlockPos getBeamTarget() {
        return linkedAbsorber;
    }

    public void setLinkedAbsorber(@Nullable BlockPos target) {
        this.linkedAbsorber = target;
    }

    public boolean shouldRenderBeam() {
        return hasMirror() && linkedAbsorber != null;
    }

    @Override
    public IManagedStorage getSyncStorage() {
        return syncStorage;
    }
}
