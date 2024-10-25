package moe.plushie.armourers_workshop.core.blockentity;

import moe.plushie.armourers_workshop.api.common.IBlockEntityHandler;
import moe.plushie.armourers_workshop.core.math.OpenQuaternion3f;
import moe.plushie.armourers_workshop.core.math.Rectangle3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public abstract class RotableContainerBlockEntity extends UpdatableContainerBlockEntity implements IBlockEntityHandler {

    public static final AABB ZERO_BOX = new AABB(0, 0, 0, 0, 0, 0);

    private AABB renderBoundingBox;

    public RotableContainerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public void setRenderChanged() {
        renderBoundingBox = null;
    }

    @Environment(EnvType.CLIENT)
    public OpenQuaternion3f getRenderRotations(BlockState blockState) {
        return null;
    }

    @Environment(EnvType.CLIENT)
    public Rectangle3f getRenderShape(BlockState blockState) {
        return null;
    }

    @Override
    public AABB getRenderBoundingBox(BlockState blockState) {
        if (renderBoundingBox != null) {
            return renderBoundingBox;
        }
        var rect = getRenderShape(blockState);
        if (rect == null) {
            return ZERO_BOX;
        }
        var quaternion = getRenderRotations(blockState);
        if (quaternion != null) {
            rect.mul(quaternion);
        }
        var blockPos = getBlockPos();
        var box = rect.offset(blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f);
        renderBoundingBox = new AABB(box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMaxY(), box.getMaxZ());
        return renderBoundingBox;
    }
}
