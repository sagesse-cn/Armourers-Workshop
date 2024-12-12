package moe.plushie.armourers_workshop.api.skin.texture;

import moe.plushie.armourers_workshop.api.core.IRegistryEntry;

public interface ISkinPaintType extends IRegistryEntry {

    int getId();

    int getIndex();

    ISkinDyeType getDyeType();

    ISkinTexturePos getTexturePos();
}
