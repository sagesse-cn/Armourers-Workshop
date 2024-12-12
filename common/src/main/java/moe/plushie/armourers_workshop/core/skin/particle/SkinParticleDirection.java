package moe.plushie.armourers_workshop.core.skin.particle;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

public abstract class SkinParticleDirection {

    public abstract void writeToStream(IOutputStream stream) throws IOException;

    /**
     * Particle direction is set to move towards the emitter
     */
    public static SkinParticleDirection inwards() {
        return Builtin.INWARDS;
    }

    /**
     * Particle direction is set to move away from the emitter
     */
    public static SkinParticleDirection outwards() {
        return Builtin.OUTWARDS;
    }

    /**
     * Set a custom direction vector in the direction field
     */
    public static SkinParticleDirection custom(OpenPrimitive x, OpenPrimitive y, OpenPrimitive z) {
        return new Custom(x, y, z);
    }

    public static SkinParticleDirection readFromStream(IInputStream stream) {
        return Builtin.OUTWARDS;
    }

    private static class Builtin extends SkinParticleDirection {

        private static final SkinParticleDirection INWARDS = new Builtin();
        private static final SkinParticleDirection OUTWARDS = new Builtin();

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {

        }
    }

    private static class Custom extends SkinParticleDirection {
        private final OpenPrimitive x;
        private final OpenPrimitive y;
        private final OpenPrimitive z;

        private Custom(OpenPrimitive x, OpenPrimitive y, OpenPrimitive z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {

        }
    }
}
