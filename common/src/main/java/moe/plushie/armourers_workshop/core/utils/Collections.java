package moe.plushie.armourers_workshop.core.utils;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Collections {


    public static <S> ArrayList<S> filter(S[] in, Predicate<? super S> predicate) {
        return filter(newList(in), predicate);
    }

    public static <S> ArrayList<S> filter(Collection<? extends S> in, Predicate<? super S> predicate) {
        return compactMap(in, it -> {
            if (predicate.test(it)) {
                return it;
            }
            return null;
        });
    }

    public static <S> Iterable<S> filter(Iterable<? extends S> in, Predicate<? super S> predicate) {
        return compactMap(in, it -> {
            if (predicate.test(it)) {
                return it;
            }
            return null;
        });
    }


    public static <S, R> ArrayList<R> collect(Collection<? extends S> in, Class<? extends R> clazz) {
        return compactMap(in, it -> {
            if (clazz.isInstance(it)) {
                return clazz.cast(it);
            }
            return null;
        });
    }

    public static <S, R> Iterable<R> collect(Iterable<? extends S> in, Class<? extends R> clazz) {
        return compactMap(in, it -> {
            if (clazz.isInstance(it)) {
                return clazz.cast(it);
            }
            return null;
        });
    }

    public static <S, R> ArrayList<R> compactMap(S[] in, Function<S, ? extends R> transform) {
        return compactMap(newList(in), transform);
    }

    public static <S, R> ArrayList<R> compactMap(Collection<? extends S> in, Function<S, ? extends R> transform) {
        var results = new ArrayList<R>(in.size());
        for (S value : in) {
            if (value == null) {
                continue;
            }
            R result = transform.apply(value);
            if (result == null) {
                continue;
            }
            results.add(result);
        }
        return results;
    }

    public static <S, R> Iterable<R> compactMap(Iterable<? extends S> in, Function<S, ? extends R> transform) {
        return () -> new Iterator<R>() {
            final Iterator<? extends S> baseIterator = in.iterator();
            R nextValue = null;

            @Override
            public boolean hasNext() {
                return peek() != null;
            }

            @Override
            public R next() {
                R value = peek();
                nextValue = null;
                return value;
            }

            private R peek() {
                while (nextValue == null && baseIterator.hasNext()) {
                    S value = baseIterator.next();
                    if (value != null) {
                        nextValue = transform.apply(value);
                    }
                }
                return nextValue;
            }
        };
    }

    @SafeVarargs
    public static <T> List<T> newList(T... elements) {
        return Lists.newArrayList(elements);
    }

    public static <T> List<T> newList(Iterable<? extends T> elements) {
        return Lists.newArrayList(elements);
    }

    public static <T> Collection<T> newList(int size, Function<Integer, T> builder) {
        ArrayList<T> results = new ArrayList<>();
        results.ensureCapacity(size);
        for (int i = 0; i < size; ++i) {
            results.add(builder.apply(i));
        }
        return results;
    }

    public static <T> List<T> emptyList() {
        return java.util.Collections.emptyList();
    }


    public static <T> Set<T> singleton(T o) {
        return java.util.Collections.singleton(o);
    }

    public static <T> T max(Collection<? extends T> coll, Comparator<? super T> comp) {
        return java.util.Collections.max(coll, comp);
    }


    public static <T> void eachTree(Iterable<T> collection, Function<T, Iterable<T>> children, Consumer<T> consumer) {
        for (T value : collection) {
            consumer.accept(value);
            eachTree(children.apply(value), children, consumer);
        }
    }
}
