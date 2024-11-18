package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnimationContext {

    private final ArrayList<Snapshot> snapshots = new ArrayList<>();
    private final HashMap<AnimationController, AnimationPlayState> playStates = new HashMap<>();

    public AnimationContext() {
    }

    public AnimationContext(AnimationContext context) {
        context.snapshots.forEach(snapshot -> {
            snapshots.add(new VariantSnapshot(snapshot));
        });
    }


    public static AnimationContext from(List<BakedSkinPart> skinParts) {
        // find all animated transform and convert to snapshot.
        var context = new AnimationContext();
        Collections.eachTree(skinParts, BakedSkinPart::getChildren, part -> {
            for (var transform : part.getTransform().getChildren()) {
                if (transform instanceof AnimatedTransform transform1) {
                    context.snapshots.add(new Snapshot(part, transform1));
                }
            }
        });
        return context;
    }

    public void beginUpdates(float animationTime) {
        for (var snapshot : snapshots) {
            snapshot.beginUpdates(animationTime);
        }
    }

    public void commitUpdates() {
        for (var snapshot : snapshots) {
            snapshot.commitUpdates();
        }
    }

    public void addAnimation(@Nullable AnimationController fromAnimationController, @Nullable AnimationController toAnimationController, float time, float speed, float duration) {
        // Find affected parts by from/to animation.
        var affectedParts = new ArrayList<BakedSkinPart>();
        affectedParts.addAll(Objects.flatMap(fromAnimationController, AnimationController::getAffectedParts, Collections.emptyList()));
        affectedParts.addAll(Objects.flatMap(toAnimationController, AnimationController::getAffectedParts, Collections.emptyList()));
        for (var snapshot : snapshots) {
            if (snapshot instanceof VariantSnapshot variant && affectedParts.contains(snapshot.part)) {
                variant.addTransitingAnimation(time, speed, duration);
            }
        }
    }

    protected void addPlayState(AnimationController animationController, AnimationPlayState playState) {
        playStates.put(animationController, playState);
    }

    @Nullable
    protected AnimationPlayState removePlayState(AnimationController animationController) {
        return playStates.remove(animationController);
    }

    @Nullable
    public AnimationPlayState getPlayState(AnimationController animationController) {
        return playStates.get(animationController);
    }

    public boolean isEmpty() {
        return snapshots.isEmpty();
    }


    private static class Snapshot {

        protected final BakedSkinPart part;
        protected final AnimatedTransform transform;

        public Snapshot(BakedSkinPart part, AnimatedTransform transform) {
            this.part = part;
            this.transform = transform;
        }

        public void beginUpdates(float animationTicks) {
            // set snapshot to null, the transform will skip calculations.
            transform.snapshot = null;
        }

        public void commitUpdates() {
            // nop.
        }

        @Override
        public String toString() {
            return Objects.toString(this, "name", part.getName());
        }
    }

    private static class VariantSnapshot extends Snapshot {

        protected final AnimatedPoint currentValue = new AnimatedPoint();

        protected TransitingAnimation transitingAnimation;

        protected boolean isExported = false;

        public VariantSnapshot(Snapshot snapshot) {
            super(snapshot.part, snapshot.transform);
        }

        @Override
        public void beginUpdates(float animationTicks) {
            // set snapshot to null, the transform will skip calculations.
            transform.snapshot = null;
            transform.clear();
            //
            if (transitingAnimation != null) {
                transitingAnimation.update(animationTicks);
                if (transitingAnimation.isCompleted()) {
                    transitingAnimation = null;
                }
            }
        }

        @Override
        public void commitUpdates() {
            // when no transiting or no change, we will need skip calculate.
            if (transitingAnimation == null && transform.dirty == 0) {
                isExported = false;
                return; // keep snapshot is null.
            }
            isExported = true;
            transform.export(currentValue);
            transform.snapshot = currentValue;
            // when the snapshot is transiting, we're mix tow snapshot calculation.
            if (transitingAnimation != null) {
                transitingAnimation.apply(currentValue);
                transform.snapshot = transitingAnimation.getOutputValue();
            }
        }

        protected void addTransitingAnimation(float time, float speed, float duration) {
            transitingAnimation = new TransitingAnimation(time, duration);
            var snapshotValue = transitingAnimation.getSnapshotValue();
            if (isExported) {
                snapshotValue.setTranslate(currentValue.getTranslate());
                snapshotValue.setRotation(currentValue.getRotation());
                snapshotValue.setScale(currentValue.getScale());
            } else {
                var original = transform.getOriginal();
                snapshotValue.setTranslate(original.getTranslate());
                snapshotValue.setRotation(original.getRotation());
                snapshotValue.setScale(original.getScale());
            }
        }
    }

    private static class TransitingAnimation {

        private final AnimatedPoint snapshotValue = new AnimatedPoint();
        private final AnimatedPoint outputValue = new AnimatedPoint();

        private final float beginTime;
        private final float endTime;
        private final float duration;

        private float progress;
        private boolean isCompleted;

        public TransitingAnimation(float time, float duration) {
            this.beginTime = time;
            this.endTime = time + duration;
            this.duration = duration;
        }

        public void update(float time) {
            this.progress = OpenMath.clamp((time - beginTime) / duration, 0.0f, 1.0f);
            this.isCompleted = time > endTime;
        }

        public void apply(AnimatedPoint currentValue) {
            var lt = snapshotValue.getTranslate();
            var lr = snapshotValue.getRotation();
            var ls = snapshotValue.getScale();
            var rt = currentValue.getTranslate();
            var rr = currentValue.getRotation();
            var rs = currentValue.getScale();
            float tx = OpenMath.lerp(progress, lt.getX(), rt.getX());
            float ty = OpenMath.lerp(progress, lt.getY(), rt.getY());
            float tz = OpenMath.lerp(progress, lt.getZ(), rt.getZ());
            float sx = OpenMath.lerp(progress, ls.getX(), rs.getX());
            float sy = OpenMath.lerp(progress, ls.getY(), rs.getY());
            float sz = OpenMath.lerp(progress, ls.getZ(), rs.getZ());
            float rx = OpenMath.lerp(progress, lr.getX(), rr.getX());
            float ry = OpenMath.lerp(progress, lr.getY(), rr.getY());
            float rz = OpenMath.lerp(progress, lr.getZ(), rr.getZ());
            outputValue.clear();
            outputValue.setTranslate(tx, ty, tz);
            outputValue.setScale(sx, sy, sz);
            outputValue.setRotation(rx, ry, rz);
        }

        public AnimatedPoint getSnapshotValue() {
            return snapshotValue;
        }

        public AnimatedPoint getOutputValue() {
            return outputValue;
        }

        public float getProgress() {
            return progress;
        }

        public boolean isCompleted() {
            return isCompleted;
        }
    }
}
