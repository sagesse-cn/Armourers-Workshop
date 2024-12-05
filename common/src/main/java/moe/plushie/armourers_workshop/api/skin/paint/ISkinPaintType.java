package moe.plushie.armourers_workshop.api.skin.paint;

import moe.plushie.armourers_workshop.api.core.IRegistryEntry;
import moe.plushie.armourers_workshop.api.skin.texture.ITexturePos;

public interface ISkinPaintType extends IRegistryEntry {

    int getId();

    int getIndex();

    ISkinDyeType getDyeType();

    ITexturePos getTexturePos();
}
