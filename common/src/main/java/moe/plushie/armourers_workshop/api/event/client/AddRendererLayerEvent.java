package moe.plushie.armourers_workshop.api.event.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

public interface AddRendererLayerEvent<T extends LivingEntity, V extends EntityModel<T>> {

    RenderLayer<T, V> getLayer();

    LivingEntityRenderer<T, V> getRenderer();
}
