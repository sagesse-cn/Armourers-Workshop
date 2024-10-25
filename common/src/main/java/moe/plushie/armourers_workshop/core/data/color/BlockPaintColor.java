package moe.plushie.armourers_workshop.core.data.color;

import moe.plushie.armourers_workshop.api.common.IBlockPaintColor;
import moe.plushie.armourers_workshop.api.skin.paint.ISkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.paint.SkinPaintColor;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Objects;

public class BlockPaintColor implements IBlockPaintColor {

    public static final BlockPaintColor WHITE = new BlockPaintColor(SkinPaintColor.WHITE);

    public static final BlockPaintColor EMPTY = new BlockPaintColor();

    protected ISkinPaintColor paintColor;
    protected EnumMap<Side, ISkinPaintColor> paintColors;

    public BlockPaintColor() {
    }

    public BlockPaintColor(ISkinPaintColor paintColor) {
        this.paintColor = paintColor;
    }

    public void deserializeNBT(CompoundTag tag) {
        this.paintColor = tag.getOptionalPaintColor(Side.fullyName(), null);
        this.paintColors = null;
        for (var side : Side.values()) {
            var paintColor = tag.getOptionalPaintColor(side.name, null);
            if (paintColor != null) {
                if (this.paintColors == null) {
                    this.paintColors = new EnumMap<>(Side.class);
                }
                this.paintColors.put(side, paintColor);
            }
        }
        this.mergePaintColorIfNeeded();
    }

    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        if (paintColor != null) {
            tag.putOptionalPaintColor(Side.fullyName(), paintColor, null);
        }
        if (paintColors != null) {
            paintColors.forEach((side, paintColor) -> tag.putOptionalPaintColor(side.name, paintColor, null));
        }
        return tag;
    }

    public void putAll(ISkinPaintColor paintColor) {
        this.paintColor = paintColor;
        this.paintColors = null;
    }

    @Override
    public void put(Direction dir, ISkinPaintColor paintColor) {
        if (this.paintColors == null) {
            if (Objects.equals(this.paintColor, paintColor)) {
                return; // not any changes.
            }
            this.paintColors = getPaintColors(this.paintColor);
            this.paintColor = null;
        }
        var side = Side.of(dir);
        if (paintColor != null) {
            this.paintColors.put(side, paintColor);
        } else {
            this.paintColors.remove(side);
        }
        this.mergePaintColorIfNeeded();
    }


    @Override
    public ISkinPaintColor get(Direction dir) {
        return getOrDefault(dir, null);
    }

    @Override
    public ISkinPaintColor getOrDefault(Direction dir, ISkinPaintColor defaultValue) {
        if (paintColor != null) {
            return paintColor;
        }
        if (paintColors != null) {
            return paintColors.getOrDefault(Side.of(dir), defaultValue);
        }
        return defaultValue;
    }

    public Collection<ISkinPaintColor> values() {
        if (paintColor != null) {
            return Collections.singleton(paintColor);
        }
        if (paintColors != null) {
            return paintColors.values();
        }
        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockPaintColor that)) return false;
        return Objects.equals(paintColor, that.paintColor) && Objects.equals(paintColors, that.paintColors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paintColor, paintColors);
    }

    @Override
    public boolean isEmpty() {
        if (paintColors != null) {
            return paintColors.isEmpty();
        }
        return paintColor == null;
    }

    @Override
    public boolean isPureColor() {
        return paintColor != null;
    }

    private void mergePaintColorIfNeeded() {
        if (this.paintColors == null) {
            return;
        }
        int total = 0;
        ISkinPaintColor lastColor = null;
        for (var paintColor : this.paintColors.values()) {
            if (lastColor != null && !lastColor.equals(paintColor)) {
                return;
            }
            lastColor = paintColor;
            total += 1;
        }
        if (total == 6) {
            this.paintColor = lastColor;
            this.paintColors = null;
        }
    }

    private EnumMap<Side, ISkinPaintColor> getPaintColors(ISkinPaintColor paintColor) {
        var paintColors = new EnumMap<Side, ISkinPaintColor>(Side.class);
        if (paintColor != null) {
            for (var side : Side.values()) {
                paintColors.put(side, paintColor);
            }
        }
        return paintColors;
    }

    // Assume the mapping for facing to the north.
    public enum Side {
        DOWN("Down", Direction.DOWN),
        UP("Up", Direction.UP),
        FRONT("Front", Direction.NORTH),
        BACK("Back", Direction.SOUTH),
        LEFT("Left", Direction.WEST),
        RIGHT("Right", Direction.EAST);

        final String name;
        final Direction direction;

        Side(String name, Direction direction) {
            this.name = name;
            this.direction = direction;
        }

        public static Side of(Direction direction) {
            for (var value : values()) {
                if (value.direction == direction) {
                    return value;
                }
            }
            return Side.DOWN;
        }

        public static String fullyName() {
            return "All";
        }

        public String getName() {
            return name;
        }

        public Direction getDirection() {
            return direction;
        }
    }
}
