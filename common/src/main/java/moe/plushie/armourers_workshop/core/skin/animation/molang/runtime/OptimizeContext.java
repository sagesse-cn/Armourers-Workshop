package moe.plushie.armourers_workshop.core.skin.animation.molang.runtime;

import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.VariableStorage;

/**
 * The optimizer will use the special context to optimize expression.
 */
public class OptimizeContext extends StaticVariableStorage implements ExecutionContext {

    public static final OptimizeContext DEFAULT = new OptimizeContext();

    private final LocalVariableStorage localStorage = new LocalVariableStorage();

    @Override
    public ExecutionContext fork(Object target) {
        return this; // optimize does not support it.
    }

    @Override
    public LocalVariableStorage stack() {
        return localStorage;
    }

    @Override
    public VariableStorage entity() {
        return this;
    }
}
