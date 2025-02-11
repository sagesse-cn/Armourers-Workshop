package moe.plushie.armourers_workshop.core.skin.serializer.v20;

import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkContext;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkDataInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkDataOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkFlags;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkType;
import moe.plushie.armourers_workshop.core.utils.Objects;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class ChunkSerializer<V, C> {

    protected final ChunkType chunkType;
    protected final Map<String, Decoder<V, C>> decoders = new LinkedHashMap<>();

    protected final V defaultValue;

    public ChunkSerializer(ChunkType chunkType, V defaultValue) {
        this.chunkType = chunkType;
        this.defaultValue = defaultValue;
        this.decoders.put(chunkType.getName(), this::read);
        this.config();
    }

    protected void config() {
    }

    public abstract V read(ChunkDataInputStream stream, C obj) throws IOException;

    public abstract void write(V value, C obj, ChunkDataOutputStream stream) throws IOException;

    public ChunkFlags getChunkFlags(V value, ChunkContext context) {
        return context.createSerializerFlags(this, value);
    }

    public ChunkType getChunkType() {
        return chunkType;
    }

    public V getDefaultValue() {
        return defaultValue;
    }

    public Decoder<V, C> createDecoder(String name) {
        return decoders.get(name);
    }

    public Encoder<V, C> createEncoder(V value, C obj, ChunkContext context) {
        if (value == null) {
            return null;
        }
        if (value instanceof Collection<?> collection) {
            if (collection.isEmpty()) {
                return null;
            }
            return this::write;
        }
        if (Objects.equals(value, defaultValue)) {
            return null;
        }
        return this::write;
    }


    public interface Encoder<V, C> {
        void encode(V value, C obj, ChunkDataOutputStream stream) throws IOException;
    }

    public interface Decoder<V, C> {
        V decode(ChunkDataInputStream stream, C obj) throws IOException;
    }
}
