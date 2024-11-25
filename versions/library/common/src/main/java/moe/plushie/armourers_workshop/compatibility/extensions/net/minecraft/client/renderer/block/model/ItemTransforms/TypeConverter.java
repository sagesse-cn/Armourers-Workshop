package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.client.renderer.block.model.ItemTransforms;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.utils.EnumToEnumMap;
import net.minecraft.world.item.ItemDisplayContext;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Extension
@Available("[1.20, )")
public class TypeConverter {

    private static final EnumToEnumMap<OpenItemDisplayContext, ItemDisplayContext> MAPPER = EnumToEnumMap.byName(OpenItemDisplayContext.NONE, ItemDisplayContext.NONE);

    public static ItemDisplayContext ofType(@ThisClass Class<?> clazz, OpenItemDisplayContext transformType) {
        return MAPPER.getValue(transformType);
    }

    public static OpenItemDisplayContext ofType(@ThisClass Class<?> clazz, ItemDisplayContext transformType) {
        return MAPPER.getKey(transformType);
    }
}
