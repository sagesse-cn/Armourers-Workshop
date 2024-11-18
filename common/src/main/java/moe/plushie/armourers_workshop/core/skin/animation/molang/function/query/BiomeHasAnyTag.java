package moe.plushie.armourers_workshop.core.skin.animation.molang.function.query;

import moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector.EntitySelector;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.animation.molang.function.EntityFunction;

import java.util.List;

public class BiomeHasAnyTag extends EntityFunction {

    private final List<Expression> tags;

    public BiomeHasAnyTag(Expression name, List<Expression> arguments) {
        super(name, 1, arguments);
        this.tags = arguments;
    }

    @Override
    public double compute(final EntitySelector entity, final ExecutionContext context) {
        var biome = entity.getBiome();
        if (biome == null) {
            return 0;
        }
        for (var tag : this.tags) {
            if (biome.hasTag(tag.evaluate(context).getAsString())) {
                return 1;
            }
        }
        return 0; // missing match.
    }
}

