package moe.plushie.armourers_workshop.compatibility.extensions.com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.math.IQuaternionf;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.18, )")
@Extension
public class ABI {

    public static void mulPose(@This PoseStack poseStack, IQuaternionf q) {
        poseStack.mulPose(AbstractPoseStack.convertQuaternion(q));
    }
}
