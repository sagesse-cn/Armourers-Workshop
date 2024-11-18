package moe.plushie.armourers_workshop.core.skin.animation.molang.function.query;

import moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector.LivingEntitySelector;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.animation.molang.function.LivingEntityFunction;

import java.util.List;

public class ItemMaxDurability extends LivingEntityFunction {

    private final Expression slot;

    public ItemMaxDurability(Expression name, List<Expression> arguments) {
        super(name, 1, arguments);
        this.slot = arguments.get(0);
    }

    @Override
    public double compute(final LivingEntitySelector entity, final ExecutionContext context) {
        var item = entity.getEquippedItem(this.slot.evaluate(context).getAsString());
        if (item != null) {
            return item.getMaxDamage();
        }
        return 0;
    }
}
