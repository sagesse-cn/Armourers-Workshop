package moe.plushie.armourers_workshop.core.skin.molang.core.ast;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;

/**
 * {@link Expression} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * An immutable double value
 */
public final class Constant implements Expression {

    public static final Constant ONE = new Constant(Result.ONE);
    public static final Constant ZERO = new Constant(Result.ZERO);

    private final Result value;

    public Constant(double value) {
        this.value = Result.valueOf(value);
    }

    public Constant(Result value) {
        this.value = value;
    }

    @Override
    public Result evaluate(final ExecutionContext context) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public Result value() {
        return value;
    }
}
