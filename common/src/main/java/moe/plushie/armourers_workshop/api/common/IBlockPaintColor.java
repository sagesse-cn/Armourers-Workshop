package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.skin.paint.ISkinPaintColor;
import net.minecraft.core.Direction;

public interface IBlockPaintColor {

    boolean isEmpty();

    boolean isPureColor();

    void put(Direction dir, ISkinPaintColor color);

    ISkinPaintColor get(Direction dir);

    default ISkinPaintColor getOrDefault(Direction dir, ISkinPaintColor defaultValue) {
        var paintColor = get(dir);
        if (paintColor == null) {
            paintColor = defaultValue;
        }
        return paintColor;
    }
}
