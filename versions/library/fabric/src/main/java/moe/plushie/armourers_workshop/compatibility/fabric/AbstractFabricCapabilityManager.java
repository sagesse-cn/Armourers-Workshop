package moe.plushie.armourers_workshop.compatibility.fabric;

import moe.plushie.armourers_workshop.api.common.IBlockEntityCapability;
import moe.plushie.armourers_workshop.api.common.IEntityCapability;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.core.data.CapabilityStorage;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.TypedRegistry;
import moe.plushie.armourers_workshop.init.ModBlockEntityTypes;
import moe.plushie.armourers_workshop.init.ModConstants;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.function.Function;

public class AbstractFabricCapabilityManager {

    public static <T> IRegistryHolder<IEntityCapability<T>> registerEntity(IResourceLocation registryName, Class<T> type, Function<Entity, Optional<T>> factory) {
        var capabilityType = new IEntityCapability<T>() {
            @Override
            public Optional<T> get(Entity entity) {
                return CapabilityStorage.getCapability(entity, this);
            }
        };
        CapabilityStorage.registerCapability(registryName, capabilityType, factory);
        return TypedRegistry.Entry.ofValue(registryName, capabilityType);
    }

    public static <T> IRegistryHolder<IBlockEntityCapability<T>> registerBlockEntity(IResourceLocation registryName, Class<T> type, Function<Entity, Optional<T>> factory) {
        if (registryName.equals(ModConstants.key("item"))) {
            return Objects.unsafeCast(createSidedCapability(registryName, ItemStorage.SIDED));
        }
        if (registryName.equals(ModConstants.key("fluid"))) {
            return Objects.unsafeCast(createSidedCapability(registryName, FluidStorage.SIDED));
        }
        if (registryName.equals(ModConstants.key("energy"))) {
            var api = findStaticField("team.reborn.energy.api.EnergyStorage", "SIDED");
            if (api != null) {
                return Objects.unsafeCast(createSidedCapability(registryName, api));
            }
        }
        return null;
    }

    private static <T> IRegistryHolder<IBlockEntityCapability<T>> createSidedCapability(IResourceLocation registryName, BlockApiLookup<T, Direction> apiLookup) {
        // register into skinnable block
        IBlockEntityCapability<T> capability1 = apiLookup::find;
        apiLookup.registerForBlockEntity((entity, context) -> entity.getCapability(capability1, context), ModBlockEntityTypes.SKINNABLE.get().get());
        return TypedRegistry.Entry.castValue(registryName, capability1);
    }


    private static <T> BlockApiLookup<T, Direction> findStaticField(String className, String fieldName) {
        try {
            var obj = Class.forName(className);
            var field = obj.getDeclaredField(fieldName);
            return Objects.unsafeCast(field.get(obj));
        } catch (Exception e) {
            return null;
        }
    }
}
