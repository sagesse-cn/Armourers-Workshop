package moe.plushie.armourers_workshop.compatibility.forge.event.common;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeCommonEventsImpl;
import moe.plushie.armourers_workshop.init.event.common.DataPackEvent;

@Available("[1.16, )")
public class AbstractForgeDataPackEvent {

    public static IEventHandler<DataPackEvent.Sync> syncFactory() {
        return AbstractForgeCommonEventsImpl.DATA_PACK_SYNC.map(event -> event::getPlayer);
    }
}
