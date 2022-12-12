package moe.plushie.armourers_workshop.compatibility.v19;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.*;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

@Available("[1.19.3, )")
@Environment(value = EnvType.CLIENT)
public abstract class PoseStack_V1920 {

    private static final Matrix3f CONVERTER_MAT3 = new Matrix3f();
    private static final Matrix4f CONVERTER_MAT4 = new Matrix4f();

    private static final FloatBuffer CONVERTER_BUFFER3 = BufferUtils.createFloatBuffer(9);
    private static final FloatBuffer CONVERTER_BUFFER4 = BufferUtils.createFloatBuffer(16);

    public static Matrix3f of(IMatrix3f mat) {
        if (mat instanceof Matrix3f) {
            return (Matrix3f) mat;
        }
        if (mat instanceof Mat3) {
            return ((Mat3) mat).mat;
        }
        IMatrix3f accessor = ObjectUtils.unsafeCast(CONVERTER_MAT3);
        mat.get(CONVERTER_BUFFER3);
        accessor.set(CONVERTER_BUFFER3);
        return CONVERTER_MAT3;
    }

    public static Matrix4f of(IMatrix4f mat) {
        if (mat instanceof Matrix4f) {
            return (Matrix4f) mat;
        }
        if (mat instanceof Mat4) {
            return ((Mat4) mat).mat;
        }
        mat.get(CONVERTER_BUFFER4);
        CONVERTER_MAT4.set(CONVERTER_BUFFER4);
        return CONVERTER_MAT4;
    }

    public static Quaternionf of(IQuaternionf qat) {
        return new Quaternionf(qat.i(), qat.j(), qat.k(), qat.r());
    }

    public static IPoseStack wrap(PoseStack poseStack) {
        return (IPoseStack) poseStack;
    }

    public static IPoseStack empty() {
        return wrap(new PoseStack());
    }


    public static class Mat3 implements IMatrix3f {

        private final Matrix3f mat;

        public Mat3(Matrix3f mat) {
            this.mat = mat;
        }

        @Override
        public void set(FloatBuffer buffer) {
            mat.set(buffer);
        }

        @Override
        public void get(FloatBuffer buffer) {
            mat.get(buffer);
        }

        @Override
        public void scale(float x, float y, float z) {
            mat.scale(x, y, z);
        }

        @Override
        public void rotate(IQuaternionf quaternion) {
            mat.rotate(new Quaternionf(quaternion.i(), quaternion.j(), quaternion.k(), quaternion.r()));
        }

        @Override
        public void multiply(IMatrix3f matrix) {
            mat.mul(of(matrix));
        }

        @Override
        public void multiply(float[] values) {
            Vector3f vec = new Vector3f(values[0], values[1], values[2]);
            vec.mul(mat);
            values[0] = vec.x();
            values[1] = vec.y();
            values[2] = vec.z();
        }

        @Override
        public void invert() {
            mat.invert();
        }

        @Override
        public IMatrix3f copy() {
            return new Mat3(new Matrix3f(mat));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Mat3 that)) return false;
            return mat.equals(that.mat);
        }

        @Override
        public int hashCode() {
            return mat.hashCode();
        }
    }

    public static class Mat4 implements IMatrix4f {

        private final Matrix4f mat;

        public Mat4(Matrix4f mat) {
            this.mat = mat;
        }

        @Override
        public void set(FloatBuffer buffer) {
            mat.set(buffer);
        }

        @Override
        public void get(FloatBuffer buffer) {
            mat.get(buffer);
        }

        @Override
        public void scale(float x, float y, float z) {
            mat.scale(x, y, z);
        }

        @Override
        public void translate(float x, float y, float z) {
            mat.translate(x, y, z);
        }

        @Override
        public void rotate(IQuaternionf quaternion) {
            mat.rotate(new Quaternionf(quaternion.i(), quaternion.j(), quaternion.k(), quaternion.r()));
        }

        @Override
        public void multiply(IMatrix4f matrix) {
            mat.mul(of(matrix));
        }

        @Override
        public void multiply(float[] values) {
            Vector4f vec = new Vector4f(values[0], values[1], values[2], values[3]);
            vec.mul(mat);
            values[0] = vec.x();
            values[1] = vec.y();
            values[2] = vec.z();
            values[3] = vec.w();
        }

        @Override
        public void invert() {
            mat.invert();
        }

        @Override
        public IMatrix4f copy() {
            return new Mat4(new Matrix4f(mat));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Mat4 that)) return false;
            return mat.equals(that.mat);
        }

        @Override
        public int hashCode() {
            return mat.hashCode();
        }
    }

    public interface Pose {

        IMatrix4f aw$getPose();

        IMatrix3f aw$getNormal();
    }
}
