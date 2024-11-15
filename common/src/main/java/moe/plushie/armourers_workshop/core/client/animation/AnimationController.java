package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationFunction;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationKeyframe;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationLoop;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationPoint;
import moe.plushie.armourers_workshop.core.skin.animation.molang.MolangVirtualMachine;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Constant;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Expression;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenSequenceSource;
import moe.plushie.armourers_workshop.init.ModLog;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnimationController {

    private final int id = OpenSequenceSource.nextInt(AnimationController.class);

    private final String name;
    private final SkinAnimation animation;

    private final float duration;
    private final SkinAnimationLoop loop;

    private final ArrayList<Animator> animators = new ArrayList<>();

    private final boolean isParallel;
    private final boolean isRequiresVirtualMachine;

    public AnimationController(SkinAnimation animation, Map<String, BakedSkinPart> bones, ISkinProperties properties) {
        this.name = animation.getName();
        this.animation = animation;

        this.loop = animation.getLoop();
        this.duration = animation.getDuration();

        // create all animation.
        animation.getKeyframes().forEach((boneName, linkedValues) -> {
            var bone = bones.get(boneName);
            if (bone != null) {
                this.animators.add(new Bone(bone, AnimationController.toTime(duration), linkedValues));
            }
            if (boneName.equals("Effects")) {
                this.animators.add(new Effect(AnimationController.toTime(duration), linkedValues));
            }
        });

        this.isParallel = calcParallel();
        this.isRequiresVirtualMachine = calcRequiresVirtualMachine();
    }

    public static int toTime(float time) {
        return (int) (time * 1000);
    }

    public void process(float animationTicks, PlayState playState) {
        int time = AnimationController.toTime(animationTicks);
        for (var animator : animators) {
            for (var channel : animator.channels) {
                var fragment = channel.getFragmentAtTime(time);
                animator.apply(fragment, channel.selector, time, playState);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnimationController that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "name", name, "duration", duration, "loop", loop);
    }

    public Collection<BakedSkinPart> getParts() {
        return Collections.compactMap(animators, it -> {
            if (it instanceof Bone bone) {
                return bone.part;
            }
            return null;
        });
    }

    public String getName() {
        return name;
    }

    public SkinAnimationLoop getLoop() {
        return loop;
    }

    public float getDuration() {
        return duration;
    }

    public boolean isRequiresVirtualMachine() {
        return isRequiresVirtualMachine;
    }

    public boolean isParallel() {
        return isParallel;
    }

    public boolean isEmpty() {
        return animators.isEmpty();
    }

    private boolean calcParallel() {
        return name != null && name.matches("^(.+\\.)?parallel\\d+$");
    }

    private boolean calcRequiresVirtualMachine() {
        for (var animator : animators) {
            for (var channel : animator.channels) {
                for (var fragment : channel.fragments) {
                    if (!fragment.startValue.isConstant() || !fragment.endValue.isConstant()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static abstract class Animator {

        protected final Output output;
        protected final List<Channel> channels;

        public Animator(int duration, List<SkinAnimationKeyframe> linkedKeyframes, Output output) {
            this.output = output;
            this.channels = build(duration, linkedKeyframes);
        }

        public abstract void apply(Fragment fragment, Selector selector, int time, PlayState playState);

        public abstract Selector selectorByName(String name);

        private List<Channel> build(float duration, List<SkinAnimationKeyframe> linkedKeyframes) {
            var namedKeyframes = new LinkedHashMap<String, ArrayList<SkinAnimationKeyframe>>();
            for (var keyframe : linkedKeyframes) {
                namedKeyframes.computeIfAbsent(keyframe.getKey(), key -> new ArrayList<>()).add(keyframe);
            }
            return Collections.compactMap(namedKeyframes.entrySet(), it -> {
                var selector = selectorByName(it.getKey());
                if (selector != null) {
                    return new Channel(selector, duration, it.getValue());
                }
                return null;
            });
        }
    }

    public static class Bone extends Animator {

        private final BakedSkinPart part;

        public Bone(BakedSkinPart part, int duration, List<SkinAnimationKeyframe> linkedKeyframes) {
            super(duration, linkedKeyframes, linkTo(part));
            this.part = part;
        }

        @Override
        public void apply(Fragment fragment, Selector selector, int time, PlayState playState) {
            if (fragment != null) {
                fragment.apply(selector, output, time - fragment.startTime);
            }
        }

        @Override
        public Selector selectorByName(String name) {
            return switch (name) {
                case "position" -> new Selector.Translation();
                case "rotation" -> new Selector.Rotation();
                case "scale" -> new Selector.Scale();
                default -> null;
            };
        }

        protected static Output linkTo(BakedSkinPart bone) {
            // when animation transform already been created, we just link it directly.
            for (var transform : bone.getTransform().getChildren()) {
                if (transform instanceof AnimatedTransform animatedTransform) {
                    return new Output(animatedTransform);
                }
            }
            // if part have a non-standard transform (preview mode),
            // we wil think this part can't be support animation.
            if (!(bone.getPart().getTransform() instanceof OpenTransform3f parent)) {
                return new Output(null);
            }
            // we will replace the standard transform to animated transform.
            var animatedTransform = new AnimatedTransform(parent);
            bone.getTransform().replaceChild(parent, animatedTransform);
            return new Output(animatedTransform);
        }
    }

    public static class Effect extends Animator {

        public Effect(int duration, List<SkinAnimationKeyframe> linkedKeyframes) {
            super(duration, linkedKeyframes, null);
        }

        @Override
        public void apply(Fragment fragment, Selector selector, int time, PlayState playState) {
            // ..?
            if (selector instanceof Selector.Timeline) {
                var newValue = Objects.flatMap(fragment, it -> it.startValue);
                if (playState.lastInstructionValue != newValue) {
                    // start?
                    // stop?
                    if (newValue != null) {
                        newValue.get();
                    }
                    playState.lastInstructionValue = newValue;
                }
            }
        }

        @Override
        public Selector selectorByName(String name) {
            return switch (name) {
                case "particle" -> new Selector.Particle();
                case "sound" -> new Selector.Sound();
                case "timeline" -> new Selector.Timeline();
                default -> null;
            };
        }
    }

    public static class Channel {

        private final Selector selector;
        private final Fragment[] fragments;

        private Fragment current;

        public Channel(Selector selector, float duration, List<SkinAnimationKeyframe> keyframes) {
            this.selector = selector;
            this.fragments = create(duration, keyframes).toArray(new Fragment[0]);
        }

        public Fragment getFragmentAtTime(int time) {
            // fast hit caching?
            if (current != null && current.contains(time)) {
                return current;
            }
            // find fragment with time.
            for (var fragment : fragments) {
                current = fragment;
                if (current.contains(time)) {
                    break;
                }
            }
            return current;
        }

        public boolean isEmpty() {
            return fragments == null || fragments.length == 0;
        }

        private List<Fragment> create(float duration, List<SkinAnimationKeyframe> keyframes) {
            var defaultValue = calcDefaultValue();
            var builders = new ArrayList<FragmentBuilder>();
            for (var keyframe : keyframes) {
                var time = AnimationController.toTime(keyframe.getTime());
                var point = compile(keyframe.getPoints(), defaultValue);
                builders.add(new FragmentBuilder(time, keyframe.getFunction(), point.getKey(), point.getValue()));
            }
            builders.sort(Comparator.comparingInt(it -> it.leftTime));
            if (!builders.isEmpty()) {
                builders.add(0, builders.get(0).copy(AnimationController.toTime(0)));
                builders.add(builders.get(builders.size() - 1).copy(AnimationController.toTime(duration)));
            }
            for (int i = 1; i < builders.size(); i++) {
                var left = builders.get(i - 1);
                var right = builders.get(i);
                left.rightTime = right.leftTime;
                left.rightValue = right.leftValue;
            }
            builders.removeIf(it -> it.leftTime == it.rightTime);
            return Collections.compactMap(builders, FragmentBuilder::build);
        }

        private Pair<Value, Value> compile(List<SkinAnimationPoint> points, float defaultValue) {
            var expressions = new ArrayList<Expression>();
            for (var point : points) {
                if (point instanceof SkinAnimationPoint.Bone bone) {
                    expressions.add(compile(bone.getX(), defaultValue));
                    expressions.add(compile(bone.getY(), defaultValue));
                    expressions.add(compile(bone.getZ(), defaultValue));
                } else if (point instanceof SkinAnimationPoint.Instruction instruction) {
                    expressions.add(compile(instruction.getScript(), defaultValue));
                    expressions.add(Constant.ZERO);
                    expressions.add(Constant.ZERO);
                } else {
                    ModLog.warn("Not support point type: {}", point);
                    expressions.add(Constant.ZERO);
                    expressions.add(Constant.ZERO);
                    expressions.add(Constant.ZERO);
                }
            }
            var left = new Value(expressions.get(0), expressions.get(1), expressions.get(2));
            if (expressions.size() <= 3) {
                return Pair.of(left, left);
            }
            var right = new Value(expressions.get(3), expressions.get(4), expressions.get(5));
            return Pair.of(left, right);
        }

        private Expression compile(Object object, double defaultValue) {
            try {
                if (object instanceof Number number) {
                    return new Constant(number.doubleValue());
                }
                if (object instanceof String script) {
                    return MolangVirtualMachine.get().eval(script);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return new Constant(defaultValue);
        }

        private float calcDefaultValue() {
            if (selector instanceof Selector.Scale) {
                return 1f;
            }
            return 0f;
        }
    }

    public static class Fragment {

        private final int startTime;
        private final Value startValue;

        private final int endTime;
        private final Value endValue;

        private final int length;
        private final SkinAnimationFunction function;

        public Fragment(int startTime, Value startValue, int endTime, Value endValue, SkinAnimationFunction function) {
            this.startTime = startTime;
            this.startValue = startValue;
            this.endTime = endTime;
            this.endValue = endValue;
            this.length = endTime - startTime;
            this.function = function;
        }

        public void apply(Selector selector, Output output, int time) {
            if (time <= 0) {
                selector.apply(output, startValue.get());
                return;
            }
            if (time >= length) {
                selector.apply(output, endValue.get());
                return;
            }
            var from = startValue.get();
            var to = endValue.get();
            var t = function.apply(time / (float) length);
            var tx = OpenMath.lerp(t, from.getX(), to.getX());
            var ty = OpenMath.lerp(t, from.getY(), to.getY());
            var tz = OpenMath.lerp(t, from.getZ(), to.getZ());
            selector.apply(output, tx, ty, tz);
        }

        public boolean contains(int time) {
            return startTime <= time && time < endTime;
        }
    }

    public static class FragmentBuilder {

        private final SkinAnimationFunction function;

        private final int leftTime;
        private final Value leftValue;

        private int rightTime;
        private Value rightValue;

        FragmentBuilder(int time, SkinAnimationFunction function, Value leftValue, Value rightValue) {
            this.function = function;
            this.leftTime = time;
            this.leftValue = leftValue;
            this.rightTime = time;
            this.rightValue = rightValue;
        }

        public FragmentBuilder copy(int time) {
            if (time > 0) {
                return new FragmentBuilder(time, SkinAnimationFunction.linear(), rightValue, rightValue); // tail
            } else {
                return new FragmentBuilder(time, SkinAnimationFunction.linear(), leftValue, leftValue); // head
            }
        }

        public Fragment build() {
            return new Fragment(leftTime, leftValue, rightTime, rightValue, function);
        }
    }

    public static class Selector {

        protected void apply(Output output, float x, float y, float z) {
        }

        protected void apply(Output snapshot, Vector3f value) {
            apply(snapshot, value.getX(), value.getY(), value.getZ());
        }

        public static class Translation extends Selector {

            @Override
            protected void apply(Output output, float x, float y, float z) {
                output.setTranslate(x, y, z);
            }
        }

        public static class Rotation extends Selector {

            @Override
            protected void apply(Output output, float x, float y, float z) {
                output.setRotate(x, y, z);
            }
        }

        public static class Scale extends Selector {

            @Override
            protected void apply(Output output, float x, float y, float z) {
                output.setScale(x, y, z);
            }
        }

        public static class Particle extends Selector {
        }

        public static class Sound extends Selector {
        }

        public static class Timeline extends Selector {
        }
    }

    public static class Output extends AnimatedPoint {

        private final AnimatedTransform transform;

        public Output(AnimatedTransform transform) {
            this.transform = transform;
            if (transform != null) {
                transform.link(this);
            }
        }

        @Override
        public void setDirty(int newFlags) {
            super.setDirty(newFlags);
            if (transform != null) {
                transform.setDirty(newFlags);
            }
        }
    }

    public static class Value {

        private final Runnable updater;
        private final Vector3f variable = new Vector3f();

        public Value(Expression x, Expression y, Expression z) {
            this.updater = build(x, y, z);
        }

        public Vector3f get() {
            if (updater != null) {
                updater.run();
            }
            return variable;
        }

        public boolean isConstant() {
            return updater == null;
        }

        private Runnable build(Expression x, Expression y, Expression z) {
            // something requires to be calculated.
            if (x.isMutable() || y.isMutable() || z.isMutable()) {
                return () -> variable.set(x.getAsFloat(), y.getAsFloat(), z.getAsFloat());
            }
            // all is constant.
            variable.set(x.getAsFloat(), y.getAsFloat(), z.getAsFloat());
            return null;
        }
    }

    public static class PlayState {

        private float beginTime;

        private final float duration;

        private int playCount;
        private boolean isCompleted = false;

        private float loopProgress;
        private float loopBeginTime;
        private float loopDuration;

        private Value lastInstructionValue;

        public PlayState(AnimationController animationController, float beginTime, float speed, int playCount) {
            this.duration = animationController.getDuration();
            this.beginTime = beginTime;
            this.playCount = calcPlayCount(playCount, animationController.getLoop());
            this.loopProgress = 0;
            this.loopBeginTime = beginTime;
            this.loopDuration = duration / speed;
        }

        public void tick(float animationTicks) {
            loopProgress = (animationTicks - loopBeginTime) / loopDuration;
            if (playCount == 0 || loopProgress < 1.0f) {
                return;
            }
            // 0 -> 1 / 0 -> 1 ...
            if (playCount > 0) {
                playCount -= 1;
            }
            if (playCount != 0) { // reset
                loopProgress -= 1.0f;
                loopBeginTime += loopDuration;
            } else {
                isCompleted = true; // yep, completed!
            }
            reset();
        }

        protected void reset() {
            lastInstructionValue = null;
        }

        public void setBeginTime(float time) {
            beginTime = time;
            loopBeginTime = time;
        }

        public float getBeginTime() {
            return beginTime;
        }

        public float getAdjustedTicks(float animationTicks) {
            // this is a future animation?
            if (animationTicks < beginTime) {
                return 0;
            }
            tick(animationTicks);
            return loopProgress * duration;
        }

        public int getPlayCount() {
            return playCount;
        }

        public boolean isCompleted() {
            return isCompleted;
        }

        private int calcPlayCount(int playCount, SkinAnimationLoop loop) {
            if (playCount == 0) {
                return switch (loop) {
                    case NONE -> 1;
                    case LAST_FRAME -> 0;
                    case LOOP -> -1;
                };
            }
            return playCount;
        }
    }
}
