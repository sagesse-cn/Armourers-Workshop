package moe.plushie.armourers_workshop.core.skin.molang.runtime.function.query;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.EntitySelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.EntityFunction;

import java.util.List;

public class PositionDelta extends EntityFunction {

    private final Expression axis;

    public PositionDelta(Expression name, List<Expression> arguments) {
        super(name, 1, arguments);
        this.axis = arguments.get(0);
    }

    @Override
    public double compute(final EntitySelector entity, final ExecutionContext context) {
        int axis = this.axis.evaluate(context).getAsInt();
        double partialTicks = entity.getPartialTick();
        return switch (axis) {
            case 0 -> entity.getX(partialTicks) - entity.getZ(0);
            case 1 -> entity.getY(partialTicks) - entity.getZ(0);
            case 2 -> entity.getZ(partialTicks) - entity.getZ(0);
            default -> 0;
        };
    }
}
