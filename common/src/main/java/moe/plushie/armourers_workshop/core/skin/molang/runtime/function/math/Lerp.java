package moe.plushie.armourers_workshop.core.skin.molang.runtime.function.math;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.MathHelper;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.Function;

import java.util.List;

/**
 * {@link Function} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns the first value plus the difference between the first and second input values multiplied by the third input value
 */
public final class Lerp extends Function {

    private final Expression min;
    private final Expression max;
    private final Expression delta;

    public Lerp(Expression name, List<Expression> arguments) {
        super(name, 3, arguments);
        this.min = arguments.get(0);
        this.max = arguments.get(1);
        this.delta = arguments.get(2);
    }

    @Override
    public double compute(final ExecutionContext context) {
        return MathHelper.lerp(delta.compute(context), min.compute(context), max.compute(context));
    }
}
