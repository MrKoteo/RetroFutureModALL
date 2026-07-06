package com.canoestudio.retrofuturethewildupdate.client.models;

import com.canoestudio.retrofuturethewildupdate.entity.EntityFrog;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelFrog extends ModelBase {

    private final ModelRenderer root;
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer eyes;
    private final ModelRenderer tongue;
    private final ModelRenderer croakingBody;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftLeg;
    private final ModelRenderer rightLeg;

    public ModelFrog() {
        this.textureWidth = 48;
        this.textureHeight = 48;

        this.root = new ModelRenderer(this, 0, 0);
        this.root.setRotationPoint(0.0F, 24.0F, 0.0F);

        this.body = new ModelRenderer(this, 3, 1);
        this.body.setRotationPoint(0.0F, -2.0F, 4.0F);
        this.body.addBox(-3.5F, -2.0F, -8.0F, 7, 3, 9);
        this.body.setTextureOffset(23, 22).addBox(-3.5F, -1.0F, -8.0F, 7, 0, 9);
        this.root.addChild(this.body);

        this.head = new ModelRenderer(this, 0, 13);
        this.head.setRotationPoint(0.0F, -2.0F, -1.0F);
        this.head.addBox(-3.5F, -2.0F, -7.0F, 7, 3, 9);
        this.head.setTextureOffset(23, 13).addBox(-3.5F, -1.0F, -7.0F, 7, 0, 9);
        this.body.addChild(this.head);

        this.eyes = new ModelRenderer(this, 0, 0);
        this.eyes.setRotationPoint(-0.5F, 0.0F, 2.0F);
        this.head.addChild(this.eyes);

        ModelRenderer rightEye = new ModelRenderer(this, 0, 0);
        rightEye.setRotationPoint(-1.5F, -3.0F, -6.5F);
        rightEye.addBox(-1.5F, -1.0F, -1.5F, 3, 2, 3);
        this.eyes.addChild(rightEye);

        ModelRenderer leftEye = new ModelRenderer(this, 0, 5);
        leftEye.setRotationPoint(2.5F, -3.0F, -6.5F);
        leftEye.addBox(-1.5F, -1.0F, -1.5F, 3, 2, 3);
        this.eyes.addChild(leftEye);

        this.croakingBody = new ModelRenderer(this, 26, 5);
        this.croakingBody.setRotationPoint(0.0F, -1.0F, -5.0F);
        this.croakingBody.addBox(-3.4F, -0.1F, -2.8F, 7, 2, 3, -0.1F);
        this.body.addChild(this.croakingBody);

        this.tongue = new ModelRenderer(this, 17, 13);
        this.tongue.setRotationPoint(0.0F, -1.01F, 1.0F);
        this.tongue.addBox(-2.0F, 0.0F, -7.1F, 4, 0, 7);
        this.body.addChild(this.tongue);

        this.leftArm = new ModelRenderer(this, 0, 32);
        this.leftArm.setRotationPoint(4.0F, -1.0F, -6.5F);
        this.leftArm.addBox(-1.0F, 0.0F, -1.0F, 2, 3, 3);
        this.body.addChild(this.leftArm);
        ModelRenderer leftHand = new ModelRenderer(this, 18, 40);
        leftHand.setRotationPoint(0.0F, 3.0F, -1.0F);
        leftHand.addBox(-4.0F, 0.01F, -4.0F, 8, 0, 8);
        this.leftArm.addChild(leftHand);

        this.rightArm = new ModelRenderer(this, 0, 38);
        this.rightArm.setRotationPoint(-4.0F, -1.0F, -6.5F);
        this.rightArm.addBox(-1.0F, 0.0F, -1.0F, 2, 3, 3);
        this.body.addChild(this.rightArm);
        ModelRenderer rightHand = new ModelRenderer(this, 2, 40);
        rightHand.setRotationPoint(0.0F, 3.0F, 0.0F);
        rightHand.addBox(-4.0F, 0.01F, -5.0F, 8, 0, 8);
        this.rightArm.addChild(rightHand);

        this.leftLeg = new ModelRenderer(this, 14, 25);
        this.leftLeg.setRotationPoint(3.5F, -3.0F, 4.0F);
        this.leftLeg.addBox(-1.0F, 0.0F, -2.0F, 3, 3, 4);
        this.root.addChild(this.leftLeg);
        ModelRenderer leftFoot = new ModelRenderer(this, 2, 32);
        leftFoot.setRotationPoint(2.0F, 3.0F, 0.0F);
        leftFoot.addBox(-4.0F, 0.01F, -4.0F, 8, 0, 8);
        this.leftLeg.addChild(leftFoot);

        this.rightLeg = new ModelRenderer(this, 0, 25);
        this.rightLeg.setRotationPoint(-3.5F, -3.0F, 4.0F);
        this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 3, 3, 4);
        this.root.addChild(this.rightLeg);
        ModelRenderer rightFoot = new ModelRenderer(this, 18, 32);
        rightFoot.setRotationPoint(-2.0F, 3.0F, 0.0F);
        rightFoot.addBox(-4.0F, 0.01F, -4.0F, 8, 0, 8);
        this.rightLeg.addChild(rightFoot);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        this.root.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                                  float headPitch, float scaleFactor, Entity entityIn) {
        this.resetRotations();

        EntityFrog frog = entityIn instanceof EntityFrog ? (EntityFrog) entityIn : null;
        boolean swimming = entityIn.isInWater();
        boolean croaking = frog != null && frog.isCroaking();
        float tongue = frog != null ? frog.getTongueTicks() / 12.0F : 0.0F;
        float speed = MathHelper.clamp(limbSwingAmount, 0.0F, swimming ? 1.0F : 1.5F);
        float walk = limbSwing * (swimming ? 0.9F : 0.65F);

        this.head.rotateAngleX = headPitch * 0.017453292F * 0.35F;
        this.head.rotateAngleY = netHeadYaw * 0.017453292F * 0.35F;
        this.croakingBody.isHidden = !croaking;
        this.tongue.isHidden = tongue <= 0.0F;

        if (croaking) {
            this.croakingBody.rotationPointY = -1.0F + MathHelper.sin(ageInTicks * 0.45F) * 0.08F;
            this.body.rotateAngleX += MathHelper.sin(ageInTicks * 0.28F) * 0.025F;
        }

        if (tongue > 0.0F) {
            float extend = MathHelper.sin(tongue * (float) Math.PI);
            this.tongue.rotationPointZ = 1.0F - extend * 2.2F;
            this.tongue.rotateAngleX = -0.08F * extend;
        }

        if (swimming) {
            float wave = MathHelper.sin(ageInTicks * 0.35F);
            this.body.rotateAngleX = -0.12F + wave * 0.05F;
            this.leftLeg.rotateAngleX = 0.55F + MathHelper.cos(walk) * 0.55F * speed;
            this.rightLeg.rotateAngleX = 0.55F + MathHelper.cos(walk + (float) Math.PI) * 0.55F * speed;
            this.leftArm.rotateAngleX = 0.25F + MathHelper.cos(walk + (float) Math.PI) * 0.45F * speed;
            this.rightArm.rotateAngleX = 0.25F + MathHelper.cos(walk) * 0.45F * speed;
        } else {
            this.body.rotateAngleX = MathHelper.clamp(speed * 0.25F, 0.0F, 0.28F);
            this.leftLeg.rotateAngleX = MathHelper.cos(walk) * 0.85F * speed;
            this.rightLeg.rotateAngleX = MathHelper.cos(walk + (float) Math.PI) * 0.85F * speed;
            this.leftArm.rotateAngleX = MathHelper.cos(walk + (float) Math.PI) * 0.55F * speed;
            this.rightArm.rotateAngleX = MathHelper.cos(walk) * 0.55F * speed;
        }

        if (!entityIn.onGround && !swimming) {
            this.body.rotateAngleX += 0.35F;
            this.leftLeg.rotateAngleX -= 0.75F;
            this.rightLeg.rotateAngleX -= 0.75F;
            this.leftArm.rotateAngleX += 0.45F;
            this.rightArm.rotateAngleX += 0.45F;
        }
    }

    private void resetRotations() {
        this.body.rotateAngleX = 0.0F;
        this.body.rotateAngleY = 0.0F;
        this.body.rotateAngleZ = 0.0F;
        this.head.rotateAngleX = 0.0F;
        this.head.rotateAngleY = 0.0F;
        this.head.rotateAngleZ = 0.0F;
        this.croakingBody.rotationPointY = -1.0F;
        this.croakingBody.rotateAngleX = 0.0F;
        this.tongue.rotationPointZ = 1.0F;
        this.tongue.rotateAngleX = 0.0F;
        this.leftArm.rotateAngleX = 0.0F;
        this.leftArm.rotateAngleY = 0.0F;
        this.leftArm.rotateAngleZ = 0.0F;
        this.rightArm.rotateAngleX = 0.0F;
        this.rightArm.rotateAngleY = 0.0F;
        this.rightArm.rotateAngleZ = 0.0F;
        this.leftLeg.rotateAngleX = 0.0F;
        this.leftLeg.rotateAngleY = 0.0F;
        this.leftLeg.rotateAngleZ = 0.0F;
        this.rightLeg.rotateAngleX = 0.0F;
        this.rightLeg.rotateAngleY = 0.0F;
        this.rightLeg.rotateAngleZ = 0.0F;
    }
}
