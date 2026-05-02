package li.gerard.solarturbulence.block.raysplitter;

import com.lowdragmc.lowdraglib2.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib2.syncdata.holder.blockentity.ISyncPersistRPCBlockEntity;
import com.lowdragmc.lowdraglib2.syncdata.storage.FieldManagedStorage;
import com.lowdragmc.lowdraglib2.syncdata.storage.IManagedStorage;
import li.gerard.solarturbulence.block.ModBlockEntities;
import li.gerard.solarturbulence.block.generic.IRayReflector;
import li.gerard.solarturbulence.block.raysplitter.RaySplitterBlock.SplitAxis;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class RaySplitterBlockEntity extends BlockEntity implements IRayReflector, ISyncPersistRPCBlockEntity {

    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    // Two outgoing ray output slots — assigned on the first tick that hits this splitter.
    // -1 = slot is free / inactive.
    @DescSynced private int outDir0Ordinal = -1;
    @DescSynced private int outBeam0 = 0;
    @DescSynced private int outPointer0 = 0;

    @DescSynced private int outDir1Ordinal = -1;
    @DescSynced private int outBeam1 = 0;
    @DescSynced private int outPointer1 = 0;

    public RaySplitterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RAY_SPLITTER_BLOCK_ENTITY.get(), pos, state);
    }

    // -------------------------------------------------------------------------
    // IRayReflector
    // -------------------------------------------------------------------------

    @Override
    public boolean canReflect() { return true; }

    @Override
    public Direction reflect(Direction incoming) {
        List<RayOutput> outputs = getOutputs(incoming);
        return outputs.isEmpty() ? incoming : outputs.get(0).dir();
    }

    @Override
    public List<RayOutput> getOutputs(Direction incoming) {
        SplitAxis axis = getBlockState().getValue(RaySplitterBlock.SPLIT_AXIS);
        Direction[] dirs = getSplitDirs(incoming, axis);
        float half = getHeatMultiplier();
        return List.of(new RayOutput(dirs[0], half), new RayOutput(dirs[1], half));
    }

    private Direction[] getSplitDirs(Direction incoming, SplitAxis axis) {
        if (axis == SplitAxis.UP_DOWN) {
            return new Direction[]{ Direction.UP, Direction.DOWN };
        }
        // LEFT_RIGHT: two directions perpendicular to incoming in the horizontal plane.
        // For vertical rays, fall back to the block's FACING as the horizontal reference.
        Direction ref = incoming.getAxis() == Direction.Axis.Y
                ? horizontalFacing()
                : incoming;
        return new Direction[]{ ref.getClockWise(), ref.getCounterClockWise() };
    }

    /** Returns the block's FACING, guaranteed to be horizontal. Falls back to NORTH if FACING is vertical. */
    private Direction horizontalFacing() {
        Direction facing = getBlockState().getValue(RaySplitterBlock.FACING);
        return facing.getAxis() == Direction.Axis.Y ? Direction.NORTH : facing;
    }

    @Override
    public float getHeatMultiplier() { return 0.5f; }

    @Override
    public int[] getBeamColors() {
        return new int[]{ 0xFFCCFFCC, 0xFF44FF88 };
    }

    @Override
    public void updateRayOutput(Direction outDir, int beamLen, int pointerLen, float power) {
        int ordinal = outDir == null ? -1 : outDir.ordinal();

        if (outDir0Ordinal == ordinal) {
            if (outBeam0 == beamLen && outPointer0 == pointerLen) return;
            outBeam0 = beamLen;
            outPointer0 = pointerLen;
        } else if (outDir1Ordinal == ordinal) {
            if (outBeam1 == beamLen && outPointer1 == pointerLen) return;
            outBeam1 = beamLen;
            outPointer1 = pointerLen;
        } else if (outDir0Ordinal == -1) {
            outDir0Ordinal = ordinal;
            outBeam0 = beamLen;
            outPointer0 = pointerLen;
        } else if (outDir1Ordinal == -1) {
            outDir1Ordinal = ordinal;
            outBeam1 = beamLen;
            outPointer1 = pointerLen;
        } else {
            // Both slots occupied with stale directions (e.g. after right-click changed split axis).
            // Evict slot 0 so this direction claims it; the next call will claim slot 1.
            outDir0Ordinal = ordinal;
            outBeam0 = beamLen;
            outPointer0 = pointerLen;
            outDir1Ordinal = -1;
            outBeam1 = 0;
            outPointer1 = 0;
        }

        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        setChanged();
    }

    @Override
    public void clearRayOutput() {
        boolean changed = outBeam0 != 0 || outPointer0 != 0 || outBeam1 != 0 || outPointer1 != 0;
        outDir0Ordinal = -1; outBeam0 = 0; outPointer0 = 0;
        outDir1Ordinal = -1; outBeam1 = 0; outPointer1 = 0;
        if (changed) {
            if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            setChanged();
        }
    }

    // -------------------------------------------------------------------------
    // Renderer helpers
    // -------------------------------------------------------------------------

    @Nullable
    public Direction getOutDir0() { return outDir0Ordinal < 0 ? null : Direction.values()[outDir0Ordinal]; }

    @Nullable
    public Direction getOutDir1() { return outDir1Ordinal < 0 ? null : Direction.values()[outDir1Ordinal]; }

    public int getOutBeam0() { return outBeam0; }
    public int getOutPointer0() { return outPointer0; }
    public int getOutBeam1() { return outBeam1; }
    public int getOutPointer1() { return outPointer1; }

    public boolean hasActiveRayOutput() {
        return (outDir0Ordinal >= 0 && (outBeam0 > 0 || outPointer0 > 0))
                || (outDir1Ordinal >= 0 && (outBeam1 > 0 || outPointer1 > 0));
    }

    @Override
    public IManagedStorage getSyncStorage() { return syncStorage; }
}
