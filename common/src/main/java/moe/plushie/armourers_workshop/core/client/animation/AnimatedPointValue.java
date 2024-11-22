package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.animation.molang.runtime.OptimizeContext;

@FunctionalInterface
public interface AnimatedPointValue {

    Vector3f get(final ExecutionContext context);

    default boolean isConstant() {
        return false;
    }

    static AnimatedPointValue of(Expression x, Expression y, Expression z) {
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
                    return new AnimatedPointValue() {
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
