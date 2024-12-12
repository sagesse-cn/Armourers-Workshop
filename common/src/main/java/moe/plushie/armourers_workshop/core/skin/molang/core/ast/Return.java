package moe.plushie.armourers_workshop.core.skin.molang.core.ast;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.core.Visitor;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.Scope;

/**
 * {@link Expression} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * The following statement and stops execution of the expression, returns the value computed.
 */
public final class Return implements Expression {

    private final Expression value;

    public Return(Expression value) {
        this.value = value;
    }

    @Override
    public Result evaluate(final ExecutionContext context) {
        var result = value.evaluate(context);
        context.stack().scope().setInterrupt(Scope.State.RETURN, result);
        return result;
    }

    @Override
    public Expression visit(Visitor visitor) {
        return visitor.visitReturn(this);
    }

    @Override
    public boolean isMutable() {
        return value.isMutable();
    }

    @Override
    public String toString() {
        return "return " + value;
    }

    public Expression value() {
        return value;
    }
}
