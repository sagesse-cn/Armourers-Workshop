package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.api.core.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.core.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.core.math.IQuaternionf;
import moe.plushie.armourers_workshop.core.utils.MatrixUtils;

import java.nio.FloatBuffer;

/**
 * Contains the definition of a 3x3 matrix of floats, and associated functions to transform
 * it.
 * <p>
 * The matrix is column-major to match OpenGL's interpretation, and it looks like this:
 * <p>
 * m00  m10  m20<br>
 * m01  m11  m21<br>
 * m02  m12  m22<br>
 */
@SuppressWarnings("unused")
public class OpenMatrix3f implements IMatrix3f {

    private static final OpenMatrix3f IDENTITY = OpenMatrix3f.createScaleMatrix(1, 1, 1);

    public float m00, m01, m02;
    public float m10, m11, m12;
    public float m20, m21, m22;

    public OpenMatrix3f() {
    }

    public OpenMatrix3f(IMatrix3f matrix) {
        set(FastLocal.from(matrix));
    }

    public OpenMatrix3f(IMatrix4f matrix) {
        set(FastLocal.from(matrix));
    }

    public OpenMatrix3f(IQuaternionf quaternion) {
        set(quaternion);
    }

    public OpenMatrix3f(FloatBuffer buffer) {
        load(buffer);
    }

    public static OpenMatrix3f createScaleMatrix(float x, float y, float z) {
        var matrix = new OpenMatrix3f();
        matrix.m00 = x;
        matrix.m11 = y;
        matrix.m22 = z;
        return matrix;
    }

    public static OpenMatrix3f identity() {
        return IDENTITY;
    }

    public static OpenMatrix3f of(IMatrix3f mat) {
        if (mat instanceof OpenMatrix3f that) {
            return that;
        }
        return new OpenMatrix3f(mat);
    }

    public static OpenMatrix3f of(IMatrix4f mat) {
        return new OpenMatrix3f(mat);
    }


    @Override
    public void scale(float x, float y, float z) {
        multiply(OpenMatrix3f.createScaleMatrix(x, y, z));
    }

    @Override
    public void rotate(IQuaternionf other) {
        multiply(FastLocal.fromRot(other));
    }

    @Override
    public void set(IMatrix3f matrix) {
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
        var dzw = zw + zw;
        var dxy = xy + xy;
        var dxz = xz + xz;
        var dyw = yw + yw;
        var dyz = yz + yz;
        var dxw = xw + xw;
        m00 = w2 + x2 - z2 - y2;
        m01 = dxy + dzw;
        m02 = dxz - dyw;
        m10 = -dzw + dxy;
        m11 = y2 - z2 + w2 - x2;
        m12 = dyz + dxw;
        m20 = dyw + dxz;
        m21 = dyz - dxw;
        m22 = z2 - y2 - x2 + w2;
    }

    public void set(OpenMatrix3f m) {
        m00 = m.m00;
        m01 = m.m01;
        m02 = m.m02;
        m10 = m.m10;
        m11 = m.m11;
        m12 = m.m12;
        m20 = m.m20;
        m21 = m.m21;
        m22 = m.m22;
    }

    public void set(OpenMatrix4f mat) {
        m00 = mat.m00;
        m01 = mat.m01;
        m02 = mat.m02;
        m10 = mat.m10;
        m11 = mat.m11;
        m12 = mat.m12;
        m20 = mat.m20;
        m21 = mat.m21;
        m22 = mat.m22;
    }

    public void setIdentity() {
        m00 = 1.0f;
        m01 = 0.0f;
        m02 = 0.0f;
        m10 = 0.0f;
        m11 = 1.0f;
        m12 = 0.0f;
        m20 = 0.0f;
        m21 = 0.0f;
        m22 = 1.0f;
    }

    @Override
    public void multiply(float[] values) {
        float x = values[0];
        float y = values[1];
        float z = values[2];
        values[0] = OpenMath.fma(m00, x, OpenMath.fma(m10, y, m20 * z));
        values[1] = OpenMath.fma(m01, x, OpenMath.fma(m11, y, m21 * z));
        values[2] = OpenMath.fma(m02, x, OpenMath.fma(m12, y, m22 * z));
    }

    @Override
    public void multiply(IMatrix3f other) {
        multiply(this, FastLocal.from(other), this);
    }

