package moe.plushie.armourers_workshop.core.skin.geometry;

import moe.plushie.armourers_workshop.api.core.math.ITransform3f;
import moe.plushie.armourers_workshop.api.skin.geometry.ISkinGeometryFace;
import moe.plushie.armourers_workshop.api.skin.paint.texture.ITextureKey;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.utils.Objects;

public abstract class SkinGeometryFace implements ISkinGeometryFace {

    protected int id;

    protected ITransform3f transform = OpenTransform3f.IDENTITY;
    protected ITextureKey textureKey;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public ITransform3f getTransform() {
        return transform;
    }

    @Override
    public ITextureKey getTextureKey() {
        return textureKey;
    }

    @Override
    public abstract Iterable<? extends SkinGeometryVertex> getVertices();

    public float getPriority() {
        return 0;
    }

    public boolean isVisible() {
        return true;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "id", getId(), "type", getType());
    }
}
