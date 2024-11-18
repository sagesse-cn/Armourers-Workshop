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
import moe.plushie.armourers_workshop.core.skin.animation.molang.ast.Constant;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
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

    private final AnimatedMixMode mode;
    private final boolean isRequiresVirtualMachine;

    public AnimationController(SkinAnimation animation, Map<String, BakedSkinPart> parts, ISkinProperties properties) {
        this.name = animation.getName();
        this.animation = animation;

        this.loop = animation.getLoop();
        this.duration = animation.getDuration();

        this.mode = AnimatedMixMode.byName(name);

        // create all animation.
        animation.getKeyframes().forEach((partName, linkedValues) -> {
            var part = parts.get(partName);
            if (part != null) {
                this.animators.add(new Bone(AnimationController.toTime(duration), mode, linkedValues, part));
            }
            if (partName.equals("Effects")) {
                this.animators.add(new Effect(AnimationController.toTime(duration), mode, linkedValues));
            }
        });

        this.isRequiresVirtualMachine = calcRequiresVirtualMachine();
    }

    public static int toTime(float time) {
        return (int) (time * 1000);
    }

    public void process(float animationTicks, AnimationPlayState playState, ExecutionContext context) {
        int time = AnimationController.toTime(animationTicks);
        for (var animator : animators) {
            for (var channel : animator.channels) {
                var fragment = channel.getFragmentAtTime(time);
                animator.apply(fragment, channel.selector, time, playState, context);
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

    public Collection<BakedSkinPart> getAffectedParts() {
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
        return mode != AnimatedMixMode.MAIN;
    }

    public boolean isEmpty() {
        return animators.isEmpty();
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

    private static abstract class Animator {

        protected final AnimatedPoint output;
        protected final List<Channel> channels;

        public Animator(int duration, List<SkinAnimationKeyframe> linkedKeyframes, AnimatedPoint output) {
            this.output = output;
            this.channels = build(duration, linkedKeyframes);
        }

        public abstract void apply(Fragment fragment, Selector selector, int time, AnimationPlayState playState, ExecutionContext context);

        public abstract Selector selectorByName(String name);

        private List<Channel> build(int duration, List<SkinAnimationKeyframe> linkedKeyframes) {
            var namedKeyframes = new LinkedHashMap<String, ArrayList<SkinAnimationKeyframe>>();
            for (var keyframe : linkedKeyframes) {
                namedKeyframes.computeIfAbsent(keyframe.getKey(), key -> new ArrayList<>()).add(keyframe);
            }
            return Collections.compactMap(namedKeyframes.entrySet(), it -> {
                var selector = selectorByName(it.getKey());
                if (selector != null) {
                    return new Channel(duration, selector, it.getValue());
                }
                return null;
            });
        }
    }

    private static class Bone extends Animator {

        private final BakedSkinPart part;

        public Bone(int duration, AnimatedMixMode mode, List<SkinAnimationKeyframe> linkedKeyframes, BakedSkinPart part) {
            super(duration, linkedKeyframes, linkTo(part, mode));
            this.part = part;
        }

        @Override
        public void apply(Fragment fragment, Selector selector, int time, AnimationPlayState playState, ExecutionContext context) {
            if (fragment != null) {
                fragment.apply(time - fragment.startTime, selector, context, output);
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

        protected static OutputPoint linkTo(BakedSkinPart bone, AnimatedMixMode mode) {
            // when animation transform already been created, we just link it directly.
            for (var transform : bone.getTransform().getChildren()) {
                if (transform instanceof AnimatedTransform animatedTransform) {
                    return new OutputPoint(animatedTransform, mode);
                }
            }
            // if part have a non-standard transform (preview mode),
            // we wil think this part can't be support animation.
            if (!(bone.getPart().getTransform() instanceof OpenTransform3f parent)) {
                return new OutputPoint(null, mode);
            }
            // we will replace the standard transform to animated transform.
            var animatedTransform = new AnimatedTransform(parent);
            bone.getTransform().replaceChild(parent, animatedTransform);
            return new OutputPoint(animatedTransform, mode);
        }
    }

    private static class Effect extends Animator {

        public Effect(int duration, AnimatedMixMode type, List<SkinAnimationKeyframe> linkedKeyframes) {
            super(duration, linkedKeyframes, null);
        }

        @Override
        public void apply(Fragment fragment, Selector selector, int time, AnimationPlayState playState, ExecutionContext context) {
            // ..?
            if (selector instanceof Selector.Timeline) {
                var newValue = Objects.flatMap(fragment, it -> it.startValue);
                if (playState.lastInstructionValue != newValue) {
                    // start?
                    // stop?
                    if (newValue != null) {
                        newValue.get(context);
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

    private static class Channel {

        private final Selector selector;
        private final Fragment[] fragments;

        private Fragment current;

        public Channel(int duration, Selector selector, List<SkinAnimationKeyframe> keyframes) {
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

        private List<Fragment> create(int duration, List<SkinAnimationKeyframe> keyframes) {
            var defaultValue = calcDefaultValue();
            var builders = new ArrayList<FragmentBuilder>();
            for (var keyframe : keyframes) {
                var time = AnimationController.toTime(keyframe.getTime());
                var point = compile(keyframe.getPoints(), defaultValue);
                builders.add(new FragmentBuilder(time, keyframe.getFunction(), point.getKey(), point.getValue()));
            }
            builders.sort(Comparator.comparingInt(it -> it.leftTime));
            if (!builders.isEmpty()) {
                builders.add(0, builders.get(0).copy(0));
                builders.add(builders.get(builders.size() - 1).copy(duration));
            }
            for (int i = 1; i < builders.size(); i++) {
                var left = builders.get(i - 1);
                var right = builders.get(i);
                left.rightTime = right.leftTime;
                left.rightValue = right.leftValue;
            }
            // we need to remove invalid builder (e.g. zero duration),
            // but it will remove all builder when total duration is zero,
            // this is wrong we require to keep least one builder.
            if (builders.size() > 1) {
                var first = builders.remove(0);
                builders.removeIf(it -> it.leftTime == it.rightTime);
                builders.add(0, first);
            }
            return Collections.compactMap(builders, FragmentBuilder::build);
        }

        private Pair<AnimatedPointValue, AnimatedPointValue> compile(List<SkinAnimationPoint> points, float defaultValue) {
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
            var left = AnimatedPointValue.of(expressions.get(0), expressions.get(1), expressions.get(2));
            if (expressions.size() <= 3) {
                return Pair.of(left, left);
            }
            var right = AnimatedPointValue.of(expressions.get(3), expressions.get(4), expressions.get(5));
            return Pair.of(left, right);
        }

        private Expression compile(Object object, double defaultValue) {
            try {
                if (object instanceof Number number) {
                    return new Constant(number.doubleValue());
                }
                if (object instanceof String script) {
                    return AnimationEngine.compile(script);
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

    private static class Fragment {

        private final int startTime;
        private final AnimatedPointValue startValue;

        private final int endTime;
        private final AnimatedPointValue endValue;

        private final int length;
        private final SkinAnimationFunction function;

        public Fragment(int startTime, AnimatedPointValue startValue, int endTime, AnimatedPointValue endValue, SkinAnimationFunction function) {
            this.startTime = startTime;
            this.startValue = startValue;
            this.endTime = endTime;
            this.endValue = endValue;
            this.length = endTime - startTime;
            this.function = function;
        }

        public void apply(int time, Selector selector, ExecutionContext context, AnimatedPoint output) {
            if (time <= 0) {
                selector.apply(startValue.get(context), output);
                return;
            }
            if (time >= length) {
                selector.apply(endValue.get(context), output);
                return;
            }
            var from = startValue.get(context);
            var to = endValue.get(context);
            var t = function.apply(time / (float) length);
            var tx = OpenMath.lerp(t, from.getX(), to.getX());
            var ty = OpenMath.lerp(t, from.getY(), to.getY());
            var tz = OpenMath.lerp(t, from.getZ(), to.getZ());
            selector.apply(tx, ty, tz, output);
        }

        public boolean contains(int time) {
            return startTime <= time && time < endTime;
        }
    }

    private static class FragmentBuilder {

        private final SkinAnimationFunction function;

        private final int leftTime;
        private final AnimatedPointValue leftValue;

        private int rightTime;
        private AnimatedPointValue rightValue;

        FragmentBuilder(int time, SkinAnimationFunction function, AnimatedPointValue leftValue, AnimatedPointValue rightValue) {
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

    private static abstract class Selector {

        public void apply(float x, float y, float z, AnimatedPoint output) {
        }

        public void apply(Vector3f value, AnimatedPoint snapshot) {
            apply(value.getX(), value.getY(), value.getZ(), snapshot);
        }

        public static class Translation extends Selector {

            @Override
            public void apply(float x, float y, float z, AnimatedPoint output) {
                output.setTranslate(x, y, z);
            }
        }

        public static class Rotation extends Selector {

            @Override
            public void apply(float x, float y, float z, AnimatedPoint output) {
                output.setRotation(x, y, z);
            }
        }

        public static class Scale extends Selector {

            @Override
            public void apply(float x, float y, float z, AnimatedPoint output) {
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

    private static class OutputPoint extends AnimatedPoint {

        private final AnimatedTransform transform;

        public OutputPoint(AnimatedTransform transform, AnimatedMixMode mode) {
            this.transform = transform;
            if (transform != null) {
                transform.link(this, mode.ordinal(), mode == AnimatedMixMode.POST_MAIN);
            }
        }

        @Override
        public void setTranslate(float x, float y, float z) {
            // always update and mark dirty, because relies on flags by method called.
            translate.set(x, y, z);
            setDirty(0x10);
        }

        @Override
        public void setRotation(float x, float y, float z) {
            // always update and mark dirty, because relies on flags by method called.
            rotation.set(x, y, z);
            setDirty(0x20);
        }

        @Override
        public void setScale(float x, float y, float z) {
            // always update and mark dirty, because relies on flags by method called.
            scale.set(x, y, z);
            setDirty(0x40);
        }

        @Override
        public void setDirty(int newFlags) {
            super.setDirty(newFlags);
            if (transform != null) {
                transform.setDirty(newFlags);
            }
        }
    }
}
