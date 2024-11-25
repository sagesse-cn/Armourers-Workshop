package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ItemModel {

    private final IResourceLocation name;
    private final Map<OpenItemDisplayContext, ItemTransform> transforms;

    private final ItemProperty[] properties;
    private final List<ItemPropertyMatcher> matchers = new ArrayList<>();

    public ItemModel(IResourceLocation name, List<ItemOverride> overrides, Map<OpenItemDisplayContext, ItemTransform> transforms) {
        this.name = name;
        this.transforms = transforms;
        // bake
        var indexedProperties = new ArrayList<ItemProperty>();
        for (var override : overrides) {
            var childTester = new ArrayList<Predicate<float[]>>();
            var childProperties = override.getProperties();
            var childValues = override.getValues();
            for (int i = 0; i < childProperties.length; ++i) {
                var childProperty = childProperties[i];
                var childValue = childValues[i];
                int idx = indexedProperties.indexOf(childProperty);
                if (idx == -1) {
                    idx = indexedProperties.size();
                    indexedProperties.add(childProperty);
                }
                var index = idx;
                childTester.add(result -> result[index] >= childValue);
            }
            this.matchers.add(new ItemPropertyMatcher(override, childTester));
        }
        this.properties = indexedProperties.toArray(new ItemProperty[0]);
    }

    public ItemModel resolve(ItemStack itemStack, @Nullable Entity entity, @Nullable Level level, int flags) {
        int length = properties.length;
        if (length == 0) {
            return this;
        }
        // evaluate all properties.
        var results = new float[length];
        for (int i = 0; i < length; ++i) {
            results[i] = properties[i].apply(itemStack, entity, level, flags);
        }
        // test all properties
        for (var matcher : matchers) {
            if (matcher.test(results)) {
                var model = matcher.override.getModel();
                if (model != null) {
                    return model;
                }
                return this;
            }
        }
        return this;
    }

    public ItemTransform getTransform(OpenItemDisplayContext transformType) {
        return transforms.getOrDefault(transformType, ItemTransform.NO_TRANSFORM);
    }

    public IResourceLocation getName() {
        return name;
    }

    private static class ItemPropertyMatcher {

        private final List<Predicate<float[]>> testers;
        private final ItemOverride override;

        public ItemPropertyMatcher(ItemOverride override, List<Predicate<float[]>> tester) {
            this.testers = tester;
            this.override = override;
        }

        public boolean test(float[] results) {
            for (var tester : testers) {
                if (!tester.test(results)) {
                    return false;
                }
            }
            return true;
        }
    }
}
