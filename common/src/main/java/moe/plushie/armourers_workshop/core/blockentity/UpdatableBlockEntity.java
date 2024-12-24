package moe.plushie.armourers_workshop.core.blockentity;

import moe.plushie.armourers_workshop.api.common.IBlockEntity;
import moe.plushie.armourers_workshop.api.common.IBlockEntityCapability;
import moe.plushie.armourers_workshop.compatibility.core.AbstractBlockEntity;
import moe.plushie.armourers_workshop.core.utils.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class UpdatableBlockEntity extends AbstractBlockEntity implements IBlockEntity {

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

    @Nullable
    @Override
    public <T> T getCapability(IBlockEntityCapability<T> capability, @Nullable Direction dir) {
        return capability.get(getLevel(), getBlockPos(), getBlockState(), this, dir);
    }
}
