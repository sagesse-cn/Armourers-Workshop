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
 * Returns the first input value if is larger than the second input value and less than the third input value; or else returns the nearest of the second two input values
 */
public final class Clamp extends Function {

    private final Expression value;
    private final Expression min;
    private final Expression max;

    public Clamp(Expression name, List<Expression> arguments) {
        super(name, 3, arguments);
        this.value = arguments.get(0);
        this.min = arguments.get(1);
        this.max = arguments.get(2);
    }

    @Override
    public double compute(final ExecutionContext context) {
        return MathHelper.clamp(value.compute(context), min.compute(context), max.compute(context));
    }
}
