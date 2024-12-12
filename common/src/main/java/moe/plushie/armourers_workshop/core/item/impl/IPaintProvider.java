package moe.plushie.armourers_workshop.core.item.impl;

import moe.plushie.armourers_workshop.api.skin.texture.ISkinPaintColor;

public interface IPaintProvider {

    ISkinPaintColor getColor();

    void setColor(ISkinPaintColor color);
}
