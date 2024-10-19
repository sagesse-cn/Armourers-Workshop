package moe.plushie.armourers_workshop.core.skin.transformer.blockbench;

import moe.plushie.armourers_workshop.utils.math.Vector3f;

public class BlockBenchDisplay {

    private final Vector3f translation;
    private final Vector3f rotation;
    private final Vector3f scale;

    public BlockBenchDisplay(Vector3f translation, Vector3f rotation, Vector3f scale) {
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Vector3f getTranslation() {
        return translation;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public static class Builder {

        private Vector3f translation = Vector3f.ZERO;
        private Vector3f rotation = Vector3f.ZERO;
        private Vector3f scale = Vector3f.ONE;

        public void translation(Vector3f translation) {
            this.translation = translation;
        }

        public void rotation(Vector3f rotation) {
            this.rotation = rotation;
        }

        public void scale(Vector3f scale) {
            this.scale = scale;
        }

        public BlockBenchDisplay build() {
            return new BlockBenchDisplay(translation, rotation, scale);
        }
    }
}
