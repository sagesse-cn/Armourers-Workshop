package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.IConfigurableToolProperty;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.item.option.MannequinToolOptions;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModEntityTypes;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class MannequinToolItem extends ConfigurableToolItem {

    public MannequinToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity entity, InteractionHand hand) {
        if (entity instanceof MannequinEntity mannequinEntity) {
            if (player.isSecondaryUseActive()) {
                var config = new CompoundTag();
                var newItemStack = itemStack.copy();
                mannequinEntity.saveMannequinToolData(config);
                config.putString(Constants.Key.ID, ModEntityTypes.MANNEQUIN.getRegistryName().toString());
                newItemStack.set(ModDataComponents.ENTITY_DATA.get(), config);
                player.setItemInHand(hand, newItemStack);
                return InteractionResult.sidedSuccess(player.getLevel().isClientSide());
            } else {
                var entityTag = itemStack.get(ModDataComponents.ENTITY_DATA.get());
                if (entityTag != null && !entityTag.isEmpty()) {
                    mannequinEntity.readMannequinToolData(entityTag, itemStack);
                    return InteractionResult.sidedSuccess(player.getLevel().isClientSide());
                }
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void createToolProperties(Consumer<IConfigurableToolProperty<?>> builder) {
        builder.accept(MannequinToolOptions.MIRROR_MODE);
        builder.accept(MannequinToolOptions.CHANGE_SCALE);
        builder.accept(MannequinToolOptions.CHANGE_ROTATION);
        builder.accept(MannequinToolOptions.CHANGE_TEXTURE);
        builder.accept(MannequinToolOptions.CHANGE_OPTION);
    }

    @Override
    public void appendSettingHoverText(ItemStack itemStack, List<Component> tooltips) {
        if (itemStack.has(ModDataComponents.ENTITY_DATA.get())) {
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.settingsSaved"));
        } else {
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.noSettingsSaved"));
        }
        super.appendSettingHoverText(itemStack, tooltips);
    }
}
