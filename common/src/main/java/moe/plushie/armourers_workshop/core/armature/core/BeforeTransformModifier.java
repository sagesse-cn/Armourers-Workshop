package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.core.math.ITransform3f;

public class BeforeTransformModifier extends AfterTransformModifier {

    public BeforeTransformModifier(ITransform3f transform) {
        super(transform);
    }

    @Override
    public IJointTransform apply(IJoint joint, IModel model, IJointTransform transform) {
        var transform1 = super.apply(joint, model, IJointTransform.NONE);
        if (transform1 == IJointTransform.NONE) {
            return transform;
        }
        return poseStack -> {
            transform1.apply(poseStack);
            transform.apply(poseStack);
        };
    }
}
