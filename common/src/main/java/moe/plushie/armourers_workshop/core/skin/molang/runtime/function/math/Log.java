package moe.plushie.armourers_workshop.core.skin.molang.runtime.function.math;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.Function;

import java.util.List;

/**
 * {@link Function} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns the log value (euler base) of the input value
 */
public final class Log extends Function {

    private final Expression value;

    public Log(Expression name, List<Expression> arguments) {
        super(name, 1, arguments);
        this.value = arguments.get(0);
    }

    @Override
    public double compute(final ExecutionContext context) {
        return Math.log(value.compute(context));
    }
}
