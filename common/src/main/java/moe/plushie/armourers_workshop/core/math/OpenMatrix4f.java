package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.api.core.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.core.math.IQuaternionf;
import moe.plushie.armourers_workshop.core.utils.MatrixUtils;

import java.nio.FloatBuffer;

/**
 * Contains the definition of a 4x4 matrix of floats, and associated functions to transform it.
 * <p>
 * The matrix is column-major to match OpenGL's interpretation, and it looks like this:
 * <p>
 * m00  m10  m20  m30<br>
 * m01  m11  m21  m31<br>
 * m02  m12  m22  m32<br>
 * m03  m13  m23  m33<br>
 */
@SuppressWarnings("unused")
public class OpenMatrix4f implements IMatrix4f {

    private static final OpenMatrix4f IDENTITY = OpenMatrix4f.createScaleMatrix(1, 1, 1);

    public float m00, m01, m02, m03;
    public float m10, m11, m12, m13;
    public float m20, m21, m22, m23;
    public float m30, m31, m32, m33;

    public OpenMatrix4f() {
    }

    public OpenMatrix4f(IMatrix4f matrix) {
        var buffer = FastLocal.LOCALS.get().buffer;
        matrix.store(buffer);
        load(buffer);
    }

    public OpenMatrix4f(IQuaternionf quaternion) {
        set(quaternion);
    }

    public OpenMatrix4f(FloatBuffer buffer) {
        load(buffer);
    }

    public static OpenMatrix4f createScaleMatrix(float x, float y, float z) {
        var matrix = new OpenMatrix4f();
        matrix.m00 = x;
        matrix.m11 = y;
        matrix.m22 = z;
        matrix.m33 = 1;
        return matrix;
    }

    public static OpenMatrix4f createTranslateMatrix(float x, float y, float z) {
        var matrix = new OpenMatrix4f();
        matrix.m00 = 1;
        matrix.m11 = 1;
        matrix.m22 = 1;
        matrix.m33 = 1;
        matrix.m30 = x;
        matrix.m31 = y;
        matrix.m32 = z;
        return matrix;
    }

    public static OpenMatrix4f createRotationMatrix(IQuaternionf q) {
        var matrix = new OpenMatrix4f();
        matrix.set(q);
        return matrix;
    }

    public static OpenMatrix4f identity() {
        return IDENTITY;
    }

    public static OpenMatrix4f of(IMatrix4f o) {
        if (o instanceof OpenMatrix4f that) {
            return that;
        }
        return new OpenMatrix4f(o);
    }

    @Override
    public void scale(float x, float y, float z) {
        multiply(OpenMatrix4f.createScaleMatrix(x, y, z));
    }

    @Override
    public void translate(float x, float y, float z) {
        multiply(OpenMatrix4f.createTranslateMatrix(x, y, z));
    }

    @Override
    public void rotate(IQuaternionf quaternion) {
        multiply(FastLocal.from(quaternion));
    }

    @Override
    public void set(IMatrix4f matrix) {
        set(FastLocal.from(matrix));
    }

    public void set(IQuaternionf q) {
        var w2 = q.w() * q.w();
        var x2 = q.x() * q.x();
        var y2 = q.y() * q.y();
        var z2 = q.z() * q.z();
        var zw = q.z() * q.w();
        var xy = q.x() * q.y();
        var xz = q.x() * q.z();
        var yw = q.y() * q.w();
        var yz = q.y() * q.z();
        var xw = q.x() * q.w();
        m00 = w2 + x2 - z2 - y2;
        m01 = xy + zw + zw + xy;
        m02 = xz - yw + xz - yw;
        m03 = 0.0f;
        m10 = -zw + xy - zw + xy;
        m11 = y2 - z2 + w2 - x2;
        m12 = yz + yz + xw + xw;
        m13 = 0.0f;
        m20 = yw + xz + xz + yw;
        m21 = yz + yz - xw - xw;
        m22 = z2 - y2 - x2 + w2;
        m30 = 0.0f;
        m31 = 0.0f;
        m32 = 0.0f;
        m33 = 1.0f;
    }

