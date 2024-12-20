package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.api.core.math.ITransform;
import moe.plushie.armourers_workshop.api.core.math.ITransform3f;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.Objects;

import java.io.IOException;

public class OpenTransform3f implements ITransform3f, ITransform {

    public static final int BYTES = OpenVector3f.BYTES * 5 + Integer.BYTES;

    public static final OpenTransform3f IDENTITY = new OpenTransform3f();

    private OpenVector3f translate = OpenVector3f.ZERO;
    private OpenVector3f rotation = OpenVector3f.ZERO;
    private OpenVector3f scale = OpenVector3f.ONE;
    private OpenVector3f afterTranslate = OpenVector3f.ZERO;
    private OpenVector3f pivot = OpenVector3f.ZERO;

    public static OpenTransform3f create(OpenVector3f translate, OpenVector3f rotation, OpenVector3f scale) {
        return create(translate, rotation, scale, OpenVector3f.ZERO, OpenVector3f.ZERO);
    }

    public static OpenTransform3f create(OpenVector3f translate, OpenVector3f rotation, OpenVector3f scale, OpenVector3f pivot, OpenVector3f afterTranslate) {
        // quick optimization
        if (translate.equals(OpenVector3f.ZERO) && rotation.equals(OpenVector3f.ZERO) && scale.equals(OpenVector3f.ONE) && pivot.equals(OpenVector3f.ZERO) && afterTranslate.equals(OpenVector3f.ZERO)) {
            return IDENTITY;
        }
        var transform = new OpenTransform3f();
        transform.translate = optimize(translate, OpenVector3f.ZERO);
        transform.rotation = optimize(rotation, OpenVector3f.ZERO);
        transform.scale = optimize(scale, OpenVector3f.ONE);
        transform.afterTranslate = optimize(afterTranslate, OpenVector3f.ZERO);
        transform.pivot = optimize(pivot, OpenVector3f.ZERO);
        return transform;
    }

    public static OpenTransform3f createRotationTransform(OpenVector3f rotation) {
        if (!rotation.equals(OpenVector3f.ZERO)) {
            var transform = new OpenTransform3f();
            transform.rotation = rotation;
            return transform;
        }
        return IDENTITY;
    }

    public static OpenTransform3f createScaleTransform(float sx, float sy, float sz) {
        if (sx != 1 || sy != 1 || sz != 1) {
            var transform = new OpenTransform3f();
            transform.scale = new OpenVector3f(sx, sy, sz);
            return transform;
        }
        return IDENTITY;
    }

    public static OpenTransform3f createScaleTransform(OpenVector3f scale) {
        if (!scale.equals(OpenVector3f.ONE)) {
            var transform = new OpenTransform3f();
            transform.scale = scale;
            return transform;
        }
        return IDENTITY;
    }

    public static OpenTransform3f createTranslateTransform(float tx, float ty, float tz) {
        if (tx != 0 || ty != 0 || tz != 0) {
            var transform = new OpenTransform3f();
            transform.translate = new OpenVector3f(tx, ty, tz);
            return transform;
        }
        return IDENTITY;
    }

    public static OpenTransform3f createTranslateTransform(OpenVector3f offset) {
        if (!offset.equals(OpenVector3f.ZERO)) {
            var transform = new OpenTransform3f();
            transform.translate = offset;
            return transform;
        }
        return IDENTITY;
    }

    @Override
    public void apply(IPoseStack poseStack) {
        if (this == IDENTITY) {
            return;
        }
        if (translate != OpenVector3f.ZERO) {
            poseStack.translate(translate.x(), translate.y(), translate.z());
        }
        if (rotation != OpenVector3f.ZERO) {
            if (pivot != OpenVector3f.ZERO) {
                poseStack.translate(pivot.x(), pivot.y(), pivot.z());
            }
            poseStack.rotate(OpenQuaternionf.fromEulerAnglesZYX(rotation, true));
            if (pivot != OpenVector3f.ZERO) {
                poseStack.translate(-pivot.x(), -pivot.y(), -pivot.z());
            }
        }
        if (scale != OpenVector3f.ONE) {
            poseStack.scale(scale.x(), scale.y(), scale.z());
        }
        if (afterTranslate != OpenVector3f.ZERO) {
            poseStack.translate(afterTranslate.x(), afterTranslate.y(), afterTranslate.z());
        }
    }

    public void readFromStream(IInputStream stream) throws IOException {
        int flags = stream.readInt();
        translate = optimize(stream.readVector3f(), OpenVector3f.ZERO);
        rotation = optimize(stream.readVector3f(), OpenVector3f.ZERO);
        scale = optimize(stream.readVector3f(), OpenVector3f.ONE);
        afterTranslate = optimize(stream.readVector3f(), OpenVector3f.ZERO);
        pivot = optimize(stream.readVector3f(), OpenVector3f.ZERO);
    }

    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writeInt(0);
        stream.writeVector3f(translate);
        stream.writeVector3f(rotation);
        stream.writeVector3f(scale);
        stream.writeVector3f(afterTranslate);
        stream.writeVector3f(pivot);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenTransform3f that)) return false;
        return translate.equals(that.translate) && rotation.equals(that.rotation) && scale.equals(that.scale) && pivot.equals(that.pivot) && afterTranslate.equals(that.afterTranslate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(translate, rotation, scale, pivot, afterTranslate);
    }

    @Override
    public String toString() {
        return Objects.toString(this, "translate", translate, "rotation", rotation, "scale", scale, "pivot", pivot, "offset", afterTranslate);
    }

    @Override
    public boolean isIdentity() {
        return this == IDENTITY;
    }

    @Override
    public OpenVector3f translate() {
        return translate;
    }

    @Override
    public OpenVector3f rotation() {
        return rotation;
    }

    @Override
    public OpenVector3f scale() {
        return scale;
    }

    @Override
    public OpenVector3f afterTranslate() {
        return afterTranslate;
    }

    @Override
    public OpenVector3f pivot() {
        return pivot;
    }

    public OpenTransform3f copy() {
        if (this == IDENTITY) {
            return IDENTITY;
        }
        var transform = new OpenTransform3f();
        transform.translate = translate;
        transform.rotation = translate;
        transform.scale = translate;
        transform.pivot = translate;
        transform.afterTranslate = translate;
        return transform;
    }

    private static <T> T optimize(T value, T targetValue) {
        if (value.equals(targetValue)) {
            return targetValue;
        }
        return value;
    }
}
