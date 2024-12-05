package moe.plushie.armourers_workshop.api.skin.texture;

import io.netty.buffer.ByteBuf;

import java.util.Collection;
import java.util.Collections;

public interface ITextureProvider {

    String getName();

    float getWidth();

    float getHeight();

    ByteBuf getBuffer();

    ITextureAnimation getAnimation();

    ITextureProperties getProperties();

    default Collection<ITextureProvider> getVariants() {
        return Collections.emptyList();
    }
}
