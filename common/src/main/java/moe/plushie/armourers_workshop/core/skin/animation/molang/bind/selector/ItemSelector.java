package moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector;

import org.jetbrains.annotations.Nullable;

public interface ItemSelector {

    String getId();

    int getDamage();

    int getMaxDamage();

    @Nullable
    EnchantmentSelector getEnchantment(String name);


    boolean hasTag(String tag);
}
