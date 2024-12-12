package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.variable;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.PlayerSelector;

@FunctionalInterface
public interface PlayerVariableBinding extends LambdaVariableBinding {

    Object apply(final PlayerSelector entity);

    @Override
    default Result evaluate(final ExecutionContext context) {
        if (context.entity() instanceof PlayerSelector entity) {
            var result = apply(entity);
            return Result.parse(result);
        }
        return Result.NULL;
    }
}
