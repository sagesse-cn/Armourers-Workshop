package moe.plushie.armourers_workshop.core.skin.animation.engine.bind;

import moe.plushie.armourers_workshop.core.skin.animation.molang.runtime.StaticVariableStorage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityVariableStorageImpl {

    private final Map<Integer, StaticVariableStorage> storage = new ConcurrentHashMap<>();

    public StaticVariableStorage get(ContextSelectorImpl context) {
        return storage.computeIfAbsent(context.getId(), it -> new StaticVariableStorage());
    }
}
