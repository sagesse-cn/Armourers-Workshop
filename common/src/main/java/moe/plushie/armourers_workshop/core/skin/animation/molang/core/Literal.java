package moe.plushie.armourers_workshop.core.skin.animation.molang.core;

import moe.plushie.armourers_workshop.core.skin.animation.molang.impl.Visitor;

public final class Literal implements Expression {

    private final String value;

    public Literal(String value) {
        this.value = value;
    }

    @Override
    public Expression visit(Visitor visitor) {
        return visitor.visitLiteral(this);
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public double getAsDouble() {
        return 0;
    }

    @Override
    public String getAsString() {
        return value;
    }

    @Override
    public Expression getAsExpression() {
        return this;
    }

    @Override
    public String toString() {
        return "'" + value + "'";
    }
}
