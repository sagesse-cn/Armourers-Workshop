package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.variable;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;

@FunctionalInterface
public interface LambdaVariableBinding {

    Result evaluate(final ExecutionContext context);
}
