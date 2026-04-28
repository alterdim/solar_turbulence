package li.gerard.solarturbulence.item;

import li.gerard.solarturbulence.SolarTurbulenceMod;
import li.gerard.solarturbulence.block.ModBlocks;
import li.gerard.solarturbulence.item.mirror.MirrorItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static li.gerard.solarturbulence.SolarTurbulenceMod.MODID;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", ModBlocks.EXAMPLE_BLOCK);
    public static final DeferredItem<BlockItem> SOLAR_ABSORBER_BI = ITEMS.registerSimpleBlockItem("solar_absorber", ModBlocks.SOLAR_ABSORBER_BLOCK);
    public static final DeferredItem<BlockItem> METALLIC_FRAME = ITEMS.registerSimpleBlockItem("metallic_frame", ModBlocks.METALLIC_FRAME_BLOCK);
    public static final DeferredItem<BlockItem> MIRROR_FRAME = ITEMS.registerSimpleBlockItem("mirror_frame", ModBlocks.MIRROR_FRAME_BLOCK);
    public static final DeferredItem<BlockItem> SOLAR_RECEIVER = ITEMS.registerSimpleBlockItem("solar_receiver", ModBlocks.SOLAR_RECEIVER_BLOCK);
    public static final DeferredItem<BlockItem> HEAT_BEAM_EMITTER = ITEMS.registerSimpleBlockItem("heat_beam_emitter", ModBlocks.HEAT_BEAM_EMITTER_BLOCK);
    public static final DeferredItem<Item> WAND = ITEMS.registerSimpleItem("wand");

    public static final DeferredItem<Item> MIRROR_LINKER = ITEMS.register("mirror_linker.json",
            () -> new MirrorLinkerItem(new Item.Properties()));

    public static final DeferredItem<MirrorItem> COPPER_MIRROR = ITEMS.register("copper_mirror",
            () -> new MirrorItem(new Item.Properties().stacksTo(1),
                    ResourceLocation.fromNamespaceAndPath(MODID, "textures/mirror_face/copper_face.png"),
                    5,
                    0xFFFFD27A,  // warm yellow
                    0xFFFF8844,  // orange
                    0xFFFF3322 ) {
                @Override
                public void onRandomMirrorEvent() { /* ... */ }
            });
}