    public void set(OpenMatrix3f m) {
        m00 = m.m00;
        m01 = m.m01;
        m02 = m.m02;
        m03 = 0.0f;
        m10 = m.m10;
        m11 = m.m11;
        m12 = m.m12;
        m13 = 0.0f;
        m20 = m.m20;
        m21 = m.m21;
        m22 = m.m22;
        m23 = 0.0f;
        m30 = 0.0f;
        m31 = 0.0f;
        m32 = 0.0f;
        m33 = 1.0f;
    }

    public void set(OpenMatrix4f mat) {
        m00 = mat.m00;
        m01 = mat.m01;
        m02 = mat.m02;
        m03 = mat.m03;
        m10 = mat.m10;
        m11 = mat.m11;
        m12 = mat.m12;
        m13 = mat.m13;
        m20 = mat.m20;
        m21 = mat.m21;
        m22 = mat.m22;
        m23 = mat.m23;
        m30 = mat.m30;
        m31 = mat.m31;
        m32 = mat.m32;
        m33 = mat.m33;
    }

    public void setIdentity() {
        m00 = 1.0f;
        m01 = 0.0f;
        m02 = 0.0f;
        m03 = 0.0f;
        m10 = 0.0f;
        m11 = 1.0f;
        m12 = 0.0f;
        m13 = 0.0f;
        m20 = 0.0f;
        m21 = 0.0f;
        m22 = 1.0f;
        m23 = 0.0f;
        m30 = 0.0f;
        m31 = 0.0f;
        m32 = 0.0f;
        m33 = 1.0f;
    }

    public void setTranslation(float x, float y, float z) {
        m30 = x;
        m31 = y;
        m32 = z;
    }

    @Override
    public void multiply(float[] values) {
        float x = values[0];
        float y = values[1];
        float z = values[2];
        float w = values[3];
        values[0] = OpenMath.fma(m00, x, OpenMath.fma(m10, y, OpenMath.fma(m20, z, m30 * w)));
        values[1] = OpenMath.fma(m01, x, OpenMath.fma(m11, y, OpenMath.fma(m21, z, m31 * w)));
        values[2] = OpenMath.fma(m02, x, OpenMath.fma(m12, y, OpenMath.fma(m22, z, m32 * w)));
    }

    @Override
    public void multiply(IMatrix4f other) {
        multiply(this, FastLocal.from(other), this);
    }

    public void multiplyFront(IMatrix4f other) {
        multiply(FastLocal.from(other), this, this);
    }

    public void multiplyFront(IQuaternionf quaternion) {
        multiplyFront(FastLocal.from(quaternion));
    }

    public void multiply(float f) {
        m00 *= f;
        m01 *= f;
        m02 *= f;
        m03 *= f;
        m10 *= f;
        m11 *= f;
        m12 *= f;
        m13 *= f;
        m20 *= f;
        m21 *= f;
        m22 *= f;
        m23 *= f;
        m30 *= f;
        m31 *= f;
        m32 *= f;
        m33 *= f;
    }

    @Override
    public void load(FloatBuffer buffer) {
        m00 = buffer.get(bufferIndex(0, 0));
        m01 = buffer.get(bufferIndex(0, 1));
        m02 = buffer.get(bufferIndex(0, 2));
        m03 = buffer.get(bufferIndex(0, 3));
        m10 = buffer.get(bufferIndex(1, 0));
        m11 = buffer.get(bufferIndex(1, 1));
        m12 = buffer.get(bufferIndex(1, 2));
        m13 = buffer.get(bufferIndex(1, 3));
        m20 = buffer.get(bufferIndex(2, 0));
        m21 = buffer.get(bufferIndex(2, 1));
        m22 = buffer.get(bufferIndex(2, 2));
        m23 = buffer.get(bufferIndex(2, 3));
        m30 = buffer.get(bufferIndex(3, 0));
        m31 = buffer.get(bufferIndex(3, 1));
        m32 = buffer.get(bufferIndex(3, 2));
        m33 = buffer.get(bufferIndex(3, 3));
    }

