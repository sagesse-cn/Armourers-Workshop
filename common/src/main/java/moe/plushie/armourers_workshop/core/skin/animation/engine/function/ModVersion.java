package moe.plushie.armourers_workshop.core.skin.animation.engine.function;

import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.animation.molang.function.Function;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;

import java.util.List;

public class ModVersion extends Function {

    private final Expression modId;

    public ModVersion(Expression name, List<Expression> arguments) {
        super(name, 1, arguments);
        this.modId = arguments.get(0);
    }

    @Override
    public double compute(final ExecutionContext context) {
        return 0;
    }

    @Override
    public Result evaluate(final ExecutionContext context) {
        var modId = this.modId.evaluate(context);
        var version = EnvironmentManager.getModVersion(modId.getAsString());
        if (version != null) {
            return Result.valueOf(version);
        }
        return Result.NULL;
    }

    @Override
    public boolean isMutable() {
        return true;
    }
}
