package moe.plushie.armourers_workshop.core.blockentity;

import moe.plushie.armourers_workshop.api.common.IBlockEntityCapability;
import moe.plushie.armourers_workshop.api.common.ITickable;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.core.block.SkinnableBlock;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector3d;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.NonNullItemList;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.init.ModLog;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SkinnableBlockEntity extends RotableContainerBlockEntity implements ITickable {

    private static final Map<?, OpenVector3f> FACING_TO_ROT = Collections.immutableMap(builder -> {
        builder.put(Pair.of(AttachFace.CEILING, Direction.EAST), new OpenVector3f(180, 270, 0));
        builder.put(Pair.of(AttachFace.CEILING, Direction.NORTH), new OpenVector3f(180, 180, 0));
        builder.put(Pair.of(AttachFace.CEILING, Direction.WEST), new OpenVector3f(180, 90, 0));
        builder.put(Pair.of(AttachFace.CEILING, Direction.SOUTH), new OpenVector3f(180, 0, 0));
        builder.put(Pair.of(AttachFace.WALL, Direction.EAST), new OpenVector3f(0, 270, 0));
        builder.put(Pair.of(AttachFace.WALL, Direction.SOUTH), new OpenVector3f(0, 180, 0));
        builder.put(Pair.of(AttachFace.WALL, Direction.WEST), new OpenVector3f(0, 90, 0));
        builder.put(Pair.of(AttachFace.WALL, Direction.NORTH), new OpenVector3f(0, 0, 0));
        builder.put(Pair.of(AttachFace.FLOOR, Direction.EAST), new OpenVector3f(0, 270, 0));
        builder.put(Pair.of(AttachFace.FLOOR, Direction.SOUTH), new OpenVector3f(0, 180, 0));
        builder.put(Pair.of(AttachFace.FLOOR, Direction.WEST), new OpenVector3f(0, 90, 0));
        builder.put(Pair.of(AttachFace.FLOOR, Direction.NORTH), new OpenVector3f(0, 0, 0));
    });

    private BlockPos reference = BlockPos.ZERO;
    private OpenRectangle3i collisionShape = OpenRectangle3i.ZERO;

    private NonNullItemList items;
    private List<BlockPos> refers;
    private List<SkinMarker> markers;

    private BlockPos linkedBlockPos = null;

    private SkinProperties properties;
    private SkinDescriptor skin = SkinDescriptor.EMPTY;

    private OpenQuaternionf renderRotations;
    private AABB renderBoundingBox;
    private VoxelShape renderVoxelShape = null;
    private ItemStack droppedStack = null;

    private LinkedSnapshot lastSnapshot;

    private boolean isParent = false;

    public SkinnableBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public static OpenVector3f getRotations(BlockState state) {
        AttachFace face = state.getOptionalValue(SkinnableBlock.FACE).orElse(AttachFace.FLOOR);
        Direction facing = state.getOptionalValue(SkinnableBlock.FACING).orElse(Direction.NORTH);
        return FACING_TO_ROT.getOrDefault(Pair.of(face, facing), OpenVector3f.ZERO);
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
        skin = serializer.read(CodingKeys.SKIN);
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
        serializer.write(CodingKeys.SKIN, skin);
        serializer.write(CodingKeys.SKIN_PROPERTIES, properties);
        serializer.write(CodingKeys.LINKED_POS, linkedBlockPos);
        getOrCreateItems().serialize(serializer);
    }

    @Override
    public void tick() {
        if (isParent()) {
            parentTick();
        } else {
            childTick();
        }
    }

    protected void parentTick() {
        // only work in link mode.
        var pos = getLinkedBlockPos();
        if (pos == null) {
            return;
        }
        var level = getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }
        // check redstone stuff
        var snapshot = new LinkedSnapshot();
        var state = level.getBlockState(pos);
        if (state.hasAnalogOutputSignal()) {
            snapshot.analogOutputSignal = state.getAnalogOutputSignal(level, pos);
        }
        for (var dir : Direction.values()) {
            snapshot.redstoneSignal[dir.get3DDataValue()] = state.getSignal(level, pos, dir);
            snapshot.directRedstoneSignal[dir.get3DDataValue()] = state.getDirectSignal(level, pos, dir);
        }
        // notify neighbors blocks when snapshot changed.
        if (lastSnapshot == null || !lastSnapshot.equals(snapshot)) {
            updateStateAndNeighbors();
            lastSnapshot = snapshot;
        }
    }

    protected void childTick() {
        // when the parent block is broken for some reason, the child will be automatically destroyed.
        var parent = getParent();
        if (parent == null) {
            ModLog.warn("found a zombie block at {}, destroy it.", getBlockPos());
            kill();
        }
    }

    public void updateBlockStates() {
        setChanged();
        var level = getLevel();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
        }
    }

    public void updateStateAndNeighbors() {
        var level = getLevel();
        if (level != null && !level.isClientSide()) {
            level.updateNeighbourForOutputSignal(getBlockPos(), getBlockState().getBlock());
            level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
        }
    }

    public void setSkin(SkinDescriptor skin) {
        this.skin = skin;
    }

    public SkinDescriptor getSkin() {
        if (isParent()) {
            return skin;
        }
        return SkinDescriptor.EMPTY;
    }

    public void setShape(OpenRectangle3i shape) {
        this.collisionShape = shape;
        this.renderVoxelShape = null;
    }

    public VoxelShape getShape() {
        if (renderVoxelShape != null) {
            return renderVoxelShape;
        }
        if (collisionShape.equals(OpenRectangle3i.ZERO)) {
            renderVoxelShape = Shapes.block();
            return renderVoxelShape;
        }
        float minX = collisionShape.minX() / 16f + 0.5f;
        float minY = collisionShape.minY() / 16f + 0.5f;
        float minZ = collisionShape.minZ() / 16f + 0.5f;
        float maxX = collisionShape.maxX() / 16f + 0.5f;
        float maxY = collisionShape.maxY() / 16f + 0.5f;
        float maxZ = collisionShape.maxZ() / 16f + 0.5f;
        renderVoxelShape = Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
        return renderVoxelShape;
    }

    public void setLinkedBlockPos(BlockPos linkedBlockPos) {
        var blockEntity = getParent();
        if (blockEntity != null) {
            blockEntity.linkedBlockPos = linkedBlockPos;
            blockEntity.updateBlockStates();
        }
    }

    public BlockPos getLinkedBlockPos() {
        return getValueFromParent(te -> te.linkedBlockPos);
    }

    public BlockState getLinkedBlockState() {
        var pos = getLinkedBlockPos();
        var level = getLevel();
        if (level != null && pos != null) {
            return level.getBlockState(pos);
        }
        return null;
    }

    public BlockEntity getLinkedBlockEntity() {
        var pos = getLinkedBlockPos();
        var level = getLevel();
        if (level != null && pos != null) {
            return level.getBlockEntity(pos);
        }
        return null;
    }

    public void kill() {
        var level = getLevel();
        if (level != null && !level.isClientSide()) {
            level.setBlockAndUpdate(getBlockPos(), Blocks.AIR.defaultBlockState());
        }
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

    public int getAnalogOutputSignal() {
        var pos = getLinkedBlockPos();
        var level = getLevel();
        if (level == null || pos == null) {
            return 0;
        }
        var state = level.getBlockState(pos);
        return state.getAnalogOutputSignal(level, pos);
    }

    public int getSignal(Direction direction) {
        var pos = getLinkedBlockPos();
        var level = getLevel();
        if (level == null || pos == null) {
            return 0;
        }
        var state = level.getBlockState(pos);
        return state.getSignal(level, pos, direction);
    }

    public int getDirectSignal(Direction direction) {
        var pos = getLinkedBlockPos();
        var level = getLevel();
        if (level == null || pos == null) {
            return 0;
        }
        var state = level.getBlockState(pos);
        return state.getDirectSignal(level, pos, direction);
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

    public OpenVector3d getSeatPos() {
        float dx = 0, dy = 0, dz = 0;
        var parentPos = getParentPos();
        var markers = getMarkers();
        if (markers != null && !markers.isEmpty()) {
            var marker = markers.iterator().next();
            dx = marker.x / 16.0f;
            dy = marker.y / 16.0f;
            dz = marker.z / 16.0f;
        }
        return new OpenVector3d(parentPos.getX() + dx, parentPos.getY() + dy, parentPos.getZ() + dz);
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

    @Nullable
    @Override
    public <T> T getCapability(IBlockEntityCapability<T> capability, @Nullable Direction dir) {
        // this.boundBlockEntity == null ? this.boundBlockState == null || this.boundBlockState.hasTileEntity() : this.boundBlockEntity.isRemoved()
        var linkedPos = getLinkedBlockPos();
        var linkedState = getLinkedBlockState();
        var linkedEntity = getLinkedBlockEntity();
        if (linkedPos != null) {
            return capability.get(getLevel(), linkedPos, linkedState, linkedEntity, dir);
        }
        return null;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public OpenQuaternionf getRenderRotations(BlockState blockState) {
        if (renderRotations != null) {
            return renderRotations;
        }
        var r = getRotations(blockState);
        renderRotations = new OpenQuaternionf(r.x(), r.y(), r.z(), true);
        return renderRotations;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public OpenRectangle3f getRenderShape(BlockState blockState) {
        var bakedSkin = SkinBakery.getInstance().loadSkin(getSkin(), Tickets.TEST);
        if (bakedSkin == null) {
            return null;
        }
        var f = 1 / 16f;
        var box = bakedSkin.getRenderBounds().copy();
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

    private static class LinkedSnapshot {

        private int analogOutputSignal = 0;
        private final int[] redstoneSignal = new int[]{0, 0, 0, 0, 0, 0};
        private final int[] directRedstoneSignal = new int[]{0, 0, 0, 0, 0, 0};

        @Override
        public final boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof LinkedSnapshot snapshot)) return false;
            return analogOutputSignal == snapshot.analogOutputSignal && Arrays.equals(redstoneSignal, snapshot.redstoneSignal) && Arrays.equals(directRedstoneSignal, snapshot.directRedstoneSignal);
        }

        @Override
        public int hashCode() {
            int result = analogOutputSignal;
            result = 31 * result + Arrays.hashCode(redstoneSignal);
            result = 31 * result + Arrays.hashCode(directRedstoneSignal);
            return result;
        }
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<BlockPos> REFERENCE = IDataSerializerKey.create("Refer", IDataCodec.BLOCK_POS, BlockPos.ZERO);
        public static final IDataSerializerKey<OpenRectangle3i> SHAPE = IDataSerializerKey.create("Shape", OpenRectangle3i.CODEC, OpenRectangle3i.ZERO);
        public static final IDataSerializerKey<BlockPos> LINKED_POS = IDataSerializerKey.create("LinkedPos", IDataCodec.BLOCK_POS, null);
        public static final IDataSerializerKey<SkinDescriptor> SKIN = IDataSerializerKey.create("Skin", SkinDescriptor.CODEC, SkinDescriptor.EMPTY);
        public static final IDataSerializerKey<SkinProperties> SKIN_PROPERTIES = IDataSerializerKey.create("SkinProperties", SkinProperties.CODEC, SkinProperties.EMPTY, SkinProperties.EMPTY::copy);
        public static final IDataSerializerKey<List<BlockPos>> REFERENCES = IDataSerializerKey.create("Refers", IDataCodec.BLOCK_POS.listOf(), Collections.emptyList());
        public static final IDataSerializerKey<List<SkinMarker>> MARKERS = IDataSerializerKey.create("Markers", SkinMarker.CODEC.listOf(), Collections.emptyList());
    }
}
