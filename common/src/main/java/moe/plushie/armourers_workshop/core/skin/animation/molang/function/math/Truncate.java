package moe.plushie.armourers_workshop.core.skin.animation.molang.function.math;

import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.animation.molang.function.Function;

import java.util.List;

/**
 * {@link Function} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns the closest value that is equal to the input value or closer to zero, and is equal to an integer
 */
public final class Truncate extends Function {

    private final Expression value;

    public Truncate(Expression name, List<Expression> arguments) {
        super(name, 1, arguments);
        this.value = arguments.get(0);
    }

    @Override
    public double compute(final ExecutionContext context) {
        return (long) value.compute(context);
    }
}
