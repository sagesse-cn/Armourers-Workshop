package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.api.skin.ISkinDescriptor;
import moe.plushie.armourers_workshop.core.data.ItemStackStorage;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModItems;
import net.minecraft.world.item.ItemStack;

import java.util.function.BooleanSupplier;

public class SkinDescriptor implements IDataSerializable.Immutable, ISkinDescriptor {

    public static final SkinDescriptor EMPTY = new SkinDescriptor("");

    public static final IDataCodec<SkinDescriptor> CODEC = IDataCodec.COMPOUND_TAG.alternative(IDataCodec.STRING, TagSerializer::parse).serializer(SkinDescriptor::new);

    private final String identifier;
    private final SkinType type;
    private final Options options;
    private final SkinPaintScheme paintScheme;

    // not a required property, but it can help we reduce memory usage and improve performance.
    private ItemStack skinItemStack;

    public SkinDescriptor(String identifier) {
        this(identifier, SkinTypes.UNKNOWN, Options.DEFAULT, SkinPaintScheme.EMPTY);
    }

    public SkinDescriptor(String identifier, SkinType type) {
        this(identifier, type, Options.DEFAULT, SkinPaintScheme.EMPTY);
    }

    public SkinDescriptor(String identifier, SkinType type, SkinPaintScheme paintScheme) {
        this(identifier, type, Options.DEFAULT, paintScheme);
    }

    public SkinDescriptor(String identifier, SkinType type, Options options, SkinPaintScheme paintScheme) {
        this.identifier = identifier;
        this.type = type;
        this.options = options;
        this.paintScheme = paintScheme;
    }

    public SkinDescriptor(SkinDescriptor descriptor, SkinPaintScheme paintScheme) {
        this(descriptor.getIdentifier(), descriptor.getType(), descriptor.getOptions(), paintScheme);
    }

    public SkinDescriptor(IDataSerializer serializer) {
        this.identifier = serializer.read(CodingKeys.IDENTIFIER);
        this.type = serializer.read(CodingKeys.TYPE);
        this.options = serializer.read(CodingKeys.OPTIONS);
        this.paintScheme = serializer.read(CodingKeys.SCHEME);
    }

