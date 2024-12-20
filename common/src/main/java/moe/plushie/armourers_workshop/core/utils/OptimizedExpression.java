package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.core.math.OpenVector2f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.OptimizeContext;

@FunctionalInterface
public interface OptimizedExpression<T> {

    T evaluate(final ExecutionContext context);

    default boolean isConstant() {
        return false;
    }

    static OptimizedExpression<OpenVector2f> of(Expression x, Expression y) {
        var variable = new OpenVector2f();
        var optimizeContext = OptimizeContext.DEFAULT;
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
                return new OptimizedExpression<>() {

                    @Override
                    public OpenVector2f evaluate(ExecutionContext context) {
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

    static OptimizedExpression<OpenVector3f> of(Expression x, Expression y, Expression z) {
        var variable = new OpenVector3f();
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
                    return new OptimizedExpression<>() {

                        @Override
                        public OpenVector3f evaluate(ExecutionContext context) {
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
