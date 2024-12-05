package moe.plushie.armourers_workshop.core.skin.animation.engine.bind;

import moe.plushie.armourers_workshop.core.data.EntityDataStorage;
import moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector.BlockEntitySelector;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Name;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.VariableStorage;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockEntitySelectorImpl<T extends BlockEntity> implements BlockEntitySelector, VariableStorage {

    protected T entity;
    protected VariableStorage variableStorage;
    protected ContextSelectorImpl contextSelector;

    public BlockEntitySelectorImpl<T> apply(T entity, ContextSelectorImpl contextSelector) {
        this.entity = entity;
        this.contextSelector = contextSelector;
        this.variableStorage = EntityDataStorage.of(entity).getVariableStorage().map(it -> it.get(contextSelector)).orElse(null);
        return this;
    }

    public T getEntity() {
        return entity;
    }

    @Override
    public float getPartialTick() {
        return contextSelector.getPartialTick();
    }

    @Override
    public void setVariable(Name name, Result value) {
        variableStorage.setVariable(name, value);
    }

    @Override
    public Result getVariable(Name name) {
        return variableStorage.getVariable(name);
    }
}
