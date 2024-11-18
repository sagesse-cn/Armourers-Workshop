package moe.plushie.armourers_workshop.core.skin.animation.molang.bind.variable;

import moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector.ContextSelector;
import moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector.LevelSelector;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Result;

@FunctionalInterface
public interface LevelVariableBinding extends LambdaVariableBinding {

    Object apply(final LevelSelector entity);

    @Override
    default Result evaluate(final ExecutionContext context) {
        if (context instanceof ContextSelector context1) {
            var level = context1.getLevel();
            if (level != null) {
                var result = apply(level);
                return Result.parse(result);
            }
        }
        return Result.NULL;
    }
}
