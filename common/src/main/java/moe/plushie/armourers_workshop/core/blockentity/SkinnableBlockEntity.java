package moe.plushie.armourers_workshop.core.blockentity;

import moe.plushie.armourers_workshop.api.client.IBlockEntityExtendedRenderer;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.core.block.SkinnableBlock;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenQuaternion3f;
import moe.plushie.armourers_workshop.core.math.Rectangle3f;
import moe.plushie.armourers_workshop.core.math.Rectangle3i;
import moe.plushie.armourers_workshop.core.math.Vector3d;
import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.utils.NonNullItemList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SkinnableBlockEntity extends RotableContainerBlockEntity implements IBlockEntityExtendedRenderer {

    private static final Map<?, Vector3f> FACING_TO_ROT = Collections.immutableMap(builder -> {
        builder.put(Pair.of(AttachFace.CEILING, Direction.EAST), new Vector3f(180, 270, 0));
        builder.put(Pair.of(AttachFace.CEILING, Direction.NORTH), new Vector3f(180, 180, 0));
        builder.put(Pair.of(AttachFace.CEILING, Direction.WEST), new Vector3f(180, 90, 0));
        builder.put(Pair.of(AttachFace.CEILING, Direction.SOUTH), new Vector3f(180, 0, 0));
        builder.put(Pair.of(AttachFace.WALL, Direction.EAST), new Vector3f(0, 270, 0));
        builder.put(Pair.of(AttachFace.WALL, Direction.SOUTH), new Vector3f(0, 180, 0));
        builder.put(Pair.of(AttachFace.WALL, Direction.WEST), new Vector3f(0, 90, 0));
        builder.put(Pair.of(AttachFace.WALL, Direction.NORTH), new Vector3f(0, 0, 0));
        builder.put(Pair.of(AttachFace.FLOOR, Direction.EAST), new Vector3f(0, 270, 0));
        builder.put(Pair.of(AttachFace.FLOOR, Direction.SOUTH), new Vector3f(0, 180, 0));
        builder.put(Pair.of(AttachFace.FLOOR, Direction.WEST), new Vector3f(0, 90, 0));
        builder.put(Pair.of(AttachFace.FLOOR, Direction.NORTH), new Vector3f(0, 0, 0));
    });

    private BlockPos reference = BlockPos.ZERO;
    private Rectangle3i collisionShape = Rectangle3i.ZERO;

    private NonNullItemList items;
    private List<BlockPos> refers;
    private List<SkinMarker> markers;

    private BlockPos linkedBlockPos = null;

    private SkinProperties properties;
    private SkinDescriptor descriptor = SkinDescriptor.EMPTY;

    private OpenQuaternion3f renderRotations;
    private AABB renderBoundingBox;
    private VoxelShape renderVoxelShape = null;
    private ItemStack droppedStack = null;

    private boolean isParent = false;

    public SkinnableBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public static Vector3f getRotations(BlockState state) {
        AttachFace face = state.getOptionalValue(SkinnableBlock.FACE).orElse(AttachFace.FLOOR);
        Direction facing = state.getOptionalValue(SkinnableBlock.FACING).orElse(Direction.NORTH);
        return FACING_TO_ROT.getOrDefault(Pair.of(face, facing), Vector3f.ZERO);
    }

    @Override
    public void readAdditionalData(IDataSerializer serializer) {
        reference = serializer.read(CodingKeys.REFERENCE);
        collisionShape = serializer.read(CodingKeys.SHAPE);
        renderVoxelShape = null;
        isParent = BlockPos.ZERO.equals(reference);
        if (!isParent()) {
            return;
        }
        var oldProperties = properties;
        refers = serializer.read(CodingKeys.REFERENCES);
        markers = serializer.read(CodingKeys.MARKERS);
        descriptor = serializer.read(CodingKeys.SKIN);
        properties = serializer.read(CodingKeys.SKIN_PROPERTIES);
        linkedBlockPos = serializer.read(CodingKeys.LINKED_POS);
        if (oldProperties != null) {
            oldProperties.clear();
            oldProperties.putAll(properties);
            properties = oldProperties;
        }
        getOrCreateItems().deserialize(serializer);
    }

    @Override
    public void writeAdditionalData(IDataSerializer serializer) {
        serializer.write(CodingKeys.REFERENCE, reference);
        serializer.write(CodingKeys.SHAPE, collisionShape);
        if (!isParent()) {
            return;
        }
        serializer.write(CodingKeys.REFERENCES, refers);
        serializer.write(CodingKeys.MARKERS, markers);
        serializer.write(CodingKeys.SKIN, descriptor);
        serializer.write(CodingKeys.SKIN_PROPERTIES, properties);
        serializer.write(CodingKeys.LINKED_POS, linkedBlockPos);
        getOrCreateItems().serialize(serializer);
    }

    public void updateBlockStates() {
        setChanged();
        var level = getLevel();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
        }
    }

    public SkinDescriptor getDescriptor() {
        if (isParent()) {
            return descriptor;
        }
        return SkinDescriptor.EMPTY;
    }

    public void setDescriptor(SkinDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public VoxelShape getShape() {
        if (renderVoxelShape != null) {
            return renderVoxelShape;
        }
        if (collisionShape.equals(Rectangle3i.ZERO)) {
            renderVoxelShape = Shapes.block();
            return renderVoxelShape;
        }
        float minX = collisionShape.getMinX() / 16f + 0.5f;
        float minY = collisionShape.getMinY() / 16f + 0.5f;
        float minZ = collisionShape.getMinZ() / 16f + 0.5f;
        float maxX = collisionShape.getMaxX() / 16f + 0.5f;
        float maxY = collisionShape.getMaxY() / 16f + 0.5f;
        float maxZ = collisionShape.getMaxZ() / 16f + 0.5f;
        renderVoxelShape = Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
        return renderVoxelShape;
    }

    public void setShape(Rectangle3i shape) {
        this.collisionShape = shape;
        this.renderVoxelShape = null;
    }

    public BlockPos getLinkedBlockPos() {
        return getValueFromParent(te -> te.linkedBlockPos);
    }

    public void setLinkedBlockPos(BlockPos linkedBlockPos) {
        var blockEntity = getParent();
        if (blockEntity != null) {
            blockEntity.linkedBlockPos = linkedBlockPos;
            blockEntity.updateBlockStates();
        }
    }

    public void kill() {
    }

    @Override
    public NonNullItemList getItems() {
        return getOrCreateItems();
    }

    @Override
    public int getContainerSize() {
        return 9 * 9;
    }

    @Nullable
    public String getInventoryName() {
        return getProperty(SkinProperty.ALL_CUSTOM_NAME);
    }

    @Nullable
    @Override
    public Container getInventory() {
        return getParent();
    }

    public Collection<BlockPos> getRefers() {
        if (refers == null) {
            refers = getValueFromParent(te -> te.refers);
        }
        if (refers == null) {
            return Collections.emptyList();
        }
        return refers;
    }

    public BlockPos getParentPos() {
        return getBlockPos().subtract(reference);
    }

    public Vector3d getSeatPos() {
        float dx = 0, dy = 0, dz = 0;
        var parentPos = getParentPos();
        var markers = getMarkers();
        if (markers != null && !markers.isEmpty()) {
            var marker = markers.iterator().next();
            dx = marker.x / 16.0f;
            dy = marker.y / 16.0f;
            dz = marker.z / 16.0f;
        }
        return new Vector3d(parentPos.getX() + dx, parentPos.getY() + dy, parentPos.getZ() + dz);
    }

    public BlockPos getBedPos() {
        var parentPos = getParentPos();
        var markers = getMarkers();
        if (markers == null || markers.isEmpty()) {
            Direction facing = getBlockState().getOptionalValue(SkinnableBlock.FACING).orElse(Direction.NORTH);
            return parentPos.relative(Rotation.CLOCKWISE_180.rotate(facing));
        }
        SkinMarker marker = markers.iterator().next();
        return parentPos.offset(marker.x / 16, marker.y / 16, marker.z / 16);
    }

    public Collection<SkinMarker> getMarkers() {
        if (markers == null) {
            markers = getValueFromParent(te -> te.markers);
        }
        return markers;
    }

    @Nullable
    public SkinProperties getProperties() {
        if (properties == null) {
            properties = getValueFromParent(te -> te.properties);
        }
        return properties;
    }

    @Nullable
    public SkinnableBlockEntity getParent() {
        if (isParent()) {
            return this;
        }
        if (getLevel() != null) {
            return Objects.safeCast(getLevel().getBlockEntity(getParentPos()), SkinnableBlockEntity.class);
        }
        return null;
    }

    public void setDropped(ItemStack itemStack) {
        this.droppedStack = itemStack;
    }

    public ItemStack getDropped() {
        return droppedStack;
    }

    public boolean isDropped() {
        return droppedStack != null;
    }

    public boolean isLadder() {
        return getProperty(SkinProperty.BLOCK_LADDER);
    }

    public boolean isGrowing() {
        return getProperty(SkinProperty.BLOCK_GLOWING);
    }

    public boolean isSeat() {
        return getProperty(SkinProperty.BLOCK_SEAT);
    }

    public boolean isBed() {
        return getProperty(SkinProperty.BLOCK_BED);
    }

    public boolean isLinked() {
        return getLinkedBlockPos() != null;
    }

    public boolean isInventory() {
        return getProperty(SkinProperty.BLOCK_INVENTORY) || isEnderInventory();
    }

    public boolean isEnderInventory() {
        return getProperty(SkinProperty.BLOCK_ENDER_INVENTORY);
    }

    public boolean isParent() {
        return isParent;
    }

    public boolean noCollision() {
        return getProperty(SkinProperty.BLOCK_NO_COLLISION);
    }

    public int getInventoryWidth() {
        return getProperty(SkinProperty.BLOCK_INVENTORY_WIDTH);
    }

    public int getInventoryHeight() {
        return getProperty(SkinProperty.BLOCK_INVENTORY_HEIGHT);
    }

    @Override
    public boolean shouldUseExtendedRenderer() {
        return isParent;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public OpenQuaternion3f getRenderRotations(BlockState blockState) {
        if (renderRotations != null) {
            return renderRotations;
        }
        var r = getRotations(blockState);
        renderRotations = new OpenQuaternion3f(r.getX(), r.getY(), r.getZ(), true);
        return renderRotations;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public Rectangle3f getRenderShape(BlockState blockState) {
        var bakedSkin = SkinBakery.getInstance().loadSkin(getDescriptor(), Tickets.TEST);
        if (bakedSkin == null) {
            return null;
        }
        var f = 1 / 16f;
        var box = bakedSkin.getRenderBounds(SkinItemSource.EMPTY).copy();
        box.mul(OpenMatrix4f.createScaleMatrix(-f, -f, f));
        return box;
    }


    private NonNullItemList getOrCreateItems() {
        if (items == null) {
            items = new NonNullItemList(getContainerSize());
        }
        return items;
    }

    @Nullable
    private <V> V getValueFromParent(Function<SkinnableBlockEntity, V> getter) {
        var blockEntity = getParent();
        if (blockEntity != null) {
            return getter.apply(blockEntity);
        }
        return null;
    }

    private <V> V getProperty(SkinProperty<V> property) {
        var properties = getProperties();
        if (properties != null) {
            return properties.get(property);
        }
        return property.getDefaultValue();
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<BlockPos> REFERENCE = IDataSerializerKey.create("Refer", IDataCodec.BLOCK_POS, BlockPos.ZERO);
        public static final IDataSerializerKey<Rectangle3i> SHAPE = IDataSerializerKey.create("Shape", Rectangle3i.CODEC, Rectangle3i.ZERO);
        public static final IDataSerializerKey<BlockPos> LINKED_POS = IDataSerializerKey.create("LinkedPos", IDataCodec.BLOCK_POS, null);
        public static final IDataSerializerKey<SkinDescriptor> SKIN = IDataSerializerKey.create("Skin", SkinDescriptor.CODEC, SkinDescriptor.EMPTY);
        public static final IDataSerializerKey<SkinProperties> SKIN_PROPERTIES = IDataSerializerKey.create("SkinProperties", SkinProperties.CODEC, SkinProperties.EMPTY, SkinProperties::new);
        public static final IDataSerializerKey<List<BlockPos>> REFERENCES = IDataSerializerKey.create("Refers", IDataCodec.BLOCK_POS.listOf(), Collections.emptyList());
        public static final IDataSerializerKey<List<SkinMarker>> MARKERS = IDataSerializerKey.create("Markers", SkinMarker.CODEC.listOf(), Collections.emptyList());
    }
}
