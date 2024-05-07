package net.nerdorg.vortexmod.entities.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.nerdorg.vortexmod.VortexMod;
import net.nerdorg.vortexmod.clientutil.render.RenderUtil;
import net.nerdorg.vortexmod.entities.client.ModModelLayers;
import net.nerdorg.vortexmod.entities.client.models.DematEffectModel;
import net.nerdorg.vortexmod.entities.client.models.VortexPortalModel;
import net.nerdorg.vortexmod.entities.custom.DematEffectEntity;
import net.nerdorg.vortexmod.entities.custom.VortexPortalEntity;
import org.joml.Vector3f;

public class DematEffectRenderer extends MobRenderer<DematEffectEntity, DematEffectModel<DematEffectEntity>> {
    public DematEffectRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new DematEffectModel<>(pContext.bakeLayer(ModModelLayers.DEMAT_EFFECT_LAYER)), 0.0001f);
    }

    @Override
    public void render(DematEffectEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
        int alpha = Math.min(255, (pEntity.tickCount / 2) * (pEntity.tickCount / 2));
        int ringAlpha = Math.min(190, alpha);

        RenderUtil.drawDonut(pBuffer, pPoseStack, new Vector3f(0, 0, 0), 0.15f, 5f, 24, -15 + pEntity.tickCount, 50 + pEntity.tickCount * 2, ringAlpha, 255, 60, 0);
        RenderUtil.drawDonut(pBuffer, pPoseStack, new Vector3f(0, 0, 0), 0.15f, 5f, 24, 35 + pEntity.tickCount * 4, -60 + pEntity.tickCount * 3, ringAlpha, 255, 60, 0);
        RenderUtil.drawDonut(pBuffer, pPoseStack, new Vector3f(0, 0, 0), 0.15f, 5f, 24, 26 + -pEntity.tickCount * 2, 90 + pEntity.tickCount * 4, ringAlpha, 255, 60, 0);
        RenderUtil.drawSphere(pBuffer, pPoseStack, new Vector3f(0, 0, 0), 5, 16, alpha, 252, 186, 3);
    }

    @Override
    public ResourceLocation getTextureLocation(DematEffectEntity pEntity) {
        return new ResourceLocation(VortexMod.MODID, "textures/entity/invisible.png");
    }
}
