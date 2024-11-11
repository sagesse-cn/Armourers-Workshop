package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractDataSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.entity.Entity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TagSerializer implements IDataSerializer {

    protected final CompoundTag tag;
    protected final IDataSerializer impl;

    public TagSerializer() {
        this(new CompoundTag());
    }

    public TagSerializer(CompoundTag tag) {
        this.tag = tag;
        this.impl = AbstractDataSerializer.wrap(tag);
    }

    public static void writeToStream(CompoundTag compoundTag, OutputStream outputStream) throws IOException {
        try (var dataOutputStream = new DataOutputStream(outputStream)) {
            NbtIo.write(compoundTag, dataOutputStream);
        }
    }

    public static CompoundTag readFromStream(InputStream inputStream) throws IOException {
        try (var datainputstream = new DataInputStream(inputStream)) {
            return NbtIo.read(datainputstream);
        }
    }

    public static CompoundTag parse(String contents) {
        try {
            return TagParser.parseTag(contents);
        } catch (Exception e) {
            return new CompoundTag();
        }
    }


    @Override
    public <T> T read(IDataSerializerKey<T> key) {
        return impl.read(key);
    }

    @Override
    public <T> void write(IDataSerializerKey<T> key, T value) {
        impl.write(key, value);
    }

    public CompoundTag getTag() {
        return tag;
    }
}
