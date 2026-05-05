package li.gerard.solarturbulence;

import li.gerard.solarturbulence.block.ModBlockEntities;
import li.gerard.solarturbulence.block.generic.MultiblockControllerBlockEntity;
import li.gerard.solarturbulence.client.renderer.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = SolarTurbulenceMod.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = SolarTurbulenceMod.MODID, value = Dist.CLIENT)
public class SolarTurbulenceModClient {
    public SolarTurbulenceModClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        SolarTurbulenceMod.LOGGER.info("HELLO FROM CLIENT SETUP");
        SolarTurbulenceMod.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.SOLAR_ABSORBER_BLOCK_ENTITY.get(), SolarAbsorberBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MIRROR_FRAME_BLOCK_ENTITY.get(), MirrorFrameBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SOLAR_RECEIVER_BLOCK_ENTITY.get(), SolarReceiverBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.HEAT_BEAM_EMITTER_BLOCK_ENTITY.get(), HeatBeamEmitterBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RAY_SPLITTER_BLOCK_ENTITY.get(), RaySplitterBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.BLAZE_THERMAL_STORAGE_BLOCK_ENTITY.get(), BlazeThermalStorageRenderer::new);

    }

    @SubscribeEvent
    public static void onRenderHud(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) return;
        if (!(mc.hitResult instanceof BlockHitResult blockHit)) return;
        Level level = mc.level;
        if (level == null) return;
        BlockPos pos = blockHit.getBlockPos();
        if (!(level.getBlockEntity(pos) instanceof MultiblockControllerBlockEntity controller)) return;

        GuiGraphics graphics = event.getGuiGraphics();
        int centerX = mc.getWindow().getGuiScaledWidth() / 2;
        int y = mc.getWindow().getGuiScaledHeight() / 2 - 4;
        for (Component line : controller.getHudLines()) {
            graphics.drawString(mc.font, line, centerX + 10, y, 0xFFFFFF, true);
            y += mc.font.lineHeight + 2;
        }
    }
}

