package moe.plushie.armourers_workshop.core.skin.paint.texture;

import moe.plushie.armourers_workshop.api.skin.paint.texture.ITextureProperties;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;

import java.io.IOException;
import java.util.StringJoiner;

public class TextureProperties implements ITextureProperties {

    public static final TextureProperties EMPTY = new TextureProperties();

    private int flags = 0;

    public void readFromStream(IInputStream stream) throws IOException {
        this.flags = stream.readInt();
    }

    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writeInt(flags);
    }

    public void setEmissive(boolean isEmissive) {
        setFlag(0, isEmissive);
    }

    @Override
    public boolean isEmissive() {
        return getFlag(0);
    }

    public void setParticle(boolean isParticle) {
        setFlag(1, isParticle);
    }

    @Override
    public boolean isParticle() {
        return getFlag(1);
    }

    public void setSpecular(boolean isSpecular) {
        setFlag(2, isSpecular);
    }

    @Override
    public boolean isSpecular() {
        return getFlag(2);
    }

    public void setNormal(boolean isNormal) {
        setFlag(3, isNormal);
    }

    @Override
    public boolean isNormal() {
        return getFlag(3);
    }

    @Override
    public String toString() {
        var joiner = new StringJoiner(", ", "[", "]");
        if (isEmissive()) {
            joiner.add("Emissive");
        }
        if (isParticle()) {
            joiner.add("Particle");
        }
        if (isNormal()) {
            joiner.add("Normal");
        }
        if (isSpecular()) {
            joiner.add("Specular");
        }
        return joiner.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextureProperties that)) return false;
        return flags == that.flags;
    }

    @Override
    public int hashCode() {
        return flags;
    }

    private void setFlag(int bit, boolean value) {
        if (value) {
            flags |= 1 << bit;
        } else {
            flags &= ~(1 << bit);
        }
    }

    private boolean getFlag(int bit) {
        return (flags & (1 << bit)) != 0;
    }
}
