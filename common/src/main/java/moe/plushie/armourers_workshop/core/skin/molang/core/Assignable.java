package moe.plushie.armourers_workshop.core.skin.molang.core;

import java.util.function.Function;

public interface Assignable extends Expression {

    Result assign(final Result value, final ExecutionContext context);

    default Result assign(final Function<Result, Result> operator, final ExecutionContext context) {
        var cur = evaluate(context);
        return assign(operator.apply(cur), context);
    }
}

