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
 * Returns the largest value that is less than or equal to the input value and is equal to an integer
 */
public final class Floor extends Function {

    private final Expression value;

    public Floor(Expression name, List<Expression> arguments) {
        super(name, 1, arguments);
        this.value = arguments.get(0);
    }

    @Override
    public double compute(final ExecutionContext context) {
        return Math.floor(value.compute(context));
    }
}
