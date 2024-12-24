package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.common.IEntityCapability;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractCapabilityStorage;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobeStorage;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Optional;
import java.util.function.Function;

public class CapabilityStorage {

    private static final ArrayList<Entry<?>> ENTRIES = new ArrayList<>();
    private static final CapabilityStorage NONE = new CapabilityStorage(new IdentityHashMap<>());

    private final IdentityHashMap<IEntityCapability<?>, Pair<Entry<?>, Optional<?>>> capabilities;

    CapabilityStorage(IdentityHashMap<IEntityCapability<?>, Pair<Entry<?>, Optional<?>>> capabilities) {
        this.capabilities = capabilities;
    }

    public static <T> void registerCapability(IResourceLocation registryName, IEntityCapability<T> capabilityType, Function<Entity, Optional<T>> provider) {
        ENTRIES.add(new Entry<>(registryName, capabilityType, provider));
    }

    public static CapabilityStorage attachCapability(Entity entity) {
        if (ENTRIES.isEmpty()) {
            return NONE;
        }
        var capabilities = new IdentityHashMap<IEntityCapability<?>, Pair<Entry<?>, Optional<?>>>();
        for (var entry : ENTRIES) {
            var cap = entry.provider.apply(entity);
            if (cap.isPresent()) {
                capabilities.put(entry.capabilityType, Pair.of(entry, cap));
            }
        }
        if (capabilities.isEmpty()) {
            return NONE;
        }
        return new CapabilityStorage(capabilities);
    }

    public static <T> Optional<T> getCapability(Entity entity, IEntityCapability<T> capabilityType) {
        var storage = ((Provider) entity).getCapabilityStorage();
        var value = storage.capabilities.get(capabilityType);
        if (value != null) {
            return Objects.unsafeCast(value.getValue());
        }
        return Optional.empty();
    }

    public void save(Entity entity, CompoundTag tag) {
        if (this == NONE) {
            return;
        }
        var capsKey = AbstractCapabilityStorage.KEY;
        var caps = tag.getCompound(capsKey);
        capabilities.values().forEach(pair -> {
            if (pair.getValue().orElse(null) instanceof IDataSerializable.Mutable provider) {
                var tag1 = new CompoundTag();
                provider.serialize(SkinWardrobeStorage.encoder(entity, tag1));
                caps.put(pair.getKey().registryName.toString(), tag1);
            }
        });
        if (!caps.isEmpty()) {
            tag.put(capsKey, caps);
        } else {
            tag.remove(capsKey);
        }
    }

    public void load(Entity entity, CompoundTag tag) {
        if (this == NONE) {
            return;
        }
        var caps = getCapTag(tag);
        if (caps.isEmpty()) {
            return;
        }
        capabilities.values().forEach(pair -> {
            if (pair.getValue().orElse(null) instanceof IDataSerializable.Mutable provider) {
                var containerTag = caps.get(pair.getKey().registryName.toString());
                if (containerTag instanceof CompoundTag compoundTag) {
                    provider.deserialize(SkinWardrobeStorage.decoder(entity, compoundTag));
                }
            }
        });
    }

    private CompoundTag getCapTag(CompoundTag tag) {
        if (tag.contains(Constants.Key.OLD_CAPABILITY, Constants.TagFlags.COMPOUND)) {
            var caps = tag.getCompound(Constants.Key.OLD_CAPABILITY);
            if (tag.contains(Constants.Key.NEW_CAPABILITY, Constants.TagFlags.COMPOUND)) {
                caps = caps.copy();
                caps.merge(tag.getCompound(Constants.Key.NEW_CAPABILITY));
            }
            return caps;
        }
        return tag.getCompound(Constants.Key.NEW_CAPABILITY);
    }

    public interface Provider {
        CapabilityStorage getCapabilityStorage();
    }

    private static class Entry<T> {
        IResourceLocation registryName;
        IEntityCapability<T> capabilityType;
        Function<Entity, Optional<T>> provider;

        Entry(IResourceLocation registryName, IEntityCapability<T> capabilityType, Function<Entity, Optional<T>> provider) {
            this.registryName = registryName;
            this.capabilityType = capabilityType;
            this.provider = provider;
        }
    }
}
