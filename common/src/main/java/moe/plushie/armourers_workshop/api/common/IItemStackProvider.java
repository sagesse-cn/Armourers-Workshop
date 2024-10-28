package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.core.utils.Collections;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public interface IItemStackProvider {

    Iterable<ItemStack> getArmorSlots(Entity entity);

    Iterable<ItemStack> getHandSlots(Entity entity);

    default Iterable<ItemStack> getAllSlots(Entity entity) {
        return Collections.concat(getHandSlots(entity), getArmorSlots(entity));
    }
}
