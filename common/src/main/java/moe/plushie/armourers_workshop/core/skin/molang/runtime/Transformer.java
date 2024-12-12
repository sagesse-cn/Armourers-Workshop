package moe.plushie.armourers_workshop.core.skin.molang.runtime;

import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Visitor;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.ArrayAccess;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Binary;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Call;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Compound;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Return;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.StructAccess;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Ternary;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Unary;

import java.util.ArrayList;
import java.util.List;

public abstract class Transformer implements Visitor {

    public Expression transform(Expression expression) {
        return expression.visit(this);
    }

    public List<Expression> transform(List<Expression> expressions) {
        var results = new ArrayList<Expression>();
        for (var expression : expressions) {
            results.add(transform(expression));
        }
        return results;
    }

    @Override
    public Expression visitStructAccess(StructAccess expression) {
        return new StructAccess(transform(expression.receiver()), expression.name());
    }

    @Override
    public Expression visitArrayAccess(final ArrayAccess expression) {
        return new ArrayAccess(transform(expression.receiver()), transform(expression.index()));
    }

    @Override
    public Expression visitCall(final Call expression) {
        return new Call(transform(expression.receiver()), transform(expression.arguments()));
    }

    @Override
    public Expression visitUnary(final Unary expression) {
        return new Unary(expression.op(), transform(expression.value()));
    }

    @Override
    public Expression visitBinary(final Binary expression) {
        return new Binary(expression.op(), transform(expression.left()), transform(expression.right()));
    }

    @Override
    public Expression visitTernary(final Ternary expression) {
        return new Ternary(transform(expression.condition()), transform(expression.trueValue()), transform(expression.falseValue()));
    }

    @Override
    public Expression visitCompound(final Compound expression) {
        return new Compound(transform(expression.expressions()));
    }

    @Override
    public Expression visitReturn(final Return expression) {
        return new Return(transform(expression.value()));
    }
}
