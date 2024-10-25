package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.api.core.math.IQuaternion3f;
import moe.plushie.armourers_workshop.api.core.math.IVector3f;

import java.util.Objects;

@SuppressWarnings("unused")
public class OpenQuaternion3f implements IQuaternion3f {

    public static final OpenQuaternion3f ONE = new OpenQuaternion3f();

    private static final float DEGREES_TO_RADIANS = OpenMath.PI_f / 180;

    public float x;
    public float y;
    public float z;
    public float w;

    public OpenQuaternion3f() {
        this(0f, 0f, 0f, 1f);
    }

    public OpenQuaternion3f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public OpenQuaternion3f(Vector3f vec, float f, boolean bl) {
        if (bl) {
            f *= (float) Math.PI / 180;
        }
        float g = OpenMath.sin(f / 2.0f);
        this.x = vec.getX() * g;
        this.y = vec.getY() * g;
        this.z = vec.getZ() * g;
        this.w = OpenMath.cos(f / 2.0f);
    }

    public OpenQuaternion3f(float x, float y, float z, boolean bl) {
        if (bl) {
            x *= (float) Math.PI / 180;
            y *= (float) Math.PI / 180;
            z *= (float) Math.PI / 180;
        }
        float i = OpenMath.sin(0.5f * x);
        float j = OpenMath.cos(0.5f * x);
        float k = OpenMath.sin(0.5f * y);
        float l = OpenMath.cos(0.5f * y);
        float m = OpenMath.sin(0.5f * z);
        float n = OpenMath.cos(0.5f * z);
        this.x = i * l * n + j * k * m;
        this.y = j * k * n - i * l * m;
        this.z = i * k * n + j * l * m;
        this.w = j * l * n - i * k * m;
    }

    public OpenQuaternion3f(IQuaternion3f other) {
        this.x = other.x();
        this.y = other.y();
        this.z = other.z();
        this.w = other.w();
    }

    public static OpenQuaternion3f fromXYZ(float angleX, float angleY, float angleZ) {
        float sx = OpenMath.sin(angleX * 0.5f);
        float cx = OpenMath.cosFromSin(sx, angleX * 0.5f);
        float sy = OpenMath.sin(angleY * 0.5f);
        float cy = OpenMath.cosFromSin(sy, angleY * 0.5f);
        float sz = OpenMath.sin(angleZ * 0.5f);
        float cz = OpenMath.cosFromSin(sz, angleZ * 0.5f);
        float cycz = cy * cz;
        float sysz = sy * sz;
        float sycz = sy * cz;
        float cysz = cy * sz;
        return new OpenQuaternion3f(sx * cycz + cx * sysz, cx * sycz - sx * cysz, cx * cysz + sx * sycz, cx * cycz - sx * sysz);
    }

    public static OpenQuaternion3f fromZYX(float angleZ, float angleY, float angleX) {
        float sx = OpenMath.sin(angleX * 0.5f);
        float cx = OpenMath.cosFromSin(sx, angleX * 0.5f);
        float sy = OpenMath.sin(angleY * 0.5f);
        float cy = OpenMath.cosFromSin(sy, angleY * 0.5f);
        float sz = OpenMath.sin(angleZ * 0.5f);
        float cz = OpenMath.cosFromSin(sz, angleZ * 0.5f);
        float cycz = cy * cz;
        float sysz = sy * sz;
        float sycz = sy * cz;
        float cysz = cy * sz;
        return new OpenQuaternion3f(sx * cycz - cx * sysz, cx * sycz + sx * cysz, cx * cysz - sx * sycz, cx * cycz + sx * sysz);
    }

