package moe.plushie.armourers_workshop.builder.data;

import moe.plushie.armourers_workshop.compatibility.core.AbstractDirection;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector2i;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureModel;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import net.minecraft.core.Direction;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BoundingBox extends OpenRectangle3i {

    public static final EntityTextureModel MODEL = EntityTextureModel.STAVE_V2;

    private final SkinPartType partType;

    public BoundingBox(SkinPartType partType, OpenRectangle3i rect) {
        super(rect.x(), rect.y(), rect.z(), rect.width(), rect.height(), rect.depth());
        this.partType = partType;
    }

    public static void setColor(SkinPartType partType, OpenVector3i offset, Direction dir, SkinPaintColor color, BiConsumer<OpenVector2i, SkinPaintColor> applier) {
        var texturePos = getTexturePos(partType, offset, dir);
        if (texturePos != null) {
            applier.accept(texturePos, color);
        }
    }

    public static SkinPaintColor getColor(SkinPartType partType, OpenVector3i offset, Direction dir, Function<OpenVector2i, SkinPaintColor> supplier) {
        var texturePos = getTexturePos(partType, offset, dir);
        if (texturePos != null) {
            return supplier.apply(texturePos);
        }
        return SkinPaintColor.CLEAR;
    }

    public static OpenVector2i getTexturePos(SkinPartType partType, OpenVector3i offset, Direction dir) {
        var box = MODEL.get(partType);
        if (box == null) {
            return null;
        }
        var rect = box.getBounds();
        var fixedDir = AbstractDirection.wrap(dir);
        return box.get(rect.x() + offset.x(), rect.y() + offset.y(), rect.z() + offset.z(), fixedDir);
    }

    public void forEach(IPixelConsumer consumer) {
        for (int ix = 0; ix < width(); ix++) {
            for (int iy = 0; iy < height(); iy++) {
                for (int iz = 0; iz < depth(); iz++) {
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

    public SkinPartType getPartType() {
        return partType;
    }

    public interface IPixelConsumer {
        void accept(int ix, int iy, int iz);
    }
}
