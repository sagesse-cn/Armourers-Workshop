package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.api.skin.texture.ITextureProvider;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.Rectangle2f;
import moe.plushie.armourers_workshop.core.math.Vector2f;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.texture.TextureAnimation;
import moe.plushie.armourers_workshop.core.skin.texture.TextureData;
import moe.plushie.armourers_workshop.core.skin.texture.TextureOptions;
import moe.plushie.armourers_workshop.core.skin.texture.TextureProperties;
import moe.plushie.armourers_workshop.core.utils.Collections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ChunkTextureData {

    private static final SkinProperty<List<Float>> USED_RECT_KEY = SkinProperty.normal("usedRect", null);

    protected ChunkTextureData proxy;
    protected Rectangle2f rect = Rectangle2f.ZERO;
    protected Rectangle2f usedRect = Rectangle2f.ZERO;
    protected ITextureProvider provider;
    protected boolean isResolved = false;

    protected int id = 0;
    protected int parentId = 0;

    private final ArrayList<TextureRef> uvs = new ArrayList<>();

    public ChunkTextureData() {
    }

    public ChunkTextureData(ITextureProvider provider) {
        this.rect = new Rectangle2f(0, 0, provider.getWidth(), provider.getHeight());
        this.usedRect = rect;
        this.provider = provider;
    }

    public void readFromStream(ChunkInputStream stream) throws IOException {
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
        this.usedRect = rect;
        var animation = stream.readTextureAnimation();
        var properties = readAdditionalData(stream.readTextureProperties());
        var file = stream.readFile();
        var provider = new TextureData(file.getName(), width, height, animation, properties);
        provider.load(file.getBytes());
        this.provider = provider;
    }

    public void writeToStream(ChunkOutputStream stream) throws IOException {
        if (proxy != null) {
            return; // ignore, when proxied.
        }
        stream.writeVarInt(id);
        stream.writeVarInt(parentId);
        stream.writeFloat(rect.getX());
        stream.writeFloat(rect.getY());
        stream.writeFloat(rect.getWidth());
        stream.writeFloat(rect.getHeight());
        stream.writeTextureAnimation((TextureAnimation) provider.getAnimation());
        stream.writeTextureProperties(writeAdditionalData((TextureProperties) provider.getProperties()));
        stream.writeFile(ChunkFile.image(provider.getName(), provider.getBuffer()));
    }

    public void freeze(float x, float y, Function<ITextureProvider, ChunkTextureData> childProvider) {
        // bind the child -> parent
        Collections.compactMap(provider.getVariants(), childProvider).forEach(it -> it.parentId = this.id);

        // alignment the coordinate 16x16.
        float minX = OpenMath.floori((usedRect.getMinX() - rect.getMinX()) / 16f) * 16f;
        float minY = OpenMath.floori((usedRect.getMinY() - rect.getMinY()) / 16f) * 16f;
        float maxX = OpenMath.ceili((usedRect.getMaxX() - rect.getMinX()) / 16f) * 16f;
        float maxY = OpenMath.ceili((usedRect.getMaxY() - rect.getMinY()) / 16f) * 16f;

        this.rect = new Rectangle2f(x - minX, y - minY, rect.getWidth(), rect.getHeight());
        this.usedRect = new Rectangle2f(x, y, maxX - minX, maxY - minY);
        this.isResolved = true;
    }

    public boolean contains(Vector2f uv) {
        if (proxy != null) {
            return proxy.contains(uv);
        }
        return usedRect.contains(uv);
    }

    public TextureRef get(Vector2f uv, ChunkColorSection section) {
        if (proxy != null) {
            return proxy.get(uv, section);
        }
        return new TextureRef(section, this, new Vector2f(uv.getX() - rect.getX(), uv.getY() - rect.getY()));
    }

    public TextureRef add(Vector2f uv, ChunkColorSection section) {
        if (proxy != null) {
            return proxy.add(uv, section);
        }
        var ref = new TextureRef(section, this, uv);
        // the texture coordinates maybe exceed the texture itself.
        if (!usedRect.contains(uv)) {
            float x0 = Math.min(usedRect.getMinX(), uv.getX());
            float y0 = Math.min(usedRect.getMinY(), uv.getY());
            float x1 = Math.max(usedRect.getMaxX(), uv.getX());
            float y1 = Math.max(usedRect.getMaxY(), uv.getY());
            usedRect = new Rectangle2f(x0, y0, x1 - x0, y1 - y0);
        }
        uvs.add(ref);
        return ref;
    }

    public Rectangle2f getRect() {
        if (proxy != null) {
            return proxy.getRect();
        }
        return rect;
    }

    public Rectangle2f getUsedRect() {
        if (proxy != null) {
            return proxy.getUsedRect();
        }
        return usedRect;
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

    private TextureProperties readAdditionalData(TextureProperties properties) {
        // have custom used rect?
        var rectValues = properties.get(USED_RECT_KEY);
        if (rectValues != null) {
            float x = rectValues.get(0);
            float y = rectValues.get(1);
            float w = rectValues.get(2);
            float h = rectValues.get(3);
            this.usedRect = new Rectangle2f(x, y, w, h);
        }
        return properties;
    }

    private TextureProperties writeAdditionalData(TextureProperties properties) {
        // have custom used rect?
        if (!rect.equals(usedRect)) {
            float x = usedRect.getX();
            float y = usedRect.getY();
            float w = usedRect.getWidth();
            float h = usedRect.getHeight();
            var newProperties = properties.copy();
            newProperties.set(USED_RECT_KEY, Collections.newList(x, y, w, h));
            properties = newProperties;
        }
        return properties;
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
        public void writeToStream(ChunkOutputStream stream) throws IOException {
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
        public void writeToStream(ChunkOutputStream stream) throws IOException {
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