    public void multiplyFront(IMatrix3f other) {
        multiply(FastLocal.from(other), this, this);
    }

    public void multiplyFront(IQuaternionf other) {
        multiplyFront(FastLocal.fromRot(other));
    }

    public void multiply(float ratio) {
        m00 *= ratio;
        m01 *= ratio;
        m02 *= ratio;
        m10 *= ratio;
        m11 *= ratio;
        m12 *= ratio;
        m20 *= ratio;
        m21 *= ratio;
        m22 *= ratio;
    }

    @Override
    public void load(FloatBuffer buffer) {
        if (buffer.remaining() == 9) {
            m00 = buffer.get(bufferIndex(0, 0));
            m01 = buffer.get(bufferIndex(0, 1));
            m02 = buffer.get(bufferIndex(0, 2));
            m10 = buffer.get(bufferIndex(1, 0));
            m11 = buffer.get(bufferIndex(1, 1));
            m12 = buffer.get(bufferIndex(1, 2));
            m20 = buffer.get(bufferIndex(2, 0));
            m21 = buffer.get(bufferIndex(2, 1));
            m22 = buffer.get(bufferIndex(2, 2));
        } else {
            m00 = buffer.get(bufferIndex4(0, 0));
            m01 = buffer.get(bufferIndex4(0, 1));
            m02 = buffer.get(bufferIndex4(0, 2));
            m10 = buffer.get(bufferIndex4(1, 0));
            m11 = buffer.get(bufferIndex4(1, 1));
            m12 = buffer.get(bufferIndex4(1, 2));
            m20 = buffer.get(bufferIndex4(2, 0));
            m21 = buffer.get(bufferIndex4(2, 1));
            m22 = buffer.get(bufferIndex4(2, 2));
        }
    }

    @Override
    public void store(FloatBuffer buffer) {
        buffer.put(bufferIndex(0, 0), m00);
        buffer.put(bufferIndex(0, 1), m01);
        buffer.put(bufferIndex(0, 2), m02);
        buffer.put(bufferIndex(1, 0), m10);
        buffer.put(bufferIndex(1, 1), m11);
        buffer.put(bufferIndex(1, 2), m12);
        buffer.put(bufferIndex(2, 0), m20);
        buffer.put(bufferIndex(2, 1), m21);
        buffer.put(bufferIndex(2, 2), m22);
    }

    @Override
    public void invert() {
        var a = OpenMath.fma(m00, m11, -m01 * m10);
        var b = OpenMath.fma(m02, m10, -m00 * m12);
        var c = OpenMath.fma(m01, m12, -m02 * m11);
        var d = OpenMath.fma(a, m22, OpenMath.fma(b, m21, c * m20));
        var s = 1.0f / d;
        var nm00 = OpenMath.fma(m11, m22, -m21 * m12) * s;
        var nm01 = OpenMath.fma(m21, m02, -m01 * m22) * s;
        var nm02 = c * s;
        var nm10 = OpenMath.fma(m20, m12, -m10 * m22) * s;
        var nm11 = OpenMath.fma(m00, m22, -m20 * m02) * s;
        var nm12 = b * s;
        var nm20 = OpenMath.fma(m10, m21, -m20 * m11) * s;
        var nm21 = OpenMath.fma(m20, m01, -m00 * m21) * s;
        var nm22 = a * s;
        m00 = nm00;
        m01 = nm01;
        m02 = nm02;
        m10 = nm10;
        m11 = nm11;
        m12 = nm12;
        m20 = nm20;
        m21 = nm21;
        m22 = nm22;
    }

    @Override
    public void transpose() {
        // | m00 m10 m20 |    | m00 m01 m02 |    
        // | m01 m11 m21 | => | m10 m11 m12 |
        // | m02 m12 m22 |    | m20 m21 m22 |    
        float nm10 = m01;
        float nm12 = m21;
        float nm20 = m02;
        float nm21 = m12;
        m01 = m10;
        m02 = m20;
        m10 = nm10;
        m12 = nm12;
        m20 = nm20;
        m21 = nm21;
    }

