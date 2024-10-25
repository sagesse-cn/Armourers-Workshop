package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public abstract class OpenProperties {

    protected final LinkedHashMap<String, Object> properties;

    public OpenProperties() {
        this(new LinkedHashMap<>());
    }

    public OpenProperties(CompoundTag tag) {
        this();
        this.readFromNBT(tag);
    }

    public OpenProperties(LinkedHashMap<String, Object> properties) {
        this.properties = properties;
    }

    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writeInt(properties.size());
        for (int i = 0; i < properties.size(); i++) {
            var key = (String) properties.keySet().toArray()[i];
            var value = properties.get(key);
            stream.writeString(key);
            if (value instanceof String stringValue) {
                stream.writeByte(DataTypes.STRING.ordinal());
                stream.writeString(stringValue);
            }
            if (value instanceof Integer intValue) {
                stream.writeByte(DataTypes.INT.ordinal());
                stream.writeInt(intValue);
            }
            if (value instanceof Double doubleValue) {
                stream.writeByte(DataTypes.DOUBLE.ordinal());
                stream.writeDouble(doubleValue);
            }
            if (value instanceof Boolean boolValue) {
                stream.writeByte(DataTypes.BOOLEAN.ordinal());
                stream.writeBoolean(boolValue);
            }
            if (value instanceof Collection<?> listValue) {
                stream.writeByte(DataTypes.LIST.ordinal());
                stream.writeInt(listValue.size());
                // TODO: NO IMPL
            }
            if (value instanceof OpenProperties compoundValue) {
                stream.writeByte(DataTypes.COMPOUND.ordinal());
                compoundValue.writeToStream(stream);
            }
        }
    }

    public void readFromStream(IInputStream stream) throws IOException {
        int count = stream.readInt();
        for (int i = 0; i < count; i++) {
            var key = stream.readString();
            var byteType = stream.readByte();
            var type = DataTypes.byId(byteType);
            if (type == null) {
                throw new IOException("Error loading properties " + byteType);
            }
            properties.put(key, switch (type) {
                case STRING -> stream.readString();
                case INT -> stream.readInt();
                case DOUBLE -> stream.readDouble();
                case BOOLEAN -> stream.readBoolean();
                case LIST -> {
                    int size = stream.readInt();
                    // TODO: NO IMPL
                    yield new ArrayList<>(size);
                }
                case COMPOUND -> {
                    var properties1 = empty();
                    properties1.readFromStream(stream);
                    yield properties1;
                }
            });
        }
    }


    public void readFromNBT(CompoundTag nbt) {
        for (String key : nbt.getAllKeys()) {
            Tag value = nbt.get(key);
            if (value instanceof StringTag stringTag) {
                properties.put(key, stringTag.getAsString());
            } else if (value instanceof IntTag intTag) {
                properties.put(key, intTag.getAsInt());
            } else if (value instanceof FloatTag floatTag) {
                properties.put(key, floatTag.getAsFloat());
            } else if (value instanceof DoubleTag doubleTag) {
                properties.put(key, doubleTag.getAsDouble());
            } else if (value instanceof ByteTag byteTag) {
                properties.put(key, byteTag.getAsByte() != 0);
            } else if (value instanceof ListTag listTag) {
                // TODO: NO IMPL
            } else if (value instanceof CompoundTag compoundTag) {
                var compoundValue = empty();
                compoundValue.readFromNBT(compoundTag);
                properties.put(key, compoundValue);
            }
        }
    }

    public void writeToNBT(CompoundTag nbt) {
        properties.forEach((key, value) -> {
            if (value instanceof String stringValue) {
                nbt.putString(key, stringValue);
            } else if (value instanceof Integer intValue) {
                nbt.putInt(key, intValue);
            } else if (value instanceof Float floatValue) {
                nbt.putDouble(key, floatValue);
            } else if (value instanceof Double doubleValue) {
                nbt.putDouble(key, doubleValue);
            } else if (value instanceof Boolean boolValue) {
                nbt.putBoolean(key, boolValue);
            } else if (value instanceof Collection<?> listValue) {
                // TODO: NO IMPL
            } else if (value instanceof OpenProperties compoundValue) {
                var compoundTag = new CompoundTag();
                compoundValue.writeToNBT(compoundTag);
                nbt.put(key, compoundTag);
            }
        });
    }

    public void put(String key, Object value) {
        if (value == null) {
            properties.remove(key);
        } else {
            properties.put(key, value);
        }
    }

    public void putAll(OpenProperties newValue) {
        properties.putAll(newValue.properties);
    }

    public void remove(String key) {
        properties.remove(key);
    }

    public void clear() {
        properties.clear();
    }

    public Object get(String key) {
        return properties.get(key);
    }

    public Object getOrDefault(String key, Object defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }

    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }

    public boolean containsValue(String key) {
        return properties.containsValue(key);
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }

    public abstract OpenProperties copy();

    public abstract OpenProperties empty();

    public Set<Map.Entry<String, Object>> entrySet() {
        return properties.entrySet();
    }

    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        writeToNBT(tag);
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenProperties that)) return false;
        return properties.equals(that.properties);
    }

    @Override
    public int hashCode() {
        return properties.hashCode();
    }

    @Override
    public String toString() {
        return properties.toString();
    }

    protected enum DataTypes {
        STRING, INT, DOUBLE, BOOLEAN, LIST, COMPOUND;

        @Nullable
        public static DataTypes byId(int id) {
            if (id >= 0 & id < DataTypes.values().length) {
                return DataTypes.values()[id];
            }
            return null;
        }
    }
}
