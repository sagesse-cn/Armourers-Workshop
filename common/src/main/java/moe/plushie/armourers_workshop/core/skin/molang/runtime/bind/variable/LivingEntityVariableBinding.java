package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.variable;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.LivingEntitySelector;

@FunctionalInterface
public interface LivingEntityVariableBinding extends LambdaVariableBinding {

    Object apply(final LivingEntitySelector entity);

    @Override
    default Result evaluate(final ExecutionContext context) {
        if (context.entity() instanceof LivingEntitySelector entity) {
            var result = apply(entity);
            return Result.parse(result);
        }
        return Result.NULL;
    }
}
