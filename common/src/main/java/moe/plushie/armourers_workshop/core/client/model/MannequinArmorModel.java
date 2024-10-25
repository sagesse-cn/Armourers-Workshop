package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.compatibility.client.AbstractEntityRendererProvider;
import moe.plushie.armourers_workshop.compatibility.client.model.AbstractMannequinArmorModel;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MannequinArmorModel<T extends MannequinEntity> extends AbstractMannequinArmorModel<T> {

    public MannequinArmorModel(AbstractEntityRendererProvider.Context context, Type type) {
        super(context, type);
    }

    public static <T extends MannequinEntity> MannequinArmorModel<T> normalInner(AbstractEntityRendererProvider.Context context) {
        return new MannequinArmorModel<>(context, Type.NORMAL_INNER);
    }

    public static <T extends MannequinEntity> MannequinArmorModel<T> normalOuter(AbstractEntityRendererProvider.Context context) {
        return new MannequinArmorModel<>(context, Type.NORMAL_OUTER);
    }

    public static <T extends MannequinEntity> MannequinArmorModel<T> slimInner(AbstractEntityRendererProvider.Context context) {
        return new MannequinArmorModel<>(context, Type.SLIM_INNER);
    }

    public static <T extends MannequinEntity> MannequinArmorModel<T> slimOuter(AbstractEntityRendererProvider.Context context) {
        return new MannequinArmorModel<>(context, Type.SLIM_OUTER);
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
    }

    public static class Slim {

    }
}
