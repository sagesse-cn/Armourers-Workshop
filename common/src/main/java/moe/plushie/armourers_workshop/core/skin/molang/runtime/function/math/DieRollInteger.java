package moe.plushie.armourers_workshop.core.skin.molang.runtime.function.math;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.MathHelper;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.Function;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * {@link Function} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns a random integer value based on the input values:
 * <ul>
 *     <li>Three inputs generates the sum of <i>n</i> (first input) random values between the second (inclusive) and third input (inclusive)</li>
 *     <li>Four inputs generates the sum of <i>n</i> (first input) random values between the second (inclusive) and third input (inclusive), seeded by the fourth input</li>
 * </ul>
 */
public final class DieRollInteger extends Function {

    private final Expression rolls;
    private final Expression min;
    private final Expression max;
    @Nullable
    private final Expression seed;
    @Nullable
    private final Random random;

    public DieRollInteger(Expression name, List<Expression> arguments) {
        super(name, 3, arguments);
        this.rolls = arguments.get(0);
        this.min = arguments.get(1);
        this.max = arguments.get(2);
        this.seed = arguments.size() >= 4 ? arguments.get(3) : null;
        this.random = this.seed != null ? new Random() : null;
    }

    @Override
    public double compute(final ExecutionContext context) {
        final int rolls = MathHelper.floor(this.rolls.compute(context));
        final int min = MathHelper.floor(this.min.compute(context));
        final int max = MathHelper.ceil(this.max.compute(context));
        int sum = 0;
        Random random;

        if (this.random != null) {
            random = this.random;
            random.setSeed((long) this.seed.compute(context));
        } else {
            random = ThreadLocalRandom.current();
        }

        for (int i = 0; i < rolls; i++) {
            sum += min + random.nextInt(max + 1 - min);
        }

        return sum;
    }

    @Override
    public boolean isMutable() {
        return true;
    }
}
