package moe.plushie.armourers_workshop.core.skin.animation;

import moe.plushie.armourers_workshop.core.skin.sound.SoundData;
import moe.plushie.armourers_workshop.core.utils.Objects;

public abstract class SkinAnimationPoint {

    public static class Bone extends SkinAnimationPoint {

        private final Object x;
        private final Object y;
        private final Object z;

        public Bone(Object x, Object y, Object z) {
            this.x = x;
            this.y = y;
            this.z = z;
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
    }

    public static class Instruct extends SkinAnimationPoint {

        private final String script;

        public Instruct(String script) {
            this.script = script;
        }

        public String getScript() {
            return script;
        }

        @Override
        public String toString() {
            return Objects.toString(this, "script", script);
        }
    }

    public static class Sound extends SkinAnimationPoint {

        private final String effect;
        private final SoundData provider;

        public Sound(String effect, SoundData provider) {
            this.effect = effect;
            this.provider = provider;
        }

        public String getEffect() {
            return effect;
        }

        public SoundData getProvider() {
            return provider;
        }

        @Override
        public String toString() {
            return Objects.toString(this, "effect", effect, "sound", provider);
        }
    }

    public static class Particle extends SkinAnimationPoint {

        @Override
        public String toString() {
            return Objects.toString(this);
        }
    }
}
