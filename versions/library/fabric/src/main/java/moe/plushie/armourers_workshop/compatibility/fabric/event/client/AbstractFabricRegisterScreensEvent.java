package moe.plushie.armourers_workshop.compatibility.fabric.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.event.client.RegisterScreensEvent;
import moe.plushie.armourers_workshop.init.platform.fabric.EventManagerImpl;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

@Available("[1.21, )")
public class AbstractFabricRegisterScreensEvent {

    public static IEventHandler<RegisterScreensEvent> registryFactory() {
        return EventManagerImpl.factory(() -> new RegisterScreensEvent() {
            @Override
            public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> arg, Factory<M, U> arg2) {
                MenuScreens.register(arg, arg2::create);
            }
        });
    }
}
