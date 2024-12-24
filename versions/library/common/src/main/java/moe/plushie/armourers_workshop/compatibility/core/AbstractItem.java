package moe.plushie.armourers_workshop.compatibility.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractTooltipContext;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenEquipmentSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

@Available("[1.21, )")
public abstract class AbstractItem extends Item {

    public AbstractItem(Properties properties) {
        super(properties);
    }

    public static OpenEquipmentSlot getEquipmentSlotForItem(ItemStack itemStack) {
        var equipable = Equipable.get(itemStack);
        if (equipable != null) {
            return AbstractEquipmentSlot.wrap(equipable.getEquipmentSlot());
        }
        return OpenEquipmentSlot.MAINHAND;
    }

    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack itemStack, List<Component> tooltips, ITooltipContext context) {
        AbstractTooltipContext<TooltipContext> context1 = Objects.unsafeCast(context);
        super.appendHoverText(itemStack, context1.context, tooltips, context1.flag);
    }

    @Override
    public final void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltips, TooltipFlag tooltipFlag) {
        this.appendHoverText(itemStack, tooltips, new AbstractTooltipContext<>(tooltipContext, tooltipFlag));
    }
}
