package moe.plushie.armourers_workshop.core.skin.animation.molang.function;

import moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector.EntitySelector;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Result;

import java.util.List;

public abstract class EntityFunction extends Function {

    protected EntityFunction(Expression receiver, int requirement, List<Expression> arguments) {
        super(receiver, requirement, arguments);
    }

    public abstract double compute(final EntitySelector entity, final ExecutionContext context);

    public Result evaluate(final EntitySelector entity, final ExecutionContext context) {
        return Result.valueOf(compute(entity, context));
    }

    @Override
    public final double compute(final ExecutionContext context) {
        if (context.entity() instanceof EntitySelector entity) {
            return compute(entity, context);
        }
        //ModLog.warn("Cannot invoke a entity function {} by {}", receiver, context.entity());
        return 0;
    }

    @Override
    public final Result evaluate(final ExecutionContext context) {
        if (context.entity() instanceof EntitySelector entity) {
            return evaluate(entity, context);
        }
        //ModLog.warn("Cannot invoke a entity function {} by {}", receiver, context.entity());
        return Result.NULL;
    }

    @Override
    public boolean isMutable() {
        return true;
    }
}
