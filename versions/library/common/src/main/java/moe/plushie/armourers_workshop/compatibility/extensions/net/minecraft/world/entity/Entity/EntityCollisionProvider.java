package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.world.entity.Entity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.data.EntityCollisionShape;
import moe.plushie.armourers_workshop.utils.DataContainer;
import moe.plushie.armourers_workshop.utils.DataContainerKey;
import net.minecraft.world.entity.Entity;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.18, )")
@Extension
public class EntityCollisionProvider {

    private static final DataContainerKey<EntityCollisionShape> KEY = DataContainerKey.of("Collisions", EntityCollisionShape.class);

    public static EntityCollisionShape getCollisionShape(@This Entity entity) {
        return DataContainer.getValue(entity, KEY);
    }

    public static void setCollisionShape(@This Entity entity, EntityCollisionShape collisionShape) {
        DataContainer.setValue(entity, KEY, collisionShape);
        entity.refreshDimensions();
    }
}
