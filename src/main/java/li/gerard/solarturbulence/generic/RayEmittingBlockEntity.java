package li.gerard.solarturbulence.generic;

import com.lowdragmc.lowdraglib2.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib2.syncdata.holder.blockentity.ISyncPersistRPCBlockEntity;
import com.lowdragmc.lowdraglib2.syncdata.storage.FieldManagedStorage;
import com.lowdragmc.lowdraglib2.syncdata.storage.IManagedStorage;
import li.gerard.solarturbulence.capability.ModCapabilities;
import li.gerard.solarturbulence.capability.heat.HeatStorage;
import li.gerard.solarturbulence.capability.heat.IHeatStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class RayEmittingBlockEntity extends BlockEntity implements ISyncPersistRPCBlockEntity {

    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    @Persisted
    @DescSynced
    protected final HeatStorage heatStorage;

    @DescSynced
    private int extractSpeed;

    @DescSynced
    private int beamLength = 0;

    @DescSynced
    private int pointerLength = 0;

    protected RayEmittingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, int heatCapacity, int heatMaxReceive, int heatMaxExtract) {
        super(type, pos, blockState);
        heatStorage = new HeatStorage(heatCapacity, heatMaxReceive, heatMaxExtract);
        extractSpeed = heatMaxExtract;
    }

    /** Direction the ray fires in. */
    public abstract Direction getEmitDirection();

    /** Maximum range in blocks. */
    protected abstract int getMaxRange();

    /**
     * Returns true if the block at {@code pos} is a valid target for this ray.
     * The ray always stops here regardless of the return value.
     */
    protected boolean isValidTarget(Level level, BlockPos pos, Direction incomingDir) {
        IHeatStorage target = level.getCapability(ModCapabilities.HEAT, pos, incomingDir.getOpposite());
        return target != null && target.canReceiveHeat();
    }

    /**
     * Called each tick when the ray reaches a valid target.
     * Returns true if emission actually occurred (beam renders), false if only pointing (pointer renders).
     */

    protected boolean onHitTarget(Level level, BlockPos targetPos, Direction incomingDir) {
        if (heatStorage.getHeatStored() <= 0) return false;
        IHeatStorage target = level.getCapability(ModCapabilities.HEAT, targetPos, incomingDir.getOpposite());
        if (target == null) return false;
        int toGive = heatStorage.extractHeat(extractSpeed ,true);
        int accepted = target.receiveHeat(toGive, true);
        if (accepted > 0) {
            heatStorage.extractHeat(accepted, false);
            target.receiveHeat(accepted, false);
            return true;
        }
        return false;
    }

    /**
     * Called once per tick before the ray scan. Override to pull energy, update state, etc.
     */
    protected void preTick(Level level, BlockPos pos, BlockState state) {}

    public static void tick(Level level, BlockPos pos, BlockState state, RayEmittingBlockEntity be) {
        if (level.isClientSide) return;
        be.preTick(level, pos, state);

        Direction facing = be.getEmitDirection();
        int newBeamLength = 0;
        int newPointerLength = 0;

        for (int dist = 1; dist <= be.getMaxRange(); dist++) {
            BlockPos target = pos.relative(facing, dist);
            BlockState targetState = level.getBlockState(target);
            newPointerLength = dist;

            if (be.isValidTarget(level, target, facing)) {
                if (be.onHitTarget(level, target, facing)) {
                    newBeamLength = dist;
                }
                break;
            }

            if (!targetState.isAir()) break;
        }

        if (newBeamLength != be.beamLength || newPointerLength != be.pointerLength) {
            be.beamLength = newBeamLength;
            be.pointerLength = newPointerLength;
            level.sendBlockUpdated(pos, state, state, 3);
            be.setChanged();
        }
    }

    public int getBeamLength() {
        return beamLength;
    }

    public int getPointerLength() {
        return pointerLength;
    }

    @Override
    public IManagedStorage getSyncStorage() {
        return syncStorage;
    }
}
