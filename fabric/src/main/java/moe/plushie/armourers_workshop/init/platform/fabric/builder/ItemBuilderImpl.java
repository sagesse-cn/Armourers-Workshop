package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.common.IItemGroup;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IItemBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryBinder;
import moe.plushie.armourers_workshop.compatibility.client.AbstractItemStackRendererProvider;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricRegistries;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Function;
import java.util.function.Supplier;

public class ItemBuilderImpl<T extends Item> implements IItemBuilder<T> {

    private Item.Properties properties = new Item.Properties();
    private IRegistryBinder<T> binder;
    private IRegistryHolder<IItemGroup> group;
    private final Function<Item.Properties, T> supplier;

    public ItemBuilderImpl(Function<Item.Properties, T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public IItemBuilder<T> stacksTo(int i) {
        this.properties = properties.stacksTo(i);
        return this;
    }

    @Override
    public IItemBuilder<T> durability(int i) {
        this.properties = properties.durability(i);
        return this;
    }

    @Override
    public IItemBuilder<T> craftRemainder(Item item) {
        this.properties = properties.craftRemainder(item);
        return this;
    }

    @Override
    public IItemBuilder<T> group(IRegistryHolder<IItemGroup> group) {
        this.group = group;
        return this;
    }

    @Override
    public IItemBuilder<T> rarity(Rarity rarity) {
        this.properties = properties.rarity(rarity);
        return this;
    }

    @Override
    public IItemBuilder<T> fireResistant() {
        this.properties = properties.fireResistant();
        return this;
    }

    @Override
    public IItemBuilder<T> bind(Supplier<AbstractItemStackRendererProvider> provider) {
        this.binder = () -> item -> {
            // here is safe call client registry.
            BuiltinItemRendererRegistry.INSTANCE.register(item.get(), provider.get().create()::renderByItem);
        };
        return this;
    }

    @Override
    public IRegistryHolder<T> build(String name) {
        var object = AbstractFabricRegistries.ITEMS.register(name, () -> supplier.apply(properties));
        if (group != null) {
            group.get().add(object::get);
        }
        EnvironmentExecutor.willInit(EnvironmentType.CLIENT, IRegistryBinder.perform(binder, object));
        return object;
    }
}
