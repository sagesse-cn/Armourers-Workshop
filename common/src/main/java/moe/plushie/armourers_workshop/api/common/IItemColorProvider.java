package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.skin.texture.ISkinPaintColor;
import net.minecraft.world.item.ItemStack;

public interface IItemColorProvider {

    void setItemColor(ItemStack itemStack, ISkinPaintColor paintColor);

    ISkinPaintColor getItemColor(ItemStack itemStack);

    default ISkinPaintColor getItemColor(ItemStack itemStack, ISkinPaintColor defaultValue) {
        ISkinPaintColor paintColor = getItemColor(itemStack);
        if (paintColor != null) {
            return paintColor;
        }
        return defaultValue;
    }
}
