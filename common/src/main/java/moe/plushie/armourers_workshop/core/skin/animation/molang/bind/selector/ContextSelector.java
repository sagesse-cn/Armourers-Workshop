package moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector;

public interface ContextSelector {

    float getAnimationTicks();

    float getPartialTick();

    float getAnimTime();

    float getLifeTime();

    double getFPS();

    int getEntityCount();

    LevelSelector getLevel();

    boolean isFirstPerson();

    boolean isRenderingInInventory();

    boolean isRenderingInFirstPersonMod();
}