    public static OpenQuaternion3f fromYXZ(float angleY, float angleX, float angleZ) {
        float sx = OpenMath.sin(angleX * 0.5f);
        float cx = OpenMath.cosFromSin(sx, angleX * 0.5f);
        float sy = OpenMath.sin(angleY * 0.5f);
        float cy = OpenMath.cosFromSin(sy, angleY * 0.5f);
        float sz = OpenMath.sin(angleZ * 0.5f);
        float cz = OpenMath.cosFromSin(sz, angleZ * 0.5f);
        float x = cy * sx;
        float y = sy * cx;
        float z = sy * sx;
        float w = cy * cx;
        return new OpenQuaternion3f(x * cz + y * sz, y * cz - x * sz, w * sz - z * cz, w * cz + z * sz);
    }

//    public Quaternionf setAngleAxis(float angle, float x, float y, float z) {
//        float s = Math.sin(angle * 0.5f);
//        this.x = x * s;
//        this.y = y * s;
//        this.z = z * s;
//        this.w = Math.cosFromSin(s, angle * 0.5f);
//        return this;


    //    public static OpenQuaternionf fromYXZ(float f, float g, float h) {
//        OpenQuaternionf quaternion = ONE.copy();
//        quaternion.mul(new OpenQuaternionf(0.0f, (float) Math.sin(f / 2.0f), 0.0f, (float) Math.cos(f / 2.0f)));
//        quaternion.mul(new OpenQuaternionf((float) Math.sin(g / 2.0f), 0.0f, 0.0f, (float) Math.cos(g / 2.0f)));
//        quaternion.mul(new OpenQuaternionf(0.0f, 0.0f, (float) Math.sin(h / 2.0f), (float) Math.cos(h / 2.0f)));
//        return quaternion;
//    }

    public static OpenQuaternion3f fromXYZ(IVector3f value, boolean bl) {
        float x = value.getX();
        float y = value.getY();
        float z = value.getZ();
        if (bl) {
            x *= DEGREES_TO_RADIANS;
            y *= DEGREES_TO_RADIANS;
            z *= DEGREES_TO_RADIANS;
        }
        return fromXYZ(x, y, z);
    }

    public static OpenQuaternion3f fromZYX(IVector3f value, boolean bl) {
        float x = value.getX();
        float y = value.getY();
        float z = value.getZ();
        if (bl) {
            x *= DEGREES_TO_RADIANS;
            y *= DEGREES_TO_RADIANS;
            z *= DEGREES_TO_RADIANS;
        }
        return fromZYX(z, y, x);
    }

    public static OpenQuaternion3f fromYXZ(IVector3f value, boolean bl) {
        float x = value.getX();
        float y = value.getY();
        float z = value.getZ();
        if (bl) {
            x *= DEGREES_TO_RADIANS;
            y *= DEGREES_TO_RADIANS;
            z *= DEGREES_TO_RADIANS;
        }
        return fromYXZ(y, x, z);
    }


    //    public static OpenQuaternionf fromUnitVectors(Vector3f from, Vector3f to) {
//        // assumes direction vectors vFrom and vTo are normalized
//        float EPS = 0.000001f;
//        float x, y, z, r = from.dot(to) + 1;
//        if (r < EPS) {
//            r = 0f;
//            if (Math.abs(from.getX()) > Math.abs(from.getZ())) {
//                x = -from.getY();
//                y = from.getX();
//                z = 0f;
//            } else {
//                x = 0f;
//                y = -from.getZ();
//                z = from.getY();
//            }
//        } else {
//            // crossVectors( vFrom, vTo ); // inlined to avoid cyclic dependency on Vector3
//            x = from.getY() * to.getZ() - from.getZ() * to.getY();
//            y = from.getZ() * to.getX() - from.getX() * to.getZ();
//            z = from.getX() * to.getY() - from.getY() * to.getX();
//        }
//        return new OpenQuaternionf(x, y, z, r).normalize();
//    }
//
//    public static OpenQuaternionf fromXYZ(float f, float g, float h) {
//        OpenQuaternionf quaternion = ONE.copy();
//        quaternion.mul(new OpenQuaternionf((float) Math.sin(f / 2.0f), 0.0f, 0.0f, (float) Math.cos(f / 2.0f)));
//        quaternion.mul(new OpenQuaternionf(0.0f, (float) Math.sin(g / 2.0f), 0.0f, (float) Math.cos(g / 2.0f)));
//        quaternion.mul(new OpenQuaternionf(0.0f, 0.0f, (float) Math.sin(h / 2.0f), (float) Math.cos(h / 2.0f)));
//        return quaternion;
//    }
//
    public Vector3f toXYZ() {
        float f = w * w;
        float g = x * x;
        float h = y * y;
        float i = z * z;
        float j = f + g + h + i;
        float k = 2.0f * w * x - 2.0f * y * z;
        float l = (float) Math.asin(k / j);
        if (Math.abs(k) > 0.999f * j) {
            return new Vector3f(2.0f * (float) Math.atan2(x, w), l, 0.0f);
        }
        return new Vector3f((float) Math.atan2(2.0f * y * z + 2.0f * x * w, f - g - h + i), l, (float) Math.atan2(2.0f * x * y + 2.0f * w * z, f + g - h - i));
    }

