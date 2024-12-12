package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector;

import org.jetbrains.annotations.Nullable;

public interface EntitySelector {


    double getEyeYaw();

    double getEyePitch();

    double getHeadYaw();

    double getHeadPitch();


    double getX(double partialTicks);

    double getY(double partialTicks);

    double getZ(double partialTicks);


    int getCardinalFacing();

    double distanceFromCamera();

    double distanceFromMove();

    double distanceFromWalk();


    double getYawSpeed();

    double getGroundSpeed();

    double getVerticalSpeed();


    boolean isVehicle();

    boolean isPassenger();

    boolean isInWater();

    boolean isInWaterRainOrBubble();

    boolean isOnFire();

    boolean isOnGround();

    boolean isSneaking();

    boolean isJumping();

    boolean isSprinting();

    boolean isSwimming();

    boolean isSleeping();

    boolean isSpectator();

    boolean isUnderWater();

    boolean isCloseEyes();

    boolean canSeeSky();

    double getTicksFrozen();

    double getAirSupply();


    @Nullable
    BiomeSelector getBiome();

    @Nullable
    BlockSelector getRelativeBlock(int offsetX, int offsetY, int offsetZ);

    float getPartialTick();
}
