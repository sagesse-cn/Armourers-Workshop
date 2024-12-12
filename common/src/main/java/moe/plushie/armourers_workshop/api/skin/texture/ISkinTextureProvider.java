package moe.plushie.armourers_workshop.api.skin.texture;

import io.netty.buffer.ByteBuf;

import java.util.Collection;
import java.util.Collections;

public interface ISkinTextureProvider {

    String getName();

    float getWidth();

    float getHeight();

    ByteBuf getBuffer();

    ISkinTextureAnimation getAnimation();

    ISkinTextureProperties getProperties();

    default Collection<? extends ISkinTextureProvider> getVariants() {
        return Collections.emptyList();
    }
}
