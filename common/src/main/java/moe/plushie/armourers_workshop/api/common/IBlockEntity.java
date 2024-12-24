package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IBlockEntity {

    void setChanged();

    void sendBlockUpdates();

    void readAdditionalData(IDataSerializer serializer);

    void writeAdditionalData(IDataSerializer serializer);

    boolean isRemoved();

    Level getLevel();

    BlockPos getBlockPos();

    BlockState getBlockState();

    @Nullable
    <T> T getCapability(IBlockEntityCapability<T> capability, @Nullable Direction dir);
}
