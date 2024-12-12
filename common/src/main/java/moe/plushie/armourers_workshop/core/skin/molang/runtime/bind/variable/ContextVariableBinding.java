package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.variable;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.ContextSelector;

@FunctionalInterface
public interface ContextVariableBinding extends LambdaVariableBinding {

    Object apply(final ContextSelector entity);

    @Override
    default Result evaluate(final ExecutionContext context) {
        if (context instanceof ContextSelector context1) {
            var result = apply(context1);
            return Result.parse(result);
        }
        return Result.NULL;
    }
}
