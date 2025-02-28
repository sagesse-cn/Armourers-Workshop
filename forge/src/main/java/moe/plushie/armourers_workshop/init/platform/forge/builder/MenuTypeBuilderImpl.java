package moe.plushie.armourers_workshop.init.platform.forge.builder;

import com.apple.library.uikit.UIWindow;
import moe.plushie.armourers_workshop.api.common.IMenuProvider;
import moe.plushie.armourers_workshop.api.common.IMenuSerializer;
import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.event.EventBus;
import moe.plushie.armourers_workshop.api.registry.IMenuTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryBinder;
import moe.plushie.armourers_workshop.compatibility.client.AbstractMenuWindowProvider;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeMenuType;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeRegistries;
import moe.plushie.armourers_workshop.core.utils.TypedRegistry;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.event.client.RegisterScreensEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class MenuTypeBuilderImpl<T extends AbstractContainerMenu, D> implements IMenuTypeBuilder<T> {

    private final IMenuProvider<T, D> factory;
    private final IMenuSerializer<D> serializer;
    private IRegistryBinder<MenuType<T>> binder;

    public MenuTypeBuilderImpl(IMenuProvider<T, D> factory, IMenuSerializer<D> serializer) {
        this.factory = factory;
        this.serializer = serializer;
    }

    @Override
    public <U extends UIWindow> IMenuTypeBuilder<T> bind(Supplier<AbstractMenuWindowProvider<T, U>> provider) {
        this.binder = () -> menuType -> {
            // here is safe call client registry.
            EventBus.register(RegisterScreensEvent.class, event -> {
                event.register(menuType.get(), provider.get()::createScreen);
            });
        };
        return this;
    }

    @Override
    public IRegistryHolder<IMenuType<T>> build(String name) {
        AbstractForgeMenuType<T> menuType = AbstractForgeMenuType.create(factory, serializer);
        IRegistryHolder<MenuType<T>> object = AbstractForgeRegistries.MENU_TYPES.register(name, menuType::getType);
        menuType.setRegistryName(object.getRegistryName());
        EnvironmentExecutor.willInit(EnvironmentType.CLIENT, IRegistryBinder.perform(binder, object));
        return TypedRegistry.Entry.of(object.getRegistryName(), () -> menuType);
    }
}
