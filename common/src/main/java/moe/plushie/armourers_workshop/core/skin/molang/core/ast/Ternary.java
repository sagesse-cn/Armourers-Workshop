package moe.plushie.armourers_workshop.core.skin.molang.core.ast;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Optimizable;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.core.Visitor;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.PrettyPrinter;

/**
 * {@link Expression} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns one of two stored values dependent on the result of the stored condition value.
 * This returns such that a non-zero result from the condition will return the <b>true</b> stored value, otherwise returning the <b>false</b> stored value
 */
public final class Ternary implements Expression, Optimizable {

    private final Expression condition;
    private final Expression trueValue;
    private final Expression falseValue;

    public Ternary(Expression condition, Expression trueValue, Expression falseValue) {
        this.condition = condition;
        this.trueValue = trueValue;
        this.falseValue = falseValue;
    }

    @Override
    public Result evaluate(final ExecutionContext context) {
        if (condition.test(context)) {
            return trueValue.evaluate(context);
        }
        return falseValue.evaluate(context);
    }

    @Override
    public Expression visit(Visitor visitor) {
        return visitor.visitTernary(this);
    }

    @Override
    public boolean isMutable() {
        return condition.isMutable() || trueValue.isMutable() || falseValue.isMutable();
    }

    @Override
    public String toString() {
        return PrettyPrinter.toString(this);
    }

    public Expression condition() {
        return condition;
    }

    public Expression trueValue() {
        return trueValue;
    }

    public Expression falseValue() {
        return falseValue;
    }
}
