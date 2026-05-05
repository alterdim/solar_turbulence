package li.gerard.solarturbulence.block.generic;

import com.lowdragmc.lowdraglib2.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib2.syncdata.holder.blockentity.ISyncPersistRPCBlockEntity;
import com.lowdragmc.lowdraglib2.syncdata.storage.FieldManagedStorage;
import com.lowdragmc.lowdraglib2.syncdata.storage.IManagedStorage;
import li.gerard.solarturbulence.capability.heat.HeatStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class HeatStorageBlockEntity extends BlockEntity implements ISyncPersistRPCBlockEntity {

    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    @Persisted
    @DescSynced
    protected final HeatStorage heatStorage;

    public HeatStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState,
                                  int capacity, int maxReceive, int maxExtract) {
        super(type, pos, blockState);
        heatStorage  =  new HeatStorage(capacity, maxReceive, maxExtract);
    }

    @Override
    public IManagedStorage getSyncStorage() {
        return syncStorage;
    }

    public HeatStorage getHeatStorage() {return heatStorage;}
}
