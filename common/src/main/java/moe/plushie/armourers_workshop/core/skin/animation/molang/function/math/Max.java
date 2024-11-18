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
 * Returns the greater of the two input values
 */
public final class Max extends Function {

    private final Expression valueA;
    private final Expression valueB;

    public Max(Expression name, List<Expression> arguments) {
        super(name, 2, arguments);
        this.valueA = arguments.get(0);
        this.valueB = arguments.get(1);
    }

    @Override
    public double compute(final ExecutionContext context) {
        return Math.max(valueA.compute(context), valueB.compute(context));
    }
}
