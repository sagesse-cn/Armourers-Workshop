package moe.plushie.armourers_workshop.core.skin.molang.core;

public interface VariableStorage {

    void setVariable(Name name, Result value);

    Result getVariable(Name name);
}
