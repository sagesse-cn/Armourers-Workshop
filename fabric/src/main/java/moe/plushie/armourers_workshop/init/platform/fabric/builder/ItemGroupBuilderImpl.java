package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.common.IItemGroup;
import moe.plushie.armourers_workshop.api.common.IItemGroupProvider;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IItemGroupBuilder;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricRegistries;
import moe.plushie.armourers_workshop.core.utils.TypedRegistry;
import moe.plushie.armourers_workshop.init.ModConstants;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ItemGroupBuilderImpl<T extends IItemGroup> implements IItemGroupBuilder<T> {

    private Supplier<Supplier<ItemStack>> icon = () -> () -> ItemStack.EMPTY;

    public ItemGroupBuilderImpl() {
    }

    @Override
    public IItemGroupBuilder<T> icon(Supplier<Supplier<ItemStack>> icon) {
        this.icon = icon;
        return this;
    }

    @Override
    public IRegistryHolder<T> build(String name) {
        var group = new ItemGroup(name);
        return TypedRegistry.Entry.cast(ModConstants.key(name), () -> group);
    }

    public class ItemGroup implements IItemGroup {

        private final IRegistryHolder<CreativeModeTab> tab;
        private final ArrayList<Supplier<Item>> items = new ArrayList<>();

        public ItemGroup(String name) {
            this.tab = AbstractFabricRegistries.ITEM_GROUPS.register(name, CreativeModeTab.createCreativeModeTabFA(name, icon, this::fill));
        }

        public void fill(List<ItemStack> results) {
            for (var itemProvider : items) {
                var item = itemProvider.get();
                results.add(item.getDefaultInstance());
                if (item instanceof IItemGroupProvider provider) {
                    provider.fillItemGroup(results, this);
                }
            }
        }

        @Override
        public void add(Supplier<Item> item) {
            items.add(item);
        }

        @Override
        public CreativeModeTab get() {
            return tab.get();
        }
    }
}
