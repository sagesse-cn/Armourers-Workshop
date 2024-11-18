package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.api.core.math.ITransform;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenQuaternion3f;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;

public class AnimatedTransform implements ITransform {

    protected AnimatedPoint snapshot;
    protected int dirty = 0;

    private final Vector3f pivot;
    private final Vector3f afterTranslate;

    private final OpenTransform3f original;
    private final ArrayList<PointRef> points = new ArrayList<>();

    public AnimatedTransform(OpenTransform3f original) {
        this.original = original;
        this.pivot = original.getPivot();
        this.afterTranslate = original.getAfterTranslate();
    }

    public void link(AnimatedPoint point, int priority, boolean isMixMode) {
        this.points.add(new PointRef(point, priority, isMixMode));
        this.points.sort(Comparator.comparingInt(it -> it.priority));
    }


    @Override
    public void apply(IPoseStack poseStack) {
        // no snapshot or no changes?
        if (snapshot == null) {
            original.apply(poseStack);
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
        var init = original.getTranslate();
        var delta = Vector3f.ZERO;
        for (var point : points) {
            var value = point.value.getTranslate();
            if (value != Vector3f.ZERO) { // has any animation change this point?
                delta = value;
            }
        }
        result.setTranslate(init.getX() + delta.getX(), init.getY() + delta.getY(), init.getZ() + delta.getZ());
    }

    private void exportRotation(AnimatedPoint result) {
        var init = original.getRotation();
        var delta = Vector3f.ZERO;
        float x = 0;
        float y = 0;
        float z = 0;
        for (var point : points) {
            var value = point.value.getRotation();
            if (value != Vector3f.ZERO) { // has any animation change this point?
                delta = value;
                // in mixed mode we need to merge all rotation.
                x += value.getX();
                y += value.getY();
                z += value.getZ();
                // mark mixed mode into last value.
                if (point.isMixMode) {
                    delta = null;
                }
            }
        }
        // in default mode we only use the last value.
        if (delta != null) {
            x = delta.getX();
            y = delta.getY();
            z = delta.getZ();
        }
        result.setRotation(OpenMath.wrapDegrees(init.getX() + x), OpenMath.wrapDegrees(init.getY() + y), OpenMath.wrapDegrees(init.getZ() + z));
    }

    private void exportScale(AnimatedPoint result) {
        var init = original.getScale();
        var delta = Vector3f.ONE;
        for (var point : points) {
            var value = point.value.getScale();
            if (value != Vector3f.ONE) { // has any animation change this point?
                delta = value;
            }
        }
        result.setScale(init.getX() * delta.getX(), init.getY() * delta.getY(), init.getZ() * delta.getZ());
    }

    public void clear() {
        points.forEach(it -> it.value.clear());
        dirty = 0;
    }

    public void setDirty(int flags) {
        dirty |= flags;
    }

    public OpenTransform3f getOriginal() {
        return original;
    }


    private static class PointRef {

        public final AnimatedPoint value;

        public final boolean isMixMode;

        public final int priority;

        public PointRef(AnimatedPoint value, int priority, boolean isMixMode) {
            this.value = value;
            this.priority = priority;
            this.isMixMode = isMixMode;
        }
    }
}

