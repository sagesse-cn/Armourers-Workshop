package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.core.client.animation.AnimationEngine;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderHelper;
import moe.plushie.armourers_workshop.core.utils.Collections;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Comparator;

public class BakedSkinAnimationHandler {

    private final ArrayList<Callback> tasks = new ArrayList<>();
    private final ArrayList<Pair<Float, Callback>> pending = new ArrayList<>();

    public BakedSkinAnimationHandler() {
        register(0, (skin, entity, armature, context) -> AnimationEngine.apply(entity, skin, context));
        register(0, (skin, entity, armature, context) -> SkinRenderHelper.apply(entity, skin, armature, context.getItemSource()));
    }

    public void register(float priority, Callback handler) {
        tasks.clear();
        pending.add(Pair.of(priority, handler));
        pending.sort(Comparator.comparingDouble(Pair::getLeft));
        tasks.addAll(Collections.compactMap(pending, Pair::getRight));
    }

    public void setup(BakedSkin skin, Entity entity, BakedArmature armature, SkinRenderContext context) {
        for (var task : tasks) {
            task.apply(skin, entity, armature, context);
        }
    }

    public interface Callback {

        void apply(BakedSkin skin, Entity entity, BakedArmature bakedArmature, SkinRenderContext context);
    }
}
