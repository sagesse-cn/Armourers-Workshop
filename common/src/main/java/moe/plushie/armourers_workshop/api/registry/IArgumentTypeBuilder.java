package moe.plushie.armourers_workshop.api.registry;

import com.mojang.brigadier.arguments.ArgumentType;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public interface IArgumentTypeBuilder<T extends ArgumentType<?>> extends IRegistryBuilder<T> {

    IArgumentTypeBuilder<T> serializer(Supplier<IArgumentSerializer<T>> argumentSerializer);
}
