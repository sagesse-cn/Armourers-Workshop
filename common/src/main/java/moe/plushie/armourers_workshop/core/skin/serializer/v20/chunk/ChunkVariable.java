package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import java.io.IOException;

public interface ChunkVariable {

    void writeToStream(ChunkOutputStream stream) throws IOException;

    boolean freeze();
}
