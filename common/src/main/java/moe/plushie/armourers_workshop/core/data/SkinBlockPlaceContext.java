package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.math.OpenVector4f;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SkinBlockPlaceContext extends BlockPlaceContext {

    private OpenVector3f rotations = OpenVector3f.ZERO;
    private SkinDescriptor skin = SkinDescriptor.EMPTY;
    private ArrayList<Part> parts = new ArrayList<>();

    private SkinProperties properties;

    public SkinBlockPlaceContext(UseOnContext context) {
        super(context);
        this.loadElements(SkinLoader.getInstance()::loadSkin);
    }

    public SkinBlockPlaceContext(Player player, InteractionHand hand, ItemStack itemStack, BlockHitResult traceResult) {
        super(player.getLevel(), player, hand, itemStack, traceResult);
        this.loadElements(SkinLoader.getInstance()::getSkin);
    }

    public static SkinBlockPlaceContext of(BlockPos pos) {
        if (pos instanceof AttachedBlockPos pos1) {
            return pos1.context;
        }
        return null;
    }

    protected void transform(OpenVector3f r) {
        for (var part : parts) {
            part.transform(r);
        }
    }

    protected void loadElements(Function<String, Skin> provider) {
        var itemStack = getItemInHand();
        var descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.isEmpty()) {
            return;
        }
        var skin = provider.apply(descriptor.getIdentifier());
        if (skin == null) {
            return;
        }
        var parts = new ArrayList<Part>();
        var parentParts = new ArrayList<ParentPart>();
        var blockPosList = new ArrayList<BlockPos>();
        skin.getBlockBounds().forEach((pos, shape) -> {
            var rect = new OpenRectangle3i(shape);
            if (pos.equals(OpenVector3i.ZERO)) {
                var part = new ParentPart(BlockPos.ZERO, rect, descriptor, skin);
                parts.add(part);
                parentParts.add(part);
            } else {
                var part = new Part(new BlockPos(pos.x(), pos.y(), pos.z()), rect);
                parts.add(part);
            }
        });
        this.skin = descriptor;
        this.parts = parts;
        this.properties = skin.getProperties();
        var state = ModBlocks.SKINNABLE.get().getStateForPlacement(this);
        if (state != null) {
            this.rotations = SkinnableBlockEntity.getRotations(state);
            this.transform(rotations);
        }
        // copy all transformed block pose into list.
        for (var part : parts) {
            blockPosList.add(part.getOffset());
        }
        parentParts.forEach(it -> it.setReferences(blockPosList));
    }

    public <V> V getProperty(SkinProperty<V> property) {
        if (properties != null && !properties.isEmpty()) {
            return properties.get(property);
        }
        return property.getDefaultValue();
    }

    public boolean canPlace(Part part) {
        if (skin.isEmpty()) {
            return false;
        }
        if (skin.getType() != SkinTypes.BLOCK) {
            return false;
        }
        BlockPos pos = super.getClickedPos().offset(part.getOffset());
        return this.getLevel().getBlockState(pos).canBeReplaced(this);
    }

    @Override
    public boolean canPlace() {
        return parts != null && parts.stream().allMatch(this::canPlace) && super.canPlace();
    }

    @Override
    public BlockPos getClickedPos() {
        return new AttachedBlockPos(this, super.getClickedPos());
    }

    public SkinDescriptor getSkin() {
        return skin;
    }

    public ArrayList<Part> getParts() {
        return parts;
    }


    // same the SkinnableBlockEntity.CodingKeys
    private static class CodingKeys {

        public static final IDataSerializerKey<BlockPos> REFERENCE = IDataSerializerKey.create("Refer", IDataCodec.BLOCK_POS, BlockPos.ZERO);
        public static final IDataSerializerKey<OpenRectangle3i> SHAPE = IDataSerializerKey.create("Shape", OpenRectangle3i.CODEC, OpenRectangle3i.ZERO);
        public static final IDataSerializerKey<BlockPos> LINKED_POS = IDataSerializerKey.create("LinkedPos", IDataCodec.BLOCK_POS, null);
        public static final IDataSerializerKey<SkinDescriptor> SKIN = IDataSerializerKey.create("Skin", SkinDescriptor.CODEC, SkinDescriptor.EMPTY);
        public static final IDataSerializerKey<SkinProperties> SKIN_PROPERTIES = IDataSerializerKey.create("SkinProperties", SkinProperties.CODEC, SkinProperties.EMPTY, SkinProperties.EMPTY::copy);
        public static final IDataSerializerKey<List<BlockPos>> REFERENCES = IDataSerializerKey.create("Refers", IDataCodec.BLOCK_POS.listOf(), Collections.emptyList());
        public static final IDataSerializerKey<List<SkinMarker>> MARKERS = IDataSerializerKey.create("Markers", SkinMarker.CODEC.listOf(), Collections.emptyList());
    }

    public static class Part implements IDataSerializable.Immutable {

        private BlockPos offset;
        private OpenRectangle3i shape;

        public Part() {
            this(BlockPos.ZERO, OpenRectangle3i.ZERO);
        }

        public Part(BlockPos offset, OpenRectangle3i shape) {
            this.offset = offset;
            this.shape = shape;
        }

        @Override
        public void serialize(IDataSerializer serializer) {
            serializer.write(CodingKeys.REFERENCE, offset);
            serializer.write(CodingKeys.SHAPE, shape);
        }

        public void transform(OpenVector3f r) {
            var q = new OpenQuaternionf(r.x(), r.y(), r.z(), true);

            var f = new OpenVector4f(offset.getX(), offset.getY(), offset.getZ(), 1.0f);
            f.transform(q);
            offset = new BlockPos(Math.round(f.x()), Math.round(f.y()), Math.round(f.z()));

            var fixedShape = new OpenRectangle3f(shape);
            fixedShape.mul(q);
            shape = new OpenRectangle3i(Math.round(fixedShape.x()), Math.round(fixedShape.y()), Math.round(fixedShape.z()), Math.round(fixedShape.width()), Math.round(fixedShape.height()), Math.round(fixedShape.depth()));
        }

        public BlockPos getOffset() {
            return offset;
        }

        public OpenRectangle3i getShape() {
            return shape;
        }
    }

    public static class ParentPart extends Part {

        private final SkinDescriptor descriptor;
        private final SkinProperties properties;

        private List<BlockPos> references = Collections.emptyList();
        private List<SkinMarker> markerList;

        public ParentPart(BlockPos offset, OpenRectangle3i shape, SkinDescriptor descriptor, Skin skin) {
            super(offset, shape);
            this.descriptor = descriptor;
            this.properties = skin.getProperties();
            this.markerList = Collections.newList(skin.getMarkers());
        }

        @Override
        public void serialize(IDataSerializer serializer) {
            super.serialize(serializer);
            serializer.write(CodingKeys.REFERENCES, references);
            serializer.write(CodingKeys.MARKERS, markerList);
            serializer.write(CodingKeys.SKIN, descriptor);
            serializer.write(CodingKeys.SKIN_PROPERTIES, properties);
        }

        @Override
        public void transform(OpenVector3f r) {
            super.transform(r);

            var q = new OpenQuaternionf(r.x(), r.y(), r.z(), true);
            var newMarkerList = new ArrayList<SkinMarker>();
            for (var marker : markerList) {
                var f = new OpenVector4f(marker.x, marker.y, marker.z, 1.0f);
                f.transform(OpenMatrix4f.createScaleMatrix(-1, -1, 1));
                f.transform(q);
                int x = Math.round(f.x());
                int y = Math.round(f.y());
                int z = Math.round(f.z());
                marker = new SkinMarker((byte) x, (byte) y, (byte) z, marker.meta);
                newMarkerList.add(marker);
            }
            this.markerList = newMarkerList;
        }

        public void setReferences(List<BlockPos> blockPosList) {
            this.references = blockPosList;
        }

        public List<BlockPos> getReferences() {
            return references;
        }
    }

    public static class AttachedBlockPos extends BlockPos {

        protected final SkinBlockPlaceContext context;

        public AttachedBlockPos(SkinBlockPlaceContext context, BlockPos pos) {
            super(pos);
            this.context = context;
        }
    }
}
