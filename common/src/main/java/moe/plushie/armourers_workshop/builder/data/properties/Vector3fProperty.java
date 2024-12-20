package moe.plushie.armourers_workshop.builder.data.properties;

import moe.plushie.armourers_workshop.core.math.OpenVector3f;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class Vector3fProperty extends DataProperty<OpenVector3f> {

    private final DataProperty<Float> x = field(OpenVector3f::setX, OpenVector3f::x);
    private final DataProperty<Float> y = field(OpenVector3f::setY, OpenVector3f::y);
    private final DataProperty<Float> z = field(OpenVector3f::setZ, OpenVector3f::z);

    public Vector3fProperty() {
        super();
    }

    public Vector3fProperty(float x, float y, float z) {
        super(new OpenVector3f(x, y, z));
    }

    @Override
    public void set(OpenVector3f value) {
        super.set(value);
        this.x.set(value.x());
        this.y.set(value.y());
        this.z.set(value.z());
    }

    public DataProperty<Float> x() {
        return x;
    }

    public DataProperty<Float> y() {
        return y;
    }

    public DataProperty<Float> z() {
        return z;
    }

    private DataProperty<Float> field(BiConsumer<OpenVector3f, Float> setter, Function<OpenVector3f, Float> getter) {
        return new DataProperty<Float>() {

            @Override
            public void beginEditing() {
                super.beginEditing();
                Vector3fProperty.super.beginEditing();
            }

            @Override
            public void endEditing() {
                super.endEditing();
                Vector3fProperty.super.endEditing();
            }

            @Override
            public void set(Float value) {
                super.set(value);
                var newValue = Vector3fProperty.this.value.copy();
                setter.accept(newValue, value);
                Vector3fProperty.super.set(newValue);
            }

            @Override
            public Float get() {
                if (Vector3fProperty.this.value != null) {
                    return getter.apply(Vector3fProperty.this.value);
                }
                return 0.0f;
            }
        };
    }
}
