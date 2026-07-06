package com.canoestudio.retrofuturethewildupdate.client.renderer;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import com.canoestudio.retrofuturethewildupdate.client.models.ModelTadpole;
import com.canoestudio.retrofuturethewildupdate.entity.EntityTadpole;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderTadpole extends RenderLiving<EntityTadpole> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(RTWU.ID, "textures/entity/tadpole/tadpole.png");

    public RenderTadpole(RenderManager renderManager) {
        super(renderManager, new ModelTadpole(), 0.14F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityTadpole entity) {
        return TEXTURE;
    }

    @Override
    protected void preRenderCallback(EntityTadpole entitylivingbaseIn, float partialTickTime) {
        GlStateManager.scale(0.8F, 0.8F, 0.8F);
    }
}
