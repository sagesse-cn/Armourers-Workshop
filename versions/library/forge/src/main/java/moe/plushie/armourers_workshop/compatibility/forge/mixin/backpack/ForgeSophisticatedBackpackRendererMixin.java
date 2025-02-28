package moe.plushie.armourers_workshop.compatibility.forge.mixin.backpack;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackLayerRenderer;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.IBackpackModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.18, )")
@Pseudo
@Mixin(BackpackLayerRenderer.class)
public class ForgeSophisticatedBackpackRendererMixin {

    @Inject(method = "renderBackpack", at = @At("HEAD"), cancellable = true, remap = false)
    private static void aw2$renderBackpack(EntityModel<?> parentModel, LivingEntity livingEntity, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, ItemStack backpack, boolean wearsArmor, IBackpackModel model, CallbackInfo ci) {
        var renderData = EntityRenderData.of(livingEntity);
        if (renderData != null && renderData.getOverriddenManager().contains(SkinProperty.OVERRIDE_MODEL_BACKPACK)) {
            ci.cancel();
        }
    }
}
