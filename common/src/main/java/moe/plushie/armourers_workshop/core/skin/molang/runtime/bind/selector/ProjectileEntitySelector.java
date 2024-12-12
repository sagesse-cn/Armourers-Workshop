package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector;

public interface ProjectileEntitySelector {

    boolean isOnGround();

    double getOnGroundTime();

    boolean isSpectral();

    Object getOwner();

    double distanceFromMove();
}
