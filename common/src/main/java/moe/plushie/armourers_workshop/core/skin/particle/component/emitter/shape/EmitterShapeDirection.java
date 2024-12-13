package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

public abstract class EmitterShapeDirection {

    public abstract void writeToStream(IOutputStream stream) throws IOException;

    public boolean isBuiltin() {
        return this instanceof Builtin;
    }

    /**
     * Particle direction is set to move towards the emitter
     */
    public static EmitterShapeDirection inwards() {
        return Builtin.INWARDS;
    }

    /**
     * Particle direction is set to move away from the emitter
     */
    public static EmitterShapeDirection outwards() {
        return Builtin.OUTWARDS;
    }

    /**
     * Set a custom direction vector in the direction field
     */
    public static EmitterShapeDirection custom(OpenPrimitive x, OpenPrimitive y, OpenPrimitive z) {
        return new Custom(x, y, z);
    }

    public static EmitterShapeDirection readFromStream(IInputStream stream) throws IOException {
        return switch (stream.readByte()) {
            case 0 -> Builtin.INWARDS;
            case 1 -> Builtin.OUTWARDS;
            default -> new Custom(stream);
        };
    }

    private static class Builtin extends EmitterShapeDirection {

        private static final EmitterShapeDirection INWARDS = new Builtin(0, -1.0);
        private static final EmitterShapeDirection OUTWARDS = new Builtin(1, +1.0);

        private final int type;
        private final double factor;

        private Builtin(int type, double factor) {
            this.factor = factor;
            this.type = type;
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            stream.writeByte(type);
        }


//        @Override
//        public void applyDirection(BedrockParticle particle, double x, double y, double z) {
//            Vector3d vector = new Vector3d(particle.position);
//
//            vector.sub(new Vector3d(x, y, z));
//
//            if (vector.length() <= 0)
//            {
//                vector.set(0, 0, 0);
//            }
//            else
//            {
//                vector.normalize();
//                vector.scale(this.factor);
//            }
//
//            particle.speed.set(vector);
//        }
    }

    private static class Custom extends EmitterShapeDirection {

        private final OpenPrimitive x;
        private final OpenPrimitive y;
        private final OpenPrimitive z;

        public Custom(OpenPrimitive x, OpenPrimitive y, OpenPrimitive z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Custom(IInputStream stream) throws IOException {
            this.x = stream.readPrimitiveObject();
            this.y = stream.readPrimitiveObject();
            this.z = stream.readPrimitiveObject();
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            stream.writeByte(0xff);
            stream.writePrimitiveObject(x);
            stream.writePrimitiveObject(y);
            stream.writePrimitiveObject(z);
        }

//        @Override
//        public void applyDirection(BedrockParticle particle, double x, double y, double z)
//        {
//            particle.speed.set((float) this.x.get(), (float) this.y.get(), (float) this.z.get());
//
//            if (particle.speed.length() <= 0)
//            {
//                particle.speed.set(0, 0, 0);
//            }
//            else
//            {
//                particle.speed.normalize();
//            }
//        }
    }
}