    public OpenMatrix3f copy() {
        return new OpenMatrix3f(this);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenMatrix3f that)) return false;
        if (Float.compare(that.m00, m00) != 0) return false;
        if (Float.compare(that.m11, m11) != 0) return false;
        if (Float.compare(that.m22, m22) != 0) return false;
        if (Float.compare(that.m20, m20) != 0) return false;
        if (Float.compare(that.m21, m21) != 0) return false;
        if (Float.compare(that.m01, m01) != 0) return false;
        if (Float.compare(that.m02, m02) != 0) return false;
        if (Float.compare(that.m10, m10) != 0) return false;
        if (Float.compare(that.m12, m12) != 0) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = (m00 != 0.0f ? Float.floatToIntBits(m00) : 0);
        result = 31 * result + (m01 != 0.0f ? Float.floatToIntBits(m01) : 0);
        result = 31 * result + (m02 != 0.0f ? Float.floatToIntBits(m02) : 0);
        result = 31 * result + (m10 != 0.0f ? Float.floatToIntBits(m10) : 0);
        result = 31 * result + (m11 != 0.0f ? Float.floatToIntBits(m11) : 0);
        result = 31 * result + (m12 != 0.0f ? Float.floatToIntBits(m12) : 0);
        result = 31 * result + (m20 != 0.0f ? Float.floatToIntBits(m20) : 0);
        result = 31 * result + (m21 != 0.0f ? Float.floatToIntBits(m21) : 0);
        result = 31 * result + (m22 != 0.0f ? Float.floatToIntBits(m22) : 0);
        return result;
    }

    private static int bufferIndex(int i, int j) {
        return i * 3 + j;
    }

    private static int bufferIndex4(int i, int j) {
        return i * 4 + j;
    }

    @Override
    public String toString() {
        return OpenMath.format("%f %f %f\n%f %f %f\n%f %f %f\n", m00, m10, m20, m01, m11, m21, m02, m12, m22);
    }

    // dest = left * right
    private static void multiply(OpenMatrix3f left, OpenMatrix3f right, OpenMatrix3f dest) {
        float nm00 = OpenMath.fma(left.m00, right.m00, OpenMath.fma(left.m10, right.m01, left.m20 * right.m02));
        float nm01 = OpenMath.fma(left.m01, right.m00, OpenMath.fma(left.m11, right.m01, left.m21 * right.m02));
        float nm02 = OpenMath.fma(left.m02, right.m00, OpenMath.fma(left.m12, right.m01, left.m22 * right.m02));
        float nm10 = OpenMath.fma(left.m00, right.m10, OpenMath.fma(left.m10, right.m11, left.m20 * right.m12));
        float nm11 = OpenMath.fma(left.m01, right.m10, OpenMath.fma(left.m11, right.m11, left.m21 * right.m12));
        float nm12 = OpenMath.fma(left.m02, right.m10, OpenMath.fma(left.m12, right.m11, left.m22 * right.m12));
        float nm20 = OpenMath.fma(left.m00, right.m20, OpenMath.fma(left.m10, right.m21, left.m20 * right.m22));
        float nm21 = OpenMath.fma(left.m01, right.m20, OpenMath.fma(left.m11, right.m21, left.m21 * right.m22));
        float nm22 = OpenMath.fma(left.m02, right.m20, OpenMath.fma(left.m12, right.m21, left.m22 * right.m22));
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m02 = nm02;
        dest.m10 = nm10;
        dest.m11 = nm11;
        dest.m12 = nm12;
        dest.m20 = nm20;
        dest.m21 = nm21;
        dest.m22 = nm22;
    }

    private static class FastLocal extends OpenMatrix3f {

        private static final ThreadLocal<FastLocal> LOCALS = ThreadLocal.withInitial(FastLocal::new);

        private final FloatBuffer buffer = MatrixUtils.createFloatBuffer(9);
        private final FloatBuffer buffer2 = MatrixUtils.createFloatBuffer(16);

        private static OpenMatrix3f from(IMatrix3f value) {
            if (value instanceof OpenMatrix3f matrix) {
                return matrix;
            }
            var local = LOCALS.get();
            value.store(local.buffer);
            local.load(local.buffer);
            return local;
        }

        private static OpenMatrix3f from(IMatrix4f value) {
            var local = LOCALS.get();
            value.store(local.buffer);
            local.load(local.buffer);
            return local;
        }

        private static OpenMatrix3f fromRot(IQuaternionf value) {
            var local = LOCALS.get();
            local.set(value);
            return local;
        }
    }
}
