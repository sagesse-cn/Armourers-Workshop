package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.api.skin.ISkinEquipmentSlot;

public enum OpenEquipmentSlot implements ISkinEquipmentSlot {

    MAINHAND("mainhand"),
    OFFHAND("offhand"),

    FEET("feet"),
    LEGS("legs"),
    CHEST("chest"),
    HEAD("head"),
    BODY("body");

    private final String name;

    OpenEquipmentSlot(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
