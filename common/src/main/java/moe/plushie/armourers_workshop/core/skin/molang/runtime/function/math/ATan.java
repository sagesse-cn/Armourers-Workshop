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
 * Returns the arc-tangent of the input value angle, with the input angle converted to radians
 */
public final class ATan extends Function {

    private final Expression value;

    public ATan(Expression name, List<Expression> arguments) {
        super(name, 1, arguments);
        this.value = arguments.get(0);
    }

    @Override
    public double compute(final ExecutionContext context) {
        return Math.atan(value.compute(context) * MathHelper.DEG_TO_RAD);
    }
}
