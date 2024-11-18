package moe.plushie.armourers_workshop.core.skin.animation.molang.bind.holder;

import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.NamedObject;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.animation.molang.function.Function;

import java.util.List;

public class FunctionHolder extends NamedObject implements Expression, Function.Factory<Function> {

    protected final Function.Factory<?> impl;

    public FunctionHolder(Function.Factory<?> impl) {
        this.impl = impl;
    }

    @Override
    public Function create(Expression receiver, List<Expression> arguments) {
        return impl.create(receiver, arguments);
    }

    @Override
    public Result evaluate(final ExecutionContext context) {
        // TODO: @SAGESSE it will get a lambda value, like: v.x = math.pow
        return Result.NULL;
    }

    @Override
    public boolean isMutable() {
        return false;
    }
}
