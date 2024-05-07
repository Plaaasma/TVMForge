package net.nerdorg.vortexmod.entities.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.nerdorg.vortexmod.VortexMod;
import net.nerdorg.vortexmod.entities.client.ModModelLayers;
import net.nerdorg.vortexmod.entities.client.models.RematEffectModel;
import net.nerdorg.vortexmod.entities.custom.RematEffectEntity;

public class RematEffectRenderer extends MobRenderer<RematEffectEntity, RematEffectModel<RematEffectEntity>> {
    public RematEffectRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new RematEffectModel<>(pContext.bakeLayer(ModModelLayers.REMAT_EFFECT_LAYER)), 0.0001f);
    }

    @Override
    public void render(RematEffectEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(RematEffectEntity pEntity) {
        return new ResourceLocation(VortexMod.MODID, "textures/entity/invisible.png");
    }
}
