package moe.plushie.armourers_workshop.compatibility.client.model;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelBabyPose;
import moe.plushie.armourers_workshop.api.client.model.IModelPartCollector;
import moe.plushie.armourers_workshop.core.client.model.CachedModel;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class AbstractModelHolder {

    private static final HashMap<Class<?>, Entry<?, ?>> ENTRIES = new HashMap<>();

    public static <V extends Model, M extends IModel> M ofNullable(V model) {
        if (model != null) {
            return of(model);
        }
        return null;
    }

    public static <M extends IModel> M of(Model model) {
        M holder = DataContainer.getOrDefault(model, null);
        if (holder != null) {
            return holder;
        }
        holder = createHolder(model);
        DataContainer.set(model, holder);
        return holder;
    }

    public static <T extends Model> void register(Class<T> clazz, Map<String, String> mapper) {
        ENTRIES.put(clazz, new Entry<>(clazz, null, AbstractModelCollector.apply(clazz, mapper)));
    }

    private static <V extends Model, M extends IModel> M createHolder(V model) {
        HashSet<Entry<?, ?>> exists = new HashSet<>();
        ArrayList<Map<String, String>> builders = new ArrayList<>();
        Function<Container, M> factory = null;
        Class<?> clazz = model.getClass();
        while (clazz != Object.class) {
            Entry<V, M> entry = getEntry(clazz);
            if (entry != null && exists.add(entry)) {
                builders.add(entry.mapper);
                if (factory == null) {
                    factory = entry.factory;
                }
            }
            clazz = clazz.getSuperclass();
        }
        if (factory == null) {
            Function<Container, CachedModel<ModelPart>> factory1 = CachedModel::new;
            factory = Objects.unsafeCast(factory1);
        }
        Container container = new Container(model);
        Map<String, ModelPart> allParts = parseModel(model);
        builders.forEach(mapper -> mapper.forEach((target, source) -> {
            var part = allParts.remove(source);
            if (part != null) {
                container.put(target, part);
            }
        }));
        allParts.forEach((key, part) -> {
            if (!key.contains(".")) {
                container.unnamed(Collections.singleton(part));
            }
        });
        return factory.apply(container);
    }

    private static <V extends Model, M extends IModel> Entry<V, M> getEntry(Class<?> clazz) {
        Entry<?, ?> entry = ENTRIES.get(clazz);
        if (entry == null) {
            // this is root class?
            if (clazz == Object.class) {
                return null;
            }
            // autofill by parent entry.
            entry = getEntry(clazz.getSuperclass());
            ENTRIES.put(clazz, entry);
        }
        return Objects.unsafeCast(entry);
    }

    private static Map<String, ModelPart> parseModel(Model model) {
        var allParts = new LinkedHashMap<String, ModelPart>();
        if (model instanceof IModelPartCollector provider) {
            provider.aw2$collect(allParts);
        }
        return allParts;
    }

    private static class Entry<T extends Model, M extends IModel> {

        Class<T> clazz;
        Function<Container, M> factory;
        Map<String, String> mapper;

        Entry(Class<T> clazz, Function<Container, M> factory, Map<String, String> mapper) {
            this.clazz = clazz;
            this.factory = factory;
            this.mapper = mapper;
        }
    }

    private static class Container extends CachedModel.Container<ModelPart> {

        private final EntityModel<?> model;

        public Container(Model model) {
            super(model.getClass(), AbstractModelPartHolder::of);
            this.model = Objects.safeCast(model, EntityModel.class);
            this.babyPose = model.getBabyPose();
        }

        @Override
        public IModelBabyPose getBabyPose() {
            if (model != null && model.young) {
                return super.getBabyPose();
            }
            return null;
        }
    }
}