    @Override
    public void store(FloatBuffer buffer) {
        buffer.put(bufferIndex(0, 0), m00);
        buffer.put(bufferIndex(0, 1), m01);
        buffer.put(bufferIndex(0, 2), m02);
        buffer.put(bufferIndex(0, 3), m03);
        buffer.put(bufferIndex(1, 0), m10);
        buffer.put(bufferIndex(1, 1), m11);
        buffer.put(bufferIndex(1, 2), m12);
        buffer.put(bufferIndex(1, 3), m13);
        buffer.put(bufferIndex(2, 0), m20);
        buffer.put(bufferIndex(2, 1), m21);
        buffer.put(bufferIndex(2, 2), m22);
        buffer.put(bufferIndex(2, 3), m23);
        buffer.put(bufferIndex(3, 0), m30);
        buffer.put(bufferIndex(3, 1), m31);
        buffer.put(bufferIndex(3, 2), m32);
        buffer.put(bufferIndex(3, 3), m33);
    }

    @Override
    public void transpose() {
        // | m00 m10 m20 m30 |    | m00 m01 m02 m03 |    
        // | m01 m11 m21 m31 | => | m10 m11 m12 m13 | 
        // | m02 m12 m22 m32 |    | m20 m21 m22 m23 |    
        // | m03 m13 m23 m33 |    | m30 m31 m32 m33 |    
        var nm10 = m01;
        var nm20 = m02;
        var nm21 = m12;
        var nm30 = m03;
        var nm31 = m13;
        var nm32 = m23;
        m01 = m10;
        m02 = m20;
        m03 = m30;
        m10 = nm10;
        m12 = m21;
        m13 = m31;
        m20 = nm20;
        m21 = nm21;
        m23 = m32;
        m30 = nm30;
        m31 = nm31;
        m32 = nm32;
    }

    @Override
    public void invert() {
        var a = m00 * m11 - m01 * m10;
        var b = m00 * m12 - m02 * m10;
        var c = m00 * m13 - m03 * m10;
        var d = m01 * m12 - m02 * m11;
        var e = m01 * m13 - m03 * m11;
        var f = m02 * m13 - m03 * m12;
        var g = m20 * m31 - m21 * m30;
        var h = m20 * m32 - m22 * m30;
        var i = m20 * m33 - m23 * m30;
        var j = m21 * m32 - m22 * m31;
        var k = m21 * m33 - m23 * m31;
        var l = m22 * m33 - m23 * m32;
        var det = a * l - b * k + c * j + d * i - e * h + f * g;
        det = 1.0f / det;
        var nm00 = OpenMath.fma(m11, l, OpenMath.fma(-m12, k, m13 * j)) * det;
        var nm01 = OpenMath.fma(-m01, l, OpenMath.fma(m02, k, -m03 * j)) * det;
        var nm02 = OpenMath.fma(m31, f, OpenMath.fma(-m32, e, m33 * d)) * det;
        var nm03 = OpenMath.fma(-m21, f, OpenMath.fma(m22, e, -m23 * d)) * det;
        var nm10 = OpenMath.fma(-m10, l, OpenMath.fma(m12, i, -m13 * h)) * det;
        var nm11 = OpenMath.fma(m00, l, OpenMath.fma(-m02, i, m03 * h)) * det;
        var nm12 = OpenMath.fma(-m30, f, OpenMath.fma(m32, c, -m33 * b)) * det;
        var nm13 = OpenMath.fma(m20, f, OpenMath.fma(-m22, c, m23 * b)) * det;
        var nm20 = OpenMath.fma(m10, k, OpenMath.fma(-m11, i, m13 * g)) * det;
        var nm21 = OpenMath.fma(-m00, k, OpenMath.fma(m01, i, -m03 * g)) * det;
        var nm22 = OpenMath.fma(m30, e, OpenMath.fma(-m31, c, m33 * a)) * det;
        var nm23 = OpenMath.fma(-m20, e, OpenMath.fma(m21, c, -m23 * a)) * det;
        var nm30 = OpenMath.fma(-m10, j, OpenMath.fma(m11, h, -m12 * g)) * det;
        var nm31 = OpenMath.fma(m00, j, OpenMath.fma(-m01, h, m02 * g)) * det;
        var nm32 = OpenMath.fma(-m30, d, OpenMath.fma(m31, b, -m32 * a)) * det;
        var nm33 = OpenMath.fma(m20, d, OpenMath.fma(-m21, b, m22 * a)) * det;
        m00 = nm00;
        m01 = nm01;
        m02 = nm02;
        m03 = nm03;
        m10 = nm10;
        m11 = nm11;
        m12 = nm12;
        m13 = nm13;
        m20 = nm20;
        m21 = nm21;
        m22 = nm22;
        m23 = nm23;
        m30 = nm30;
        m31 = nm31;
        m32 = nm32;
        m33 = nm33;
    }

