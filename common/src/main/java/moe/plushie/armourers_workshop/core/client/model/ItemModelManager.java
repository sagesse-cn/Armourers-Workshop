package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.core.IResourceManager;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.FileUtils;
import moe.plushie.armourers_workshop.core.utils.JsonSerializer;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemModelManager {

    private static final ItemModelManager INSTANCE = new ItemModelManager();

    private ItemModel missingModel;

    private final Map<ISkinType, ItemModel> typedItemModels = new ConcurrentHashMap<>();
    private final Map<IResourceLocation, ItemModel> namedItemModels = new ConcurrentHashMap<>();

    private final Map<IResourceLocation, ItemProperty> namedItemProperties = Collections.immutableMap(builder -> {
        builder.put(ModConstants.key("is_skin"), vanilla("armourers_workshop:is_skin"));
        builder.put(ModConstants.key("is_crossbow"), vanilla("armourers_workshop:is_crossbow"));
        builder.put(ModConstants.key("is_blocking"), vanilla("minecraft:blocking"));
        builder.put(ModConstants.key("is_throwing"), vanilla("minecraft:throwing"));
    });

    public static ItemModelManager getInstance() {
        return INSTANCE;
    }

    public static void init() {
        new SimpleLoader(INSTANCE).load();
    }


    public ItemModel getModel(ISkinType skinType) {
        return typedItemModels.computeIfAbsent(skinType, it -> {
            var id = ModConstants.key("skin/" + skinType.getRegistryName().getPath());
            return namedItemModels.getOrDefault(id, missingModel);
        });
    }

    @Nullable
    public ItemProperty getProperty(IResourceLocation id) {
        return namedItemProperties.get(id);
    }

    private static ItemProperty vanilla(String id) {
        var registryName = OpenResourceLocation.parse(id);
        var location = registryName.toLocation();
        return new ItemProperty() {
            @Override
            public float apply(ItemStack itemStack, @Nullable Entity entity, @Nullable Level level, int flags) {
                var func = ItemProperties.getProperty(itemStack, location);
                if (func != null) {
                    return func.call(itemStack, (ClientLevel) level, (LivingEntity) entity, flags);
                }
                return Float.NEGATIVE_INFINITY;
            }

            @Override
            public String toString() {
                return registryName.toString();
            }
        };
    }

    private static class SimpleLoader {

        private final ItemModelManager modelManager;
        private final IResourceManager resourceManager;

        private final Map<IResourceLocation, SimpleBuilder> builders = new LinkedHashMap<>();
        private final Map<IResourceLocation, ItemModel> models = new LinkedHashMap<>();

        public SimpleLoader(ItemModelManager modelManager) {
            this.modelManager = modelManager;
            this.resourceManager = EnvironmentManager.getResourceManager();
        }

        public void load() {
            resourceManager.readResources(ModConstants.key("models/skin"), s -> s.endsWith(".json"), (location, resource) -> {
                var object = JsonSerializer.readFromResource(resource);
                if (object == null) {
                    return;
                }
                var path = FileUtils.getRegistryName(location.getPath(), "models/");
                var location1 = OpenResourceLocation.create(location.getNamespace(), FileUtils.removeExtension(path));
                var builder = builders.computeIfAbsent(location1, SimpleBuilder::new);
                object.get("parent").ifPresent(it -> {
                    var key = OpenResourceLocation.parse(it.stringValue());
                    builder.parent = builders.computeIfAbsent(key, SimpleBuilder::new);
                });
                object.get("display").entrySet().forEach(entry -> {
                    var name = entry.getKey();
                    var translation = parseVector3f(entry.getValue().get("translation"), Vector3f.ZERO);
                    var rotation = parseVector3f(entry.getValue().get("rotation"), Vector3f.ZERO);
                    var scale = parseVector3f(entry.getValue().get("scale"), Vector3f.ONE);
                    var rightTranslation = parseVector3f(entry.getValue().get("post_translation"), Vector3f.ZERO);
                    var rightRotation = parseVector3f(entry.getValue().get("post_rotation"), Vector3f.ZERO);
                    builder.addTransform(name, ItemTransform.create(rev(translation), rev(rotation), scale, rev(rightTranslation), rev(rightRotation)));
                });
                object.get("overrides").allValues().forEach(it -> {
                    var model = it.get("model").stringValue();
                    var predicate = new ArrayList<Pair<IResourceLocation, Number>>();
                    it.get("predicate").entrySet().forEach(entry -> {
                        // the value only is double.
                        var key = ModConstants.key(entry.getKey());
                        var value = entry.getValue().numberValue();
                        predicate.add(Pair.of(key, value));
                    });
                    builder.addOverride(model, predicate);
                });
            });
            // resolve the parent depends.
            var references = new IdentityHashMap<ItemOverride, IResourceLocation>();
            builders.forEach((name, builder) -> {
                var itemModel = builder.build(references);
                models.put(name, itemModel);
            });
            references.forEach((override, reference) -> override.setModel(models.get(reference)));
            // setup the missing item model.
            var missingModel = models.get(ModConstants.key("skin/unknown"));
            if (missingModel == null) {
                throw new RuntimeException("Can't find missing model, some think wrong!");
            }
            modelManager.namedItemModels.putAll(models);
            modelManager.missingModel = missingModel;
        }

        private Vector3f parseVector3f(IODataObject value, Vector3f defaultValue) {
            if (value.isNull()) {
                return defaultValue;
            }
            if (value.size() != 3) {
                throw new RuntimeException("Expected 3 double values, found: " + value.allValues());
            }
            float x = value.at(0).floatValue();
            float y = value.at(1).floatValue();
            float z = value.at(2).floatValue();
            return new Vector3f(x, y, z);
        }

        private Vector3f rev(Vector3f value) {
            if (value.equals(Vector3f.ZERO)) {
                return Vector3f.ZERO;
            }
            float x = value.getX();
            if (x != 0) {
                x = -x;
            }
            float y = value.getY();
            if (y != 0) {
                y = -y;
            }
            float z = value.getZ();
            return new Vector3f(x, y, z);
        }
    }

    private static class SimpleBuilder {

        private SimpleBuilder parent;

        private final IResourceLocation name;

        private final Map<IResourceLocation, List<Pair<IResourceLocation, Number>>> overrides = new LinkedHashMap<>();
        private final Map<OpenItemDisplayContext, ItemTransform> transforms = new LinkedHashMap<>();

        public SimpleBuilder(IResourceLocation name) {
            this.name = name;
        }

        public void addOverride(String name, List<Pair<IResourceLocation, Number>> predicate) {
            overrides.put(OpenResourceLocation.parse(name), predicate);
        }

        public void addTransform(String name, ItemTransform transform) {
            transforms.put(OpenItemDisplayContext.byName(name), transform);
        }

        public ItemModel build(Map<ItemOverride, IResourceLocation> references) {
            // ..
            var itemOverrides = new ArrayList<ItemOverride>();
            for (var override : overrides.entrySet()) {
                var it = override.getValue();
                var properties = new ItemProperty[it.size()];
                var values = new float[it.size()];
                for (int i = 0; i < properties.length; i++) {
                    properties[i] = getInstance().getProperty(it.get(i).getKey());
                    values[i] = it.get(i).getValue().floatValue();
                }
                var itemOverride = new ItemOverride(properties, values);
                itemOverrides.add(itemOverride);
                references.put(itemOverride, override.getKey());
            }
            // ..
            var itemTransforms = new EnumMap<OpenItemDisplayContext, ItemTransform>(OpenItemDisplayContext.class);
            for (var displayContext : OpenItemDisplayContext.values()) {
                itemTransforms.put(displayContext, resolveTransformValue(displayContext, ItemTransform.NO_TRANSFORM));
            }
            return new ItemModel(name, itemOverrides, itemTransforms);
        }

        private ItemTransform resolveTransformValue(OpenItemDisplayContext transformType, ItemTransform defaultValue) {
            var transform = transforms.get(transformType);
            if (transform != null) {
                return transform;
            }
            if (parent != null) {
                return parent.resolveTransformValue(transformType, defaultValue);
            }
            return defaultValue;
        }
    }
}
