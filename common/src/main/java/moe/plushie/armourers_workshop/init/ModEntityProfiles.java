package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.IEntityTypeProvider;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.data.IDataPackBuilder;
import moe.plushie.armourers_workshop.core.data.DataPackType;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.menu.SkinSlotType;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;
import moe.plushie.armourers_workshop.core.utils.FileUtils;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.platform.DataPackManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@SuppressWarnings("unused")
public class ModEntityProfiles {

    private static final ArrayList<BiConsumer<IEntityTypeProvider<?>, EntityProfile>> INSERT_HANDLERS = new ArrayList<>();
    private static final ArrayList<BiConsumer<IEntityTypeProvider<?>, EntityProfile>> REMOVE_HANDLERS = new ArrayList<>();
    private static final ArrayList<BiConsumer<IEntityTypeProvider<?>, EntityProfile>> UPDATE_HANDLERS = new ArrayList<>();

    private static final Map<IResourceLocation, EntityProfile> USING_PROFILES = new LinkedHashMap<>();
    private static final Map<IResourceLocation, EntityProfile> CUSTOM_PROFILES = new LinkedHashMap<>();
    private static final Map<IResourceLocation, EntityProfile> BUILTIN_PROFILES = new LinkedHashMap<>();

    private static final Map<IEntityTypeProvider<?>, EntityProfile> USING_ENTITIES = new LinkedHashMap<>();
    private static final Map<IEntityTypeProvider<?>, EntityProfile> CUSTOM_ENTITIES = new LinkedHashMap<>();
    private static final Map<IEntityTypeProvider<?>, EntityProfile> BUILTIN_ENTITIES = new LinkedHashMap<>();
    private static final Map<IEntityTypeProvider<?>, EntityProfile> SERVER_ENTITIES = new LinkedHashMap<>();

    public static void init() {
        DataPackManager.register(DataPackType.SERVER_DATA, "skin/profiles", SimpleLoader::custom, null, SimpleLoader::freezeCustom, 1);
        DataPackManager.register(DataPackType.BUNDLED_DATA, "skin/profiles", SimpleLoader::builtin, null, SimpleLoader::freezeBuiltin, 1);
    }

    public static void addListener(BiConsumer<IEntityTypeProvider<?>, EntityProfile> changeHandler) {
        REMOVE_HANDLERS.add((entityType, entityProfile) -> changeHandler.accept(entityType, null));
        INSERT_HANDLERS.add(changeHandler);
        UPDATE_HANDLERS.add(changeHandler);
        // if it add listener after the loading, we need manual send a notification.
        USING_ENTITIES.forEach(changeHandler);
    }

    @Nullable
    public static <T extends Entity> EntityProfile getProfile(T entity) {
        return getProfile(entity.getType());
    }

