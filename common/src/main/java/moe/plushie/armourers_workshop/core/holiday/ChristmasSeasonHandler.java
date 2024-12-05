package moe.plushie.armourers_workshop.core.holiday;

import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ChristmasSeasonHandler implements Holiday.IHandler {

    @Override
    public int getBackgroundColor() {
        return 0x990000;
    }

    @Override
    public int getForegroundColor() {
        return 0x267f00;
    }

    @Override
    public ItemStack getGift(Player player) {
        var entityData = new MannequinEntity.EntityData();
        entityData.setScale(0.5f);
        if (player != null) {
            entityData.setTexture(EntityTextureDescriptor.fromProfile(player.getGameProfile()));
        }
        return entityData.getItemStack();
    }
}
