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
 * Returns the input value raised to the power of the second input value
 */
public final class Pow extends Function {

    private final Expression value;
    private final Expression power;

    public Pow(Expression name, List<Expression> arguments) {
        super(name, 2, arguments);
        this.value = arguments.get(0);
        this.power = arguments.get(1);
    }

    @Override
    public double compute(final ExecutionContext context) {
        return Math.pow(value.compute(context), power.compute(context));
    }
}
