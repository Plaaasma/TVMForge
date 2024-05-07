package net.nerdorg.vortexmod.entities.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.nerdorg.vortexmod.VortexMod;
import net.nerdorg.vortexmod.entities.client.ModModelLayers;
import net.nerdorg.vortexmod.entities.client.models.VortexPortalModel;
import net.nerdorg.vortexmod.entities.custom.VortexPortalEntity;
import org.joml.Random;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class VortexPortalRenderer extends MobRenderer<VortexPortalEntity, VortexPortalModel<VortexPortalEntity>> {
    public VortexPortalRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new VortexPortalModel<>(pContext.bakeLayer(ModModelLayers.VORTEX_PORTAL_LAYER)), 0.0001f);
    }

    @Override
    public void render(VortexPortalEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(VortexPortalEntity pEntity) {
        return new ResourceLocation(VortexMod.MODID, "textures/entity/invisible.png");
    }
}
