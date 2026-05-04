package li.gerard.solarturbulence.item.heatrod;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class HeatRodItem extends Item {

    private final int held_heat;

    public HeatRodItem(Item.Properties properties, int held_heat) {
        super(properties);
        this.held_heat = held_heat;

    }

    public int getHeldHeat() {return this.held_heat;}
}
