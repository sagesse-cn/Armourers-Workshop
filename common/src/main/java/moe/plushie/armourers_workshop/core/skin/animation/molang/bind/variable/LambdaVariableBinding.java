package moe.plushie.armourers_workshop.core.skin.animation.molang.bind.variable;

import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Result;

@FunctionalInterface
public interface LambdaVariableBinding {

    Result evaluate(final ExecutionContext context);
}
