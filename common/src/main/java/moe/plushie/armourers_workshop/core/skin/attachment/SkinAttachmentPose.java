package moe.plushie.armourers_workshop.core.skin.attachment;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;

public class SkinAttachmentPose {

    public static final SkinAttachmentPose EMPTY = new SkinAttachmentPose();

    private final OpenPoseStack.Pose last;

    public SkinAttachmentPose() {
        this.last = new OpenPoseStack.Pose(OpenMatrix4f.createScaleMatrix(0, 0, 0), OpenMatrix3f.createScaleMatrix(0, 0, 0));
    }

    public IPoseStack.Pose last() {
        return last;
    }
}
