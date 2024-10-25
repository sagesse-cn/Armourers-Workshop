package moe.plushie.armourers_workshop.api.skin.paint;

import moe.plushie.armourers_workshop.api.core.IRegistryEntry;
import moe.plushie.armourers_workshop.api.skin.paint.texture.ITextureKey;

public interface ISkinPaintType extends IRegistryEntry {

    int getId();

    int getIndex();

    ITextureKey getTextureKey();

    ISkinDyeType getDyeType();
}
