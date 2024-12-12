package moe.plushie.armourers_workshop.builder.data.undo.action;

import moe.plushie.armourers_workshop.api.action.IUserAction;
import moe.plushie.armourers_workshop.api.common.IPaintable;
import moe.plushie.armourers_workshop.api.skin.texture.ISkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.Map;

public class SetBlockColorAction extends BlockUserAction {

    private final Map<Direction, ISkinPaintColor> newValue;

    public SetBlockColorAction(Level level, BlockPos pos, Map<Direction, ISkinPaintColor> newValue) {
        super(level, pos);
        this.newValue = new HashMap<>(newValue);
    }

    @Override
    public IUserAction apply() throws RuntimeException {
        var target = (IPaintable) getBlockEntity();
        var oldValue = new HashMap<Direction, ISkinPaintColor>();
        for (var direction : newValue.keySet()) {
            var paintColor = target.getColor(direction);
            if (paintColor == null) {
                paintColor = SkinPaintColor.CLEAR;
            }
            oldValue.put(direction, paintColor);
        }
        var revertAction = new SetBlockColorAction(level, blockPos, oldValue);
        target.setColors(newValue);
        return revertAction;
    }

    @Override
    public BlockEntity getBlockEntity() {
        var blockEntity = super.getBlockEntity();
        if (blockEntity instanceof IPaintable) {
            return blockEntity;
        }
        return null;
    }
}
