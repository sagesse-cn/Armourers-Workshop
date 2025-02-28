package moe.plushie.armourers_workshop.init.mixin.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.init.platform.fabric.event.RenderLivingEntityEvents;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class FabricLivingRendererMixin<T extends LivingEntity> {

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "HEAD"))
    private void aw2$renderPre(T entity, float p_225623_2_, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, CallbackInfo ci) {
        RenderLivingEntityEvents.PRE.invoker().render(entity, partialTicks, light, poseStack, buffers, LivingEntityRenderer.class.cast(this));
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V", shift = At.Shift.AFTER))
    private void aw2$render(T entity, float p_225623_2_, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, CallbackInfo ci) {
        RenderLivingEntityEvents.SETUP.invoker().render(entity, partialTicks, light, poseStack, buffers, LivingEntityRenderer.class.cast(this));
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "RETURN"))
    private void aw2$renderPost(T entity, float p_225623_2_, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, CallbackInfo ci) {
        RenderLivingEntityEvents.POST.invoker().render(entity, partialTicks, light, poseStack, buffers, LivingEntityRenderer.class.cast(this));
    }
}
