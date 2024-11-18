package moe.plushie.armourers_workshop.core.skin.animation.molang.ast;


import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Result;

/**
 * {@link Expression} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * An optimized expression result value.
 */
public final class Optimized implements Expression {

    private final Result value;
    private final Expression expression;

    public Optimized(Result value, Expression expression) {
        this.value = value;
        this.expression = expression;
    }

    @Override
    public Result evaluate(final ExecutionContext context) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public String toString() {
        return value + "/*" + expression + "*/";
    }

    public Expression expression() {
        return expression;
    }
}
