package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenQuaternion3f;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.Vector3f;

public class ItemTransform {

    public static final ItemTransform NO_TRANSFORM = new ItemTransform(Vector3f.ZERO, Vector3f.ZERO, Vector3f.ONE);

    private final Vector3f translation;
    private final Vector3f rotation;
    private final Vector3f scale;

    public ItemTransform(Vector3f translation, Vector3f rotation, Vector3f scale) {
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
    }

    public static ItemTransform create(OpenTransform3f transform) {
        return new ItemTransform(transform.getTranslate(), transform.getRotation(), transform.getScale());
    }

    public static ItemTransform create(Vector3f translation, Vector3f rotation, Vector3f scale) {
        translation = optimize(translation, Vector3f.ZERO);
        rotation = optimize(rotation, Vector3f.ZERO);
        scale = optimize(scale, Vector3f.ONE);
        if (translation == Vector3f.ZERO && rotation == Vector3f.ZERO && scale == Vector3f.ONE) {
            return NO_TRANSFORM;
        }
        return new ItemTransform(translation, rotation, scale);
    }

    public static ItemTransform create(Vector3f translation, Vector3f rotation, Vector3f scale, Vector3f rightTranslation, Vector3f rightRotation) {
        var leftTransform = create(translation, rotation, scale);
        rightTranslation = optimize(rightTranslation, Vector3f.ZERO);
        rightRotation = optimize(rightRotation, Vector3f.ZERO);
        if (rightTranslation == Vector3f.ZERO && rightRotation == Vector3f.ZERO) {
            return leftTransform;
        }
        return new Post(leftTransform, rightTranslation, rightRotation, Vector3f.ONE);
    }


    public void apply(boolean applyLeftHandTransform, IPoseStack poseStack) {
        if (this == NO_TRANSFORM) {
            return;
        }
        int i = applyLeftHandTransform ? -1 : 1;
        if (translation != Vector3f.ZERO) {
            poseStack.translate(i * translation.getX(), translation.getY(), translation.getZ());
        }
        if (rotation != Vector3f.ZERO) {
            float f = rotation.getX();
            float g = rotation.getY();
            float h = rotation.getZ();
            if (applyLeftHandTransform) {
                g = -g;
                h = -h;
            }
            poseStack.rotate(OpenQuaternion3f.fromXYZ(f * 0.017453292F, g * 0.017453292F, h * 0.017453292F));
        }
        if (scale != Vector3f.ONE) {
            poseStack.scale(scale.getX(), scale.getY(), scale.getZ());
        }
    }

    public Vector3f getTranslation() {
        return translation;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
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

        public Post(ItemTransform leftTransform, Vector3f translation, Vector3f rotation, Vector3f scale) {
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
