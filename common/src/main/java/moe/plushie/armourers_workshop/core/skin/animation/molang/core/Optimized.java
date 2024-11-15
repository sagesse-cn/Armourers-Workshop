package moe.plushie.armourers_workshop.core.skin.animation.molang.core;


import moe.plushie.armourers_workshop.core.skin.animation.molang.impl.Visitor;

/**
 * {@link Expression} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * An optimized expression result value.
 */
public final class Optimized implements Expression {

    private final double doubleValue;
    private final String stringValue;

    private final Expression expression;

    public Optimized(Expression expression) {
        this.doubleValue = expression.getAsDouble();
        this.stringValue = expression.getAsString();
        this.expression = expression;
    }

    @Override
    public Expression visit(Visitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public double getAsDouble() {
        return doubleValue;
    }

    @Override
    public String getAsString() {
        return stringValue;
    }

    @Override
    public Expression getAsExpression() {
        return this;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public String toString() {
        return stringValue + "/*" + expression + "*/";
    }

    public Expression expression() {
        return expression;
    }
}
