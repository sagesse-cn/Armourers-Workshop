package moe.plushie.armourers_workshop.core.skin.animation.engine.bind;

import moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector.ContextSelector;
import moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector.LevelSelector;
import net.minecraft.world.entity.Entity;

public class ContextSelectorImpl implements ContextSelector {

    private int skinId = 0;

    private float startAnimTime = 0;
    private float animTime = 0;

    private float animationTicks = 0;
    private float partialTicks = 0;

    public void upload(int skinId, float startAnimTime, float animTime, float animationTicks, float partialTicks) {
        this.skinId = skinId;
        this.startAnimTime = startAnimTime;
        this.animTime = animTime;
        this.animationTicks = animationTicks;
        this.partialTicks = partialTicks;
    }

    @Override
    public float getAnimationTicks() {
        return animationTicks;
    }

    @Override
    public float getPartialTick() {
        return partialTicks;
    }

    @Override
    public float getAnimTime() {
        return startAnimTime;
    }

    @Override
    public float getLifeTime() {
        return animTime;
    }

    @Override
    public double getFPS() {
        return 0;
    }


    @Override
    public LevelSelector getLevel() {
        return null;
    }

    public double getCameraDistanceFormEntity(Entity entity) {
        return 0;
    }

    @Override
    public int getEntityCount() {
        return 0;
    }

    @Override
    public boolean isRenderingInInventory() {
        return false;
    }

    @Override
    public boolean isRenderingInFirstPersonMod() {
        return false;
    }

    @Override
    public boolean isFirstPerson() {
        return false;
    }
}
