package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.other.menu.IMenuProvider;
import moe.plushie.armourers_workshop.api.other.menu.IMenuScreenProvider;
import moe.plushie.armourers_workshop.api.common.IPlayerDataSerializer;
import moe.plushie.armourers_workshop.api.other.builder.IMenuTypeBuilder;
import moe.plushie.armourers_workshop.api.other.IRegistryObject;
import moe.plushie.armourers_workshop.core.registry.Registry;
import moe.plushie.armourers_workshop.init.platform.MenuManager;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class MenuTypeBuilderImpl<T extends AbstractContainerMenu, D> implements IMenuTypeBuilder<T> {

    protected IMenuProvider<T, D> factory;
    protected IPlayerDataSerializer<D> serializer;
    protected Supplier<Consumer<MenuType<T>>> binder;

    public MenuTypeBuilderImpl(IMenuProvider<T, D> factory, IPlayerDataSerializer<D> serializer) {
        this.factory = factory;
        this.serializer = serializer;
    }

    @Override
    public <U extends Screen & MenuAccess<T>> IMenuTypeBuilder<T> bind(Supplier<IMenuScreenProvider<T, U>> provider) {
        this.binder = () -> menuType -> {
            // here is safe call client registry.
            ScreenRegistry.register(menuType, provider.get()::createMenuScreen);
        };
        return this;
    }

    @Override
    public IRegistryObject<MenuType<T>> build(String name) {
        MenuType<T> menuType = new ExtendedScreenHandlerType<>((id, inv, buf) -> factory.createMenu(id, inv, serializer.read(buf, inv.player)));
        IRegistryObject<MenuType<T>> object = Registry.MENU_TYPE.register(name, () -> menuType);
        MenuManager.registerMenuOpener(menuType, serializer, (player, title, value) -> {
            SimpleMenuProvider menuProvider = new SimpleMenuProvider((window, inv, player2) -> factory.createMenu(window, inv, value), title);
            player.openMenu(new ExtendedScreenHandlerFactory() {

                @Override
                public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                    return menuProvider.createMenu(i, inventory, player);
                }

                @Override
                public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                    serializer.write(buf, player, value);
                }

                @Override
                public Component getDisplayName() {
                    return menuProvider.getDisplayName();
                }
            });
            return true;
        });
        EnvironmentExecutor.setupOn(EnvironmentType.CLIENT, binder, object);
        return object;
    }
}
