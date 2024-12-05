package moe.plushie.armourers_workshop.builder.data;

import moe.plushie.armourers_workshop.api.skin.paint.ISkinPaintColor;
import moe.plushie.armourers_workshop.api.skin.part.ISkinPartType;
import moe.plushie.armourers_workshop.core.math.Rectangle3i;
import moe.plushie.armourers_workshop.core.math.Vector2i;
import moe.plushie.armourers_workshop.core.math.Vector3i;
import moe.plushie.armourers_workshop.core.skin.paint.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureModel;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import net.minecraft.core.Direction;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BoundingBox extends Rectangle3i {

    public static final EntityTextureModel MODEL = EntityTextureModel.STAVE_V2;

    private final ISkinPartType partType;

    public BoundingBox(ISkinPartType partType, Rectangle3i rect) {
        super(rect.getX(), rect.getY(), rect.getZ(), rect.getWidth(), rect.getHeight(), rect.getDepth());
        this.partType = partType;
    }

    public static void setColor(ISkinPartType partType, Vector3i offset, Direction dir, ISkinPaintColor color, BiConsumer<Vector2i, ISkinPaintColor> applier) {
        var texturePos = getTexturePos(partType, offset, dir);
        if (texturePos != null) {
            applier.accept(texturePos, color);
        }
    }

    public static ISkinPaintColor getColor(ISkinPartType partType, Vector3i offset, Direction dir, Function<Vector2i, ISkinPaintColor> supplier) {
        var texturePos = getTexturePos(partType, offset, dir);
        if (texturePos != null) {
            return supplier.apply(texturePos);
        }
        return SkinPaintColor.CLEAR;
    }

    public static Vector2i getTexturePos(ISkinPartType partType, Vector3i offset, Direction dir) {
        var box = MODEL.get(partType);
        if (box == null) {
            return null;
        }
        var rect = box.getBounds();
        var fixedDir = OpenDirection.of(dir);
        return box.get(rect.getX() + offset.getX(), rect.getY() + offset.getY(), rect.getZ() + offset.getZ(), fixedDir);
    }

    public void forEach(IPixelConsumer consumer) {
        for (int ix = 0; ix < getWidth(); ix++) {
            for (int iy = 0; iy < getHeight(); iy++) {
                for (int iz = 0; iz < getDepth(); iz++) {
                    consumer.accept(ix, iy, iz);
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoundingBox that)) return false;
        if (!super.equals(o)) return false;
        return partType.equals(that.partType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), partType);
    }

    public ISkinPartType getPartType() {
        return partType;
    }

    public interface IPixelConsumer {
        void accept(int ix, int iy, int iz);
    }
}
