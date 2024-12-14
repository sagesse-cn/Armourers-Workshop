package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector;

public interface ContextSelector {

    float getPartialTick();

    double getAnimationTicks();

    double getAnimTime();

    double getLifeTime();

    double getFPS();

    int getEntityCount();

    LevelSelector getLevel();

    boolean isFirstPerson();

    boolean isRenderingInInventory();

    boolean isRenderingInFirstPersonMod();
}
