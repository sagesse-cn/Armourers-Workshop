package moe.plushie.armourers_workshop.core.skin.serializer.importer;

import java.io.IOException;
import java.io.InputStream;

public abstract class PackResource {

    public abstract String getName();

    public abstract InputStream getInputStream() throws IOException;
}
