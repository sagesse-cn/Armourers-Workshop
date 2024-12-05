package moe.plushie.armourers_workshop.core.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class OpenRandomSource {

    private final static ConcurrentHashMap<Class<?>, AtomicInteger> IDS = new ConcurrentHashMap<>();

    public static int nextInt(Class<?> clazz) {
        return IDS.computeIfAbsent(clazz, k -> new AtomicInteger()).incrementAndGet();
    }
}
