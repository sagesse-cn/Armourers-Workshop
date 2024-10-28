package moe.plushie.armourers_workshop.api.core;

import java.util.function.Supplier;

public interface IDataSerializerKey<T> {

    String getName();

    T getDefault();

    IDataCodec<T> getCodec();

    Supplier<T> getConstructor();


    static <T> IDataSerializerKey<T> create(String name, IDataCodec<T> codec) {
        return create(name, codec, null, null);
    }

    static <T> IDataSerializerKey<T> create(String name, IDataCodec<T> codec, T defaultValue) {
        return create(name, codec, defaultValue, null);
    }

    static <T> IDataSerializerKey<T> create(String name, IDataCodec<T> codec, T defaultValue, Supplier<T> constructor) {
        return new IDataSerializerKey<T>() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public T getDefault() {
                return defaultValue;
            }

            @Override
            public IDataCodec<T> getCodec() {
                return codec;
            }

            @Override
            public Supplier<T> getConstructor() {
                return constructor;
            }
        };
    }
}
