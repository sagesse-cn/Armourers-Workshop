package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.core.math.Rectangle3f;
import moe.plushie.armourers_workshop.core.math.Size3f;
import moe.plushie.armourers_workshop.core.utils.Objects;

import java.util.List;

public class EntityCollisionShape {

    public static final IDataCodec<EntityCollisionShape> CODEC = IDataCodec.FLOAT.listOf().xmap(EntityCollisionShape::new, EntityCollisionShape::toList);

    private final Rectangle3f rect;

    public EntityCollisionShape(Rectangle3f rect) {
        this.rect = rect;
    }

    public EntityCollisionShape(List<Float> values) {
        this.rect = new Rectangle3f(values);
    }

    public static EntityCollisionShape size(Rectangle3f rect) {
        return new EntityCollisionShape(rect);
    }

    public Rectangle3f getRect() {
        return rect;
    }

    public List<Float> toList() {
        return rect.toList();
    }

    @Override
    public String toString() {
        return Objects.toString(this, "rect", rect);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityCollisionShape that)) return false;
        return Objects.equals(rect, that.rect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rect);
    }
}
