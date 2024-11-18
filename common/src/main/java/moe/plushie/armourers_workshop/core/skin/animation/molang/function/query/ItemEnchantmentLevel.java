package moe.plushie.armourers_workshop.core.skin.animation.molang.function.query;

import moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector.LivingEntitySelector;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.animation.molang.function.LivingEntityFunction;

import java.util.List;

public class ItemEnchantmentLevel extends LivingEntityFunction {

    private final Expression slot;
    private final Expression enchantment;

    public ItemEnchantmentLevel(Expression name, List<Expression> arguments) {
        super(name, 2, arguments);
        this.slot = arguments.get(0);
        this.enchantment = arguments.get(1);
    }

    @Override
    public double compute(final LivingEntitySelector entity, final ExecutionContext context) {
        var item = entity.getEquippedItem(this.slot.evaluate(context).getAsString());
        if (item == null) {
            return 0;
        }
        var enchantment = item.getEnchantment(this.enchantment.evaluate(context).getAsString());
        if (enchantment != null) {
            return enchantment.getLevel();
        }
        return 0;
    }
}
