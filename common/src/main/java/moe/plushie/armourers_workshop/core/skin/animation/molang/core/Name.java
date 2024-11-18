package moe.plushie.armourers_workshop.core.skin.animation.molang.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Name {

    private static final Map<String, Name> SHARED = new ConcurrentHashMap<>();

    private final int id;
    private final String name;

    private Name(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Name of(String name) {
        return SHARED.computeIfAbsent(name.toLowerCase(), it -> new Name(SHARED.size(), it));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Name that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }

    public int id() {
        return id;
    }

    public String value() {
        return name;
    }
}
