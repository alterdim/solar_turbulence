package li.gerard.solarturbulence.capability.heat;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HeatStorage implements IHeatStorage, INBTSerializable<Tag> {

    protected int heat;
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    public HeatStorage(int capacity) {
        this(capacity, capacity, capacity, 0);
    }

    public HeatStorage(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public HeatStorage(int capacity, int maxReceive, int maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public HeatStorage(int capacity, int maxReceive, int maxExtract, int heat) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.heat = Math.clamp(heat, 0, capacity);
    }

    @Override
    public int receiveHeat(int maxReceive, boolean simulate) {
        if (!canReceiveHeat() || maxReceive <= 0) return 0;
        int accepted = Math.min(capacity - heat, Math.min(this.maxReceive, maxReceive));
        if (!simulate) heat += accepted;
        return accepted;
    }

    @Override
    public int extractHeat(int maxExtract, boolean simulate) {
        if (!canExtractHeat() || maxExtract <= 0) return 0;
        int extracted = Math.min(heat, Math.min(this.maxExtract, maxExtract));
        if (!simulate) heat -= extracted;
        return extracted;
    }

    /**
     * Apply passive heat loss.
     * Call once per tick from the owning BlockEntity.
     *
     * @param lossPerTick flat heat units lost per tick
     */
    public void applyPassiveLoss(int lossPerTick) {
        heat = Math.max(0, heat - lossPerTick);
    }

    @Override
    public int getHeatStored() { return heat; }

    @Override
    public int getHeatCapacity() { return capacity; }

    @Override
    public boolean canReceiveHeat() { return maxReceive > 0; }

    @Override
    public boolean canExtractHeat() { return maxExtract > 0; }

    public void setHeat(int heat) {
        this.heat = Math.max(0, Math.min(capacity, heat));
    }

    @Override
    public Tag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("heat", heat);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, Tag tag) {
        if (tag instanceof CompoundTag compound) {
            this.heat = Math.clamp(compound.getInt("heat"), 0, capacity);
        }
        else Logger.getAnonymousLogger().log(Level.WARNING, "[HeatStorage]: Invalid tag received!");
    }
}
