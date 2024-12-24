package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.common.IEntityCapability;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IEntityCapabilityBuilder;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeCapabilityManager;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.function.Function;

public class EntityCapabilityBuilderImpl<T> implements IEntityCapabilityBuilder<T> {

    private final Class<T> type;
    private final Function<Entity, Optional<T>> factory;

    public EntityCapabilityBuilderImpl(Class<T> type, Function<Entity, Optional<T>> factory) {
        this.type = type;
        this.factory = factory;
    }

    @Override
    public IRegistryHolder<IEntityCapability<T>> build(String name) {
        var registryName = ModConstants.key(name);
        ModLog.debug("Registering Entity Capability '{}'", registryName);
        return AbstractForgeCapabilityManager.registerEntity(registryName, type, factory);
    }
}
