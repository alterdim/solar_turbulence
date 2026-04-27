package li.gerard.solarturbulence.capability.fluid;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class MonoFluidTank implements IFluidHandler, INBTSerializable<CompoundTag> {

    private final int capacity;
    private final int maxFill;
    private final int maxDrain;
    private Predicate<FluidStack> validator = stack -> true;
    private Runnable onContentsChanged = () -> {};

    private FluidStack fluid = FluidStack.EMPTY;

    public MonoFluidTank(int capacity, int maxFill, int maxDrain, Predicate<FluidStack> validator) {
        this.capacity = capacity;
        this.maxFill = maxFill;
        this.maxDrain = maxDrain;
        this.validator = validator;
    }

    public MonoFluidTank(int capacity, int maxFill, int maxDrain) {
        this.capacity = capacity;
        this.maxFill = maxFill;
        this.maxDrain = maxDrain;
    }

    public MonoFluidTank(int capacity) {
        this(capacity, capacity, capacity);
    }

    // --- configuration ---

    public MonoFluidTank setValidator(Predicate<FluidStack> validator) {
        this.validator = validator == null ? stack -> true : validator;
        return this;
    }

    public MonoFluidTank setOnContentsChanged(Runnable callback) {
        this.onContentsChanged = callback == null ? () -> {} : callback;
        return this;
    }

    // --- accessors ---

    public FluidStack getFluid() {
        return fluid;
    }

    public int getFluidAmount() {
        return fluid.getAmount();
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isEmpty() {
        return fluid.isEmpty();
    }

    public boolean isFull() {
        return fluid.getAmount() >= capacity;
    }

    public float getFillRatio() {
        return capacity == 0 ? 0f : (float) fluid.getAmount() / capacity;
    }

    /**
     * Directly sets the contents of the tank. Bypasses validation and fill/drain limits.
     * Intended for sync, loading, and admin/debug use.
     */
    public void setFluid(FluidStack stack) {
        this.fluid = stack.copy();
        if (this.fluid.getAmount() > capacity) {
            this.fluid.setAmount(capacity);
        }
        onContentsChanged.run();
    }

    // --- IFluidHandler ---

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return fluid;
    }

    @Override
    public int getTankCapacity(int tank) {
        return capacity;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return validator.test(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !validator.test(resource)) {
            return 0;
        }

        // Mono-fluid: must be empty or matching
        if (!fluid.isEmpty() && !FluidStack.isSameFluidSameComponents(fluid, resource)) {
            return 0;
        }

        int spaceLeft = capacity - fluid.getAmount();
        int filled = Math.min(Math.min(resource.getAmount(), maxFill), spaceLeft);
        if (filled <= 0) {
            return 0;
        }

        if (action.execute()) {
            if (fluid.isEmpty()) {
                fluid = resource.copyWithAmount(filled);
            } else {
                fluid.grow(filled);
            }
            onContentsChanged.run();
        }
        return filled;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || fluid.isEmpty() || !FluidStack.isSameFluidSameComponents(fluid, resource)) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Override
    public @NotNull FluidStack drain(int maxAmount, FluidAction action) {
        if (maxAmount <= 0 || fluid.isEmpty()) {
            return FluidStack.EMPTY;
        }

        int drained = Math.min(Math.min(maxAmount, maxDrain), fluid.getAmount());
        if (drained <= 0) {
            return FluidStack.EMPTY;
        }

        FluidStack result = fluid.copyWithAmount(drained);
        if (action.execute()) {
            fluid.shrink(drained);
            if (fluid.isEmpty()) {
                fluid = FluidStack.EMPTY;
            }
            onContentsChanged.run();
        }
        return result;
    }

    // --- INBTSerializable ---

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        if (!fluid.isEmpty()) {
            tag.put("Fluid", fluid.save(provider));
        }
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        if (nbt.contains("Fluid")) {
            this.fluid = FluidStack.parseOptional(provider, nbt.getCompound("Fluid"));
        } else {
            this.fluid = FluidStack.EMPTY;
        }
    }
}