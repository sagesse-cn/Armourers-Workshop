package moe.plushie.armourers_workshop.core.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.compatibility.client.AbstractEntityRendererProvider;
import moe.plushie.armourers_workshop.compatibility.client.model.AbstractPlayerModel;
import moe.plushie.armourers_workshop.core.client.render.MannequinEntityRenderer;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Rotations;

@Environment(EnvType.CLIENT)
public class MannequinModel<T extends MannequinEntity> extends AbstractPlayerModel<T> {

    private Rotations mainPose;

    public MannequinModel(AbstractEntityRendererProvider.Context context, float scale, Type type) {
        super(context, scale, type);
    }

    public static <T extends MannequinEntity> MannequinModel<T> placeholder() {
        return normal(AbstractEntityRendererProvider.Context.sharedContext());
    }

    public static <T extends MannequinEntity> MannequinModel<T> normal(AbstractEntityRendererProvider.Context context) {
        return new MannequinModel<>(context, 0, Type.NORMAL);
    }

    public static <T extends MannequinEntity> MannequinModel<T> slim(AbstractEntityRendererProvider.Context context) {
        return new MannequinModel<>(context, 0, Type.SLIM);
    }


    @Override
    public void setupAnim(T entity, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
        this.head.xRot = OpenMath.toRadians(entity.getHeadPose().getX());
        this.head.yRot = OpenMath.toRadians(entity.getHeadPose().getY());
        this.head.zRot = OpenMath.toRadians(entity.getHeadPose().getZ());
        this.leftArm.xRot = OpenMath.toRadians(entity.getLeftArmPose().getX());
        this.leftArm.yRot = OpenMath.toRadians(entity.getLeftArmPose().getY());
        this.leftArm.zRot = OpenMath.toRadians(entity.getLeftArmPose().getZ());
        this.rightArm.xRot = OpenMath.toRadians(entity.getRightArmPose().getX());
        this.rightArm.yRot = OpenMath.toRadians(entity.getRightArmPose().getY());
        this.rightArm.zRot = OpenMath.toRadians(entity.getRightArmPose().getZ());
        this.leftLeg.xRot = OpenMath.toRadians(entity.getLeftLegPose().getX());
        this.leftLeg.yRot = OpenMath.toRadians(entity.getLeftLegPose().getY());
        this.leftLeg.zRot = OpenMath.toRadians(entity.getLeftLegPose().getZ());
        this.rightLeg.xRot = OpenMath.toRadians(entity.getRightLegPose().getX());
        this.rightLeg.yRot = OpenMath.toRadians(entity.getRightLegPose().getY());
        this.rightLeg.zRot = OpenMath.toRadians(entity.getRightLegPose().getZ());
        this.hat.copyFrom(this.head);
        this.leftPants.copyFrom(this.leftLeg);
        this.rightPants.copyFrom(this.rightLeg);
        this.leftSleeve.copyFrom(this.leftArm);
        this.rightSleeve.copyFrom(this.rightArm);
        this.jacket.copyFrom(this.body);
        this.mainPose = entity.getBodyPose();
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer builder, int light, int overlay, int color) {
        float rx = mainPose.getX();
        float ry = mainPose.getY();
        float rz = mainPose.getZ();
        // when rendering in the GUI, we don't use body rotation
        // to avoid display entity at weird angles.
        if (MannequinEntityRenderer.enableLimitYRot) {
            ry = 0;
        }
        poseStack.mulPose(new OpenQuaternionf(rx, ry, rz, true));
        super.renderToBuffer(poseStack, builder, light, overlay, color);
    }
}
