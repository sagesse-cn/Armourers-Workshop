package moe.plushie.armourers_workshop.core.skin.texture;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.api.skin.texture.ITextureProvider;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenRandomSource;
import moe.plushie.armourers_workshop.core.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class TextureData implements ITextureProvider {

    public static final TextureData EMPTY = new TextureData("", 256, 256);

    private final int id = OpenRandomSource.nextInt(TextureData.class);
    private final String name;

    private final float width;
    private final float height;

    private TextureAnimation animation;
    private TextureProperties properties;

    private ByteBuf bytes;
    private Collection<ITextureProvider> variants = Collections.emptyList();

    public TextureData(String name, float width, float height) {
        this(name, width, height, TextureAnimation.EMPTY, TextureProperties.EMPTY);
    }

    public TextureData(String name, float width, float height, TextureAnimation animation, TextureProperties properties) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.animation = animation;
        this.properties = properties;
    }

    public void load(ByteBuf buf) {
        bytes = buf.duplicate();
    }

    public void load(InputStream inputStream) throws IOException {
        bytes = Unpooled.buffer(1024);
        try (var outputStream = new ByteBufOutputStream(bytes)) {
            StreamUtils.transferTo(inputStream, outputStream);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    public void setAnimation(TextureAnimation animation) {
        this.animation = animation;
    }

    @Override
    public TextureAnimation getAnimation() {
        return animation;
    }

    public void setProperties(TextureProperties properties) {
        this.properties = properties;
    }

    @Override
    public TextureProperties getProperties() {
        return properties;
    }

    @Override
    public ByteBuf getBuffer() {
        return bytes;
    }

    public void setVariants(Collection<ITextureProvider> variants) {
        this.variants = variants;
    }

    @Override
    public Collection<ITextureProvider> getVariants() {
        return variants;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "name", name, "width", width, "height", height, "animation", animation, "properties", properties);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextureData that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
