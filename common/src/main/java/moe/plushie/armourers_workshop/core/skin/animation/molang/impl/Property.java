package moe.plushie.armourers_workshop.core.skin.animation.molang.impl;

import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Expression;

public interface Property extends Supplier {

    void update(Expression expression);

    default boolean isNull() {
        return false;
    }
}

