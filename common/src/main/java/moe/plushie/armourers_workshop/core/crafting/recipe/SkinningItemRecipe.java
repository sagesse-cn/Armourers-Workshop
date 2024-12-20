package moe.plushie.armourers_workshop.core.crafting.recipe;

import moe.plushie.armourers_workshop.core.skin.SkinType;
import net.minecraft.world.item.ItemStack;

public class SkinningItemRecipe extends SkinningRecipe {

    private SkinType.Tool toolType;

    public SkinningItemRecipe(SkinType skinType) {
        super(skinType);
        if (skinType instanceof SkinType.Tool toolType) {
            this.toolType = toolType;
        }
    }

    @Override
    protected boolean isValidTarget(ItemStack itemStack) {
        if (toolType != null) {
            return toolType.contains(itemStack);
        }
        return super.isValidTarget(itemStack);
    }
}
