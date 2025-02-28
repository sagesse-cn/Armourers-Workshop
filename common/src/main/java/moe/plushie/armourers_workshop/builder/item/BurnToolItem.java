package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.IBlockPaintViewer;
import moe.plushie.armourers_workshop.api.common.IConfigurableToolProperty;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolAction;
import moe.plushie.armourers_workshop.builder.item.option.PaintingToolOptions;
import moe.plushie.armourers_workshop.builder.other.CubePaintingEvent;
import moe.plushie.armourers_workshop.init.ModSounds;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

import java.util.List;
import java.util.function.Consumer;

public class BurnToolItem extends AbstractColoredToolItem implements IBlockPaintViewer {

    public BurnToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public void createToolProperties(Consumer<IConfigurableToolProperty<?>> builder) {
        builder.accept(PaintingToolOptions.FULL_BLOCK_MODE);
        builder.accept(PaintingToolOptions.INTENSITY);
    }

    @Override
    public IPaintToolAction createPaintToolAction(UseOnContext context) {
        var itemStack = context.getItemInHand();
        var intensity = itemStack.get(PaintingToolOptions.INTENSITY);
        return new CubePaintingEvent.BrightnessAction(-intensity);
    }

    @Override
    public void appendSettingHoverText(ItemStack itemStack, List<Component> tooltips) {
        var intensity = itemStack.get(PaintingToolOptions.INTENSITY);
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.intensity", intensity));
        super.appendSettingHoverText(itemStack, tooltips);
    }

    @Override
    public IRegistryHolder<SoundEvent> getItemSoundEvent(UseOnContext context) {
        return ModSounds.BURN;
    }
}
