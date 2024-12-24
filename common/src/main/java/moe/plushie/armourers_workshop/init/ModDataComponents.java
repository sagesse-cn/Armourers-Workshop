package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataComponentType;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IDataComponentTypeBuilder;
import moe.plushie.armourers_workshop.core.holiday.Holiday;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("unused")
public class ModDataComponents {

    public static final IRegistryHolder<IDataComponentType<SkinDescriptor>> SKIN = create(SkinDescriptor.CODEC).tag("ArmourersWorkshop").build("skin");

    public static final IRegistryHolder<IDataComponentType<Holiday>> HOLIDAY = create(Holiday.CODEC).tag("Holiday").build("holiday");

    public static final IRegistryHolder<IDataComponentType<GlobalPos>> LINKED_POS = create(IDataCodec.GLOBAL_POS).tag("LinkedPos").build("linked_pos");

    public static final IRegistryHolder<IDataComponentType<CompoundTag>> ENTITY_DATA = create(IDataCodec.COMPOUND_TAG).tag("EntityTag").build("entity_data");
    public static final IRegistryHolder<IDataComponentType<CompoundTag>> BLOCK_ENTITY_DATA = create(IDataCodec.COMPOUND_TAG).tag("BlockEntityTag").build("block_entity_data");


    public static final IRegistryHolder<IDataComponentType<ItemStack>> GIFT = create(IDataCodec.ITEM_STACK).tag("Gift").build("gift");

    public static final IRegistryHolder<IDataComponentType<Integer>> GIFT_COLOR_BG = create(IDataCodec.INT).tag("Color1").build("color1");
    public static final IRegistryHolder<IDataComponentType<Integer>> GIFT_COLOR_FG = create(IDataCodec.INT).tag("Color2").build("color2");

    public static final IRegistryHolder<IDataComponentType<SkinPaintColor>> TOOL_COLOR = create(SkinPaintColor.CODEC).tag("Color").build("color");

    public static final IRegistryHolder<IDataComponentType<Integer>> TOOL_FLAGS = create(IDataCodec.INT).tag("Flags").build("tool_flags");
    public static final IRegistryHolder<IDataComponentType<CompoundTag>> TOOL_OPTIONS = create(IDataCodec.COMPOUND_TAG).tag("Options").build("tool_options");


    public static void init() {
    }

    private static <T> IDataComponentTypeBuilder<T> create(IDataCodec<T> codec) {
        return BuilderManager.getInstance().createDataComponentTypeBuilder(codec);
    }
}
