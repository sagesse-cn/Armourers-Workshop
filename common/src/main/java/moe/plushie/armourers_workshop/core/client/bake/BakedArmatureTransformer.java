package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.armature.IJointFilter;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.client.model.IModelProvider;
import moe.plushie.armourers_workshop.core.armature.Armature;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformer;
import moe.plushie.armourers_workshop.core.utils.Collections;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.Collection;

public class BakedArmatureTransformer {

    public static final BakedArmatureTransformer EMPTY = new BakedArmatureTransformer();

    private final Armature armature;
    private final ArmatureTransformer armatureTransformer;
    private final IJointTransform[] transforms;
    private final ArrayList<ArmaturePlugin> plugins = new ArrayList<>();

    private IJointFilter filter;

    private BakedArmatureTransformer() {
        this.armature = null;
        this.armatureTransformer = null;
        this.transforms = null;
    }

    public BakedArmatureTransformer(ArmatureTransformer armatureTransformer) {
        this.armature = armatureTransformer.getArmature();
        this.armatureTransformer = armatureTransformer;
        this.transforms = armatureTransformer.getTransforms();
    }

    public static BakedArmatureTransformer create(ArmatureTransformer transformer, EntityRenderer<?> entityRenderer) {
        if (transformer == null) {
            return null;
        }
        var context = transformer.getContext();
        var plugins = Collections.newList(transformer.getPlugins());
        context.setEntityRenderer(entityRenderer);
        // we need tried load entity model from entity renderer.
        if (context.getEntityModel() == null && entityRenderer instanceof IModelProvider<?> modelProvider) {
            context.setEntityModel(modelProvider.getModel(null));
        }
        plugins.removeIf(plugin -> !plugin.freeze());
        var armatureTransformer1 = new BakedArmatureTransformer(transformer);
        armatureTransformer1.setPlugins(plugins);
        return armatureTransformer1;
    }


    public void prepare(Entity entity, ArmaturePlugin.Context context) {
        for (var plugin : plugins) {
            plugin.prepare(entity, context);
        }
    }

    public void activate(Entity entity, ArmaturePlugin.Context context) {
        for (var plugin : plugins) {
            plugin.activate(entity, context);
        }
    }

    public void deactivate(Entity entity, ArmaturePlugin.Context context) {
        for (var plugin : plugins) {
            plugin.deactivate(entity, context);
        }
    }

    public void applyTo(BakedArmature bakedArmature) {
        // safe updates
        if (bakedArmature.getArmature() == armature) {
            bakedArmature.setFilter(filter);
            bakedArmature.seTransforms(transforms);
        }
    }

    public void setPlugins(Collection<ArmaturePlugin> plugins) {
        this.plugins.clear();
        this.plugins.addAll(plugins);
    }

    public Collection<ArmaturePlugin> getPlugins() {
        return plugins;
    }

    public void setFilter(IJointFilter filter) {
        this.filter = filter;
    }

    public IJointFilter getFilter() {
        return filter;
    }

    public ArmatureTransformer getTransformer() {
        return armatureTransformer;
    }

    public Armature getArmature() {
        return armature;
    }
}
