package moe.plushie.armourers_workshop.init.event.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;

public interface EntityEvent {

    Entity getEntity();

    interface ReloadSize extends EntityEvent {

        void setSize(EntityDimensions size);

        EntityDimensions getSize();
    }
}
