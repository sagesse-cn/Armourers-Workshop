package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;

public interface DataPackBuilder {

    void append(IODataObject object, IResourceLocation location);

    void build();
}
