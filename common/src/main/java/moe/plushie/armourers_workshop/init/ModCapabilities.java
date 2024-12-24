package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.IBlockEntityCapability;
import moe.plushie.armourers_workshop.api.common.IEntityCapability;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IRegistryBuilder;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("SameParameterValue")
public class ModCapabilities {

    public static final IRegistryHolder<IEntityCapability<SkinWardrobe>> ENTITY_WARDROBE = entity(SkinWardrobe.class, SkinWardrobe::create).build("entity-skin-provider");

    public static final IRegistryHolder<IBlockEntityCapability<Object>> BLOCK_ENTITY_ITEM = blockEntity(Object.class, null).build("item");
    public static final IRegistryHolder<IBlockEntityCapability<Object>> BLOCK_ENTITY_FLUID = blockEntity(Object.class, null).build("fluid");
    public static final IRegistryHolder<IBlockEntityCapability<Object>> BLOCK_ENTITY_ENERGY = blockEntity(Object.class, null).build("energy");

    private static <T> IRegistryBuilder<IEntityCapability<T>> entity(Class<T> type, Function<Entity, Optional<T>> provider) {
        return BuilderManager.getInstance().createEntityCapabilityBuilder(type, provider);
    }

    private static <T> IRegistryBuilder<IBlockEntityCapability<T>> blockEntity(Class<T> type, Function<Entity, Optional<T>> provider) {
        return BuilderManager.getInstance().createBlockEntityCapabilityBuilder(type, provider);
    }

    public static void init() {
    }
}
