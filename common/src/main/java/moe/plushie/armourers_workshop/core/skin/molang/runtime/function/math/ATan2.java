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
 * Returns the arc-tangent theta of the input rectangular coordinate values (y,x), with the output converted to degrees
 */
public final class ATan2 extends Function {

    private final Expression y;
    private final Expression x;

    public ATan2(Expression name, List<Expression> arguments) {
        super(name, 2, arguments);
        this.y = arguments.get(0);
        this.x = arguments.get(1);
    }

    @Override
    public double compute(final ExecutionContext context) {
        return Math.atan2(y.compute(context), x.compute(context)) * MathHelper.RAD_TO_DEG;
    }
}
