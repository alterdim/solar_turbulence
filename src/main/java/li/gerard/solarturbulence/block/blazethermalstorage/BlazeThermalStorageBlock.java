package li.gerard.solarturbulence.block.blazethermalstorage;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.FluidSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ProgressBar;
import com.lowdragmc.lowdraglib2.gui.ui.elements.inventory.InventorySlots;
import com.lowdragmc.lowdraglib2.utils.XmlUtils;
import li.gerard.solarturbulence.SolarTurbulenceMod;
import li.gerard.solarturbulence.block.solarreceiver.SolarReceiverBlockEntity;
import li.gerard.solarturbulence.capability.fluid.MonoFluidTank;
import li.gerard.solarturbulence.capability.heat.HeatStorage;
import li.gerard.solarturbulence.capability.heat.IHeatStorage;
import li.gerard.solarturbulence.capability.heat.ItemStackHeatStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public class BlazeThermalStorageBlock extends Block implements EntityBlock, BlockUIMenuType.BlockUI {
    public BlazeThermalStorageBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new  BlazeThermalStorageBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
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

        if (!(holder.player.level().getBlockEntity(holder.pos) instanceof BlazeThermalStorageBlockEntity be)) {
            return ModularUI.of(UI.of(new UIElement()), holder.player);
        }



        ItemStackHandler itemStackHandler = be.getItemStackHandler();


        var xml = XmlUtils.loadXml(ResourceLocation.fromNamespaceAndPath(SolarTurbulenceMod.MODID, "xml/blaze_thermal_storage.xml"));
        if (xml != null) {
            var ui = UI.of(xml);

            var slots = ui.select("item-slot").toList();
            ItemSlot slot = (ItemSlot) slots.get(0);
            slot.bind(itemStackHandler, 0);


            var progress_bars = ui.select("progress-bar").toList();
            ProgressBar heatBar = (ProgressBar) progress_bars.get(0);
            heatBar.setRange(0, 100);

            heatBar.bind(DataBindingBuilder.floatValS2C(
                    () -> {
                        IHeatStorage heat = be.getHeatStorage();
                        if (heat == null) return 0f;
                        return (heat.getHeatStored() / (float) heat.getHeatCapacity()) * 100;
                    }
            ).build());

            return ModularUI.of(
                    ui,
                    holder.player
            );
        }
        return null;
    }
}
