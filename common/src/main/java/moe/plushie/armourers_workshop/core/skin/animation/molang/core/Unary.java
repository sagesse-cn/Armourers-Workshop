package moe.plushie.armourers_workshop.core.skin.animation.molang.core;

import moe.plushie.armourers_workshop.core.skin.animation.molang.impl.Visitor;

/**
 * Unary expression implementation, performs a single operation
 * to a single expression, like logical negation, arithmetical
 * negation
 *
 * <p>Example unary expressions: {@code -hello}, {@code !p},
 * {@code !q}, {@code -(10 * 5)}</p>
 */
public final class Unary implements Expression {

    private final Op op;
    private final Expression value;

    public Unary(Op op, Expression value) {
        this.op = op;
        this.value = value;
    }

    @Override
    public Expression visit(Visitor visitor) {
        return visitor.visitUnary(this);
    }

    @Override
    public boolean isMutable() {
        return value.isMutable();
    }

    @Override
    public double getAsDouble() {
        return getAsExpression().getAsDouble();
    }

    @Override
    public Expression getAsExpression() {
        return op.compute(value);
    }

    @Override
    public String toString() {
        var contents = value.toString();
        if (value instanceof Constant) {
            contents = "(" + contents + ")";
        }
        return op.symbol() + contents;
    }

    public Op op() {
        return op;
    }

    public Expression value() {
        return value;
    }

    public enum Op {
        LOGICAL_NEGATION("!") {
            @Override
            public Expression compute(Expression expression) {
                if (expression.getAsBoolean()) {
                    return Constant.ZERO;
                }
                return Constant.ONE;
            }
        },
        ARITHMETICAL_NEGATION("-") {
            @Override
            public Expression compute(Expression expression) {
                var result = -expression.getAsDouble();
                return new Constant(result);
            }
        };

        private final String symbol;

        Op(final String symbol) {
            this.symbol = symbol;
        }

        public String symbol() {
            return symbol;
        }

        /**
         * Computing the mathematical result of input arguments
         *
         * @param value The first input argument
         * @return The computed value of the two inputs
         */
        public abstract Expression compute(Expression value);
    }
}
