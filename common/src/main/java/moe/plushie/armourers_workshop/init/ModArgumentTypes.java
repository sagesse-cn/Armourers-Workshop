package moe.plushie.armourers_workshop.init;

import com.mojang.brigadier.arguments.ArgumentType;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IArgumentTypeBuilder;
import moe.plushie.armourers_workshop.init.command.ColorArgumentType;
import moe.plushie.armourers_workshop.init.command.ColorSchemeArgumentType;
import moe.plushie.armourers_workshop.init.command.FileArgumentType;
import moe.plushie.armourers_workshop.init.command.ListArgumentType;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;

@SuppressWarnings("unused")
public class ModArgumentTypes {

    public static IRegistryHolder<?> ITEMS = normal(ListArgumentType.class).serializer(ListArgumentType.Serializer::new).build("items");
    public static IRegistryHolder<?> FILES = normal(FileArgumentType.class).serializer(FileArgumentType.Serializer::new).build("files");
    public static IRegistryHolder<?> DYE = normal(ColorSchemeArgumentType.class).serializer(ColorSchemeArgumentType.Serializer::new).build("dye");
    public static IRegistryHolder<?> COLOR = normal(ColorArgumentType.class).serializer(ColorArgumentType.Serializer::new).build("color");

    private static <T extends ArgumentType<?>> IArgumentTypeBuilder<T> normal(Class<T> clazz) {
        return BuilderManager.getInstance().createArgumentTypeBuilder(clazz);
    }

    public static void init() {
    }
}
