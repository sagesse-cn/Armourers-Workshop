package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.api.core.math.ITransform;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenQuaternion3f;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.utils.Collections;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AnimatedTransform implements ITransform {

    private final Vector3f pivot;
    private final Vector3f afterTranslate;

    private final OpenTransform3f parent;

    private final List<Triple<AnimatedPoint, Integer, Boolean>> pendingPoints = new ArrayList<>();

    private final List<AnimatedPoint> points = new ArrayList<>();
    private final List<AnimatedPoint> defaultPoints = new ArrayList<>();
    private final List<AnimatedPoint> mixedPoints = new ArrayList<>();

    protected AnimatedPoint snapshot;
    protected int dirty = 0;

    public AnimatedTransform(OpenTransform3f parent) {
        this.parent = parent;
        this.pivot = parent.getPivot();
        this.afterTranslate = parent.getAfterTranslate();
    }

    public void link(AnimatedPoint point, int priority, boolean isMixedMode) {
        // add point and sort it.
        this.pendingPoints.add(Triple.of(point, priority, isMixedMode));
        this.pendingPoints.sort(Comparator.comparingInt(Triple::getMiddle));
        // rebuild linked values.
        this.points.clear();
        this.defaultPoints.clear();
        this.mixedPoints.clear();
        this.points.addAll(Collections.compactMap(pendingPoints, Triple::getLeft));
        this.defaultPoints.addAll(Collections.compactMap(Collections.filter(pendingPoints, it -> !it.getRight()), Triple::getLeft));
        this.mixedPoints.addAll(Collections.compactMap(Collections.filter(pendingPoints, Triple::getRight), Triple::getLeft));
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
        var delta = Vector3f.ZERO;
        for (var point : points) {
            var value = point.getTranslate();
            if (value != Vector3f.ZERO) { // has any animation change this point?
                delta = value;
            }
        }
        result.setTranslate(base.getX() + delta.getX(), base.getY() + delta.getY(), base.getZ() + delta.getZ());
    }

    private void exportRotation(AnimatedPoint result) {
        var base = parent.getRotation();
        var delta = Vector3f.ZERO;
        for (var point : defaultPoints) {
            var value = point.getRotation();
            if (value != Vector3f.ZERO) { // has any animation change this point?
                delta = value;
            }
        }
        // in mixed mode we need to merge all rotation.
        float x = base.getX() + delta.getX();
        float y = base.getY() + delta.getY();
        float z = base.getZ() + delta.getZ();
        for (var point : mixedPoints) {
            var value = point.getRotation();
            if (value != Vector3f.ZERO) { // has any animation change this point?
                x += value.getX();
                y += value.getY();
                z += value.getZ();
            }
        }
        result.setRotation(OpenMath.wrapDegrees(x), OpenMath.wrapDegrees(y), OpenMath.wrapDegrees(z));
    }

    private void exportScale(AnimatedPoint result) {
        var base = parent.getScale();
        var delta = Vector3f.ONE;
        for (var point : points) {
            var value = point.getScale();
            if (value != Vector3f.ONE) { // has any animation change this point?
                delta = value;
            }
        }
        result.setScale(base.getX() * delta.getX(), base.getY() * delta.getY(), base.getZ() * delta.getZ());
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

