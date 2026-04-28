package li.gerard.solarturbulence.block.solarreceiver;

import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import li.gerard.solarturbulence.block.ModBlockEntities;
import li.gerard.solarturbulence.capability.fluid.MonoFluidTank;
import li.gerard.solarturbulence.capability.heat.HeatStorage;
import li.gerard.solarturbulence.generic.RayEmittingBlockEntity;
import li.gerard.solarturbulence.property.FluidPropertyHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SolarReceiverBlockEntity extends RayEmittingBlockEntity implements GeoBlockEntity {

    AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);



    @Persisted
    private final EnergyStorage energyStorage = new EnergyStorage(100000, 1000, 10);

    @Persisted
    public final MonoFluidTank tank = new MonoFluidTank(10000, 1000, 0).setValidator(this::isFluidAllowed);

    public SolarReceiverBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.SOLAR_RECEIVER_BLOCK_ENTITY.get(), pos, blockState, 100000, 100, 10);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    @Override
    public Direction getEmitDirection() {
        return Direction.DOWN;
    }

    @Override
    protected int getMaxRange() {
        return 16;
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
