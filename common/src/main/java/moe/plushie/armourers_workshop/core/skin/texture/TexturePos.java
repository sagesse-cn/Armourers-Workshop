package moe.plushie.armourers_workshop.core.skin.texture;

import moe.plushie.armourers_workshop.api.skin.texture.ITextureOptions;
import moe.plushie.armourers_workshop.api.skin.texture.ITexturePos;
import moe.plushie.armourers_workshop.api.skin.texture.ITextureProvider;
import moe.plushie.armourers_workshop.core.math.Vector2i;
import moe.plushie.armourers_workshop.core.utils.Objects;
import org.jetbrains.annotations.Nullable;

public class TexturePos implements ITexturePos {

    public static final TexturePos DEFAULT = new TexturePos(0, 0, 1, 1, 256, 256);

    protected final float u;
    protected final float v;
    protected final float width;
    protected final float height;
    protected final float totalWidth;
    protected final float totalHeight;
    protected final ITextureOptions options;
    protected final ITextureProvider provider;

    public TexturePos(float u, float v, float width, float height, ITextureProvider provider) {
        this(u, v, width, height, provider.getWidth(), provider.getHeight(), null, provider);
    }

    public TexturePos(float u, float v, float width, float height, ITextureOptions options, ITextureProvider provider) {
        this(u, v, width, height, provider.getWidth(), provider.getHeight(), options, provider);
    }

    public TexturePos(float u, float v, float width, float height, float totalWidth, float totalHeight) {
        this(u, v, width, height, totalWidth, totalHeight, null, null);
    }

    public TexturePos(float u, float v, float width, float height, float totalWidth, float totalHeight, ITextureOptions options, ITextureProvider provider) {
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
        this.totalWidth = totalWidth;
        this.totalHeight = totalHeight;
        this.options = options;
        this.provider = provider;
    }

    @Override
    public float getU() {
        return u;
    }

    @Override
    public float getV() {
        return v;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public float getTotalWidth() {
        return totalWidth;
    }

    @Override
    public float getTotalHeight() {
        return totalHeight;
    }

    @Override
    public ITextureOptions getOptions() {
        return options;
    }

    @Nullable
    @Override
    public ITextureProvider getProvider() {
        return provider;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TexturePos that)) return false;
        return Float.compare(that.u, u) == 0 && Float.compare(that.v, v) == 0 && Float.compare(that.width, width) == 0 && Float.compare(that.height, height) == 0 && Float.compare(that.totalWidth, totalWidth) == 0 && Float.compare(that.totalHeight, totalHeight) == 0 && that.options == options;
    }

    @Override
    public int hashCode() {
        return Objects.hash(u, v, width, height, totalWidth, totalHeight, options);
    }

    @Override
    public String toString() {
        return Objects.toString(this, "uv", new Vector2i(u, v), "size", new Vector2i(width, height), "total", new Vector2i(totalWidth, totalHeight), "options", options);
    }
}
