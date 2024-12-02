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
public class CustomCollisionProvider {

    private static final DataContainerKey<EntityCollisionShape> KEY = DataContainerKey.of("Collisions", EntityCollisionShape.class);

    public static EntityCollisionShape getCustomCollision(@This Entity entity) {
        return DataContainer.getValue(entity, KEY);
    }

    public static void setCustomCollision(@This Entity entity, EntityCollisionShape collisionShape) {
        DataContainer.setValue(entity, KEY, collisionShape);
        entity.refreshDimensions();
    }
}
