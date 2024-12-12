package moe.plushie.armourers_workshop.core.skin.molang.thirdparty.function;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.Function;

import java.util.List;

public class DumpFunction extends Function {

    public DumpFunction(Expression receiver, List<Expression> arguments) {
        super(receiver, 0, arguments);
    }

    public static DumpFunction items(Expression name, List<Expression> arguments) {
        return new DumpFunction(name, arguments);
    }

    public static DumpFunction blocks(Expression name, List<Expression> arguments) {
        return new DumpFunction(name, arguments);
    }

    public static DumpFunction effects(Expression name, List<Expression> arguments) {
        return new DumpFunction(name, arguments);
    }

    public static Function biomes(Expression name, List<Expression> arguments) {
        return new DumpFunction(name, arguments);
    }

    public static DumpFunction mods(Expression name, List<Expression> arguments) {
        return new DumpFunction(name, arguments);
    }

    @Override
    public double compute(final ExecutionContext context) {
        // send command
        return 0;
    }

    @Override
    public boolean isMutable() {
        return true;
    }
}
