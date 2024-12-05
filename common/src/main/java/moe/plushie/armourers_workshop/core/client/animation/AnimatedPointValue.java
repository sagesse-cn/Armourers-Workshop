package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationPoint;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.animation.molang.runtime.OptimizeContext;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface AnimatedPointValue {

    boolean isConstant();

    @FunctionalInterface
    interface Bone extends AnimatedPointValue {

        Vector3f get(final ExecutionContext context);

        default boolean isConstant() {
            return false;
        }

        static Bone of(Expression x, Expression y, Expression z) {
            var variable = new Vector3f();
            var optimizeContext = OptimizeContext.DEFAULT;
            if (z.isMutable()) {
                if (y.isMutable()) {
                    if (x.isMutable()) {
                        return context -> {
                            variable.setX((float) x.compute(context));
                            variable.setY((float) y.compute(context));
                            variable.setZ((float) z.compute(context));
                            return variable;
                        };
                    } else {
                        variable.setX((float) x.compute(optimizeContext));
                        return context -> {
                            variable.setY((float) y.compute(context));
                            variable.setZ((float) z.compute(context));
                            return variable;
                        };
                    }
                } else {
                    variable.setY((float) y.compute(optimizeContext));
                    if (x.isMutable()) {
                        return context -> {
                            variable.setX((float) x.compute(context));
                            variable.setZ((float) z.compute(context));
                            return variable;
                        };
                    } else {
                        variable.setX((float) x.compute(optimizeContext));
                        return context -> {
                            variable.setZ((float) z.compute(context));
                            return variable;
                        };
                    }
                }
            } else {
                variable.setZ((float) z.compute(optimizeContext));
                if (y.isMutable()) {
                    if (x.isMutable()) {
                        return context -> {
                            variable.setX((float) x.compute(context));
                            variable.setY((float) y.compute(context));
                            return variable;
                        };
                    } else {
                        variable.setX((float) x.compute(optimizeContext));
                        return context -> {
                            variable.setY((float) y.compute(context));
                            return variable;
                        };
                    }
                } else {
                    variable.setY((float) y.compute(optimizeContext));
                    if (x.isMutable()) {
                        return context -> {
                            variable.setX((float) x.compute(context));
                            return variable;
                        };
                    } else {
                        variable.setX((float) x.compute(optimizeContext));
                        return new Bone() {
                            @Override
                            public Vector3f get(ExecutionContext context) {
                                return variable;
                            }

                            @Override
                            public boolean isConstant() {
                                return true;
                            }
                        };
                    }
                }
            }
        }
    }

    @FunctionalInterface
    interface Effect extends AnimatedPointValue {

        @Nullable
        Runnable apply(final ExecutionContext context);

        default boolean isConstant() {
            return false;
        }

        static Effect of(List<Effect> effects) {
            if (effects.size() == 1) {
                return effects.get(0);
            }
            return context -> {
                var handlers = new ArrayList<Runnable>();
                for (var effect : effects) {
                    var handler = effect.apply(context);
                    if (handler != null) {
                        handlers.add(handler);
                    }
                }
                return () -> handlers.forEach(Runnable::run);
            };
        }
    }

    @FunctionalInterface
    interface Sound extends Effect {

        static Sound of(SkinAnimationPoint.Sound sound) {

//            context -> {
//                SmartSoundManager.getInstance().playSound(sound.getProvider(), 1, 1);
//                return null;
//            });
            return null;
        }
    }
}
