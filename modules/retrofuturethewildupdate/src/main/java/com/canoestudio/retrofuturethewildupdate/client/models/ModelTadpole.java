package com.canoestudio.retrofuturethewildupdate.client.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelTadpole extends ModelBase {

    private final ModelRenderer body;
    private final ModelRenderer tail;

    public ModelTadpole() {
        this.textureWidth = 16;
        this.textureHeight = 16;

        this.body = new ModelRenderer(this, 0, 0);
        this.body.setRotationPoint(0.0F, 22.0F, -3.0F);
        this.body.addBox(-1.5F, -1.0F, 0.0F, 3, 2, 3);

        this.tail = new ModelRenderer(this, 0, 0);
        this.tail.setRotationPoint(0.0F, 22.0F, 0.0F);
        this.tail.addBox(0.0F, -1.0F, 0.0F, 0, 2, 7);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        this.body.render(scale);
        this.tail.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                                  float headPitch, float scaleFactor, Entity entityIn) {
        float amplitude = entityIn.isInWater() ? 1.0F : 1.5F;
        this.tail.rotateAngleY = -amplitude * 0.25F * MathHelper.sin(0.3F * ageInTicks);
        this.body.rotateAngleX = entityIn.isInWater() ? headPitch * 0.017453292F * 0.35F : 0.0F;
    }
}
