package li.gerard.solarturbulence.fluid;

import li.gerard.solarturbulence.SolarTurbulenceMod;
import li.gerard.solarturbulence.block.ModBlocks;
import li.gerard.solarturbulence.item.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static li.gerard.solarturbulence.SolarTurbulenceMod.MODID;

public class ModFluids {

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, MODID);

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(BuiltInRegistries.FLUID, MODID);


    public static final DeferredHolder<FluidType, FluidType> LIQUID_DARGLASS_TYPE =
            FLUID_TYPES.register("liquid_darglass", () ->
                    new FluidType(FluidType.Properties.create()
                            .density(3000)
                            .viscosity(6000)
                            .temperature(1300)
                            .lightLevel(15)
                            .canSwim(false)
                            .canDrown(false)
                            .supportsBoating(false)
                    ) {
                        // Optional: override getRenderPropertiesInternal() for client-side visuals
                    }
            );


    // --- Source & Flowing fluids (forward-referenced via Supplier) ---
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> LIQUID_DARGLASS_SOURCE =
            FLUIDS.register("liquid_darglass", () ->
                    new BaseFlowingFluid.Source(ModFluids.fluidProperties())
            );

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> LIQUID_DARGLASS_FLOWING =
            FLUIDS.register("liquid_darglass_flowing", () ->
                    new BaseFlowingFluid.Flowing(ModFluids.fluidProperties())
            );


    // --- Fluid block ---
    public static final DeferredHolder<Block, LiquidBlock> LIQUID_DARGLASS_BLOCK =
            ModBlocks.BLOCKS.register("liquid_darglass", () ->
                    new LiquidBlock(ModFluids.LIQUID_DARGLASS_SOURCE.get(),
                            BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)
                    )
            );


    // --- Bucket item ---
    public static final DeferredHolder<Item, BucketItem> LIQUID_DARGLASS_BUCKET =
            ModItems.ITEMS.register("liquid_darglass_bucket", () ->
                    new BucketItem(ModFluids.LIQUID_DARGLASS_SOURCE.get(),
                            new Item.Properties()
                                    .stacksTo(1)
                                    .craftRemainder(Items.BUCKET)
                    )
            );


    // --- Shared properties (avoids circular field initialization) ---
    private static BaseFlowingFluid.Properties fluidProperties() {
        return new BaseFlowingFluid.Properties(
                ModFluids.LIQUID_DARGLASS_TYPE,
                ModFluids.LIQUID_DARGLASS_SOURCE,
                ModFluids.LIQUID_DARGLASS_FLOWING
        )
                .slopeFindDistance(2)
                .levelDecreasePerBlock(2)
                .block(ModFluids.LIQUID_DARGLASS_BLOCK)
                .bucket(ModFluids.LIQUID_DARGLASS_BUCKET);
    }

    private static final IClientFluidTypeExtensions liquidExt = new IClientFluidTypeExtensions() {
        @Override
        public ResourceLocation getStillTexture() {
            return ResourceLocation.fromNamespaceAndPath(MODID, "block/liquid_darglass");
        }

        @Override
        public ResourceLocation getFlowingTexture() {
            return ResourceLocation.fromNamespaceAndPath(MODID, "block/liquid_darglass_flow");
        }
    };

    public static void clientExt(RegisterClientExtensionsEvent event) {
        event.registerFluidType(liquidExt, LIQUID_DARGLASS_TYPE.get());
    }

}
