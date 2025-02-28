package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.menu.SkinSlotType;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SkinUnlockItem extends FlavouredItem {

    private final SkinSlotType slotType;

    public SkinUnlockItem(SkinSlotType slotType, Properties properties) {
        super(properties);
        this.slotType = slotType;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var itemStack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResultHolder.success(itemStack);
        }
        var skinType = slotType.getSkinType();
        var wardrobe = SkinWardrobe.of(player);
        if (wardrobe == null || skinType == null) {
            return InteractionResultHolder.fail(itemStack);
        }
        var skinName = TranslateUtils.Name.of(skinType);
        if (wardrobe.getUnlockedSize(slotType) >= slotType.getMaxSize()) {
            player.sendSystemMessage(Component.translatable("chat.armourers_workshop.slotUnlockedFailed", skinName));
            return InteractionResultHolder.fail(itemStack);
        }
        var count = wardrobe.getUnlockedSize(slotType) + 1;
        wardrobe.setUnlockedSize(slotType, count);
        wardrobe.broadcast();
        player.sendSystemMessage(Component.translatable("chat.armourers_workshop.slotUnlocked", skinName, Integer.toString(count)));
        // we need consume item stack even in creative mode.
        var resultStack = itemStack.copy();
        resultStack.shrink(1);
        player.setItemInHand(hand, resultStack);
        return InteractionResultHolder.consume(itemStack);
    }
}
