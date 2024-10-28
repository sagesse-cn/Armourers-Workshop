package moe.plushie.armourers_workshop.core.skin.property;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperty;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenProperties;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SkinProperties extends OpenProperties implements ISkinProperties {

    public static final SkinProperties EMPTY = new SkinProperties();

    public static final IDataCodec<SkinProperties> CODEC = IDataCodec.COMPOUND_TAG.xmap(SkinProperties::new, SkinProperties::serializeNBT);

    public SkinProperties() {
        super();
    }

    public SkinProperties(CompoundTag tag) {
        super(tag);
    }

    protected SkinProperties(LinkedHashMap<String, Object> properties) {
        super(properties);
    }

    @Override
    public <T> T get(ISkinProperty<T> property) {
        var value = getOrDefault(property.getKey(), property.getDefaultValue());
        return Objects.unsafeCast(value);
    }

    @Override
    public <T> void put(ISkinProperty<T> property, T value) {
        if (Objects.equals(value, property.getDefaultValue())) {
            remove(property.getKey());
        } else {
            put(property.getKey(), value);
        }
    }

    @Override
    public <T> void remove(ISkinProperty<T> property) {
        remove(property.getKey());
    }

    @Override
    public <T> boolean containsKey(ISkinProperty<T> property) {
        return containsKey(property.getKey());
    }

    @Override
    public <T> boolean containsValue(ISkinProperty<T> property) {
        return containsValue(property.getKey());
    }

    public ArrayList<String> getPropertiesList() {
        var list = new ArrayList<String>();
        for (int i = 0; i < properties.size(); i++) {
            var key = (String) properties.keySet().toArray()[i];
            list.add(key + ":" + properties.get(key));
        }
        return list;
    }

    @Override
    public OpenProperties empty() {
        return new SkinProperties();
    }

    @Override
    public SkinProperties copy() {
        return new SkinProperties(new LinkedHashMap<>(properties));
    }

    public SkinProperties slice(int index) {
        return new SkinProperties.Stub(this, index);
    }

    public static class Stub extends SkinProperties {
        private final int index;

        public Stub(SkinProperties parent, int index) {
            super(parent.properties);
            this.index = index;
        }

        @Override
        public <T> void put(ISkinProperty<T> property, T value) {
            String indexedKey = getResolvedKey(property);
            if (indexedKey != null) {
                properties.put(indexedKey, value);
            } else {
                properties.put(property.getKey(), value);
            }
        }

        @Override
        public <T> void remove(ISkinProperty<T> property) {
            String indexedKey = getResolvedKey(property);
            if (indexedKey != null) {
                properties.remove(indexedKey);
            } else {
                properties.remove(property.getKey());
            }
        }

        @Override
        public <T> T get(ISkinProperty<T> property) {
            var indexedKey = getResolvedKey(property);
            Object value;
            if (indexedKey != null && properties.containsKey(indexedKey)) {
                value = properties.getOrDefault(indexedKey, property.getDefaultValue());
            } else {
                value = properties.getOrDefault(property.getKey(), property.getDefaultValue());
            }
            return Objects.unsafeCast(value);
        }

        @Override
        public <T> boolean containsKey(ISkinProperty<T> property) {
            String indexedKey = getResolvedKey(property);
            if (indexedKey != null && properties.containsKey(indexedKey)) {
                return true;
            }
            return properties.containsKey(property.getKey());
        }

        @Override
        public <T> boolean containsValue(ISkinProperty<T> property) {
            var indexedKey = getResolvedKey(property);
            if (indexedKey != null && properties.containsValue(indexedKey)) {
                return true;
            }
            return properties.containsValue(property.getKey());
        }

        @Nullable
        private <T> String getResolvedKey(ISkinProperty<T> property) {
            if (property instanceof SkinProperty<?> property1) {
                if (property1.isMultipleKey()) {
                    return property.getKey() + index;
                }
            }
            return null;
        }
    }
}
