package moe.plushie.armourers_workshop.api.core.utils;

import java.util.Map;
import java.util.Objects;

public interface IPair<K, V> extends Map.Entry<K, V> {

    static <K, V> IPair<K, V> of(K key, V value) {
        return new IPair<K, V>() {
            @Override
            public K getKey() {
                return key;
            }

            @Override
            public V getValue() {
                return value;
            }

            @Override
            public V setValue(V value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof Map.Entry<?, ?> entry)) return false;
                return Objects.equals(key, entry.getKey()) && Objects.equals(value, entry.getValue());
            }

            @Override
            public int hashCode() {
                return Objects.hash(key, value);
            }
        };
    }
}
