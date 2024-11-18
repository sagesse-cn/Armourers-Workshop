package moe.plushie.armourers_workshop.core.skin.animation.molang.ast;

import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Optimizable;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Visitor;
import moe.plushie.armourers_workshop.core.skin.animation.molang.runtime.Scope;

import java.util.List;
import java.util.StringJoiner;

/**
 * {@link Expression} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Contains a collection of sub-expressions that evaluate before returning the last expression, or 0 if no return is defined.
 * Sub-expressions have no bearing on the final return with exception for where they may be setting variable values
 */
public final class Compound implements Expression, Optimizable {

    private final List<Expression> expressions;

    public Compound(List<Expression> expressions) {
        this.expressions = expressions;
    }

    @Override
    public Result evaluate(final ExecutionContext context) {
        var scope = context.stack().scope().push(Scope.Kind.BLOCK);
        for (var expression : expressions) {
            expression.evaluate(context);
            if (scope.isContinueOrBreakOrReturn()) {
                break;
            }
        }
        return scope.pop();
    }

    @Override
    public Expression visit(Visitor visitor) {
        return visitor.visitCompound(this);
    }

    @Override
    public boolean isMutable() {
        for (var expression : expressions) {
            if (expression.isMutable()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        var joiner = new StringJoiner("; ", "{ ", "; }");
        for (var expr : expressions) {
            joiner.add(expr.toString());
        }
        return joiner.toString();
    }

    public List<Expression> expressions() {
        return expressions;
    }
}
