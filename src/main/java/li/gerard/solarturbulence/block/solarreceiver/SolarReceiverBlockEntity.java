package li.gerard.solarturbulence.block.solarreceiver;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.inventory.InventorySlots;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import com.lowdragmc.lowdraglib2.misc.FluidStorage;
import com.lowdragmc.lowdraglib2.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib2.syncdata.holder.blockentity.ISyncPersistRPCBlockEntity;
import com.lowdragmc.lowdraglib2.syncdata.storage.FieldManagedStorage;
import com.lowdragmc.lowdraglib2.syncdata.storage.IManagedStorage;
import li.gerard.solarturbulence.block.ModBlockEntities;
import li.gerard.solarturbulence.capability.fluid.MonoFluidTank;
import li.gerard.solarturbulence.capability.heat.HeatStorage;
import li.gerard.solarturbulence.property.FluidPropertyHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SolarReceiverBlockEntity extends BlockEntity implements GeoBlockEntity, ISyncPersistRPCBlockEntity {

    AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    @Persisted @DescSynced
    private final HeatStorage heatStorage = new HeatStorage(100000, 100000, 1000);

    @Persisted
    private final EnergyStorage energyStorage = new EnergyStorage(100000, 1000, 1000);

    @Persisted
    public final MonoFluidTank tank = new MonoFluidTank(10000, 1000, 0).setValidator(this::isFluidAllowed);

    public SolarReceiverBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.SOLAR_RECEIVER_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }



    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    @Override
    public IManagedStorage getSyncStorage() {
        return syncStorage;
    }

    public HeatStorage getHeatStorage() {
        return heatStorage;
    }

    public EnergyStorage getEnergyStorage() {return energyStorage;}

    private boolean isFluidAllowed(FluidStack stack) {
        if (stack.isEmpty() || level == null) return false;
        return FluidPropertyHelper.get(level.registryAccess(), stack.getFluid()).isPresent();
    }


}
