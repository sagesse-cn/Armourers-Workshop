package moe.plushie.armourers_workshop.compatibility.core;

import moe.plushie.armourers_workshop.core.utils.OpenEquipmentSlot;
import moe.plushie.armourers_workshop.utils.EnumMapper;
import net.minecraft.world.entity.EquipmentSlot;

public class AbstractEquipmentSlot {

    private static final EnumMapper<OpenEquipmentSlot, EquipmentSlot> MAPPER = EnumMapper.create(OpenEquipmentSlot.MAINHAND, EquipmentSlot.MAINHAND, builder -> {
        builder.add(OpenEquipmentSlot.MAINHAND, EquipmentSlot.MAINHAND);
        builder.add(OpenEquipmentSlot.OFFHAND, EquipmentSlot.OFFHAND);
        builder.add(OpenEquipmentSlot.FEET, EquipmentSlot.FEET);
        builder.add(OpenEquipmentSlot.LEGS, EquipmentSlot.LEGS);
        builder.add(OpenEquipmentSlot.CHEST, EquipmentSlot.CHEST);
        builder.add(OpenEquipmentSlot.HEAD, EquipmentSlot.HEAD);
    });

    public static OpenEquipmentSlot wrap(EquipmentSlot equipmentSlot) {
        return MAPPER.getKey(equipmentSlot);
    }

    public static EquipmentSlot unwrap(OpenEquipmentSlot equipmentSlot) {
        return MAPPER.getValue(equipmentSlot);
    }
}
