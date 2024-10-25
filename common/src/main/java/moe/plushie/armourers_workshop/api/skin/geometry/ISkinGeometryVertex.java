package moe.plushie.armourers_workshop.api.skin.geometry;

import moe.plushie.armourers_workshop.api.core.math.IVector2f;
import moe.plushie.armourers_workshop.api.core.math.IVector3f;
import moe.plushie.armourers_workshop.api.skin.paint.ISkinPaintColor;

public interface ISkinGeometryVertex {

    int getId();

    IVector3f getPosition();

    IVector3f getNormal();

    IVector2f getTextureCoords();

    ISkinPaintColor getColor();
}
