package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.variable;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.EntitySelector;

@FunctionalInterface
public interface EntityVariableBinding extends LambdaVariableBinding {

    Object apply(final EntitySelector entity);

    @Override
    default Result evaluate(final ExecutionContext context) {
        if (context.entity() instanceof EntitySelector entity) {
            var result = apply(entity);
            return Result.parse(result);
        }
        return Result.NULL;
    }
}
