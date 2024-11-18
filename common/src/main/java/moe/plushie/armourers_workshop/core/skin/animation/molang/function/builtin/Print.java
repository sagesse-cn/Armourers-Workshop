package moe.plushie.armourers_workshop.core.skin.animation.molang.function.builtin;

import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.animation.molang.function.Function;
import moe.plushie.armourers_workshop.init.ModLog;

import java.util.List;
import java.util.StringJoiner;

/**
 * {@link Function} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Print the debug log.
 */
public class Print extends Function {

    public Print(Expression name, List<Expression> arguments) {
        super(name, 1, arguments);
    }

    @Override
    public double compute(final ExecutionContext context) {
        var contents = new StringJoiner(" ");
        for (var argument : arguments()) {
            contents.add(argument.evaluate(context).toString());
        }
        ModLog.debug("{}", contents);
        return 0;
    }

    @Override
    public boolean isMutable() {
        return true;
    }
}
