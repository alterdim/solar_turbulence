package li.gerard.solarturbulence.item;

import li.gerard.solarturbulence.SolarTurbulenceMod;
import li.gerard.solarturbulence.block.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SolarTurbulenceMod.MODID);

    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", ModBlocks.EXAMPLE_BLOCK);
    public static final DeferredItem<BlockItem> SOLAR_ABSORBER_BI = ITEMS.registerSimpleBlockItem("solar_absorber", ModBlocks.SOLAR_ABSORBER_BLOCK);
    public static final DeferredItem<BlockItem> METALLIC_FRAME = ITEMS.registerSimpleBlockItem("metallic_frame", ModBlocks.METALLIC_FRAME_BLOCK);
}
