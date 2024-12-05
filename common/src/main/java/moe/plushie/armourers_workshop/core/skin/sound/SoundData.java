package moe.plushie.armourers_workshop.core.skin.sound;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.skin.sound.ISoundProvider;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenRandomSource;


public class SoundData implements ISoundProvider {

    public static final SoundData EMPTY = new SoundData(null, null);

    private final int id = OpenRandomSource.nextInt(SoundData.class);

    private final String name;
    private final ByteBuf buffer;

    public SoundData(String name, ByteBuf buffer) {
        this.name = name;
        this.buffer = buffer;
    }

    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ByteBuf getBuffer() {
        return buffer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SoundData that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "name", name);
    }
}
