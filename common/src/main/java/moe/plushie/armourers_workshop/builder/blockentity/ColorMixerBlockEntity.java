package moe.plushie.armourers_workshop.builder.blockentity;

import moe.plushie.armourers_workshop.api.data.IDataSerializer;
import moe.plushie.armourers_workshop.api.skin.paint.ISkinPaintColor;
import moe.plushie.armourers_workshop.core.blockentity.UpdatableBlockEntity;
import moe.plushie.armourers_workshop.core.item.impl.IPaintProvider;
import moe.plushie.armourers_workshop.core.skin.paint.SkinPaintColor;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.DataSerializerKey;
import moe.plushie.armourers_workshop.utils.DataTypeCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ColorMixerBlockEntity extends UpdatableBlockEntity implements IPaintProvider {

    private static final DataSerializerKey<ISkinPaintColor> COLOR_KEY = DataSerializerKey.create("Color", DataTypeCodecs.PAINT_COLOR, SkinPaintColor.WHITE);

    private ISkinPaintColor color = SkinPaintColor.WHITE;

    public ColorMixerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public void readAdditionalData(IDataSerializer serializer) {
        color = serializer.read(COLOR_KEY);
    }

    public void writeAdditionalData(IDataSerializer serializer) {
        serializer.write(COLOR_KEY, color);
    }

    @Override
    public ISkinPaintColor getColor() {
        return color;
    }

    @Override
    public void setColor(ISkinPaintColor color) {
        this.color = color;
        BlockUtils.combine(this, this::sendBlockUpdates);
    }
}
