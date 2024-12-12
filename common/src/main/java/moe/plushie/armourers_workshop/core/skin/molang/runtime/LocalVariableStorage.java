package moe.plushie.armourers_workshop.core.skin.molang.runtime;

import moe.plushie.armourers_workshop.core.skin.molang.core.Result;

import java.util.Arrays;

public class LocalVariableStorage {

    private Result[] elements;
    private final Scope scope = new Scope();

    public LocalVariableStorage() {
        this(16);
    }

    public LocalVariableStorage(int initialCapacity) {
        this.elements = new Result[Math.max(initialCapacity, 1)];
    }

    public Scope scope() {
        return scope;
    }

    public void setVariable(int address, Result value) {
        ensureCapacity(address + 1);
        this.elements[address] = value;
    }

    public Result getVariable(int address) {
        ensureCapacity(address + 1);
        var result = this.elements[address];
        if (result == null) {
            result = Result.NULL;
        }
        return result;
    }

    private void ensureCapacity(int minCapacity) {
        if (elements.length < minCapacity) {
            int newSize = Math.max(elements.length * 2, minCapacity);
            elements = Arrays.copyOf(elements, newSize);
        }
    }
}
