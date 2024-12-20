package moe.plushie.armourers_workshop.core.utils;

public enum OpenEquipmentSlot {

    MAINHAND("mainhand"),
    OFFHAND("offhand"),

    FEET("feet"),
    LEGS("legs"),
    CHEST("chest"),
    HEAD("head");

    private final String name;

    OpenEquipmentSlot(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
