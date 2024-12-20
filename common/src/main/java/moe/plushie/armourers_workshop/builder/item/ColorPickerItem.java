package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.IBlockPaintViewer;
import moe.plushie.armourers_workshop.api.common.IConfigurableToolProperty;
import moe.plushie.armourers_workshop.api.common.IItemColorProvider;
import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.api.common.IItemPropertiesProvider;
import moe.plushie.armourers_workshop.api.common.IItemTintColorProvider;
import moe.plushie.armourers_workshop.api.common.IPaintable;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.skin.texture.ISkinPaintColor;
import moe.plushie.armourers_workshop.builder.item.option.PaintingToolOptions;
import moe.plushie.armourers_workshop.builder.network.UpdateColorPickerPacket;
import moe.plushie.armourers_workshop.core.item.impl.IPaintProvider;
import moe.plushie.armourers_workshop.core.item.impl.IPaintToolPicker;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.ColorUtils;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModSounds;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ColorPickerItem extends AbstractPaintToolItem implements IItemTintColorProvider, IItemPropertiesProvider, IItemColorProvider, IPaintToolPicker, IBlockPaintViewer {

    public ColorPickerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return usePickTool(context);
    }

    @Override
    public InteractionResult usePickTool(Level level, BlockPos pos, Direction dir, BlockEntity blockEntity, UseOnContext context) {
        var itemStack = context.getItemInHand();
        if (blockEntity instanceof IPaintable paintable) {
            if (!level.isClientSide()) {
                return InteractionResult.CONSUME;
            }
            var color = (SkinPaintColor) paintable.getColor(dir);
            itemStack.set(ModDataComponents.TOOL_COLOR.get(), color);
            var packet = new UpdateColorPickerPacket(context.getHand(), itemStack);
            NetworkManager.sendToServer(packet);
            // we only play local sound, color pick not need send to other players.
            playSound(context);
            return InteractionResult.SUCCESS;
        }
        if (blockEntity instanceof IPaintProvider provider) {
            var player = context.getPlayer();
            if (player != null && !player.isSecondaryUseActive()) {
                return InteractionResult.PASS;
            }
            var newColor = getItemColor(itemStack);
            if (newColor == null) {
                // this is an empty color picker, we don't need to do anything.
                return InteractionResult.CONSUME;
            }
            if (!itemStack.get(PaintingToolOptions.CHANGE_PAINT_TYPE)) {
                newColor = newColor.withPaintType(provider.getColor().getPaintType());
            }
            provider.setColor(newColor);
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    @Override
    public void createModelProperties(BiConsumer<IResourceLocation, IItemModelProperty> builder) {
        builder.accept(ModConstants.key("empty"), (itemStack, level, entity, id) -> itemStack.has(ModDataComponents.TOOL_COLOR.get()) ? 0 : 1);
    }

    @Override
    public void createToolProperties(Consumer<IConfigurableToolProperty<?>> builder) {
        builder.accept(PaintingToolOptions.CHANGE_PAINT_TYPE);
    }

    @Override
    public void appendColorHoverText(ItemStack itemStack, List<Component> tooltips) {
        var paintColor = getItemColor(itemStack);
        if (paintColor != null) {
            tooltips.addAll(ColorUtils.getColorTooltips(paintColor, false));
        } else {
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.empty"));
        }
    }

    @Override
    public void setItemColor(ItemStack itemStack, ISkinPaintColor paintColor) {
        itemStack.set(ModDataComponents.TOOL_COLOR.get(), (SkinPaintColor) paintColor);
    }

    @Override
    public SkinPaintColor getItemColor(ItemStack itemStack) {
        return itemStack.get(ModDataComponents.TOOL_COLOR.get());
    }

    @Override
    public int getTintColor(ItemStack itemStack, int index) {
        if (index == 1) {
            return ColorUtils.getDisplayRGB(itemStack);
        }
        return 0xffffffff;
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        var paintColor = getItemColor(itemStack, SkinPaintColor.WHITE);
        return paintColor.getPaintType() != SkinPaintTypes.NORMAL;
    }

    @Override
    public IRegistryHolder<SoundEvent> getItemSoundEvent(UseOnContext context) {
        return ModSounds.PICKER;
    }
}
