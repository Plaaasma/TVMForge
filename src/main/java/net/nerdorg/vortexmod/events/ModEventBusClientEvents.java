package net.nerdorg.vortexmod.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.nerdorg.vortexmod.VortexMod;
import net.nerdorg.vortexmod.entities.client.ModModelLayers;
import net.nerdorg.vortexmod.entities.client.models.DematEffectModel;
import net.nerdorg.vortexmod.entities.client.models.RematEffectModel;
import net.nerdorg.vortexmod.entities.client.models.VortexPortalModel;
import net.nerdorg.vortexmod.entities.custom.VortexPortalEntity;

@Mod.EventBusSubscriber(modid = VortexMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.VORTEX_PORTAL_LAYER, VortexPortalModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.DEMAT_EFFECT_LAYER, DematEffectModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.REMAT_EFFECT_LAYER, RematEffectModel::createBodyLayer);
    }

//    @SubscribeEvent
//    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
//        event.registerBlockEntityRenderer(ModBlockEntities.BIOMETRIC_BLOCK_BE.get(), BioscannerBlockEntityRenderer::new);
//        event.registerBlockEntityRenderer(ModBlockEntities.MONITOR_BE.get(), MonitorBlockEntityRenderer::new);
//    }
}
