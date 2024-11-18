package moe.plushie.armourers_workshop.core.skin.animation.molang.ast;

import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Assignable;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Name;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Optimizable;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Visitor;
import moe.plushie.armourers_workshop.init.ModLog;

import java.util.function.Function;

/**
 * Struct accessing expression implementation,
 * access to a property on another expression result.
 *
 * <p>Example property accessing expressions: {@code v.x},
 * {@code v.location.x}, {@code 'str'.length}, {@code query.print}</p>
 */
public final class StructAccess implements Expression, Optimizable, Assignable {

    private final Expression receiver;
    private final Name name;

    public StructAccess(Expression receiver, String name) {
        this.receiver = receiver;
        this.name = Name.of(name);
    }

    private Result create(final ExecutionContext context) {
        var result = receiver.evaluate(context);
        if (result.isNull()) {
            // create a new struct instance when receiver not exists.
            if (!(receiver instanceof Assignable variable)) {
                ModLog.warn("Cannot assign a value to {}", receiver);
                return Result.NULL; // the receiver is constant variable.
            }
            result = Result.newStruct();
            variable.assign(result, context);
        }
        return result;
    }

    @Override
    public Result assign(Result value, ExecutionContext context) {
        var result = create(context);
        result.set(name, value);
        return value;
    }

    @Override
    public Result assign(Function<Result, Result> operator, ExecutionContext context) {
        var result = create(context);
        var value = operator.apply(result.get(name));
        result.set(name, value);
        return value;
    }

    @Override
    public Result evaluate(final ExecutionContext context) {
        var result = receiver.evaluate(context);
        return result.get(name);
    }

    @Override
    public Expression visit(Visitor visitor) {
        return visitor.visitStructAccess(this);
    }

    @Override
    public boolean isMutable() {
        return receiver.isMutable();
    }

    @Override
    public String toString() {
        return receiver + "." + name;
    }

    public Expression receiver() {
        return receiver;
    }

    public String name() {
        return name.toString();
    }
}
