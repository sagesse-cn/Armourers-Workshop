package moe.plushie.armourers_workshop.init.platform.fabric.addon;

import moe.plushie.armourers_workshop.api.common.IItemStackProvider;
import moe.plushie.armourers_workshop.core.data.ItemStackProvider;
import moe.plushie.armourers_workshop.core.utils.Collections;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public class TravelersBackpackAddon {

    public static void register(Function<Player, ItemStack> provider) {
        ItemStackProvider.getInstance().register(new IItemStackProvider() {
            @Override
            public Iterable<ItemStack> getArmorSlots(Entity entity) {
                if (entity instanceof Player player) {
                    return Collections.singleton(provider.apply(player));
                }
                return null;
            }

            @Override
            public Iterable<ItemStack> getHandSlots(Entity entity) {
                return null;
            }
        });
    }
}
