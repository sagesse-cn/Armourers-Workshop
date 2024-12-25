package moe.plushie.armourers_workshop.core.blockentity;

import moe.plushie.armourers_workshop.api.common.IBlockEntityHandler;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.compatibility.core.AbstractBlockEntity;
import moe.plushie.armourers_workshop.core.utils.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class UpdatableBlockEntity extends AbstractBlockEntity implements IBlockEntityHandler {

    public UpdatableBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void sendBlockUpdates() {
        var level = getLevel();
        if (level != null) {
            var state = getBlockState();
            level.sendBlockUpdated(getBlockPos(), state, state, Constants.BlockFlags.DEFAULT_AND_RERENDER);
        }
    }

    @Override
    public void handleUpdatePacket(BlockState state, IDataSerializer serializer) {
        this.readAdditionalData(serializer);
        this.sendBlockUpdates();
    }
}
