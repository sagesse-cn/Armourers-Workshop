package moe.plushie.armourers_workshop.core.crafting.recipe;

import moe.plushie.armourers_workshop.api.skin.ISkinArmorType;
import moe.plushie.armourers_workshop.api.skin.ISkinEquipmentSlot;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.compatibility.core.AbstractItem;
import net.minecraft.world.item.ItemStack;

public class SkinningArmourRecipe extends SkinningRecipe {

    private ISkinEquipmentSlot slotType;

    public SkinningArmourRecipe(ISkinType skinType) {
        super(skinType);
        if (skinType instanceof ISkinArmorType armorType) {
            slotType = armorType.getSlotType();
        }
    }

    @Override
    protected boolean isValidTarget(ItemStack itemStack) {
        if (slotType != null) {
            return slotType.getName().equals(AbstractItem.getEquipmentSlotForItem(itemStack).getName());
        }
        return false;
    }
}
