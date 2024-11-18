package moe.plushie.armourers_workshop.core.skin.animation.molang.bind.variable;

import moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector.PlayerSelector;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Result;

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
