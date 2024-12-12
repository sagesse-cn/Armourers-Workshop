package moe.plushie.armourers_workshop.core.skin.texture;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.api.skin.texture.ISkinTextureProvider;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenRandomSource;
import moe.plushie.armourers_workshop.core.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SkinTextureData implements ISkinTextureProvider {

    public static final SkinTextureData EMPTY = new SkinTextureData("", 256, 256);

    private final int id = OpenRandomSource.nextInt(SkinTextureData.class);
    private final String name;

    private final float width;
    private final float height;

    private SkinTextureAnimation animation;
    private SkinTextureProperties properties;

    private ByteBuf bytes;
    private List<SkinTextureData> variants = Collections.emptyList();

    public SkinTextureData(String name, float width, float height) {
        this(name, width, height, SkinTextureAnimation.EMPTY, SkinTextureProperties.EMPTY);
    }

    public SkinTextureData(String name, float width, float height, SkinTextureAnimation animation, SkinTextureProperties properties) {
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

    public void setAnimation(SkinTextureAnimation animation) {
        this.animation = animation;
    }

    @Override
    public SkinTextureAnimation getAnimation() {
        return animation;
    }

    public void setProperties(SkinTextureProperties properties) {
        this.properties = properties;
    }

    @Override
    public SkinTextureProperties getProperties() {
        return properties;
    }

    @Override
    public ByteBuf getBuffer() {
        return bytes;
    }

    public void setVariants(List<SkinTextureData> variants) {
        this.variants = variants;
    }

    @Override
    public List<SkinTextureData> getVariants() {
        return variants;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "name", name, "width", width, "height", height, "animation", animation, "properties", properties);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinTextureData that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
