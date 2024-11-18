package moe.plushie.armourers_workshop.core.skin.animation.engine.bind;

import moe.plushie.armourers_workshop.compatibility.core.AbstractRegistryManager;
import moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector.EnchantmentSelector;
import moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector.ItemSelector;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemSelectorImpl implements ItemSelector {

    protected ItemStack itemStack;

    protected EnchantmentSelectorImpl enchantmentSelector = new EnchantmentSelectorImpl();

    public ItemSelectorImpl apply(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    @Override
    public String getId() {
        return AbstractRegistryManager.getItemKey(itemStack.getItem());
    }

    @Override
    public int getDamage() {
        return itemStack.getDamageValue();
    }

    @Override
    public int getMaxDamage() {
        return itemStack.getMaxDamage();
    }

    @Nullable
    @Override
    public EnchantmentSelector getEnchantment(String name) {
        var enchantment = AbstractRegistryManager.getEnchantment(itemStack, name);
        if (enchantment != null) {
            return enchantmentSelector.apply(enchantment);
        }
        return null;
    }

    @Override
    public boolean hasTag(String name) {
        return AbstractRegistryManager.hasItemTag(itemStack, name);
    }
}

