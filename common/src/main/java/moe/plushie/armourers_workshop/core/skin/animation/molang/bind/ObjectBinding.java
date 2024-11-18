package moe.plushie.armourers_workshop.core.skin.animation.molang.bind;

import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Result;

public interface ObjectBinding extends Expression {

    Expression getProperty(String name);

    @Override
    default Result evaluate(final ExecutionContext context) {
        return Result.NULL;
    }
}
