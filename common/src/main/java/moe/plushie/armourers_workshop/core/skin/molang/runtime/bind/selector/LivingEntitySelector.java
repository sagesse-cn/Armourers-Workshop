package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector;

import org.jetbrains.annotations.Nullable;

public interface LivingEntitySelector {

    double getBodyYaw();

    double getBodyPitch();


    double getHealth();

    double getMaxHealth();


    double getArmorValue();


    double getHurtTime();


    boolean isDeadOrDying();

    boolean isEating();

    boolean isSleeping();

    boolean isUsingItem();


    boolean isAutoSpinAttack();

    boolean isOnClimbable();


    double getUsingItemDuration();

    double getUsingItemMaxDuration();

    double getUsingItemRemainingDuration();

    int getArrowCount();

    int getStingerCount();

    double getAttributeValue(String name);

    @Nullable
    EffectSelector getEffect(String name);

    @Nullable
    ItemSelector getEquippedItem(String slot);

    int getEquipmentCount();

    int getLastClimbableFacing();
}
