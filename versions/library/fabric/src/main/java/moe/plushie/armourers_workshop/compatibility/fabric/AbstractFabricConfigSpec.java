package moe.plushie.armourers_workshop.compatibility.fabric;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.config.IConfigBuilder;
import moe.plushie.armourers_workshop.api.config.IConfigSpec;
import moe.plushie.armourers_workshop.compatibility.core.AbstractConfigSpec;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.platform.fabric.config.FabricConfig;
import moe.plushie.armourers_workshop.init.platform.fabric.config.FabricConfigSpec;
import net.fabricmc.loader.api.FabricLoader;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Available("[1.16, )")
public class AbstractFabricConfigSpec extends AbstractConfigSpec {

    public AbstractFabricConfigSpec(Type type, HashMap<String, Value<Object>> values) {
        super(type, values);
    }

    public static <B extends IConfigBuilder> IConfigSpec create(Type type, Function<IConfigBuilder, B> applier) {
        // create a builder from loader.
        var pair = new FabricConfigSpec.Builder().configure(builder -> applier.apply(new Builder() {

            @Override
            public IConfigSpec build() {
                return new AbstractFabricConfigSpec(type, values);
            }

            @Override
            protected Builder push(String name) {
                builder.push(name);
                return this;
            }

            @Override
            protected Builder pop() {
                builder.pop();
                return this;
            }

            @Override
            protected Builder comment(String... comment) {
                builder.comment(comment);
                return this;
            }

            @Override
            protected Value<Boolean> define(String path, boolean defaultValue) {
                return cast(path, builder.define(path, defaultValue), defaultValue);
            }

            @Override
            protected Value<String> define(String path, String defaultValue) {
                return cast(path, builder.define(path, defaultValue), defaultValue);
            }

            @Override
            protected Value<Integer> defineInRange(String path, int defaultValue, int minValue, int maxValue) {
                return cast(path, builder.defineInRange(path, defaultValue, minValue, maxValue), defaultValue);
            }

            @Override
            protected Value<Double> defineInRange(String path, double defaultValue, double minValue, double maxValue) {
                return cast(path, builder.defineInRange(path, defaultValue, minValue, maxValue), defaultValue);
            }

            @Override
            protected <T> Value<List<? extends T>> defineList(String path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
                return cast(path, builder.defineList(path, defaultValue, elementValidator), defaultValue);
            }

            <T> Value<T> cast(String path, FabricConfigSpec.ConfigValue<T> value, T defaultValue) {
                return new Value<>(path, defaultValue, value::get, value::set);
            }
        }));

        // bind the config to spec.
        var spec = (AbstractFabricConfigSpec) pair.getKey().build();
        spec.bind(pair.getValue(), FabricConfigSpec::save);

        // registry the config into loader.
        var config = pair.getValue();
        var container = FabricLoader.getInstance().getModContainer(ModConstants.MOD_ID);
        if (container.isPresent()) {
            var ignored = new FabricConfig(FabricConfig.Type.valueOf(type.name()), config, container.get());
        }

        return spec;
    }
}
