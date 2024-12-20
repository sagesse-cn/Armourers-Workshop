package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.api.core.math.ITransform;
import moe.plushie.armourers_workshop.core.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;

import java.io.IOException;
import java.nio.FloatBuffer;

public class ChunkTransform {

    private static final float[] IDENTITY_MATRIX_BUFFER = new float[]{
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 0,
    };

    private static final float[] IDENTITY_VECTOR_BUFFER = new float[]{
            0, 0, 0, // translate
            0, 0, 0, // rotation
            1, 1, 1, // scale
            0, 0, 0, // offset
            0, 0, 0, // pivot
    };

    private OpenVector3f translate;
    private OpenVector3f rotation;
    private OpenVector3f scale;
    private OpenVector3f afterTranslate;
    private OpenVector3f pivot;

    private FloatBuffer buffer;

    public ChunkTransform() {
    }

    public ChunkTransform(OpenTransform3f transform) {
        if (transform.isIdentity()) {
            this.setIdentity();
            return;
        }
        this.translate = transform.translate();
        this.rotation = transform.rotation();
        this.scale = transform.scale();
        this.afterTranslate = transform.afterTranslate();
        this.pivot = transform.pivot();
    }

    public ChunkTransform(FloatBuffer buffer) {
        this.buffer = buffer;
    }

    public static ChunkTransform of(ITransform transform) {
        if (transform instanceof OpenTransform3f) {
            return new ChunkTransform((OpenTransform3f) transform);
        }
        return flat(transform);
    }

    public static ChunkTransform flat(ITransform transform) {
        var buffer = FloatBuffer.allocate(16);
        var poseStack = new OpenPoseStack();
        transform.apply(poseStack);
        poseStack.last().pose().store(buffer);
        return new ChunkTransform(buffer);
    }


    public void readFromStream(ChunkDataInputStream stream) throws IOException {
        int flags = stream.readByte();
        if ((flags & 0x10) != 0) {
            setIdentity();
            return;
        }
        if ((flags & 0x20) != 0) {
            buffer = FloatBuffer.allocate(16);
            readZippedBuffer(stream, buffer, IDENTITY_MATRIX_BUFFER);
            return;
        }
        var buffer = FloatBuffer.allocate(IDENTITY_VECTOR_BUFFER.length);
        readZippedBuffer(stream, buffer, IDENTITY_VECTOR_BUFFER);
        translate = readVector(buffer, 0);
        rotation = readVector(buffer, 3);
        scale = readVector(buffer, 6);
        afterTranslate = readVector(buffer, 9);
        pivot = readVector(buffer, 12);
    }

    public void writeToStream(ChunkDataOutputStream stream) throws IOException {
        if (isIdentity()) {
            stream.writeByte(0x10);
            return;
        }
        if (buffer != null) {
            stream.writeByte(0x20);
            writeZippedBuffer(stream, buffer, IDENTITY_MATRIX_BUFFER);
            return;
        }
        stream.writeByte(0x40);
        var buffer = FloatBuffer.allocate(IDENTITY_VECTOR_BUFFER.length);
        buffer.put(translate.x()).put(translate.y()).put(translate.z());
        buffer.put(rotation.x()).put(rotation.y()).put(rotation.z());
        buffer.put(scale.x()).put(scale.y()).put(scale.z());
        buffer.put(afterTranslate.x()).put(afterTranslate.y()).put(afterTranslate.z());
        buffer.put(pivot.x()).put(pivot.y()).put(pivot.z());
        buffer.rewind();
        writeZippedBuffer(stream, buffer, IDENTITY_VECTOR_BUFFER);
    }

    public void setIdentity() {
        pivot = null;
        translate = null;
        scale = null;
        rotation = null;
        buffer = null;
    }

    public boolean isIdentity() {
        return buffer == null && scale == null;
    }

    public ITransform build() {
        if (isIdentity()) {
            return OpenTransform3f.IDENTITY;
        }
        if (buffer != null) {
            var pose = new OpenMatrix4f(buffer);
            var normal = new OpenMatrix3f(buffer);
            return new FlatTransform(pose, normal);
        }
        return OpenTransform3f.create(translate, rotation, scale, pivot, afterTranslate);
    }

    private static OpenVector3f readVector(FloatBuffer buffer, int offset) {
        float x = buffer.get(offset);
        float y = buffer.get(offset + 1);
        float z = buffer.get(offset + 2);
        if (x == 0 && y == 0 && z == 0) {
            return OpenVector3f.ZERO;
        }
        if (x == 1 && y == 1 && z == 1) {
            return OpenVector3f.ONE;
        }
        return new OpenVector3f(x, y, z);
    }


    private static void readZippedBuffer(ChunkDataInputStream stream, FloatBuffer bufferOut, float[] bufferDef) throws IOException {
        int flags = stream.readShort();
        for (int i = 0; i < bufferDef.length; ++i) {
            if ((flags & (1 << i)) == 0) {
                bufferOut.put(i, stream.readFloat());
            } else {
                bufferOut.put(i, bufferDef[i]);
            }
        }
        bufferOut.rewind();
    }

    private static void writeZippedBuffer(ChunkDataOutputStream stream, FloatBuffer bufferIn, float[] bufferDef) throws IOException {
        int flags = 0;
        for (int i = 0; i < bufferDef.length; ++i) {
            if (bufferDef[i] == bufferIn.get(i)) {
                flags |= 1 << i;
            }
        }
        stream.writeShort(flags);
        for (int i = 0; i < bufferDef.length; ++i) {
            if ((flags & (1 << i)) == 0) {
                stream.writeFloat(bufferIn.get(i));
            }
        }
    }

    public static class FlatTransform implements ITransform {

        private final OpenMatrix4f pose;
        private final OpenMatrix3f normal;

        public FlatTransform(OpenMatrix4f pose, OpenMatrix3f normal) {
            this.pose = pose;
            this.normal = normal;
        }

        @Override
        public void apply(IPoseStack poseStack) {
            poseStack.multiply(normal);
            poseStack.multiply(pose);
        }
    }
}
