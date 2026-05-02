package li.gerard.solarturbulence.block.solarmirror;

import com.lowdragmc.lowdraglib2.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib2.syncdata.holder.blockentity.ISyncPersistRPCBlockEntity;
import com.lowdragmc.lowdraglib2.syncdata.storage.FieldManagedStorage;
import com.lowdragmc.lowdraglib2.syncdata.storage.IManagedStorage;
import li.gerard.solarturbulence.block.ModBlockEntities;
import li.gerard.solarturbulence.block.generic.IRayReflector;
import li.gerard.solarturbulence.item.mirror.MirrorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class MirrorFrameBlockEntity extends BlockEntity implements GeoBlockEntity, ISyncPersistRPCBlockEntity, IRayReflector {

    AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    @Persisted @DescSynced
    private ItemStack mirrorStack = ItemStack.EMPTY;

    @Persisted @DescSynced
    @Nullable private BlockPos linkedAbsorber;

    // Outgoing ray segment driven by the upstream RayEmittingBlockEntity each tick.
    // -1 = inactive (no ray hitting this frame).
    @DescSynced
    private int outDirectionOrdinal = -1;

    @DescSynced
    private int outBeamLength = 0;

    @DescSynced
    private int outPointerLength = 0;

    private float outPower = 0f;

    public MirrorFrameBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MIRROR_FRAME_BLOCK_ENTITY.get(), pos, blockState);
    }

    // -------------------------------------------------------------------------
    // GeckoLib
    // -------------------------------------------------------------------------

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    // -------------------------------------------------------------------------
    // Mirror item access
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Legacy linker support (kept for data compatibility)
    // -------------------------------------------------------------------------

    public BlockPos getBeamTarget() {
        return linkedAbsorber;
    }

    public void setLinkedAbsorber(@Nullable BlockPos target) {
        this.linkedAbsorber = target;
    }

    // -------------------------------------------------------------------------
    // IRayReflector
    // -------------------------------------------------------------------------

    @Override
    public boolean canReflect() {
        return hasMirror();
    }

    @Override
    public Direction reflect(Direction incoming) {
        if (!hasMirror()) return incoming;
        Direction mirrorFacing = getBlockState().getValue(MirrorFrameBlock.FACING);
        return ((MirrorItem) mirrorStack.getItem()).reflect(incoming, mirrorFacing);
    }

    @Override
    public float getHeatMultiplier() {
        if (!hasMirror()) return 1.0f;
        return ((MirrorItem) mirrorStack.getItem()).getHeatMultiplier();
    }

    @Override
    public int[] getBeamColors() {
        if (!hasMirror()) return new int[]{0xFFFFFFAA};
        return ((MirrorItem) mirrorStack.getItem()).getBeamColors();
    }

    @Override
    public void updateRayOutput(Direction outDir, int beamLen, int pointerLen, float power) {
        int newOrdinal = outDir == null ? -1 : outDir.ordinal();
        if (newOrdinal == outDirectionOrdinal && beamLen == outBeamLength && pointerLen == outPointerLength && power == outPower) return;
        outDirectionOrdinal = newOrdinal;
        outBeamLength = beamLen;
        outPointerLength = pointerLen;
        outPower = power;
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        setChanged();
    }

    @Override
    public void clearRayOutput() {
        updateRayOutput(null, 0, 0, 0f);
    }

    // -------------------------------------------------------------------------
    // Renderer helpers
    // -------------------------------------------------------------------------

    @Nullable
    public Direction getOutDirection() {
        return outDirectionOrdinal < 0 ? null : Direction.values()[outDirectionOrdinal];
    }

    public int getOutBeamLength() { return outBeamLength; }

    public int getOutPointerLength() { return outPointerLength; }

    public boolean hasActiveRayOutput() {
        return outDirectionOrdinal >= 0 && (outBeamLength > 0 || outPointerLength > 0);
    }

    // -------------------------------------------------------------------------
    // LDLib2 sync
    // -------------------------------------------------------------------------

    @Override
    public IManagedStorage getSyncStorage() {
        return syncStorage;
    }
}
