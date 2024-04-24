package net.nerdorg.vortexmod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.nerdorg.vortexmod.block.ModBlockEntities;
import net.nerdorg.vortexmod.block.ModBlocks;
import net.nerdorg.vortexmod.entities.ModEntities;
import net.nerdorg.vortexmod.entities.client.renderers.VortexPortalRenderer;
import net.nerdorg.vortexmod.item.ModItems;
import net.nerdorg.vortexmod.sound.ModSounds;
import org.slf4j.Logger;
import org.valkyrienskies.core.apigame.VSCore;
import org.valkyrienskies.core.apigame.VSCoreClient;
import org.valkyrienskies.core.apigame.world.VSPipeline;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(VortexMod.MODID)
public class VortexMod
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "vortexmod";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static VSPipeline vsPipeline;
    public static VSPipeline vsClientPipeline;

    public VortexMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModCreativeModeTabs.register(modEventBus);

        ModItems.register(modEventBus);

        ModBlocks.register(modEventBus);

        ModBlockEntities.register(modEventBus);

        ModSounds.register(modEventBus);

        ModEntities.register(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        vsPipeline = ValkyrienSkiesMod.getVsCore().newPipeline();
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntities.VORTEX_PORTAL.get(), VortexPortalRenderer::new);
            vsClientPipeline = ValkyrienSkiesMod.getVsCoreClient().newPipeline();
        }
    }
}
