package moe.plushie.armourers_workshop.core.skin.animation.molang.ast;

import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Visitor;

public final class Literal implements Expression {

    private final Result value;

    public Literal(String value) {
        this.value = Result.valueOf(value);
    }

    @Override
    public Result evaluate(final ExecutionContext context) {
        return value;
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
    public String toString() {
        return "'" + value + "'";
    }
}
