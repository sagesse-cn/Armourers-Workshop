package moe.plushie.armourers_workshop.api.data;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;

public interface IDataPackBuilder {

    void append(IODataObject object, IResourceLocation location);

    void build();
}
