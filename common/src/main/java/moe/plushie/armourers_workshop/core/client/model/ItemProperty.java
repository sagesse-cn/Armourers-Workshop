package moe.plushie.armourers_workshop.core.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface ItemProperty {

    float apply(ItemStack itemStack, @Nullable Entity entity, @Nullable Level level, int flags);
}
