package moe.plushie.armourers_workshop.compatibility.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class ItemInHandLayerMixin {

//    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ArmedModel;translateToHand(Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;)V", shift = At.Shift.BEFORE))
//    private void aw$translateToHandPre(LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext itemDisplayContext, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
//    }

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ArmedModel;translateToHand(Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;)V", shift = At.Shift.AFTER))
    private void aw$translateToHandPost(LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext itemDisplayContext, HumanoidArm humanoidArm, PoseStack poseStackIn, MultiBufferSource bufferSourceIn, int i, CallbackInfo ci) {
//        var poseStack = AbstractPoseStack.wrap(poseStackIn);
//        var renderData = EntityRenderData.of(livingEntity);
//        if (renderData != null && renderData.handPoseStack != null && ModDebugger.flag0 == 0) {
//            poseStack.last().set(renderData.handPoseStack.last());
//        }
//        ShapeTesselator.vector(Vector3f.ZERO, 1, poseStack, AbstractBufferSource.wrap(bufferSourceIn));
//
//        poseStack.pushPose();
//        poseStack.rotate(Vector3f.XP.rotationDegrees(-90.0F));
//        poseStack.rotate(Vector3f.YP.rotationDegrees(180.0F));
//        poseStack.translate(1 / 16f, 2 / 16f, -10 / 16f);
//        ShapeTesselator.vector(Vector3f.ZERO, 1, poseStack, AbstractBufferSource.wrap(bufferSourceIn));
//        poseStack.popPose();
    }
}
