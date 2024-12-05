package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;

import java.io.IOException;

public interface ChunkInputStream extends IInputStream {

    default ChunkFile readFile() throws IOException {
        return getFileProvider().readItem(this);
    }

    ChunkContext getContext();

    default ChunkFileData getFileProvider() {
        return getContext().getFileProvider();
    }

    default ChunkPaletteData getPaletteProvider() {
        return getContext().getPaletteProvider();
    }
}
