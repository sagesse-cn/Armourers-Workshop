package moe.plushie.armourers_workshop.api.skin.serializer;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;

public interface ISkinFileHeader {

    int getVersion();

    int getLastModified();

    ISkinType getType();

    ISkinProperties getProperties();
}
