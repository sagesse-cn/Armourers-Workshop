package moe.plushie.armourers_workshop.init.platform.forge.proxy;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.event.EventBus;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.event.common.LauncherCommonSetupEvent;
import moe.plushie.armourers_workshop.init.event.common.LauncherLoadCompleteEvent;
import moe.plushie.armourers_workshop.init.event.common.ServerStartingEvent;
import moe.plushie.armourers_workshop.init.event.common.ServerStoppedEvent;
import moe.plushie.armourers_workshop.init.platform.forge.EnvironmentManagerImpl;

public class CommonProxyImpl {

    public static void init() {
        ArmourersWorkshop.init();

        // prioritize handle.
        EventBus.register(ServerStartingEvent.class, event -> EnvironmentManagerImpl.attach(event.getServer()));
        EventBus.register(ServerStoppedEvent.class, event -> EnvironmentManagerImpl.detach(event.getServer()));

        EnvironmentExecutor.willInit(EnvironmentType.COMMON);
        EnvironmentExecutor.willSetup(EnvironmentType.COMMON);

        // listen the fml events.
        EventBus.register(LauncherCommonSetupEvent.class, event -> EnvironmentExecutor.didInit(EnvironmentType.COMMON));
        EventBus.register(LauncherLoadCompleteEvent.class, event -> event.enqueueWork(() -> EnvironmentExecutor.didSetup(EnvironmentType.COMMON)));
    }
}
