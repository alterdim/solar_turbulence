package li.gerard.solarturbulence.block.blazethermalstorage;

import li.gerard.solarturbulence.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BlazeThermalStorageBlockEntity extends BlockEntity implements GeoBlockEntity {

    AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

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
}
