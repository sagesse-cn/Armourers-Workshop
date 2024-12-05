package moe.plushie.armourers_workshop.api.skin.geometry;

import moe.plushie.armourers_workshop.api.core.math.ITransform3f;
import moe.plushie.armourers_workshop.api.skin.texture.ITexturePos;

public interface ISkinGeometryFace {

    /**
     * Gets the geometry face id.
     */
    int getId();

    /**
     * Gets the geometry type.
     */
    ISkinGeometryType getType();

    /**
     * Gets the face transform.
     */
    ITransform3f getTransform();

    /**
     * Get the face used texture key.
     */
    ITexturePos getTexturePos();

    /**
     * Gets the all vertices of the face.
     */
    Iterable<? extends ISkinGeometryVertex> getVertices();
}
