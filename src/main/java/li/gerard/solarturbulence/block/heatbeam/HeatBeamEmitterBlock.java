package li.gerard.solarturbulence.block.heatbeam;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.FluidSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ProgressBar;
import com.lowdragmc.lowdraglib2.utils.XmlUtils;
import li.gerard.solarturbulence.SolarTurbulenceMod;
import li.gerard.solarturbulence.block.ModBlockEntities;
import li.gerard.solarturbulence.block.solarreceiver.SolarReceiverBlockEntity;
import li.gerard.solarturbulence.capability.fluid.MonoFluidTank;
import li.gerard.solarturbulence.capability.heat.HeatStorage;
import li.gerard.solarturbulence.generic.RayEmittingBlock;
import li.gerard.solarturbulence.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class HeatBeamEmitterBlock extends RayEmittingBlock implements BlockUIMenuType.BlockUI {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public HeatBeamEmitterBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                              BlockPos pos, Player player, InteractionHand hand,
                                              BlockHitResult hit) {
        if (stack.is(ModItems.WAND.get())) {
            if (!level.isClientSide) {
                Direction current = state.getValue(FACING);
                Direction next = Direction.values()[(current.ordinal() + 1) % 6];
                level.setBlockAndUpdate(pos, state.setValue(FACING, next));
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HeatBeamEmitterBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {

        if (!level.isClientSide) {
            BlockUIMenuType.openUI((ServerPlayer) player, pos);
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {

        if (!(holder.player.level().getBlockEntity(holder.pos) instanceof HeatBeamEmitterBlockEntity be)) {
            return ModularUI.of(UI.of(new UIElement()), holder.player);
        }

        HeatStorage heat = be.getHeatStorage();

        var xml = XmlUtils.loadXml(ResourceLocation.fromNamespaceAndPath(SolarTurbulenceMod.MODID, "xml/testhud.xml"));
        if (xml != null) {
            var ui = UI.of(xml);
            Label heat_label = (Label) ui.select("label").toList().get(1);
            heat_label.bind(DataBindingBuilder.componentS2C(() ->
                    Component.literal("Heat :  " + heat.getHeatStored())
            ).build());

            var progress_bars = ui.select("progress-bar").toList();
            ProgressBar heatBar = (ProgressBar) progress_bars.get(0);
            heatBar.setRange(0, 100);
            heatBar.bind(DataBindingBuilder.floatValS2C(
                    () -> (heat.getHeatStored() / (float) heat.getHeatCapacity()) * 100
            ).build());

            return ModularUI.of(
                    ui,
                    holder.player
            );
        }
        return null;
    }
}
