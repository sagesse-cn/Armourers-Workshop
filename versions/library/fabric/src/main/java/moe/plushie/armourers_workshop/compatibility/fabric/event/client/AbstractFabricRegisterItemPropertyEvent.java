package moe.plushie.armourers_workshop.compatibility.fabric.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.event.client.RegisterItemPropertyEvent;
import moe.plushie.armourers_workshop.init.platform.fabric.EventManagerImpl;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;

@Available("[1.18, )")
public class AbstractFabricRegisterItemPropertyEvent {

    public static IEventHandler<RegisterItemPropertyEvent> propertyFactory() {
        return EventManagerImpl.factory(() -> ((registryName, item, property) -> FabricModelPredicateProviderRegistry.register(item, registryName.toLocation(), property::getValue)));
    }
}
