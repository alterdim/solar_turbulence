package li.gerard.solarturbulence.item;

import li.gerard.solarturbulence.block.solarabsorber.SolarAbsorberBlockEntity;
import li.gerard.solarturbulence.block.solarmirror.MirrorFrameBlock;
import li.gerard.solarturbulence.block.solarmirror.MirrorFrameBlockEntity;
import li.gerard.solarturbulence.block.solarreceiver.SolarReceiverBlockEntity;
import li.gerard.solarturbulence.data.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class MirrorLinkerItem extends Item {

    public MirrorLinkerItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos clickedPos = ctx.getClickedPos();
        Player player = ctx.getPlayer();
        ItemStack stack = ctx.getItemInHand();

        if (player == null) return InteractionResult.PASS;

        BlockEntity be = level.getBlockEntity(clickedPos);


        // --- Case 1: clicked an absorber — remember its position ---
        if (be instanceof SolarReceiverBlockEntity) {
            if (!level.isClientSide) {
                stack.set(ModDataComponents.LINKED_ABSORBER.get(), clickedPos.immutable());
                player.displayClientMessage(
                        Component.literal("Absorber selected at " + formatPos(clickedPos))
                                .withStyle(ChatFormatting.GREEN),
                        true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        // --- Case 2: clicked a mirror frame — link it to the remembered absorber ---
        if (be instanceof MirrorFrameBlockEntity frame) {
            BlockPos rememberedAbsorber = stack.get(ModDataComponents.LINKED_ABSORBER.get());

            if (rememberedAbsorber == null) {
                if (!level.isClientSide) {
                    player.displayClientMessage(
                            Component.literal("Right-click a Solar Absorber first.")
                                    .withStyle(ChatFormatting.RED),
                            true);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            // Verify the absorber still exists where we remembered it
            if (!level.isClientSide) {
                BlockEntity remembered = level.getBlockEntity(rememberedAbsorber);
                if (!(remembered instanceof SolarReceiverBlockEntity)) {
                    stack.remove(ModDataComponents.LINKED_ABSORBER.get());
                    player.displayClientMessage(
                            Component.literal("Remembered absorber no longer exists. Selection cleared.")
                                    .withStyle(ChatFormatting.RED),
                            true);
                    return InteractionResult.sidedSuccess(false);
                }
                frame.setLinkedAbsorber(rememberedAbsorber);
                player.displayClientMessage(
                        Component.literal("Frame linked to absorber at " + formatPos(rememberedAbsorber))
                                .withStyle(ChatFormatting.AQUA),
                        true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip,
                                TooltipFlag flag) {
        BlockPos linked = stack.get(ModDataComponents.LINKED_ABSORBER.get());
        if (linked != null) {
            tooltip.add(Component.literal("Selected absorber: " + formatPos(linked))
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.literal("Right-click a frame to link it.")
                    .withStyle(ChatFormatting.DARK_GRAY));
        } else {
            tooltip.add(Component.literal("Right-click a Solar Absorber to select it.")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    private static String formatPos(BlockPos pos) {
        return "[" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]";
    }
}