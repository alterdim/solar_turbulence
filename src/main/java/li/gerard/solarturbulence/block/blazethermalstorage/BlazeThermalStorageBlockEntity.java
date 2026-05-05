package li.gerard.solarturbulence.block.blazethermalstorage;

import com.lowdragmc.lowdraglib2.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib2.syncdata.holder.blockentity.ISyncPersistRPCBlockEntity;
import com.lowdragmc.lowdraglib2.syncdata.storage.FieldManagedStorage;
import com.lowdragmc.lowdraglib2.syncdata.storage.IManagedStorage;
import li.gerard.solarturbulence.block.ModBlockEntities;
import li.gerard.solarturbulence.block.generic.HeatStorageBlockEntity;
import li.gerard.solarturbulence.capability.ModCapabilities;
import li.gerard.solarturbulence.capability.heat.HeatStorage;
import li.gerard.solarturbulence.capability.heat.IHeatStorage;
import li.gerard.solarturbulence.item.heatrod.HeatRodItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class BlazeThermalStorageBlockEntity extends BlockEntity implements GeoBlockEntity {

    AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    @Persisted
    @DescSynced
    private final ItemStackHandler itemStackHandler = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getItem() instanceof HeatRodItem;
        }

        @Override
        protected void onContentsChanged(int slot) {
            invalidateCapabilities();
            setChanged();
        }
    };


    public BlazeThermalStorageBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLAZE_THERMAL_STORAGE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    public ItemStackHandler getItemStackHandler() {
        return itemStackHandler;
    }

    @Nullable
    public IHeatStorage getHeatStorage() {
        ItemStack stack = itemStackHandler.getStackInSlot(0);
        if (stack.isEmpty()) return null;
        return stack.getCapability(ModCapabilities.HEAT_ITEM);
    }

}
