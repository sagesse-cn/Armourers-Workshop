package moe.plushie.armourers_workshop.init.client;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentPose;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentType;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentTypes;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class ClientAttachmentHandler {

    private static final Vector3f GUN_LEFT_WAIST_ORIGIN = new Vector3f(-4, 12, 0);
    private static final Vector3f GUN_RIGHT_WAIST_ORIGIN = new Vector3f(4, 12, 0);
    private static final Vector3f GUN_BACKPACK_ORIGIN = new Vector3f(0, 24, 2);

    public static void onRenderName(Entity entity, Component name, PoseStack poseStackIn, MultiBufferSource bufferSourceIn) {
        apply(entity, SkinAttachmentTypes.NAME, poseStackIn, bufferSourceIn, (poseStack, attachmentPose) -> {
            // calculate the distance from target.
            var offset1 = Vector3f.ZERO.transforming(poseStack.last().pose());
            var offset2 = Vector3f.ZERO.transforming(attachmentPose.pose());

            float dx = offset2.getX() - offset1.getX();
            float dy = (offset2.getY() + 0.5f) - offset1.getY();
            float dz = offset2.getZ() - offset1.getZ();

            poseStack.translate(dx, dy, dz);
        });
    }

    public static void onRenderHand(Entity entity, ItemStack itemStack, OpenItemDisplayContext displayContext, PoseStack poseStackIn, MultiBufferSource bufferSourceIn) {
        var attachmentType = switch (displayContext) {
            case FIRST_PERSON_LEFT_HAND, THIRD_PERSON_LEFT_HAND -> SkinAttachmentTypes.LEFT_HAND;
            case FIRST_PERSON_RIGHT_HAND, THIRD_PERSON_RIGHT_HAND -> SkinAttachmentTypes.RIGHT_HAND;
            default -> SkinAttachmentTypes.UNKNOWN;
        };
        apply(entity, attachmentType, poseStackIn, bufferSourceIn, (poseStack, attachmentPose) -> {
            poseStack.last().set(attachmentPose);
            poseStack.rotate(Vector3f.XP.rotationDegrees(-90));
            poseStack.rotate(Vector3f.YP.rotationDegrees(180));
        });
    }

    public static void onRenderParrot(Entity entity, boolean isLeft, PoseStack poseStackIn, MultiBufferSource bufferSourceIn) {
        var attachmentType = SkinAttachmentTypes.RIGHT_SHOULDER;
        if (isLeft) {
            attachmentType = SkinAttachmentTypes.LEFT_SHOULDER;
        }
        apply(entity, attachmentType, poseStackIn, bufferSourceIn, (poseStack, attachmentPose) -> {
            poseStack.last().set(attachmentPose);
            poseStack.translate(0, -1.5f, 0);
        });
    }

    public static void onRenderGun(Entity entity, ItemStack itemStack, Vector3f offset, PoseStack poseStackIn, MultiBufferSource bufferSourceIn) {
        var attachmentType = SkinAttachmentTypes.BACKPACK;
        var attachmentOrigin = GUN_BACKPACK_ORIGIN;

        float tx = offset.getX();
        float ty = offset.getY();
        float tz = offset.getZ();
        if (ty < 16) {
            if (tx < 0) {
                attachmentType = SkinAttachmentTypes.LEFT_WAIST;
                attachmentOrigin = GUN_LEFT_WAIST_ORIGIN;
            } else {
                attachmentType = SkinAttachmentTypes.RIGHT_WAIST;
                attachmentOrigin = GUN_RIGHT_WAIST_ORIGIN;
            }
        }

        float dx = (tx - attachmentOrigin.getX()) / 16f;
        float dy = (ty - attachmentOrigin.getY()) / 16f;
        float dz = (tz - attachmentOrigin.getZ()) / 16f;

        apply(entity, attachmentType, poseStackIn, bufferSourceIn, (poseStack, attachmentPose) -> {
            poseStack.last().set(attachmentPose);
            poseStack.translate(-dx, -dy, dz);
        });
    }

    private static void apply(Entity entity, SkinAttachmentType attachmentType, PoseStack poseStackIn, MultiBufferSource buffersIn, BiConsumer<IPoseStack, SkinAttachmentPose> transform) {
        var renderData = EntityRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        var attachmentPose = renderData.getAttachmentPose(attachmentType, 0);
        if (attachmentPose == null) {
            return; // pass, use vanilla behavior.
        }
        var poseStack = AbstractPoseStack.wrap(poseStackIn);
        transform.accept(poseStack, attachmentPose);
    }
}
