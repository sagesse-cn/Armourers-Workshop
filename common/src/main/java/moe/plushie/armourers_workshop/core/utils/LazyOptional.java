package moe.plushie.armourers_workshop.core.utils;

import java.util.Optional;
import java.util.function.Supplier;

public class LazyOptional<T> {

    private static final LazyOptional<Void> EMPTY = new LazyOptional<>(null);

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<T> value;
    private Supplier<Optional<T>> provider;

    public LazyOptional(Supplier<Optional<T>> provider) {
        this.provider = provider;
    }

    public static <T> LazyOptional<T> of(Supplier<Optional<T>> provider) {
        return new LazyOptional<>(provider);
    }

    public static <T> LazyOptional<T> ofNullable(Supplier<T> provider) {
        return new LazyOptional<>(() -> Optional.ofNullable(provider.get()));
    }

    @SuppressWarnings("unchecked")
    public static <T> LazyOptional<T> empty() {
        return (LazyOptional<T>) EMPTY;
    }

    public Optional<T> resolve() {
        if (provider != null) {
            value = provider.get();
            provider = null;
        }
        return value;
    }
}
