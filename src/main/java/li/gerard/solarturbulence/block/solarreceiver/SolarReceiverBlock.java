package li.gerard.solarturbulence.block.solarreceiver;

import com.lowdragmc.lowdraglib2.LDLib2;
import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.IDataProvider;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.SupplierDataSource;
import com.lowdragmc.lowdraglib2.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.FillDirection;
import com.lowdragmc.lowdraglib2.gui.ui.elements.FluidSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ProgressBar;
import com.lowdragmc.lowdraglib2.gui.ui.elements.inventory.InventorySlots;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import com.lowdragmc.lowdraglib2.gui.ui.style.values.FloatValue;
import com.lowdragmc.lowdraglib2.utils.XmlUtils;
import li.gerard.solarturbulence.SolarTurbulenceMod;
import li.gerard.solarturbulence.capability.fluid.MonoFluidTank;
import li.gerard.solarturbulence.capability.heat.HeatStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.appliedenergistics.yoga.YogaFlexDirection;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public class SolarReceiverBlock extends Block implements EntityBlock, BlockUIMenuType.BlockUI {

    public static IGuiTexture MAGMA_BAR = SpriteTexture.of(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/magma.png"));

    public SolarReceiverBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SolarReceiverBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Block.box(0, 0, 0, 16, 32, 16);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {

        if (!level.isClientSide) {
            BlockUIMenuType.openUI((ServerPlayer) player, pos);
            if (level.getBlockEntity(pos) instanceof  SolarReceiverBlockEntity be) {
                be.getHeatStorage().receiveHeat(1000, false);
                Logger.getAnonymousLogger().log(java.util.logging.Level.INFO, "ADDED HEAT, SERVER total is " + be.getHeatStorage().getHeatStored());
            }
        }
        if (level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof  SolarReceiverBlockEntity be) {
                Logger.getAnonymousLogger().log(java.util.logging.Level.INFO, "ADDED HEAT, CLIENT total is " + be.getHeatStorage().getHeatStored());
            }
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {

        if (!(holder.player.level().getBlockEntity(holder.pos) instanceof SolarReceiverBlockEntity be)) {
            return ModularUI.of(UI.of(new UIElement()), holder.player);
        }

        HeatStorage heat = be.getHeatStorage();
        final int maxHeat = heat.getHeatCapacity();

        MonoFluidTank tank = be.tank;
        final int maxTank = tank.getCapacity();

        var xml = XmlUtils.loadXml(ResourceLocation.fromNamespaceAndPath(SolarTurbulenceMod.MODID, "xml/testhud.xml"));
        if (xml != null) {
            var ui = UI.of(xml);
            Label heat_label = (Label) ui.select("label").toList().get(1);
            heat_label.bind(DataBindingBuilder.componentS2C(() ->
                    Component.literal("Burn time: " + heat.getHeatStored())
            ).build());

            var progress_bars = ui.select("progress-bar").toList();
            ProgressBar heatBar = (ProgressBar) progress_bars.get(0);
            heatBar.setRange(0, 100);
            heatBar.bind(DataBindingBuilder.floatValS2C(
                    () -> (heat.getHeatStored() / (float) heat.getHeatCapacity()) * 100
            ).build());

            var fluidslots = ui.select("fluid-slot").toList();
            FluidSlot fluidSlot = (FluidSlot) fluidslots.get(0);
            fluidSlot.bind(tank, 0);
            fluidSlot.setCapacity(maxTank);

            return ModularUI.of(
                    ui,
                    holder.player
            );
        }
        return null;
    }
}
