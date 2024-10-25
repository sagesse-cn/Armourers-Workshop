package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.api.skin.paint.texture.ITextureProvider;
import moe.plushie.armourers_workshop.core.math.Rectangle2f;
import moe.plushie.armourers_workshop.core.math.Vector2f;
import moe.plushie.armourers_workshop.core.skin.paint.texture.TextureAnimation;
import moe.plushie.armourers_workshop.core.skin.paint.texture.TextureData;
import moe.plushie.armourers_workshop.core.skin.paint.texture.TextureOptions;
import moe.plushie.armourers_workshop.core.skin.paint.texture.TextureProperties;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.Collections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

public class ChunkTextureData {

    protected ChunkTextureData proxy;
    protected Rectangle2f rect = Rectangle2f.ZERO;
    protected ITextureProvider provider;
    protected boolean isResolved = false;

    protected int id = 0;
    protected int parentId = 0;

    private final ArrayList<TextureRef> uvs = new ArrayList<>();

    public ChunkTextureData() {
    }

    public ChunkTextureData(ITextureProvider provider) {
        this.rect = new Rectangle2f(0, 0, provider.getWidth(), provider.getHeight());
        this.provider = provider;
    }

    public void readFromStream(IInputStream stream) throws IOException {
        if (proxy != null) {
            return; // ignore, when proxied.
        }
        this.id = stream.readVarInt();
        this.parentId = stream.readVarInt();
        var x = stream.readFloat();
        var y = stream.readFloat();
        var width = stream.readFloat();
        var height = stream.readFloat();
        this.rect = new Rectangle2f(x, y, width, height);
        var animation = stream.readTextureAnimation();
        var properties = stream.readTextureProperties();
        var byteSize = stream.readInt();
        var provider = new TextureData(String.valueOf(id), width, height, animation, properties);
        provider.load(stream.readBytes(byteSize));
        this.provider = provider;
    }

    public void writeToStream(IOutputStream stream) throws IOException {
        if (proxy != null) {
            return; // ignore, when proxied.
        }
        var buffer = provider.getBuffer();
        stream.writeVarInt(id);
        stream.writeVarInt(parentId);
        stream.writeFloat(rect.getX());
        stream.writeFloat(rect.getY());
        stream.writeFloat(rect.getWidth());
        stream.writeFloat(rect.getHeight());
        stream.writeTextureAnimation((TextureAnimation) provider.getAnimation());
        stream.writeTextureProperties((TextureProperties) provider.getProperties());
        stream.writeInt(buffer.remaining());
        stream.writeBytes(buffer);
    }

    public void freeze(float x, float y, Function<ITextureProvider, ChunkTextureData> childProvider) {
        this.rect = new Rectangle2f(x, y, rect.getWidth(), rect.getHeight());
        this.isResolved = true;
        // bind the child -> parent
        Collections.compactMap(provider.getVariants(), childProvider).forEach(it -> it.parentId = this.id);
    }

    public boolean contains(Vector2f uv) {
        if (proxy != null) {
            return proxy.contains(uv);
        }
        float x0 = rect.getMinX();
        float x1 = uv.getX();
        float x2 = rect.getMaxX();
        return x0 <= x1 && x1 <= x2;
    }

    public TextureRef get(Vector2f uv, ChunkColorSection section) {
        if (proxy != null) {
            return proxy.get(uv, section);
        }
        return new TextureRef(section, this, new Vector2f(uv.getX() - rect.getX(), uv.getY()));
    }

    public TextureRef add(Vector2f uv, ChunkColorSection section) {
        if (proxy != null) {
            return proxy.add(uv, section);
        }
        var ref = new TextureRef(section, this, uv);
        uvs.add(ref);
        return ref;
    }

    public Rectangle2f getRect() {
        if (proxy != null) {
            return proxy.getRect();
        }
        return rect;
    }

    public boolean isProxy() {
        return proxy != null;
    }

    public boolean isResolved() {
        if (proxy != null) {
            return proxy.isResolved();
        }
        return isResolved;
    }

    public static class TextureRef implements ChunkVariable {

        private final Vector2f uv;
        private final ChunkTextureData list;
        private final ChunkColorSection section;

        public TextureRef(ChunkColorSection section, ChunkTextureData list, Vector2f uv) {
            this.section = section;
            this.list = list;
            this.uv = uv;
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            var rect = list.getRect();
            stream.writeFixedFloat(rect.getX() + uv.getX(), section.textureIndexBytes);
            stream.writeFixedFloat(rect.getY() + uv.getY(), section.textureIndexBytes);
        }

        @Override
        public boolean freeze() {
            return section.isResolved() && list.isResolved();
        }

        public float getU() {
            return uv.getX();
        }

        public float getV() {
            return uv.getY();
        }

        public Vector2f getPos() {
            return uv;
        }

        public ITextureProvider getProvider() {
            return list.provider;
        }
    }

    public static class OptionsRef implements ChunkVariable {

        private final TextureOptions textureOptions;
        private final ChunkColorSection section;

        public OptionsRef(ChunkColorSection section, TextureOptions options) {
            this.section = section;
            this.textureOptions = options;
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            long value = textureOptions.asLong();
            stream.writeFixedInt((int) (value), section.textureIndexBytes);
            stream.writeFixedInt((int) (value >> 32), section.textureIndexBytes);
        }

        @Override
        public boolean freeze() {
            return section.isResolved();
        }
    }
}
