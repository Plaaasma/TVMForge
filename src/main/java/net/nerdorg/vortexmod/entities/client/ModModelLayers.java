package net.nerdorg.vortexmod.entities.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.nerdorg.vortexmod.VortexMod;

public class ModModelLayers {
    public static final ModelLayerLocation VORTEX_PORTAL_LAYER = new ModelLayerLocation(
            new ResourceLocation(VortexMod.MODID, "vortex_portal_layer"), "main");

    public static final ModelLayerLocation DEMAT_EFFECT_LAYER = new ModelLayerLocation(
            new ResourceLocation(VortexMod.MODID, "demat_effect_layer"), "main");

    public static final ModelLayerLocation REMAT_EFFECT_LAYER = new ModelLayerLocation(
            new ResourceLocation(VortexMod.MODID, "remat_effect_layer"), "main");
}
