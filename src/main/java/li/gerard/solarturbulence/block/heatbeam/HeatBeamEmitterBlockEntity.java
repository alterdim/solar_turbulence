package li.gerard.solarturbulence.block.heatbeam;

import com.lowdragmc.lowdraglib2.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib2.syncdata.holder.blockentity.ISyncPersistRPCBlockEntity;
import com.lowdragmc.lowdraglib2.syncdata.storage.FieldManagedStorage;
import com.lowdragmc.lowdraglib2.syncdata.storage.IManagedStorage;
import li.gerard.solarturbulence.block.ModBlockEntities;
import li.gerard.solarturbulence.capability.ModCapabilities;
import li.gerard.solarturbulence.capability.heat.HeatStorage;
import li.gerard.solarturbulence.capability.heat.IHeatStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public class HeatBeamEmitterBlockEntity extends BlockEntity implements ISyncPersistRPCBlockEntity {

    public static final int MAX_BEAM_RANGE = 16;
    private static final int HEAT_PER_TICK = 50;

    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    @DescSynced
    @Persisted
    private final HeatStorage heatStorage = new HeatStorage(10000, 1000, 1000);

    private int beamLength = 0;

    public HeatBeamEmitterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.HEAT_BEAM_EMITTER_BLOCK_ENTITY.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HeatBeamEmitterBlockEntity be) {
        // Pull heat from adjacent blocks
        for (Direction side : Direction.values()) {
            if (be.heatStorage.getHeatStored() >= be.heatStorage.getHeatCapacity()) break;
            IHeatStorage source = level.getCapability(ModCapabilities.HEAT, pos.relative(side), side.getOpposite());
            if (source != null && source.canExtractHeat()) {
                int canReceive = be.heatStorage.receiveHeat(HEAT_PER_TICK, true);
                int pulled = source.extractHeat(canReceive, true);
                if (pulled > 0) {
                    source.extractHeat(pulled, false);
                    be.heatStorage.receiveHeat(pulled, false);
                }
            }
        }

        // Emit beam in FACING direction, transfer heat to first IHeatStorage in range
        Direction facing = state.getValue(HeatBeamEmitterBlock.FACING);
        int newBeamLength = 0;

        be.heatStorage.receiveHeat(1000, false);

        if (be.heatStorage.getHeatStored() > 0) {
            for (int dist = 1; dist <= MAX_BEAM_RANGE; dist++) {
                BlockPos target = pos.relative(facing, dist);
                BlockState targetState = level.getBlockState(target);

                IHeatStorage targetHeat = level.getCapability(ModCapabilities.HEAT, target, facing.getOpposite());
                if (targetHeat != null && targetHeat.canReceiveHeat()) {
                    int toGive = be.heatStorage.extractHeat(HEAT_PER_TICK, true);
                    int accepted = targetHeat.receiveHeat(toGive, true);
                    if (accepted > 0) {
                        be.heatStorage.extractHeat(accepted, false);
                        targetHeat.receiveHeat(accepted, false);
                        newBeamLength = dist;
                    }
                    break;
                }

                // Beam stops at non-air blocks that cannot receive heat
                if (!targetState.isAir()) break;
            }
        }

        if (newBeamLength != be.beamLength) {
            be.beamLength = newBeamLength;
            level.sendBlockUpdated(pos, state, state, 3);
            be.setChanged();
        }
    }

    public int getBeamLength() {
        return beamLength;
    }

    public HeatStorage getHeatStorage() {
        return heatStorage;
    }

    @Override
    public IManagedStorage getSyncStorage() {
        return syncStorage;
    }
}
