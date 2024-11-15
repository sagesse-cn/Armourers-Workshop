package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerManager;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.layer.SkinWardrobeLayer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.utils.DataContainer;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class EntityRendererContext {

    private int version = 0;

    private EntityType<?> entityType;
    private EntityProfile entityProfile;

    private final EntityRenderer<?> entityRenderer;
    private final HashMap<EntityModel<?>, BakedArmatureTransformer> cachedTransformers = new HashMap<>();

    public EntityRendererContext(EntityRenderer<?> entityRenderer) {
        this.entityRenderer = entityRenderer;
    }

    public static EntityRendererContext of(EntityRenderer<?> entityRenderer) {
        return DataContainer.lazy(entityRenderer, EntityRendererContext::new);
    }

    @Nullable
    public BakedArmatureTransformer createTransformer(@Nullable IModel entityModel, ArmatureTransformerManager transformerManager) {
        // when entity type and entity profile not provide, this means entity renderer not support yet.
        if (entityType == null || entityProfile == null) {
            return null;
        }
        var transformer = transformerManager.getTransformer(entityType, entityProfile, entityModel);
        if (transformer != null) {
            return BakedArmatureTransformer.create(transformer, entityRenderer);
        }
        return null;
    }

    @Nullable
    public BakedArmatureTransformer getTransformer(@Nullable EntityModel<?> entityModel) {
        // when entity type and entity profile not provide, this means entity renderer not support yet.
        if (entityType == null || entityProfile == null) {
            return null;
        }
        // when the caller does not provide the entity model we need to query it from entity render.
        if (entityModel == null) {
            entityModel = getEntityModel();
        }
        // in the normal, the entityRenderer only have a model type,
        // but some mods(Custom NPC) generate dynamically models,
        // so we need to be compatible with that
        return cachedTransformers.computeIfAbsent(entityModel, entityModel1 -> {
            // if it can't transform this, it means we do not support this renderer.
            var model = ModelHolder.ofNullable(entityModel1);
            return createTransformer(model, SkinRendererManager.DEFAULT);
        });
    }

    public void setEntityType(EntityType<?> entityType) {
        this.entityType = entityType;
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    public void setEntityProfile(EntityProfile entityProfile) {
        if (Objects.equals(this.entityProfile, entityProfile)) {
            return;
        }
        var oldValue = this.entityProfile;
        this.cachedTransformers.clear();
        this.entityProfile = entityProfile;
        this.version += 1;
        // add or remove our own custom armor layer.
        if (entityRenderer instanceof LivingEntityRenderer<?, ?> livingEntityRenderer) {
            if (oldValue == null && entityProfile != null) {
                addLayer(livingEntityRenderer);
            }
            if (oldValue != null && entityProfile == null) {
                removeLayer(livingEntityRenderer);
            }
        }
    }

    public EntityProfile getEntityProfile() {
        return entityProfile;
    }

    public EntityModel<?> getEntityModel() {
        if (entityRenderer instanceof RenderLayerParent<?, ?> modelProvider) {
            return modelProvider.getModel();
        }
        return null;
    }

    public int getVersion() {
        return version;
    }

    private <T extends LivingEntity, V extends EntityModel<T>> void addLayer(LivingEntityRenderer<T, V> livingRenderer) {
        removeLayer(livingRenderer);
        var transformer = getTransformer(null);
        if (transformer != null) {
            livingRenderer.layers.add(0, new SkinWardrobeLayer<>(transformer, livingRenderer));
        }
    }

    private <T extends LivingEntity, V extends EntityModel<T>> void removeLayer(LivingEntityRenderer<T, V> livingRenderer) {
        var iterator = livingRenderer.layers.iterator();
        while (iterator.hasNext()) {
            var layer = iterator.next();
            if (layer instanceof SkinWardrobeLayer<?, ?, ?>) {
                iterator.remove();
            }
        }
    }
}
