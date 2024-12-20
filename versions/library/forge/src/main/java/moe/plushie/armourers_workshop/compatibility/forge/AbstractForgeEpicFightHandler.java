package moe.plushie.armourers_workshop.compatibility.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.client.model.CachedModel;
import moe.plushie.armourers_workshop.core.client.other.thirdparty.EpicFlightModelPartBuilder;
import moe.plushie.armourers_workshop.core.client.other.thirdparty.EpicFlightModelTransformer;
import moe.plushie.armourers_workshop.core.client.skinrender.patch.EpicFightEntityRendererPatch;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.utils.MatrixUtils;
import moe.plushie.armourers_workshop.init.ModConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

@Available("[1.16, )")
public class AbstractForgeEpicFightHandler extends AbstractForgeEpicFightHandlerImpl {

    private static final FloatBuffer AW_MAT_BUFFER4 = MatrixUtils.createFloatBuffer(16);

    public static void onRenderPre(LivingEntity entityIn, int packedLightIn, float partialTicks, boolean isFirstPerson, PoseStack poseStackIn, MultiBufferSource bufferSourceIn, LivingEntityRenderer<?, ?> renderer) {
        EpicFightEntityRendererPatch.activate(entityIn, partialTicks, packedLightIn, poseStackIn, renderer, patch -> {
            patch.setFirstPerson(isFirstPerson);
        });
    }

    public static void onRenderEntity(LivingEntity entityIn, Armature armature, int packedLightIn, float partialTicks, PoseStack poseStackIn, MultiBufferSource bufferSourceIn, CallbackInfoReturnable<OpenMatrix4f[]> cir) {
        EpicFightEntityRendererPatch.apply(entityIn, poseStackIn, bufferSourceIn, patch -> {
            var poses = cir.getReturnValue();
            var overridePoses = Arrays.copyOf(poses, poses.length);
            var cachedPoses = new HashMap<String, OpenPoseStack.Pose>();
            patch.setOverridePose(AbstractPoseStack.create(poseStackIn));
            patch.setTransformProvider(name -> cachedPoses.computeIfAbsent(name, it -> {
                var joint = armature.searchJointByName(it);
                if (joint == null) {
                    return null;
                }
                var pose = new OpenPoseStack.Pose();
                copyTo(joint, poses, AW_MAT_BUFFER4);
                pose.pose().load(AW_MAT_BUFFER4);
                pose.normal().load(AW_MAT_BUFFER4);
                return pose;
            }));
            patch.setMesh(new EpicFlightModelPartBuilder() {
                @Override
                public Consumer<Boolean> build(String name) {
                    var joint = armature.searchJointByName(name);
                    if (joint != null) {
                        return visible -> overridePoses[joint.getId()] = OpenMatrix4f.createScale(0, 0, 0);
                    }
                    return null;
                }
            });
            cir.setReturnValue(overridePoses);
        });
    }

    public static void onRenderPost(LivingEntity entityIn, int packedLightIn, float partialTicks, PoseStack poseStackIn, MultiBufferSource bufferSourceIn, LivingEntityRenderer<?, ?> renderer) {
        EpicFightEntityRendererPatch.deactivate(entityIn, patch -> {
            patch.setFirstPerson(false);
            patch.setOverridePose(null);
            patch.setTransformProvider(null);
        });
    }

    public static void onInit() {
        ModConfig.Client.enablePartSubdivide = true;

        EpicFlightModelTransformer.register(EpicFlightModelPartBuilder.class, CachedModel::new, (model, it) -> {
            it.put("Head", model.build("Head"));
            it.put("Chest", model.build("Chest"));
            it.put("Torso", model.build("Torso"));
            it.put("Arm_L", model.build("Arm_L"));
            it.put("Arm_R", model.build("Arm_R"));
            it.put("Hand_L", model.build("Hand_L"));
            it.put("Hand_R", model.build("Hand_R"));
            it.put("Thigh_L", model.build("Thigh_L"));
            it.put("Thigh_R", model.build("Thigh_R"));
            it.put("Leg_L", model.build("Leg_L"));
            it.put("Leg_R", model.build("Leg_R"));
            it.put("Shoulder_L", model.build("Shoulder_L"));
            it.put("Shoulder_R", model.build("Shoulder_R"));
            it.put("Elbow_L", model.build("Elbow_L"));
            it.put("Elbow_R", model.build("Elbow_R"));
            it.put("Knee_L", model.build("Knee_L"));
            it.put("Knee_R", model.build("Knee_R"));
        });
    }
}
