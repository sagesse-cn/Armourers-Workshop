package moe.plushie.armourers_workshop.core.skin.texture;

import moe.plushie.armourers_workshop.api.skin.texture.ITextureProperties;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperty;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.Objects;

import java.io.IOException;

public class TextureProperties implements ITextureProperties {

    public static final TextureProperties EMPTY = new TextureProperties();

    private static final SkinProperty<String> NAME_KEY = SkinProperty.normal("name", "");

    private int flags = 0;
    private final SkinProperties storage = new SkinProperties();

    public TextureProperties() {
    }

    public void readFromStream(IInputStream stream) throws IOException {
        flags = stream.readInt();
        if ((flags & 0x80000000) != 0) {
            storage.readFromStream(stream);
        }
    }

    public void writeToStream(IOutputStream stream) throws IOException {
        if (storage.isEmpty()) {
            stream.writeInt(flags);
        } else {
            stream.writeInt(flags | 0x80000000);
            storage.writeToStream(stream);
        }
    }

    public <T> void set(ISkinProperty<T> property, T value) {
        storage.put(property, value);
    }

    public <T> T get(ISkinProperty<T> property) {
        return storage.get(property);
    }

    public void setName(String name) {
        storage.put(NAME_KEY, name);
    }

    public String getName() {
        return storage.get(NAME_KEY);
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

    public TextureProperties copy() {
        var properties = new TextureProperties();
        properties.flags = flags;
        properties.storage.putAll(storage);
        return properties;
    }

    @Override
    public String toString() {
        var properties = storage.copy();
        if (isEmissive()) {
            properties.put("isEmissive", true);
        }
        if (isParticle()) {
            properties.put("isParticle", true);
        }
        if (isNormal()) {
            properties.put("isNormal", true);
        }
        if (isSpecular()) {
            properties.put("isSpecular", true);
        }
        return properties.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextureProperties that)) return false;
        return flags == that.flags && storage.equals(that.storage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flags, storage);
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
