package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.holder;

import moe.plushie.armourers_workshop.core.skin.molang.core.Assignable;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.NamedObject;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;

public abstract class VariableHolder extends NamedObject implements Expression, Assignable {

    @Override
    public abstract Result assign(Result value, ExecutionContext context);

    @Override
    public abstract Result evaluate(final ExecutionContext context);
}
