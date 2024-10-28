package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.api.core.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.core.math.IMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class MatrixUtils {

    private static final FloatBuffer BUFFER3x3 = createFloatBuffer(9);
    private static final FloatBuffer BUFFER4x4 = createFloatBuffer(16);


    public static ByteBuffer createByteBuffer(int capacity) {
        return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
    }

    public static FloatBuffer createFloatBuffer(int capacity) {
        return createByteBuffer(Float.BYTES * capacity).asFloatBuffer();
    }

    public static OpenMatrix4f createPoseMatrix(FloatBuffer buffer) {
        return new OpenMatrix4f(buffer);
    }

    public static OpenMatrix3f createNormalMatrix(FloatBuffer buffer) {
        return new OpenMatrix3f(buffer);
    }

    public static void set(IMatrix3f matrixIn, IMatrix3f matrixOut) {
        matrixIn.store(BUFFER3x3);
        matrixOut.load(BUFFER3x3);
    }

    public static void set(IMatrix4f matrixIn, IMatrix4f matrixOut) {
        matrixIn.store(BUFFER4x4);
        matrixOut.load(BUFFER4x4);
    }
}
