package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;

public class ItemTransform {

    public static final ItemTransform NO_TRANSFORM = new ItemTransform(OpenVector3f.ZERO, OpenVector3f.ZERO, OpenVector3f.ONE);

    private final OpenVector3f translation;
    private final OpenVector3f rotation;
    private final OpenVector3f scale;

    public ItemTransform(OpenVector3f translation, OpenVector3f rotation, OpenVector3f scale) {
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
    }

    public static ItemTransform create(OpenTransform3f transform) {
        return new ItemTransform(transform.translate(), transform.rotation(), transform.scale());
    }

    public static ItemTransform create(OpenVector3f translation, OpenVector3f rotation, OpenVector3f scale) {
        translation = optimize(translation, OpenVector3f.ZERO);
        rotation = optimize(rotation, OpenVector3f.ZERO);
        scale = optimize(scale, OpenVector3f.ONE);
        if (translation == OpenVector3f.ZERO && rotation == OpenVector3f.ZERO && scale == OpenVector3f.ONE) {
            return NO_TRANSFORM;
        }
        return new ItemTransform(translation, rotation, scale);
    }

    public static ItemTransform create(OpenVector3f translation, OpenVector3f rotation, OpenVector3f scale, OpenVector3f rightTranslation, OpenVector3f rightRotation) {
        var leftTransform = create(translation, rotation, scale);
        rightTranslation = optimize(rightTranslation, OpenVector3f.ZERO);
        rightRotation = optimize(rightRotation, OpenVector3f.ZERO);
        if (rightTranslation == OpenVector3f.ZERO && rightRotation == OpenVector3f.ZERO) {
            return leftTransform;
        }
        return new Post(leftTransform, rightTranslation, rightRotation, OpenVector3f.ONE);
    }


    public void apply(boolean applyLeftHandTransform, IPoseStack poseStack) {
        if (this == NO_TRANSFORM) {
            return;
        }
        int i = applyLeftHandTransform ? -1 : 1;
        if (translation != OpenVector3f.ZERO) {
            poseStack.translate(i * translation.x(), translation.y(), translation.z());
        }
        if (rotation != OpenVector3f.ZERO) {
            float f = rotation.x();
            float g = rotation.y();
            float h = rotation.z();
            if (applyLeftHandTransform) {
                g = -g;
                h = -h;
            }
            poseStack.rotate(OpenQuaternionf.fromEulerAnglesXYZ(f, g, h, true));
        }
        if (scale != OpenVector3f.ONE) {
            poseStack.scale(scale.x(), scale.y(), scale.z());
        }
    }

    public OpenVector3f getTranslation() {
        return translation;
    }

    public OpenVector3f getRotation() {
        return rotation;
    }

    public OpenVector3f getScale() {
        return scale;
    }


    private static <T> T optimize(T value, T targetValue) {
        if (value.equals(targetValue)) {
            return targetValue;
        }
        return value;
    }

    private static class Post extends ItemTransform {

        private final ItemTransform leftTransform;

        public Post(ItemTransform leftTransform, OpenVector3f translation, OpenVector3f rotation, OpenVector3f scale) {
            super(translation, rotation, scale);
            this.leftTransform = leftTransform;
        }

        @Override
        public void apply(boolean applyLeftHandTransform, IPoseStack poseStack) {
            this.leftTransform.apply(applyLeftHandTransform, poseStack);
            // the post transform never apply hand transform.
            super.apply(false, poseStack);
        }
    }
}
