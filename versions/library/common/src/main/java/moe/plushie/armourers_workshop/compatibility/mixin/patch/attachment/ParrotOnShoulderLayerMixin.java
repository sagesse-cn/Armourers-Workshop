package moe.plushie.armourers_workshop.compatibility.mixin.patch.attachment;


import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentTypes;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.20, )")
@Mixin(ParrotOnShoulderLayer.class)
public class ParrotOnShoulderLayerMixin {

    private static final OpenTransform3f LEFT_SHOULDER_OFFSET = OpenTransform3f.createTranslateTransform(new Vector3f(0, -1.5f, 0));
    private static final OpenTransform3f RIGHT_SHOULDER_OFFSET = OpenTransform3f.createTranslateTransform(new Vector3f(0, -1.5f, 0));

    // lambda$render$1
    @Inject(method = "method_17958", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", shift = At.Shift.AFTER))
    private void aw$renderOnShoulder(PoseStack poseStackIn, boolean bl, Player player, CompoundTag tag, MultiBufferSource bufferSourceIn, int i, float f, float g, float h, float j, EntityType<?> entityType, CallbackInfo ci) {
        if (bl) {
            ClientWardrobeHandler.onRenderAttachment(player, ItemStack.EMPTY, SkinAttachmentTypes.LEFT_SHOULDER, poseStackIn, bufferSourceIn, LEFT_SHOULDER_OFFSET);
        } else {
            ClientWardrobeHandler.onRenderAttachment(player, ItemStack.EMPTY, SkinAttachmentTypes.RIGHT_SHOULDER, poseStackIn, bufferSourceIn, RIGHT_SHOULDER_OFFSET);
        }
    }
}
