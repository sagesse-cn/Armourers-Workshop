package moe.plushie.armourers_workshop.builder.data.properties;

import moe.plushie.armourers_workshop.core.math.OpenVector3d;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class Vector3dProperty extends DataProperty<OpenVector3d> {

    private final DataProperty<Double> x = field(OpenVector3d::setX, OpenVector3d::x);
    private final DataProperty<Double> y = field(OpenVector3d::setY, OpenVector3d::y);
    private final DataProperty<Double> z = field(OpenVector3d::setZ, OpenVector3d::z);

    public Vector3dProperty() {
        super();
    }

    public Vector3dProperty(double x, double y, double z) {
        super(new OpenVector3d(x, y, z));
    }

    @Override
    public void set(OpenVector3d value) {
        super.set(value);
        this.x.set(value.x());
        this.y.set(value.y());
        this.z.set(value.z());
    }

    public DataProperty<Double> x() {
        return x;
    }

    public DataProperty<Double> y() {
        return y;
    }

    public DataProperty<Double> z() {
        return z;
    }

    private DataProperty<Double> field(BiConsumer<OpenVector3d, Double> setter, Function<OpenVector3d, Double> getter) {
        return new DataProperty<Double>() {

            @Override
            public void beginEditing() {
                super.beginEditing();
                Vector3dProperty.super.beginEditing();
            }

            @Override
            public void endEditing() {
                super.endEditing();
                Vector3dProperty.super.endEditing();
            }

            @Override
            public void set(Double value) {
                super.set(value);
                var newValue = Vector3dProperty.this.value.copy();
                setter.accept(newValue, value);
                Vector3dProperty.super.set(newValue);
            }

            @Override
            public Double get() {
                if (Vector3dProperty.this.value != null) {
                    return getter.apply(Vector3dProperty.this.value);
                }
                return 0.0;
            }
        };
    }
}