    public OpenMatrix4f copy() {
        return new OpenMatrix4f(this);
    }

    @Override
    public String toString() {
        return OpenMath.format("%f %f %f %f\n%f %f %f %f\n%f %f %f %f\n%f %f %f %f\n", m00, m10, m20, m30, m01, m11, m21, m31, m02, m12, m22, m32, m03, m13, m23, m33);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenMatrix4f that)) return false;
        if (Float.compare(that.m10, m10) != 0) return false;
        if (Float.compare(that.m20, m20) != 0) return false;
        if (Float.compare(that.m30, m30) != 0) return false;
        if (Float.compare(that.m00, m00) != 0) return false;
        if (Float.compare(that.m11, m11) != 0) return false;
        if (Float.compare(that.m22, m22) != 0) return false;
        if (Float.compare(that.m33, m33) != 0) return false;
        if (Float.compare(that.m31, m31) != 0) return false;
        if (Float.compare(that.m32, m32) != 0) return false;
        if (Float.compare(that.m01, m01) != 0) return false;
        if (Float.compare(that.m02, m02) != 0) return false;
        if (Float.compare(that.m03, m03) != 0) return false;
        if (Float.compare(that.m12, m12) != 0) return false;
        if (Float.compare(that.m13, m13) != 0) return false;
        if (Float.compare(that.m21, m21) != 0) return false;
        if (Float.compare(that.m23, m23) != 0) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Float.floatToIntBits(m00);
        result = 31 * result + Float.floatToIntBits(m01);
        result = 31 * result + Float.floatToIntBits(m02);
        result = 31 * result + Float.floatToIntBits(m03);
        result = 31 * result + Float.floatToIntBits(m10);
        result = 31 * result + Float.floatToIntBits(m11);
        result = 31 * result + Float.floatToIntBits(m12);
        result = 31 * result + Float.floatToIntBits(m13);
        result = 31 * result + Float.floatToIntBits(m20);
        result = 31 * result + Float.floatToIntBits(m21);
        result = 31 * result + Float.floatToIntBits(m22);
        result = 31 * result + Float.floatToIntBits(m23);
        result = 31 * result + Float.floatToIntBits(m30);
        result = 31 * result + Float.floatToIntBits(m31);
        result = 31 * result + Float.floatToIntBits(m32);
        result = 31 * result + Float.floatToIntBits(m33);
        return result;
    }

    // column major index
    private static int bufferIndex(int i, int j) {
        return i * 4 + j;
    }

    // dest = left * right
    private static void multiply(OpenMatrix4f left, OpenMatrix4f right, OpenMatrix4f dest) {
        var m00 = OpenMath.fma(left.m00, right.m00, OpenMath.fma(left.m10, right.m01, OpenMath.fma(left.m20, right.m02, left.m30 * right.m03)));
        var m01 = OpenMath.fma(left.m01, right.m00, OpenMath.fma(left.m11, right.m01, OpenMath.fma(left.m21, right.m02, left.m31 * right.m03)));
        var m02 = OpenMath.fma(left.m02, right.m00, OpenMath.fma(left.m12, right.m01, OpenMath.fma(left.m22, right.m02, left.m32 * right.m03)));
        var m03 = OpenMath.fma(left.m03, right.m00, OpenMath.fma(left.m13, right.m01, OpenMath.fma(left.m23, right.m02, left.m33 * right.m03)));
        var m10 = OpenMath.fma(left.m00, right.m10, OpenMath.fma(left.m10, right.m11, OpenMath.fma(left.m20, right.m12, left.m30 * right.m13)));
        var m11 = OpenMath.fma(left.m01, right.m10, OpenMath.fma(left.m11, right.m11, OpenMath.fma(left.m21, right.m12, left.m31 * right.m13)));
        var m12 = OpenMath.fma(left.m02, right.m10, OpenMath.fma(left.m12, right.m11, OpenMath.fma(left.m22, right.m12, left.m32 * right.m13)));
        var m13 = OpenMath.fma(left.m03, right.m10, OpenMath.fma(left.m13, right.m11, OpenMath.fma(left.m23, right.m12, left.m33 * right.m13)));
        var m20 = OpenMath.fma(left.m00, right.m20, OpenMath.fma(left.m10, right.m21, OpenMath.fma(left.m20, right.m22, left.m30 * right.m23)));
        var m21 = OpenMath.fma(left.m01, right.m20, OpenMath.fma(left.m11, right.m21, OpenMath.fma(left.m21, right.m22, left.m31 * right.m23)));
        var m22 = OpenMath.fma(left.m02, right.m20, OpenMath.fma(left.m12, right.m21, OpenMath.fma(left.m22, right.m22, left.m32 * right.m23)));
        var m23 = OpenMath.fma(left.m03, right.m20, OpenMath.fma(left.m13, right.m21, OpenMath.fma(left.m23, right.m22, left.m33 * right.m23)));
        var m30 = OpenMath.fma(left.m00, right.m30, OpenMath.fma(left.m10, right.m31, OpenMath.fma(left.m20, right.m32, left.m30 * right.m33)));
        var m31 = OpenMath.fma(left.m01, right.m30, OpenMath.fma(left.m11, right.m31, OpenMath.fma(left.m21, right.m32, left.m31 * right.m33)));
        var m32 = OpenMath.fma(left.m02, right.m30, OpenMath.fma(left.m12, right.m31, OpenMath.fma(left.m22, right.m32, left.m32 * right.m33)));
        var m33 = OpenMath.fma(left.m03, right.m30, OpenMath.fma(left.m13, right.m31, OpenMath.fma(left.m23, right.m32, left.m33 * right.m33)));
        dest.m00 = m00;
        dest.m01 = m01;
        dest.m02 = m02;
        dest.m03 = m03;
        dest.m10 = m10;
        dest.m11 = m11;
        dest.m12 = m12;
        dest.m13 = m13;
        dest.m20 = m20;
        dest.m21 = m21;
        dest.m22 = m22;
        dest.m23 = m23;
        dest.m30 = m30;
        dest.m31 = m31;
        dest.m32 = m32;
        dest.m33 = m33;
    }

    private static class FastLocal extends OpenMatrix4f {

        private static final ThreadLocal<FastLocal> LOCALS = ThreadLocal.withInitial(FastLocal::new);

        private final FloatBuffer buffer = MatrixUtils.createFloatBuffer(16);

        private static OpenMatrix4f from(IMatrix4f value) {
            if (value instanceof OpenMatrix4f matrix) {
                return matrix;
            }
            var local = LOCALS.get();
            value.store(local.buffer);
            local.load(local.buffer);
            return local;
        }

        private static OpenMatrix4f from(IQuaternionf value) {
            var local = LOCALS.get();
            local.set(value);
            return local;
        }
    }
}
