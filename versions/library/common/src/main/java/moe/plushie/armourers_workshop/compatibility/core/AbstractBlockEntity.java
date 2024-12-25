package moe.plushie.armourers_workshop.compatibility.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IBlockEntity;
import moe.plushie.armourers_workshop.api.common.IBlockEntityCapability;
import moe.plushie.armourers_workshop.api.common.IBlockEntityHandler;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@Available("[1.16, )")
public abstract class AbstractBlockEntity extends AbstractBlockEntityImpl implements IBlockEntity {

    public AbstractBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public abstract void readAdditionalData(IDataSerializer serializer);

    @Override
    public abstract void writeAdditionalData(IDataSerializer serializer);

    @Override
    public abstract void sendBlockUpdates();

    @Nullable
    @Override
    public <T> T getCapability(IBlockEntityCapability<T> capability, @Nullable Direction dir) {
        return null;
    }
}

