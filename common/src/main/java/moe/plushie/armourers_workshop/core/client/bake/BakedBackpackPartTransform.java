package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.api.core.math.ITransform;
import moe.plushie.armourers_workshop.core.client.other.PlaceholderManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentPose;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentTypes;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class BakedBackpackPartTransform implements ITransform {

    SkinAttachmentPose attachmentPose;

    public void setup(@Nullable Entity entity, SkinRenderContext renderContext) {
        var renderData = renderContext.getRenderData();
        if (renderData != null && !PlaceholderManager.isPlaceholder(entity)) {
            attachmentPose = renderData.getAttachmentPose(SkinAttachmentTypes.BACKPACK, 0);
        } else {
            attachmentPose = null;
        }
    }

    @Override
    public void apply(IPoseStack poseStack) {
        if (attachmentPose != null) {
            poseStack.last().set(attachmentPose);
            poseStack.scale(1 / 16f, 1 / 16f, 1 / 16f);
        }
    }
}
