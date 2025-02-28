package moe.plushie.armourers_workshop.core.block;

import moe.plushie.armourers_workshop.compatibility.core.AbstractBlockEntityProvider;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.init.ModBlockEntityTypes;
import moe.plushie.armourers_workshop.init.ModMenuTypes;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class HologramProjectorBlock extends AbstractAttachedHorizontalBlock implements AbstractBlockEntityProvider {

    public static final BooleanProperty LIT = SkinnableBlock.LIT;

    public HologramProjectorBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.WALL).setValue(LIT, false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockGetter level, BlockPos blockPos, BlockState blockState) {
        return ModBlockEntityTypes.HOLOGRAM_PROJECTOR.get().create(level, blockPos, blockState);
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        var blockEntity = getBlockEntity(level, blockPos);
        if (blockEntity != null) {
            blockEntity.updateBlockStates();
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE, LIT);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        return ModMenuTypes.HOLOGRAM_PROJECTOR.get().openMenu(player, level.getBlockEntity(blockPos));
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState.is(blockState2.getBlock())) {
            return;
        }
        var blockEntity = getBlockEntity(level, blockPos);
        if (blockEntity != null) {
            DataSerializers.dropContents(level, blockPos, blockEntity);
        }
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }

    private HologramProjectorBlockEntity getBlockEntity(Level level, BlockPos blockPos) {
        var blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof HologramProjectorBlockEntity blockEntity1) {
            return blockEntity1;
        }
        return null;
    }
}
