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
 * Returns the <a href="https://en.wikipedia.org/wiki/Hermite_polynomials">Hermite</a>> basis <code>3t^2 - 2t^3</code> curve interpolation value based on the input value
 */
public final class HermiteBlend extends Function {

    private final Expression valueA;

    public HermiteBlend(Expression name, List<Expression> arguments) {
        super(name, 1, arguments);
        this.valueA = arguments.get(0);
    }

    @Override
    public double compute(final ExecutionContext context) {
        double value = valueA.compute(context);
        return (3 * value * value) - (2 * value * value * value);
    }
}