    @Nullable
    public static <T extends Entity> EntityProfile getProfile(EntityType<T> entityType) {
        //
        for (var entry : USING_ENTITIES.entrySet()) {
            if (entityType.equals(entry.getKey().get())) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Nullable
    public static EntityProfile getProfile(IResourceLocation registryName) {
        return USING_PROFILES.get(registryName);
    }

    public static void setCustomProfiles(Map<IEntityTypeProvider<?>, EntityProfile> snapshot) {
        // ignore when no changes.
        if (SERVER_ENTITIES.equals(snapshot)) {
            return;
        }
        ModLog.debug("apply entity profile changes from server");
        SERVER_ENTITIES.clear();
        SERVER_ENTITIES.putAll(snapshot);
        SimpleLoader.freeze();
    }

    public static Map<IEntityTypeProvider<?>, EntityProfile> getCustomProfiles() {
        return CUSTOM_ENTITIES;
    }

    private static class SimpleLoader implements IDataPackBuilder {

        private static final Map<IResourceLocation, SimpleBuilder> CUSTOM_PROFILE_BUILDERS = new LinkedHashMap<>();
        private static final Map<IResourceLocation, SimpleBuilder> BUILTIN_PROFILE_BUILDERS = new LinkedHashMap<>();

        private final SimpleBuilder builder;

        public SimpleLoader(SimpleBuilder builder) {
            this.builder = builder;
        }

        public static SimpleLoader builtin(IResourceLocation registryName) {
            return new SimpleLoader(BUILTIN_PROFILE_BUILDERS.computeIfAbsent(registryName, SimpleBuilder::builtin));
        }

        public static SimpleLoader custom(IResourceLocation registryName) {
            return new SimpleLoader(CUSTOM_PROFILE_BUILDERS.computeIfAbsent(registryName, SimpleBuilder::custom));
        }

        @Override
        public void append(IODataObject object, IResourceLocation location) {
            if (object.get("replace").boolValue()) {
                builder.isLocked = false;
                builder.supports.clear();
                builder.transformers.clear();
                builder.entities.clear();
            }
            object.get("locked").ifPresent(o -> {
                builder.isLocked = o.boolValue();
            });
            object.get("slots").entrySet().forEach(it -> {
                var type = SkinSlotType.byName(it.getKey());
                var name = it.getValue().stringValue();
                if (type != null) {
                    builder.supports.put(type, name);
                }
            });
            object.get("transformers").allValues().forEach(o -> {
                builder.transformers.add(OpenResourceLocation.parse(o.stringValue()));
            });
            object.get("entities").allValues().forEach(o -> {
                builder.entities.add(IEntityTypeProvider.of(o.stringValue()));
            });
        }

        @Override
        public void build() {
            // ignore.
        }

        private static void freezeCustom() {
            // regenerate all entity profile.
            var newEntities = new LinkedHashMap<IEntityTypeProvider<?>, EntityProfile>();
            CUSTOM_ENTITIES.clear();
            CUSTOM_PROFILE_BUILDERS.forEach((key, builder) -> {
                var profile = builder.build();
                builder.entities.forEach(entityType -> newEntities.put(entityType, profile));
            });
            CUSTOM_PROFILE_BUILDERS.clear();
            // only use when custom profile changed.
            var usedProfiles = new LinkedHashMap<IResourceLocation, EntityProfile>();
            newEntities.forEach((entityType, profile) -> {
                var oldProfile = BUILTIN_ENTITIES.get(entityType);
                if (oldProfile != null && EntityProfile.same(oldProfile, profile)) {
                    return; // not any change.
                }
                CUSTOM_ENTITIES.put(entityType, profile);
                usedProfiles.put(profile.getRegistryName(), profile);
            });
            // apply the patch
            difference(CUSTOM_PROFILES, usedProfiles, (registryName, entityProfile) -> {
                CUSTOM_PROFILES.remove(registryName);
                ModLog.debug("Unregistering Entity Profile '{}'", registryName);
            }, (registryName, entityProfile) -> {
                ModLog.debug("Registering Entity Profile '{}'", registryName);
                CUSTOM_PROFILES.put(registryName, entityProfile);
            }, null);
            // freeze all data.
            freeze();
        }

        private static void freezeBuiltin() {
            // regenerate all entity profile.
            var newProfiles = new LinkedHashMap<IResourceLocation, EntityProfile>();
            BUILTIN_ENTITIES.clear();
            BUILTIN_PROFILE_BUILDERS.forEach((key, builder) -> {
                var profile = builder.build();
                newProfiles.put(builder.registryName, profile);
                builder.entities.forEach(entityType -> BUILTIN_ENTITIES.put(entityType, profile));
            });
            BUILTIN_PROFILE_BUILDERS.clear();
            // apply the patch
            difference(BUILTIN_PROFILES, newProfiles, (registryName, entityProfile) -> {
                BUILTIN_PROFILES.remove(registryName);
                ModLog.debug("Unregistering Entity Profile '{}'", registryName);
            }, (registryName, entityProfile) -> {
                ModLog.debug("Registering Entity Profile '{}'", registryName);
                BUILTIN_PROFILES.put(registryName, entityProfile);
            }, null);
            // freeze all data.
            freeze();
        }

        private static void freeze() {
            // apply the patch.
            var entities = new LinkedHashMap<IEntityTypeProvider<?>, EntityProfile>();
            entities.putAll(BUILTIN_ENTITIES);
            entities.putAll(CUSTOM_ENTITIES);
            entities.putAll(SERVER_ENTITIES);
            difference(USING_ENTITIES, entities, (entityType, entityProfile) -> {
                USING_ENTITIES.remove(entityType);
                REMOVE_HANDLERS.forEach(handler -> handler.accept(entityType, entityProfile));
            }, (entityType, entityProfile) -> {
                USING_ENTITIES.put(entityType, entityProfile);
                INSERT_HANDLERS.forEach(handler -> handler.accept(entityType, entityProfile));
            }, (entityType, entityProfile) -> {
                USING_ENTITIES.put(entityType, entityProfile);
                UPDATE_HANDLERS.forEach(handler -> handler.accept(entityType, entityProfile));
            });
            USING_PROFILES.clear();
            entities.values().forEach(profile -> USING_PROFILES.put(profile.getRegistryName(), profile));
        }

        private static <K, V> void difference(Map<K, V> oldValue, Map<K, V> newValue, BiConsumer<K, V> removeHandler, BiConsumer<K, V> insertHandler, BiConsumer<K, V> updateHandler) {
            var removedEntities = new LinkedHashMap<>(oldValue);
            newValue.forEach((key, value) -> {
                var oldEntry = removedEntities.remove(key);
                if (oldEntry == null) {
                    if (insertHandler != null) {
                        insertHandler.accept(key, value);
                    }
                } else if (oldEntry != value) {
                    if (updateHandler != null) {
                        updateHandler.accept(key, value);
                    }
                }
            });
            if (removeHandler != null) {
                removedEntities.forEach(removeHandler);
            }
        }
    }

    private static class SimpleBuilder {

        private final IResourceLocation registryName;

        private final List<IEntityTypeProvider<?>> entities = new ArrayList<>();
        private final List<IResourceLocation> transformers = new ArrayList<>();

        private final Map<SkinSlotType, String> supports = new LinkedHashMap<>();

        private boolean isLocked = false;

        public SimpleBuilder(IResourceLocation registryName) {
            this.registryName = registryName;
        }

        public static SimpleBuilder builtin(IResourceLocation location) {
            var path = FileUtils.getRegistryName(location.getPath(), "skin/profiles/");
            return new SimpleBuilder(location.withPath("builtin/" + path));
        }

        public static SimpleBuilder custom(IResourceLocation location) {
            var path = FileUtils.getRegistryName(location.getPath(), "skin/profiles/");
            return new SimpleBuilder(location.withPath(path));
        }

        public EntityProfile build() {
            return new EntityProfile(registryName, supports, transformers, isLocked);
        }
    }
}
