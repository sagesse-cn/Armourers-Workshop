package moe.plushie.armourers_workshop.core.skin.animation.molang.bind.holder;

import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Result;

public class StaticVariableHolder extends ConstantHolder {

    private final Result value;

    public StaticVariableHolder(Result value) {
        this.value = value;
    }

    @Override
    public Result evaluate(final ExecutionContext context) {
        return value;
    }
}
