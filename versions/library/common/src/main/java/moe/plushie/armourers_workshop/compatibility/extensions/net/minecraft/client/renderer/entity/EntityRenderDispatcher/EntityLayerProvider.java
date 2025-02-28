package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.client.renderer.entity.EntityRenderDispatcher;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.client.AbstractEntityRendererLayerProvider;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Extension
@Available("[1.20, )")
public class EntityLayerProvider {

    public static <E extends LivingEntity, Q extends EntityModel<E>> AbstractEntityRendererLayerProvider createLayerProvider(@ThisClass Class<?> clazz, LivingEntityRenderer<E, Q> renderer) {
        return new AbstractEntityRendererLayerProvider() {
            @Override
            public <T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> RenderLayer<T, M> createHumanoidArmorLayer(Context context, A innerModel, A outerModel) {
                return new HumanoidArmorLayer<>(Objects.unsafeCast(renderer), innerModel, outerModel, context.getModelManager());
            }

            @Override
            public <T extends LivingEntity, M extends EntityModel<T> & ArmedModel> RenderLayer<T, M> createItemInHandLayer(Context context) {
                return new ItemInHandLayer<>(Objects.unsafeCast(renderer), context.getItemInHandRenderer());
            }

            @Override
            public <T extends LivingEntity, M extends EntityModel<T>> RenderLayer<T, M> createElytraLayer(Context context) {
                return new ElytraLayer<>(Objects.unsafeCast(renderer), context.getModelSet());
            }

            @Override
            public <T extends LivingEntity, M extends EntityModel<T> & HeadedModel> RenderLayer<T, M> createCustomHeadLayer(Context context) {
                return new CustomHeadLayer<>(Objects.unsafeCast(renderer), context.getModelSet(), context.getItemInHandRenderer());
            }
        };
    }
}
