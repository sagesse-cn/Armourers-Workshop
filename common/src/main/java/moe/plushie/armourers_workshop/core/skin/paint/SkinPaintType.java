package moe.plushie.armourers_workshop.core.skin.paint;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.skin.paint.ISkinPaintType;
import moe.plushie.armourers_workshop.core.skin.paint.texture.TexturePos;

public class SkinPaintType implements ISkinPaintType {

    private final int id;
    private final int index;

    private SkinDyeType dyeType;
    private IResourceLocation registryName;
    private TexturePos texturePos = TexturePos.DEFAULT;

    public SkinPaintType(int index, int id) {
        this.id = id;
        this.index = index;
    }

    public void setRegistryName(IResourceLocation registryName) {
        this.registryName = registryName;
    }

    @Override
    public IResourceLocation getRegistryName() {
        return registryName;
    }

    public SkinPaintType setTexturePos(float u, float v) {
        this.texturePos = new TexturePos(u, v, 1, 1, 256, 256);
        return this;
    }

    @Override
    public TexturePos getTexturePos() {
        return texturePos;
    }

    public SkinPaintType setDyeType(SkinDyeType dyeType) {
        this.dyeType = dyeType;
        return this;
    }

    @Override
    public SkinDyeType getDyeType() {
        return dyeType;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinPaintType that)) return false;
        return id == that.id;
    }

    @Override
    public String toString() {
        return registryName.toString();
    }
}
