package li.gerard.solarturbulence.block.solarreceiver;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.SupplierDataSource;
import com.lowdragmc.lowdraglib2.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.FillDirection;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ProgressBar;
import com.lowdragmc.lowdraglib2.gui.ui.elements.inventory.InventorySlots;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import li.gerard.solarturbulence.capability.heat.HeatStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
import org.appliedenergistics.yoga.YogaFlexDirection;
import org.jetbrains.annotations.Nullable;

public class SolarReceiverBlock extends Block implements EntityBlock, BlockUIMenuType.BlockUI {

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
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
        // Resolve the BE (works on both sides — client level has it too via DescSynced)
        if (!(holder.player.level().getBlockEntity(holder.pos) instanceof SolarReceiverBlockEntity be)) {
            return ModularUI.of(UI.of(new UIElement()), holder.player);
        }

        HeatStorage heat = be.getHeatStorage();
        final int maxHeat = heat.getHeatCapacity();

        // ── Heat bar ────────────────────────────────────────────────────────
        ProgressBar heatBar = new ProgressBar();
        heatBar.setRange(0f, 1f); // we feed it a normalized 0..1 value
        heatBar.layout(l -> l.width(20).height(70));
        heatBar.progressBarStyle(s -> s
                .fillDirection(FillDirection.LEFT_TO_RIGHT) // thermometer style
                .interpolate(true)
                .interpolateStep(-1f) // partial-tick lerp = buttery smooth
        );
        // Tint the fill red-orange so it reads as "heat"
        heatBar.bar(c -> c.style(s -> s.background(new ColorBorderTexture(0xFFFF00, 0xFF0000))));
        heatBar.barContainer(c -> c.style(s -> s.background(new ColorBorderTexture(0xFF0000, 0xFF0000))));

        // Server → client live binding for the fill ratio
        heatBar.bindDataSource(SupplierDataSource.of(
                () -> maxHeat <= 0 ? 0f : (heat.getHeatStored() / (float) maxHeat)
        ));

        // ── Live numeric readout under the bar ──────────────────────────────
        Label readout = new Label();
        readout.layout(l -> l.marginTop(4));
        readout.bindDataSource(SupplierDataSource.of(() -> {
            int current = heat.getHeatStored();
            // Format as "X / Y kJ" with thousands separator
            return Component.literal(
                    String.format("%,d / %,d kJ", current / 1000, maxHeat / 1000)
            ).withStyle(ChatFormatting.GOLD);
        }));

        // ── Title ───────────────────────────────────────────────────────────
        Label title = new Label();
        title.setText(Component.translatable("block.solarturbulence.solar_receiver")
                .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));
        title.layout(l -> l.marginBottom(6));

        // ── Vertical column holding everything ──────────────────────────────
        UIElement column = new UIElement().addChildren(title, heatBar, readout);

        // ── Root with the standard panel chrome ─────────────────────────────
        UIElement root = new UIElement()
                .addClass("panel_bg")
                .addChild(column);

        return ModularUI.of(
                UI.of(root, StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.GDP)),
                holder.player
        );
    }
}
