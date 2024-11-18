package moe.plushie.armourers_workshop.core.skin.animation.molang.function.math;

import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.animation.molang.function.Function;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * {@link Function} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns a random value based on the input values:
 * <ul>
 *     <li>A single input generates a value between 0 and that input (exclusive)</li>
 *     <li>Two inputs generates a random value between the first (inclusive) and second input (exclusive)</li>
 *     <li>Three inputs generates a random value between the first (inclusive) and second input (exclusive), seeded by the third input</li>
 * </ul>
 */
public final class Random extends Function {

    private final Expression valueA;
    @Nullable
    private final Expression valueB;
    @Nullable
    private final Expression seed;
    @Nullable
    private final java.util.Random random;

    public Random(Expression name, List<Expression> arguments) {
        super(name, 1, arguments);
        this.valueA = arguments.get(0);
        this.valueB = arguments.size() >= 2 ? arguments.get(1) : null;
        this.seed = arguments.size() >= 3 ? arguments.get(2) : null;
        this.random = this.seed != null ? new java.util.Random() : null;
    }

    @Override
    public double compute(final ExecutionContext context) {
        double result;
        double valueA = this.valueA.compute(context);

        if (this.random != null) {
            this.random.setSeed((long) this.seed.compute(context));
            result = this.random.nextDouble();
        } else {
            result = Math.random();
        }

        if (this.valueB != null) {
            double valueB = this.valueB.compute(context);
            double min = Math.min(valueA, valueB);
            double max = Math.max(valueA, valueB);
            result = min + result * (max - min);
        } else {
            result = result * valueA;
        }

        return result;
    }

    @Override
    public boolean isMutable() {
        return true;
    }
}
