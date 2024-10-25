package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.skin.paint.ISkinPaintColor;
import net.minecraft.core.Direction;

import java.util.Map;

public interface IPaintable {

    ISkinPaintColor getColor(Direction direction);

    void setColor(Direction direction, ISkinPaintColor color);

    default void setColors(Map<Direction, ISkinPaintColor> colors) {
        colors.forEach(this::setColor);
    }

    default boolean hasColor(Direction direction) {
        return true;
    }

    default boolean shouldChangeColor(Direction direction) {
        return true;
    }
}
