package moe.plushie.armourers_workshop.compatibility.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.client.AbstractItemDisplayContext;
import moe.plushie.armourers_workshop.core.client.model.EmbeddedItemModels;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Available("[1.20, )")
@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Inject(method = "getModel", at = @At("RETURN"))
    private void aw2$getModel(ItemStack itemStack, Level level, LivingEntity entity, int i, CallbackInfoReturnable<BakedModel> cir) {
        var model = cir.getReturnValue();
        var itemModel = ClientWardrobeHandler.getEmbeddedItemModel(itemStack, entity, level, model);
        var itemModels = EmbeddedItemModels.of(itemStack);
        itemModels.put(model, itemModel);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void aw2$render(ItemStack itemStack, ItemDisplayContext transformType, boolean leftHandHackery, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int overlay, BakedModel bakedModel, CallbackInfo ci) {
        var itemModels = EmbeddedItemModels.of(itemStack);
        var itemModel = itemModels.get(bakedModel);
        if (itemModel != null) {
            ClientWardrobeHandler.renderEmbeddedItemModel(itemStack, AbstractItemDisplayContext.wrap(transformType), bakedModel, itemModel, packedLight, overlay, leftHandHackery, poseStack, buffers, ci);
        }
    }
}
