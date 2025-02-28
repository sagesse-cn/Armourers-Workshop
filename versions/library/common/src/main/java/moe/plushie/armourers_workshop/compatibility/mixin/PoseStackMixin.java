package moe.plushie.armourers_workshop.compatibility.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.18, )")
@Mixin(PoseStack.class)
public abstract class PoseStackMixin {

    private Object aw2$oldValue;

    @Shadow
    public abstract PoseStack.Pose last();

    @Inject(method = "pushPose", at = @At("HEAD"))
    private void aw2$pushPosePre(CallbackInfo ci) {
        aw2$oldValue = last();
    }

    @Inject(method = "pushPose", at = @At("RETURN"))
    private void aw2$pushPosePost(CallbackInfo ci) {
        var oldPose = DataContainer.getOrDefault(aw2$oldValue, (AbstractPoseStack.Pose) null);
        if (oldPose != null && oldPose.properties() != 0) {
            var newPose = DataContainer.of(last(), AbstractPoseStack.Pose::new);
            newPose.setProperties(oldPose.properties());
        }
        aw2$oldValue = null;
    }

    @Inject(method = "setIdentity", at = @At("RETURN"))
    private void aw2$setIdentity(CallbackInfo ci) {
        var pose = DataContainer.getOrDefault(last(), (AbstractPoseStack.Pose) null);
        if (pose != null) {
            pose.setProperties(0);
        }
    }
}
