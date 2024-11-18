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
 * Returns the lesser of the two input values
 */
public final class Min extends Function {

    private final Expression valueA;
    private final Expression valueB;

    public Min(Expression name, List<Expression> arguments) {
        super(name, 2, arguments);
        this.valueA = arguments.get(0);
        this.valueB = arguments.get(1);
    }

    @Override
    public double compute(final ExecutionContext context) {
        return Math.min(valueA.compute(context), valueB.compute(context));
    }
}
