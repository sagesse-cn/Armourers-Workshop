package moe.plushie.armourers_workshop.compatibility.fabric;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IBlockEntityType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Available("[1.18, )")
public interface AbstractFabricBlockEntity {

    static <T extends BlockEntity> T create(BlockEntityType<T> entityType, BlockGetter level, BlockPos blockPos, BlockState blockState) {
        return entityType.create(blockPos, blockState);
    }

    static <T extends BlockEntity> BlockEntityType<T> createType(IBlockEntityType.Serializer<T> supplier, Block... blocks) {
        BlockEntityType<?>[] entityTypes = {null};
        var entityType = FabricBlockEntityTypeBuilder.create((blockPos, blockState) -> supplier.create(entityTypes[0], blockPos, blockState), blocks).build();
        entityTypes[0] = entityType;
        return entityType;
    }
}
