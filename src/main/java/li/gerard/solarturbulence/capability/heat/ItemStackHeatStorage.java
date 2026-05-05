package li.gerard.solarturbulence.capability.heat;

import li.gerard.solarturbulence.data.ModDataComponents;
import net.minecraft.world.item.ItemStack;

public class ItemStackHeatStorage implements IHeatStorage {
    private final ItemStack stack;
    private final int capacity;
    private final int maxReceive;
    private final int maxExtract;

    public ItemStackHeatStorage(ItemStack stack, int capacity, int maxReceive, int maxExtract) {
        this.stack = stack;
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    @Override
    public int receiveHeat(int maxReceive, boolean simulate) {
        int current = stack.getOrDefault(ModDataComponents.HEAT, 0);
        int accepted = Math.min(capacity - current, Math.min(this.maxReceive, maxReceive));
        if (!simulate) stack.set(ModDataComponents.HEAT, current + accepted);
        return accepted;
    }

    @Override
    public int extractHeat(int maxExtract, boolean simulate) {
        int current = stack.getOrDefault(ModDataComponents.HEAT, 0);
        int extracted = Math.min(current, Math.min(this.maxExtract, maxExtract));
        if (!simulate) stack.set(ModDataComponents.HEAT, current - extracted);
        return extracted;
    }

    @Override
    public int getHeatStored() { return stack.getOrDefault(ModDataComponents.HEAT, 0); }

    @Override
    public int getHeatCapacity() { return capacity; }

    @Override
    public boolean canReceiveHeat() { return maxReceive > 0; }

    @Override
    public boolean canExtractHeat() { return maxExtract > 0; }
}