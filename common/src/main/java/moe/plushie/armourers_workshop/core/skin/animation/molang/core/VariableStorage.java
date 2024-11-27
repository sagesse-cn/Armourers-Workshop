package moe.plushie.armourers_workshop.core.skin.animation.molang.core;

public interface VariableStorage {

    void setVariable(Name name, Result value);

    Result getVariable(Name name);
}
