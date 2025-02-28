package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.config.IConfigBuilder;
import moe.plushie.armourers_workshop.api.config.IConfigSpec;
import moe.plushie.armourers_workshop.compatibility.core.AbstractConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Available("[1.21, )")
public class AbstractForgeConfigSpec extends AbstractConfigSpec {

    public AbstractForgeConfigSpec(Type type, HashMap<String, Value<Object>> values) {
        super(type, values);
    }

    public static <B extends IConfigBuilder> IConfigSpec create(Type type, Function<IConfigBuilder, B> applier) {
        // create a builder from loader.
        var pair = new ModConfigSpec.Builder().configure(builder -> applier.apply(new Builder() {

            @Override
            public IConfigSpec build() {
                return new AbstractForgeConfigSpec(type, values);
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

            <T> Value<T> cast(String path, ModConfigSpec.ConfigValue<T> value, T defaultValue) {
                return new Value<>(path, defaultValue, value, value::set);
            }
        }));

        // bind the config to spec.
        var spec = (AbstractForgeConfigSpec) pair.getKey().build();
        spec.bind(pair.getValue(), ModConfigSpec::save);

        // registry the config into loader.
        var config = pair.getValue();
        AbstractForgeInitializer.getModContainer().registerConfig(ModConfig.Type.valueOf(type.name()), config);

        return spec;
    }
}