    public Vector3f toYXZ() {
        float f = w * w;
        float g = x * x;
        float h = y * y;
        float i = z * z;
        float j = f + g + h + i;
        float k = 2.0f * w * x - 2.0f * y * z;
        float l = (float) Math.asin(k / j);
        if (Math.abs(k) > 0.999f * j) {
            return new Vector3f(l, 2.0f * (float) Math.atan2(y, w), 0.0f);
        }
        return new Vector3f(l, (float) Math.atan2(2.0f * x * z + 2.0f * y * w, f - g - h + i), (float) Math.atan2(2.0f * x * y + 2.0f * w * z, f - g + h - i));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenQuaternion3f that)) return false;
        return Float.compare(x, that.x) == 0 && Float.compare(y, that.y) == 0 && Float.compare(z, that.z) == 0 && Float.compare(w, that.w) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, w);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Quaternionf[").append(w).append(" + ");
        stringBuilder.append(x).append("i + ");
        stringBuilder.append(y).append("j + ");
        stringBuilder.append(z).append("k]");
        return stringBuilder.toString();
    }

    @Override
    public float x() {
        return this.x;
    }

    @Override
    public float y() {
        return this.y;
    }

    @Override
    public float z() {
        return this.z;
    }

    @Override
    public float w() {
        return this.w;
    }

    public void mul(OpenQuaternion3f other) {
        float f = x;
        float g = y;
        float h = z;
        float i = w;
        float j = other.x;
        float k = other.y;
        float l = other.z;
        float m = other.w;
        this.x = i * j + f * m + g * l - h * k;
        this.y = i * k - f * l + g * m + h * j;
        this.z = i * l + f * k - g * j + h * m;
        this.w = i * m - f * j - g * k - h * l;
    }

    public void mul(float f) {
        this.x *= f;
        this.y *= f;
        this.z *= f;
        this.w *= f;
    }

    public float dot(OpenQuaternion3f other) {
        return x * other.x + y * other.y + z * other.z + w * other.w;
    }

    public OpenQuaternion3f conjugate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }

    public OpenQuaternion3f inverse() {
        return this.conjugate();
    }

    public void set(float f, float g, float h, float i) {
        this.x = f;
        this.y = g;
        this.z = h;
        this.w = i;
    }

    public void set(OpenQuaternion3f other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
    }

    public OpenQuaternion3f normalize() {
        float invNorm = OpenMath.invsqrt(OpenMath.fma(x, x, OpenMath.fma(y, y, OpenMath.fma(z, z, w * w))));
        x *= invNorm;
        y *= invNorm;
        z *= invNorm;
        w *= invNorm;
        return this;
    }

    public void slerp(OpenQuaternion3f quaternion, float f) {
        throw new UnsupportedOperationException();
    }

    public OpenQuaternion3f copy() {
        return new OpenQuaternion3f(this);
    }
}
