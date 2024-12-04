package moe.plushie.armourers_workshop.compatibility.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentTypes;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

@Available("[1.16, )")
public class AbstractForgeTacZeroHandler {

    private static final Vector3f LEFT_WAIST_ORIGIN = new Vector3f(-4, 12, 0);
    private static final Vector3f RIGHT_WAIST_ORIGIN = new Vector3f(4, 12, 0);
    private static final Vector3f BACKPACK_ORIGIN = new Vector3f(0, 24, 2);

    public static void onRenderGunAttachment(Entity entity, ItemStack itemStack, Vector3f offset, PoseStack poseStackIn, MultiBufferSource bufferSourceIn) {
        var attachmentType = SkinAttachmentTypes.BACKPACK;
        var attachmentOrigin = BACKPACK_ORIGIN;

        float tx = offset.getX();
        float ty = offset.getY();
        float tz = offset.getZ();
        if (ty < 16) {
            if (tx < 0) {
                attachmentType = SkinAttachmentTypes.LEFT_WAIST;
                attachmentOrigin = LEFT_WAIST_ORIGIN;
            } else {
                attachmentType = SkinAttachmentTypes.RIGHT_WAIST;
                attachmentOrigin = RIGHT_WAIST_ORIGIN;
            }
        }

        float dx = (tx - attachmentOrigin.getX()) / 16f;
        float dy = (ty - attachmentOrigin.getY()) / 16f;
        float dz = (tz - attachmentOrigin.getZ()) / 16f;

        ClientWardrobeHandler.onRenderAttachment(entity, itemStack, attachmentType, poseStackIn, bufferSourceIn, OpenTransform3f.createTranslateTransform(-dx, -dy, dz));
    }

}
