package moe.plushie.armourers_workshop.compatibility.forge.event.common;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeCommonEventsImpl;
import moe.plushie.armourers_workshop.init.platform.event.common.EntityEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;

@Available("[1.21, )")
public class AbstractForgeEntityEvent {

    public static IEventHandler<EntityEvent.ReloadSize> reloadSizeFactory() {
        return AbstractForgeCommonEventsImpl.ENTITY_RELOAD_SIZE.flatMap(event -> new EntityEvent.ReloadSize() {

            @Override
            public Entity getEntity() {
                return event.getEntity();
            }

            @Override
            public void setSize(EntityDimensions size) {
                event.setNewSize(size);
            }

            @Override
            public EntityDimensions getSize() {
                return event.getNewSize();
            }
        });
    }
}
