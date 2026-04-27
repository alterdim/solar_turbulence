package li.gerard.solarturbulence.block.solarabsorber;

import li.gerard.solarturbulence.block.ModBlockEntities;
import li.gerard.solarturbulence.block.ModBlocks;
import li.gerard.solarturbulence.generic.MetallicFrameBlock;
import li.gerard.solarturbulence.generic.MultiblockControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

import static li.gerard.solarturbulence.block.solarabsorber.SolarAbsorberBlock.ASSEMBLED;

public class SolarAbsorberBlockEntity extends MultiblockControllerBlockEntity implements GeoBlockEntity {

    AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    public final EnergyStorage energyStorage = new EnergyStorage(100000, 1000, 1000, 1000);

    private static final List<BlockPos> FILLER_OFFSETS = List.of(
            new BlockPos(1, 1, 0),
            new BlockPos(-1, 1, 0),
            new BlockPos(0, 1, 1),
            new BlockPos(0, 1, -1),
            new BlockPos(0, 1, 0)
    );

    private static final List<BlockPos> PANELS = List.of(
            new BlockPos(1, 1, 0),
            new BlockPos(-1, 1, 0),
            new BlockPos(0, 1, 1),
            new BlockPos(0, 1, -1)
    );

    public SolarAbsorberBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.SOLAR_ABSORBER_BLOCK_ENTITY.get(), pos, blockState);
    }

    public BlockPos getPanel(Direction mirrorDirection) {
        return switch (mirrorDirection) {
            case EAST -> getBlockPos().above().west();
            case WEST -> getBlockPos().above().east();
            case NORTH -> getBlockPos().above().south();
            case SOUTH -> getBlockPos().above().north();
            default -> getBlockPos();
        };

    }

    public static void tick(Level level, BlockPos pos, BlockState state, SolarAbsorberBlockEntity be) {
        MultiblockControllerBlockEntity.tick(level, pos, state, be);
    }

    @Override
    protected void onTick(Level level, BlockPos pos, BlockState state) {
        if (!state.getValue(ASSEMBLED)) return;

        if (level.isDay()) {
            energyStorage.receiveEnergy(100, false);
            setChanged();
        }

        for (Direction direction : Direction.values()) {
            if (energyStorage.getEnergyStored() == 0) break;
            IEnergyStorage neighbor = level.getCapability(
                    Capabilities.EnergyStorage.BLOCK, pos.relative(direction), direction.getOpposite());
            if (neighbor == null || !neighbor.canReceive()) continue;
            int toSend = energyStorage.extractEnergy(1000, true);
            int accepted = neighbor.receiveEnergy(toSend, true);
            if (accepted > 0) {
                energyStorage.extractEnergy(accepted, false);
                neighbor.receiveEnergy(accepted, false);
                setChanged();
            }
        }
    }

    @Override
    public List<Component> getHudLines() {
        return List.of(Component.literal(getEnergy() + " / " + getMaxEnergy() + " FE"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    public boolean isMultiFormed() {
        return getBlockState().getValue(ASSEMBLED);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    public void tryAssemble() {
        if (isStructureValid()) {
            assemble();
        }
    }

    private boolean isStructureValid() {
        for (BlockPos offset : FILLER_OFFSETS) {
            BlockPos target = worldPosition.offset(offset);
            BlockState state = level.getBlockState(target);

            if (!state.is(ModBlocks.METALLIC_FRAME_BLOCK.get()) || state.getValue(MetallicFrameBlock.ASSEMBLED)) {
                return false;
            }
        }
        return true;
    }

    private void assemble() {
        for (BlockPos offset : FILLER_OFFSETS) {
            BlockPos target = worldPosition.offset(offset);
            level.setBlock(target, level.getBlockState(target)
                    .setValue(MetallicFrameBlock.ASSEMBLED, true), 3);
        }
        level.setBlock(worldPosition, getBlockState()
                .setValue(SolarAbsorberBlock.ASSEMBLED, true), 3);
    }

    public void disassemble() {
        for (BlockPos offset : FILLER_OFFSETS) {
            BlockPos target = worldPosition.offset(offset);
            BlockState state = level.getBlockState(target);

            if (state.is(ModBlocks.METALLIC_FRAME_BLOCK.get())) {
                level.setBlock(target, state.setValue(MetallicFrameBlock.ASSEMBLED, false), 3);
            }
        }
        level.setBlock(worldPosition, getBlockState()
                .setValue(SolarAbsorberBlock.ASSEMBLED, false), 3);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("energy", energyStorage.serializeNBT(registries));
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("energy")) {
            energyStorage.deserializeNBT(registries, tag.get("energy"));
        }
    }

    public int getEnergy() { return energyStorage.getEnergyStored(); }
    public int getMaxEnergy() { return energyStorage.getMaxEnergyStored(); }
}
