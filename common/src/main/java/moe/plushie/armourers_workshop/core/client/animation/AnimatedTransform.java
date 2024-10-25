package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.api.core.math.ITransform;
import moe.plushie.armourers_workshop.core.math.OpenQuaternion3f;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.Vector3f;

import java.util.ArrayList;

public class AnimatedTransform implements ITransform {

    protected final Vector3f pivot;
    protected final Vector3f afterTranslate;

    protected final OpenTransform3f parent;
    protected final ArrayList<AnimatedPoint> points = new ArrayList<>();

    protected AnimatedPoint snapshot;
    protected int dirty = 0;

    public AnimatedTransform(OpenTransform3f parent) {
        this.parent = parent;
        this.pivot = parent.getPivot();
        this.afterTranslate = parent.getAfterTranslate();
    }

    public void link(AnimatedPoint point) {
        points.add(point);
    }


    @Override
    public void apply(IPoseStack poseStack) {
        // no snapshot or no changes?
        if (snapshot == null) {
            parent.apply(poseStack);
            return;
        }
        // the translation have changes?
        var translate = snapshot.getTranslate();
        if (translate != Vector3f.ZERO) {
            poseStack.translate(translate.getX(), translate.getY(), translate.getZ());
        }
        // the rotation have changes?
        var rotation = snapshot.getRotation();
        if (rotation != Vector3f.ZERO) {
            if (pivot != Vector3f.ZERO) {
                poseStack.translate(pivot.getX(), pivot.getY(), pivot.getZ());
            }
            poseStack.rotate(OpenQuaternion3f.fromZYX(rotation, true));
            if (pivot != Vector3f.ZERO) {
                poseStack.translate(-pivot.getX(), -pivot.getY(), -pivot.getZ());
            }
        }
        // the scale have changes?
        var scale = snapshot.getScale();
        if (scale != Vector3f.ONE) {
            if (pivot != Vector3f.ZERO) {
                poseStack.translate(pivot.getX(), pivot.getY(), pivot.getZ());
            }
            poseStack.scale(scale.getX(), scale.getY(), scale.getZ());
            if (pivot != Vector3f.ZERO) {
                poseStack.translate(-pivot.getX(), -pivot.getY(), -pivot.getZ());
            }
        }
        // the after translate have changes?
        if (afterTranslate != Vector3f.ZERO) {
            poseStack.translate(afterTranslate.getX(), afterTranslate.getY(), afterTranslate.getZ());
        }
    }

    public void export(AnimatedPoint value) {
        value.clear();
        exportTranslate(value);
        exportRotation(value);
        exportScale(value);
    }

    private void exportTranslate(AnimatedPoint result) {
        var base = parent.getTranslate();
        float x = base.getX();
        float y = base.getY();
        float z = base.getZ();
        for (var point : points) {
            var value = point.getTranslate();
            x += value.getX();
            y += value.getY();
            z += value.getZ();
        }
        result.setTranslate(x, y, z);
    }

    private void exportRotation(AnimatedPoint result) {
        var base = parent.getRotation();
        float x = base.getX();
        float y = base.getY();
        float z = base.getZ();
        for (var point : points) {
            var value = point.getRotation();
            x += value.getX();
            y += value.getY();
            z += value.getZ();
        }
        result.setRotate(x % 360, y % 360, z % 360);
    }

    private void exportScale(AnimatedPoint result) {
        var base = parent.getScale();
        float x = base.getX();
        float y = base.getY();
        float z = base.getZ();
        for (var point : points) {
            var value = point.getScale();
            x *= value.getX();
            y *= value.getY();
            z *= value.getZ();
        }
        result.setScale(x, y, z);
    }

    public void clear() {
        points.forEach(AnimatedPoint::clear);
        dirty = 0;
    }

    public void setDirty(int flags) {
        dirty |= flags;
    }

    public OpenTransform3f getParent() {
        return parent;
    }
}

