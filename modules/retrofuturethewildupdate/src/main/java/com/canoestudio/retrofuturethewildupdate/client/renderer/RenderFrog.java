package com.canoestudio.retrofuturethewildupdate.client.renderer;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import com.canoestudio.retrofuturethewildupdate.client.models.ModelFrog;
import com.canoestudio.retrofuturethewildupdate.entity.EntityFrog;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderFrog extends RenderLiving<EntityFrog> {

    public RenderFrog(RenderManager renderManager) {
        super(renderManager, new ModelFrog(), 0.3F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityFrog entity) {
        return new ResourceLocation(RTWU.ID, "textures/entity/frog/frog_" + entity.getVariantName() + ".png");
    }

    @Override
    protected void preRenderCallback(EntityFrog entitylivingbaseIn, float partialTickTime) {
        if (entitylivingbaseIn.isChild()) {
            GlStateManager.scale(0.55F, 0.55F, 0.55F);
        }
    }
}
