package moe.plushie.armourers_workshop.compatibility.forge.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IBlockEntityHandler;
import moe.plushie.armourers_workshop.compatibility.core.AbstractBlockEntity;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractDataSerializer;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeBlockEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

@Available("[1.21, )")
@Mixin(AbstractBlockEntity.class)
public abstract class ForgeBlockEntityHandlerMixin implements AbstractForgeBlockEntity {

    @Override
    public AABB getRenderBoundingBox() {
        var blockEntity = AbstractBlockEntity.class.cast(this);
        var result = blockEntity.getVisibleBox(blockEntity.getBlockState());
        if (result != null) {
            return result;
        }
        return AbstractForgeBlockEntity.super.getRenderBoundingBox();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
        if (this instanceof IBlockEntityHandler handler) {
            var blockEntity = AbstractBlockEntity.class.cast(this);
            handler.handleUpdatePacket(blockEntity.getBlockState(), AbstractDataSerializer.wrap(pkt.getTag(), provider));
        }
    }
}
