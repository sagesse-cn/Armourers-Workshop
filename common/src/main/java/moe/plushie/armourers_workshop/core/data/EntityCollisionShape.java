package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.core.math.Rectangle3f;
import moe.plushie.armourers_workshop.core.math.Size3f;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;

import java.util.List;

public class EntityCollisionShape {

    public static final IDataCodec<EntityCollisionShape> CODEC = IDataCodec.FLOAT.listOf().xmap(EntityCollisionShape::new, EntityCollisionShape::toList);

    private final Size3f size;

    public EntityCollisionShape(Size3f size) {
        this.size = size;
    }

    public EntityCollisionShape(List<Float> values) {
        this.size = new Size3f(values.get(0), values.get(1), values.get(2));
    }

    public static EntityCollisionShape size(Rectangle3f rect) {
        var size = new Size3f(rect.getWidth(), rect.getHeight(), rect.getDepth());
        return new EntityCollisionShape(size);
    }

    public Size3f getSize() {
        return size;
    }

    public List<Float> toList() {
        return Collections.newList(size.getWidth(), size.getHeight(), size.getDepth());
    }

    @Override
    public String toString() {
        return Objects.toString(this, "size", size);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityCollisionShape that)) return false;
        return Objects.equals(size, that.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(size);
    }
}
