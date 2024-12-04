package moe.plushie.armourers_workshop.core.skin.attachment;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;

public class SkinAttachmentPose extends OpenPoseStack.Pose {

    public static final SkinAttachmentPose EMPTY = new SkinAttachmentPose();

    public SkinAttachmentPose() {
    }

    public SkinAttachmentPose(IPoseStack.Pose pose) {
        super(pose);
    }
}
