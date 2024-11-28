package moe.plushie.armourers_workshop.init.platform.event.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;

public interface EntityEvent {

    Entity getEntity();

    interface ReloadSize extends EntityEvent {

        void setSize(EntityDimensions size);

        EntityDimensions getSize();
    }
}
