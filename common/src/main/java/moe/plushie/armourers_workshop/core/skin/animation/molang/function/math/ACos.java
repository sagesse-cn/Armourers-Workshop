package moe.plushie.armourers_workshop.core.skin.animation.molang.function.math;

import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.animation.molang.function.Function;
import moe.plushie.armourers_workshop.core.skin.animation.molang.runtime.MathHelper;

import java.util.List;

/**
 * {@link Function} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns the arc-cosine of the input value angle, with the input angle converted to radians
 */
public final class ACos extends Function {

    private final Expression value;

    public ACos(Expression name, List<Expression> arguments) {
        super(name, 1, arguments);
        this.value = arguments.get(0);
    }

    @Override
    public double compute(final ExecutionContext context) {
        return Math.acos(value.compute(context) * MathHelper.DEG_TO_RAD);
    }
}
