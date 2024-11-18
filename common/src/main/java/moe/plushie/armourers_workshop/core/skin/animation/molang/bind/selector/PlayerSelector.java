package moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector;

public interface PlayerSelector {


    double getElytraYaw();

    double getElytraPitch();

    double getElytraRoll();

    boolean hasCape();

    double getCapeFlapAmount();

    int getFoodLevel();

    double getExperience();

    boolean hasLeftShoulderParrot();

    boolean hasRightShoulderParrot();

    int getLeftShoulderParrotVariant();

    int getRightShoulderParrotVariant();
}
