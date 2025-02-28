package moe.plushie.armourers_workshop.core.block;

import moe.plushie.armourers_workshop.api.common.IBlockHandler;
import moe.plushie.armourers_workshop.api.common.ILootContext;
import moe.plushie.armourers_workshop.api.common.ILootContextParam;
import moe.plushie.armourers_workshop.compatibility.core.AbstractBlockEntityProvider;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractDataSerializer;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.data.SkinBlockPlaceContext;
import moe.plushie.armourers_workshop.core.entity.SeatEntity;
import moe.plushie.armourers_workshop.core.math.OpenVector3d;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.init.ModBlockEntityTypes;
import moe.plushie.armourers_workshop.init.ModEntityTypes;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.init.ModMenuTypes;
import moe.plushie.armourers_workshop.init.ModPermissions;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SkinnableBlock extends AbstractAttachedHorizontalBlock implements AbstractBlockEntityProvider, IBlockHandler {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;

    public SkinnableBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(FACE, AttachFace.WALL)
                .setValue(LIT, false)
                .setValue(PART, BedPart.HEAD)
                .setValue(OCCUPIED, false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockGetter level, BlockPos blockPos, BlockState blockState) {
        return ModBlockEntityTypes.SKINNABLE.get().create(level, blockPos, blockState);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity entity, ItemStack itemStack) {
        var context = SkinBlockPlaceContext.of(blockPos);
        if (context == null) {
            return;
        }
        // add all part into level
        context.getParts().forEach(part -> {
            var target = blockPos.offset(part.getOffset());
            level.setBlock(target, blockState, 11);
            var blockEntity = getBlockEntity(level, target);
            if (blockEntity != null) {
                var serializer = AbstractDataSerializer.wrap(new CompoundTag(), level);
                part.serialize(serializer);
                blockEntity.readAdditionalData(serializer);
                blockEntity.updateBlockStates();
            }
        });
        super.setPlacedBy(level, blockPos, blockState, entity, itemStack);
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        // update the block state also calls `onRemove`.
        if (!blockState.is(blockState2.getBlock())) {
            this.brokenByAnything(level, blockPos, blockState, null);
        }
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }

    @Override
    public List<ItemStack> getDrops(BlockState blockState, ILootContext context) {
        var results = super.getDrops(blockState, context);
        var blockEntity = context.getOptionalParameter(ILootContextParam.BLOCK_ENTITY);
        if (!(blockEntity instanceof SkinnableBlockEntity blockEntity1) || results.isEmpty()) {
            return results;
        }
        var fixedResults = new ArrayList<ItemStack>(results.size());
        for (var itemStack : results) {
            // we will add an invalid skin item from loot table at data pack,
            // so we need fix the skin info in the drop event.
            if (itemStack.is(ModItems.SKIN.get()) && SkinDescriptor.of(itemStack).isEmpty()) {
                // when not found any dropped stack,
                // we must be remove invalid skin item.
                itemStack = blockEntity1.getDropped();
                if (itemStack == null) {
                    continue;
                }
            }
            fixedResults.add(itemStack);
        }
        return fixedResults;
    }

    @Override
    public BlockState destroyByPlayer(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        this.brokenByAnything(level, blockPos, blockState, player);
        return super.destroyByPlayer(level, blockPos, blockState, player);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        var blockEntity = getBlockEntity(level, blockPos);
        if (blockEntity == null) {
            return InteractionResult.FAIL;
        }
        if (blockEntity.isLinked()) {
            var result = blockEntity.getLinkedValueFromParent((level1, pos) -> {
                var state = level1.getBlockState(pos);
                return super.useWithoutItem(state, level1, pos, player, blockHitResult);
            });
            return result.orElse(InteractionResult.FAIL);
        }
        if (blockEntity.isBed() && !player.isSecondaryUseActive()) {
            if (ModPermissions.SKINNABLE_SLEEP.accept(blockEntity, player)) {
                var bedState = Blocks.RED_BED.defaultBlockState().setValue(PART, BedPart.HEAD);
                return super.useWithoutItem(bedState, level, blockEntity.getBedPos(), player, blockHitResult);
            }
        }
        if (blockEntity.isSeat() && !player.isSecondaryUseActive()) {
            if (ModPermissions.SKINNABLE_SIT.accept(blockEntity, player)) {
                if (level.isClientSide()) {
                    return InteractionResult.CONSUME;
                }
                var seatPos = blockEntity.getSeatPos().add(0.5f, 0.5f, 0.5f);
                var seatEntity = getSeatEntity((ServerLevel) level, blockEntity.getParentPos(), seatPos);
                if (seatEntity == null) {
                    return InteractionResult.FAIL; // it is using
                }
                player.startRiding(seatEntity, true);
                return InteractionResult.SUCCESS;
            }
        }
        if (blockEntity.isInventory()) {
            var result = ModMenuTypes.SKINNABLE.get().openMenu(player, level.getBlockEntity(blockPos));
            if (result.consumesAction()) {
                player.awardStat(Stats.CUSTOM.get(Stats.OPEN_CHEST));
            }
            return result;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var state = super.getStateForPlacement(context);
        if (state != null && context instanceof SkinBlockPlaceContext context1) {
            if (context1.getProperty(SkinProperty.BLOCK_GLOWING)) {
                state = state.setValue(LIT, true);
            }
        }
        return state;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader blockGetter, BlockPos blockPos, BlockState blockState) {
        var blockEntity = getParentBlockEntity(blockGetter, blockPos);
        if (blockEntity != null) {
            return blockEntity.getSkin().asItemStack();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isCustomBed(BlockGetter level, BlockPos blockPos, BlockState blockState, @Nullable Entity player) {
        var blockEntity = getBlockEntity(level, blockPos);
        if (blockEntity != null) {
            return blockEntity.isBed();
        }
        return false;
    }

    @Override
    public boolean isCustomLadder(BlockGetter level, BlockPos blockPos, BlockState blockState, LivingEntity entity) {
        var blockEntity = getBlockEntity(level, blockPos);
        if (blockEntity != null) {
            return blockEntity.isLadder();
        }
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE, LIT, PART, OCCUPIED);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        var blockEntity = getParentBlockEntity(level, blockPos);
        if (blockEntity != null) {
            return blockEntity.getAnalogOutputSignal();
        }
        return 0;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos blockPos, Direction direction) {
        var blockEntity = getParentBlockEntity(level, blockPos);
        if (blockEntity != null) {
            return blockEntity.getSignal(direction);
        }
        return 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos blockPos, Direction direction) {
        var blockEntity = getParentBlockEntity(level, blockPos);
        if (blockEntity != null) {
            return blockEntity.getDirectSignal(direction);
        }
        return 0;
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        var blockEntity = getBlockEntity(blockGetter, blockPos);
        if (blockEntity != null) {
            return blockEntity.getShape();
        }
        return Shapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        var blockEntity = getBlockEntity(blockGetter, blockPos);
        if (blockEntity != null) {
            return blockEntity.getCollisionShape();
        }
        return Shapes.empty();
    }

    public void forEach(Level level, BlockPos pos, Consumer<BlockPos> consumer) {
        var blockEntity = getParentBlockEntity(level, pos);
        if (blockEntity == null) {
            return;
        }
        var parentPos = blockEntity.getBlockPos();
        for (var offset : blockEntity.getRefers()) {
            var targetPos = parentPos.offset(offset);
            if (!targetPos.equals(pos)) {
                consumer.accept(targetPos);
            }
        }
    }

    public void brokenByAnything(Level level, BlockPos blockPos, BlockState blockState, @Nullable Player player) {
        if (dropItems(level, blockPos, player)) {
            killSeatEntities(level, blockPos);
            forEach(level, blockPos, target -> level.setBlock(target, Blocks.AIR.defaultBlockState(), 35));
        }
    }

    public void killSeatEntities(Level level, BlockPos blockPos) {
        var blockEntity = getParentBlockEntity(level, blockPos);
        if (blockEntity != null) {
            OpenVector3d seatPos = blockEntity.getSeatPos().add(0.5f, 0.5f, 0.5f);
            killSeatEntity(level, blockEntity.getParentPos(), seatPos);
        }
    }

    public boolean dropItems(Level level, BlockPos blockPos, @Nullable Player player) {
        var blockEntity = getBlockEntity(level, blockPos);
        var parentBlockEntity = getParentBlockEntity(level, blockPos);
        if (blockEntity == null || parentBlockEntity == null || parentBlockEntity.isDropped()) {
            return false;
        }
        // anyway, we only drop all items once.
        var droppedStack = parentBlockEntity.getSkin().asItemStack();
        blockEntity.setDropped(droppedStack); // mark the attacked block
        parentBlockEntity.setDropped(droppedStack);
        if (parentBlockEntity.isInventory()) {
            DataSerializers.dropContents(level, blockPos, parentBlockEntity);
        }
        return true;
    }

    private SkinnableBlockEntity getBlockEntity(BlockGetter level, BlockPos pos) {
        var blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof SkinnableBlockEntity blockEntity1) {
            return blockEntity1;
        }
        return null;
    }

    private SkinnableBlockEntity getParentBlockEntity(BlockGetter level, BlockPos blockPos) {
        var blockEntity = getBlockEntity(level, blockPos);
        if (blockEntity != null) {
            return blockEntity.getParent();
        }
        return null;
    }

    @Nullable
    private SeatEntity getSeatEntity(ServerLevel level, BlockPos blockPos, OpenVector3d pos) {
        var searchRect = new AABB(pos.x(), pos.y(), pos.z(), pos.x() + 1, pos.y() + 1, pos.z() + 1);
        for (var entity : level.getEntitiesOfClass(SeatEntity.class, searchRect)) {
            if (entity.isAlive() && blockPos.equals(entity.getBlockPos())) {
                if (entity.getPassengers().isEmpty()) {
                    return entity;
                }
                return null; // is using
            }
        }
        var entity = ModEntityTypes.SEAT.get().create(level, BlockPos.ZERO, null, MobSpawnType.SPAWN_EGG);
        entity.setPos(pos.x(), pos.y(), pos.z());
        entity.setBlockPos(blockPos);
        level.addFreshEntity(entity);
        return entity;
    }

    private void killSeatEntity(Level level, BlockPos blockPos, OpenVector3d pos) {
        var searchRect = new AABB(pos.x(), pos.y(), pos.z(), pos.x() + 1, pos.y() + 1, pos.z() + 1);
        for (var entity : level.getEntitiesOfClass(SeatEntity.class, searchRect)) {
            if (entity.isAlive() && blockPos.equals(entity.getBlockPos())) {
                entity.kill();
            }
        }
    }
}
