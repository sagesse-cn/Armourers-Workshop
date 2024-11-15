package moe.plushie.armourers_workshop.core.skin.animation;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOFunction;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.Map;

public abstract class SkinAnimationPoint {

    private static final Map<Integer, Pair<Class<?>, IOFunction<IInputStream, SkinAnimationPoint>>> CODERS = Collections.immutableMap(builder -> {
        builder.put(8, Pair.of(Bone.class, Bone::new));
        builder.put(9, Pair.of(Instruction.class, Instruction::new));
    });

    public static int getType(SkinAnimationPoint point) {
        for (var entry : CODERS.entrySet()) {
            if (entry.getValue().getKey().isInstance(point)) {
                return entry.getKey();
            }
        }
        return 0;
    }

    public static IOFunction<IInputStream, SkinAnimationPoint> getDecoder(int type) {
        var entry = CODERS.get(type);
        if (entry != null) {
            return entry.getValue();
        }
        return null;
    }

    public abstract void writeToStream(IOutputStream stream) throws IOException;

    public static class Bone extends SkinAnimationPoint {

        private final Object x;
        private final Object y;
        private final Object z;

        public Bone(Object x, Object y, Object z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Bone(IInputStream stream) throws IOException {
            this.x = readField(stream);
            this.y = readField(stream);
            this.z = readField(stream);
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            writeField(x, stream);
            writeField(y, stream);
            writeField(z, stream);
        }

        public Object getX() {
            return x;
        }

        public Object getY() {
            return y;
        }

        public Object getZ() {
            return z;
        }

        @Override
        public String toString() {
            return Objects.toString(this, "x", x, "y", y, "z", z);
        }

        private static Object readField(IInputStream stream) throws IOException {
            var len = stream.readVarInt();
            if (len == 0) {
                return stream.readFloat();
            }
            return stream.readString(len);
        }

        private static void writeField(Object value, IOutputStream stream) throws IOException {
            if (value instanceof String script) {
                stream.writeVarInt(script.length());
                stream.writeString(script, script.length());
            } else if (value instanceof Number number) {
                stream.writeVarInt(0);
                stream.writeFloat(number.floatValue());
            } else {
                stream.writeVarInt(0);
                stream.writeFloat(0);
            }
        }
    }

    public static class Instruction extends SkinAnimationPoint {

        private final String script;

        public Instruction(String script) {
            this.script = script;
        }

        public Instruction(IInputStream stream) throws IOException {
            this.script = stream.readString();
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            stream.writeString(script);
        }

        public String getScript() {
            return script;
        }

        @Override
        public String toString() {
            return Objects.toString(this, "script", script);
        }
    }
}
