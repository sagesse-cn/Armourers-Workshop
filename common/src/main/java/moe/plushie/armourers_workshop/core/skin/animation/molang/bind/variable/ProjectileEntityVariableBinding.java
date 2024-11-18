package moe.plushie.armourers_workshop.core.skin.animation.molang.bind.variable;

import moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector.ProjectileEntitySelector;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Result;

@FunctionalInterface
public interface ProjectileEntityVariableBinding extends LambdaVariableBinding {

    Object apply(final ProjectileEntitySelector entity);

    @Override
    default Result evaluate(final ExecutionContext context) {
        if (context.entity() instanceof ProjectileEntitySelector entity) {
            var result = apply(entity);
            return Result.parse(result);
        }
        return Result.NULL;
    }
}
