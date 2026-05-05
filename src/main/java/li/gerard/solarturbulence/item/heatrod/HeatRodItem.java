package li.gerard.solarturbulence.item.heatrod;

import li.gerard.solarturbulence.capability.heat.HeatStorage;
import li.gerard.solarturbulence.data.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class HeatRodItem extends Item {

    private final int capacity;
    private final int maxReceive;
    private final int maxExtract;

    public HeatRodItem(Properties properties, int capacity, int maxReceive, int maxExtract) {
        super(properties.stacksTo(1));
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    public int getCapacity() { return capacity; }
    public int getMaxReceive() { return maxReceive; }
    public int getMaxExtract() { return maxExtract; }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        int heat = stack.getOrDefault(ModDataComponents.HEAT, 0);
        int maxHeat = capacity;

        tooltipComponents.add(
                Component.translatable("tooltip.solar_turbulence.heat", heat, maxHeat)
                        .withStyle(ChatFormatting.RED)
        );
    }

}