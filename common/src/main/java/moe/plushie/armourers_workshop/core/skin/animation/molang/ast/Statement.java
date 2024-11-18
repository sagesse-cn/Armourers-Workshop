package moe.plushie.armourers_workshop.core.skin.animation.molang.ast;


import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Visitor;
import moe.plushie.armourers_workshop.core.skin.animation.molang.runtime.Scope;

/**
 * Statement expression implementation. Statement expressions
 * do not have children expressions, they just have a single
 * operation type.
 *
 * <p>Example statement expressions: {@code break}, {@code continue}</p>
 */
public final class Statement implements Expression {

    private final Operator op;

    public Statement(Operator op) {
        this.op = op;
    }

    @Override
    public Result evaluate(final ExecutionContext context) {
        context.stack().scope().setInterrupt(op.mode());
        return Result.NULL;
    }

    @Override
    public Expression visit(Visitor visitor) {
        return visitor.visitStatement(this);
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public String toString() {
        return op.symbol();
    }

    public Operator op() {
        return op;
    }

    public enum Operator {
        BREAK("break", Scope.State.BREAK),
        CONTINUE("continue", Scope.State.CONTINUE);

        private final Scope.State mode;
        private final String symbol;

        Operator(final String symbol, final Scope.State mode) {
            this.mode = mode;
            this.symbol = symbol;
        }

        public Scope.State mode() {
            return mode;
        }

        public String symbol() {
            return symbol;
        }
    }
}