    public static SkinDescriptor of(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return EMPTY;
        }
        var storage = ItemStackStorage.of(itemStack);
        var descriptor = storage.skinDescriptor;
        if (descriptor != null) {
            return descriptor;
        }
        descriptor = itemStack.getOrDefault(ModDataComponents.SKIN.get(), EMPTY);
        storage.skinDescriptor = descriptor;
        return descriptor;
    }

    public boolean accept(ItemStack itemStack) {
        if (itemStack.isEmpty() || isEmpty()) {
            return false;
        }
        var skinType = getType();
        if (skinType == SkinTypes.ITEM) {
            return true;
        }
        if (skinType instanceof SkinType.Tool toolType) {
            return toolType.contains(itemStack);
        }
        return false;
    }

    @Override
    public void serialize(IDataSerializer serializer) {
        serializer.write(CodingKeys.IDENTIFIER, identifier);
        serializer.write(CodingKeys.TYPE, type);
        serializer.write(CodingKeys.OPTIONS, options);
        serializer.write(CodingKeys.SCHEME, paintScheme);
    }

    public ItemStack sharedItemStack() {
        if (isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (skinItemStack != null) {
            return skinItemStack;
        }
        var itemStack = new ItemStack(ModItems.SKIN.get());
        itemStack.set(ModDataComponents.SKIN.get(), this);
        skinItemStack = itemStack;
        return itemStack;
    }

    public ItemStack asItemStack() {
        return sharedItemStack().copy();
    }

    public boolean isEmpty() {
        return this == EMPTY || identifier.isEmpty();
    }

    public SkinPaintScheme getPaintScheme() {
        return paintScheme;
    }

    public SkinType getType() {
        return type;
    }

    public Options getOptions() {
        return options;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return String.format("%s@%s[%s]", identifier, type.getRegistryName().getPath(), type.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinDescriptor that)) return false;
        return identifier.equals(that.identifier) && paintScheme.equals(that.paintScheme);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<String> IDENTIFIER = IDataSerializerKey.create("Identifier", IDataCodec.STRING, "");
        public static final IDataSerializerKey<SkinType> TYPE = IDataSerializerKey.create("SkinType", SkinTypes.CODEC, SkinTypes.UNKNOWN);
        public static final IDataSerializerKey<Options> OPTIONS = IDataSerializerKey.create("SkinOptions", Options.CODEC, Options.DEFAULT);
        public static final IDataSerializerKey<SkinPaintScheme> SCHEME = IDataSerializerKey.create("SkinDyes", SkinPaintScheme.CODEC, SkinPaintScheme.EMPTY);

        public static final IDataSerializerKey<Integer> TOOLTIP_FLAGS = IDataSerializerKey.create("TooltipFlags", IDataCodec.INT, 0);
        public static final IDataSerializerKey<Integer> USING_EMBEDDED_RENDERER = IDataSerializerKey.create("EmbeddedItemRenderer", IDataCodec.INT, 0);
    }

    public static class Options implements IDataSerializable.Immutable {

        public static Options DEFAULT = new Options();

        public static final IDataCodec<Options> CODEC = IDataCodec.COMPOUND_TAG.serializer(Options::new);

        private int tooltipFlags = 0;
        private int enableEmbeddedItemRenderer = 0;

        public Options() {
        }

        public Options(IDataSerializer serializer) {
            this.tooltipFlags = serializer.read(CodingKeys.TOOLTIP_FLAGS);
            this.enableEmbeddedItemRenderer = serializer.read(CodingKeys.USING_EMBEDDED_RENDERER);
        }

        @Override
        public void serialize(IDataSerializer serializer) {
            serializer.write(CodingKeys.TOOLTIP_FLAGS, tooltipFlags);
            serializer.write(CodingKeys.USING_EMBEDDED_RENDERER, enableEmbeddedItemRenderer);
        }

        public Options copy() {
            var options = new Options();
            options.tooltipFlags = tooltipFlags;
            options.enableEmbeddedItemRenderer = enableEmbeddedItemRenderer;
            return options;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Options that)) return false;
            return tooltipFlags == that.tooltipFlags && enableEmbeddedItemRenderer == that.enableEmbeddedItemRenderer;
        }

        @Override
        public int hashCode() {
            return Objects.hash(tooltipFlags, enableEmbeddedItemRenderer);
        }


        public boolean contains(TooltipFlags flags) {
            // the server disabled this feature.
            if ((tooltipFlags & flags.flags) != 0) {
                return false;
            }
            return flags.supplier.getAsBoolean();
        }

        public void setTooltip(TooltipFlags flags, boolean newValue) {
            if (newValue) {
                tooltipFlags &= ~flags.flags;
            } else {
                tooltipFlags |= flags.flags;
            }
        }

        public boolean getTooltip(TooltipFlags flags) {
            return (tooltipFlags & flags.flags) == 0;
        }

        public void setEnableEmbeddedItemRenderer(int enableEmbeddedItemRenderer) {
            this.enableEmbeddedItemRenderer = enableEmbeddedItemRenderer;
        }

        public int getEmbeddedItemRenderer() {
            return enableEmbeddedItemRenderer;
        }
    }

    public enum TooltipFlags {

        NAME(0x01, () -> ModConfig.Common.tooltipSkinName),
        AUTHOR(0x02, () -> ModConfig.Common.tooltipSkinAuthor),
        TYPE(0x04, () -> ModConfig.Common.tooltipSkinType),
        FLAVOUR(0x08, () -> ModConfig.Common.tooltipFlavour),

        HAS_SKIN(0x10, () -> ModConfig.Common.tooltipHasSkin),
        OPEN_WARDROBE(0x20, () -> ModConfig.Common.tooltipHasSkin),

        PREVIEW(0x80, () -> ModConfig.Common.tooltipSkinPreview);

        private final int flags;
        private final BooleanSupplier supplier;

        TooltipFlags(int flags, BooleanSupplier supplier) {
            this.flags = flags;
            this.supplier = supplier;
        }
    }
}
