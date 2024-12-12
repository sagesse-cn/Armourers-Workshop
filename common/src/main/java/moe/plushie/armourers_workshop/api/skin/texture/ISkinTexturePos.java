package moe.plushie.armourers_workshop.api.skin.texture;

import org.jetbrains.annotations.Nullable;

public interface ISkinTexturePos {

    float getU();

    float getV();

    float getWidth();

    float getHeight();

    float getTotalWidth();

    float getTotalHeight();

    @Nullable
    default ISkinTextureOptions getOptions() {
        return null;
    }

    @Nullable
    default ISkinTextureProvider getProvider() {
        return null;
    }
}
