package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.IEntityTypeProvider;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.data.IDataPackBuilder;
import moe.plushie.armourers_workshop.core.data.DataPackType;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.menu.SkinSlotType;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;
import moe.plushie.armourers_workshop.core.utils.Collections;
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

    private static final Map<IEntityTypeProvider<?>, EntityProfile> ALL_ENTITIES = new LinkedHashMap<>();

    private static final Map<IResourceLocation, EntityProfile> ALL_BUILTIN_PROFILES = new LinkedHashMap<>();
    private static final Map<IResourceLocation, EntityProfile> ALL_CUSTOM_PROFILES = new LinkedHashMap<>();

    public static void init() {
        DataPackManager.register(DataPackType.SERVER_DATA, "skin/profiles", SimpleLoader::custom, null, SimpleLoader::freezeCustom, 1);
        DataPackManager.register(DataPackType.BUNDLED_DATA, "skin/profiles", SimpleLoader::builtin, null, SimpleLoader::freezeBuiltin, 1);
    }

    public static void addListener(BiConsumer<IEntityTypeProvider<?>, EntityProfile> changeHandler) {
        REMOVE_HANDLERS.add((entityType, entityProfile) -> changeHandler.accept(entityType, null));
        INSERT_HANDLERS.add(changeHandler);
        UPDATE_HANDLERS.add(changeHandler);
        // if it add listener after the loading, we need manual send a notification.
        ALL_ENTITIES.forEach(changeHandler);
    }

    @Nullable
    public static <T extends Entity> EntityProfile getProfile(T entity) {
        return getProfile(entity.getType());
    }

    @Nullable
    public static <T extends Entity> EntityProfile getProfile(EntityType<T> entityType) {
        //
        for (var entry : ALL_ENTITIES.entrySet()) {
            if (entityType.equals(entry.getKey().get())) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Nullable
    public static EntityProfile getProfile(IResourceLocation registryName) {
        return ALL_BUILTIN_PROFILES.get(registryName);
    }

    public static void setCustomProfiles(List<EntityProfile> snapshot) {
        ModLog.debug("apply custom profile changes");
        SimpleLoader.freezeCustom(Collections.immutableMap(builder -> {
            if (snapshot != null) {
                snapshot.forEach(it -> builder.put(it.getRegistryName(), it));
            }
        }));
    }

    public static List<EntityProfile> getCustomProfiles() {
        return Collections.newList(ALL_CUSTOM_PROFILES.values());
    }

    private static class SimpleLoader implements IDataPackBuilder {

        private static final Map<IResourceLocation, EntityProfile> PENDING_BUILTIN_PROFILES = new LinkedHashMap<>();
        private static final Map<IResourceLocation, EntityProfile> PENDING_CUSTOM_PROFILES = new LinkedHashMap<>();

        private boolean isLocked = false;

        private final OpenResourceLocation registryName;

        private final List<IEntityTypeProvider<Entity>> entities = new ArrayList<>();
        private final List<IResourceLocation> transformers = new ArrayList<>();
        private final Map<SkinSlotType, String> supports = new LinkedHashMap<>();

        private final Map<IResourceLocation, EntityProfile> container;

        public SimpleLoader(IResourceLocation location, Map<IResourceLocation, EntityProfile> container) {
            var path = FileUtils.getRegistryName(location.getPath(), "skin/profiles/");
            this.container = container;
            this.registryName = OpenResourceLocation.create(location.getNamespace(), path);
        }

        public static SimpleLoader builtin(IResourceLocation registryName) {
            return new SimpleLoader(registryName, PENDING_BUILTIN_PROFILES);
        }

        public static SimpleLoader custom(IResourceLocation registryName) {
            return new SimpleLoader(registryName, PENDING_CUSTOM_PROFILES);
        }

        @Override
        public void append(IODataObject object, IResourceLocation location) {
            if (object.get("replace").boolValue()) {
                isLocked = false;
                supports.clear();
                entities.clear();
            }
            object.get("locked").ifPresent(o -> {
                isLocked = o.boolValue();
            });
            object.get("slots").entrySet().forEach(it -> {
                var type = SkinSlotType.byName(it.getKey());
                var name = it.getValue().stringValue();
                if (type != null) {
                    supports.put(type, name);
                }
            });
            object.get("transformers").allValues().forEach(o -> {
                transformers.add(OpenResourceLocation.parse(o.stringValue()));
            });
            object.get("entities").allValues().forEach(o -> {
                entities.add(IEntityTypeProvider.of(o.stringValue()));
            });
        }

        @Override
        public void build() {
            var profile = new EntityProfile(registryName, supports, transformers, entities, isLocked);
            container.put(registryName, profile);
        }


        private static void freezeCustom() {
            // remove all builtin
            ALL_BUILTIN_PROFILES.forEach((registryName, entityProfile) -> {
                var entityProfile1 = PENDING_CUSTOM_PROFILES.get(registryName);
                if (entityProfile1 != null && entityProfile1.equals(entityProfile)) {
                    PENDING_CUSTOM_PROFILES.remove(registryName); // ignore duplicate entry.
                }
            });
            freezeCustom(PENDING_CUSTOM_PROFILES);
            PENDING_CUSTOM_PROFILES.clear();
        }

        private static void freezeCustom(Map<IResourceLocation, EntityProfile> pending) {
            // apply the patch
            difference(ALL_CUSTOM_PROFILES, pending, (registryName, entityProfile) -> {
                ALL_CUSTOM_PROFILES.remove(registryName);
                ModLog.debug("Unregistering Custom Entity Profile '{}'", registryName);
            }, (registryName, entityProfile) -> {
                ModLog.debug("Registering Custom Entity Profile '{}'", registryName);
                ALL_CUSTOM_PROFILES.put(registryName, entityProfile);
            }, null);
            freeze();
        }

        private static void freezeBuiltin() {
            // apply the patch
            difference(ALL_BUILTIN_PROFILES, PENDING_BUILTIN_PROFILES, (registryName, entityProfile) -> {
                ALL_BUILTIN_PROFILES.remove(registryName);
                ModLog.debug("Unregistering Entity Profile '{}'", registryName);
            }, (registryName, entityProfile) -> {
                ModLog.debug("Registering Entity Profile '{}'", registryName);
                ALL_BUILTIN_PROFILES.put(registryName, entityProfile);
            }, null);
            PENDING_BUILTIN_PROFILES.clear();
            freeze();
        }

        private static void freeze() {
            // generate the entity profile.
            var entities = new LinkedHashMap<IEntityTypeProvider<?>, EntityProfile>();
            ALL_BUILTIN_PROFILES.forEach((registryName, profile) -> profile.getEntities().forEach(entityType -> entities.put(entityType, profile)));
            ALL_CUSTOM_PROFILES.forEach((registryName, profile) -> profile.getEntities().forEach(entityType -> entities.put(entityType, profile)));
            // apply the patch.
            difference(ALL_ENTITIES, entities, (entityType, entityProfile) -> {
                ALL_ENTITIES.remove(entityType);
                REMOVE_HANDLERS.forEach(handler -> handler.accept(entityType, entityProfile));
            }, (entityType, entityProfile) -> {
                ALL_ENTITIES.put(entityType, entityProfile);
                INSERT_HANDLERS.forEach(handler -> handler.accept(entityType, entityProfile));
            }, (entityType, entityProfile) -> {
                ALL_ENTITIES.put(entityType, entityProfile);
                UPDATE_HANDLERS.forEach(handler -> handler.accept(entityType, entityProfile));
            });
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
}
