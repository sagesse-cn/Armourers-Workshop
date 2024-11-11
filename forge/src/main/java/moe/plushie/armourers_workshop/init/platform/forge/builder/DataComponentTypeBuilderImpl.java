package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataComponentType;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IDataComponentTypeBuilder;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractDataComponentType;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeRegistries;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.core.utils.TypedRegistry;

public class DataComponentTypeBuilderImpl<T> implements IDataComponentTypeBuilder<T> {

    private final IDataCodec<T> codec;
    private String tag;

    public DataComponentTypeBuilderImpl(IDataCodec<T> codec) {
        this.codec = codec;
    }

    @Override
    public IDataComponentTypeBuilder<T> tag(String tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public IRegistryHolder<IDataComponentType<T>> build(String name) {
        var componentType = AbstractDataComponentType.create(tag, codec);
        if (!componentType.isProxy()) {
            AbstractForgeRegistries.DATA_COMPONENT_TYPES.register(name, () -> componentType);
        }
        return TypedRegistry.Entry.of(ModConstants.key(name), () -> componentType);
    }
}
