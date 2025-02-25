package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.init.ModDebugger;

/**
 * the skin need automatic mirror model.
 */
public class SkinItemTransform extends ItemTransform {

    private final ItemTransform rightTransform;

    public SkinItemTransform(OpenVector3f translation, OpenVector3f rotation, OpenVector3f scale, ItemTransform rightTransform) {
        super(translation, rotation, scale);
        this.rightTransform = rightTransform;
    }

    public static ItemTransform create(OpenVector3f translation, OpenVector3f rotation, OpenVector3f scale, OpenVector3f rightTranslation, OpenVector3f rightRotation, OpenVector3f rightScale) {
        var rightTransform = ItemTransform.create(rightTranslation, rightRotation, rightScale);
        translation = optimize(translation, OpenVector3f.ZERO);
        rotation = optimize(rotation, OpenVector3f.ZERO);
        scale = optimize(scale, OpenVector3f.ONE);
        return new SkinItemTransform(translation, rotation, scale, rightTransform);
    }

    @Override
    public void apply(boolean applyLeftHandTransform, IPoseStack poseStack) {
        ModDebugger.translate(poseStack);
        super.apply(applyLeftHandTransform, poseStack);
        this.rightTransform.apply(applyLeftHandTransform, poseStack);
        ModDebugger.rotate(poseStack);
        if (applyLeftHandTransform) {
            poseStack.scale(-1, 1, 1);
        }
    }
}
