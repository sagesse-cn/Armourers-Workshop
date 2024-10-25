package moe.plushie.armourers_workshop.api.skin.paint.texture;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;

public interface ITextureProvider {

    String getName();

    float getWidth();

    float getHeight();

    ByteBuffer getBuffer();

    ITextureAnimation getAnimation();

    ITextureProperties getProperties();

    default Collection<ITextureProvider> getVariants() {
        return Collections.emptyList();
    }
}
