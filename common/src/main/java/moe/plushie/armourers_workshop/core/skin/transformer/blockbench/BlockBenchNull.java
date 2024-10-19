package moe.plushie.armourers_workshop.core.skin.transformer.blockbench;

import moe.plushie.armourers_workshop.utils.math.Vector3f;

public class BlockBenchNull extends BlockBenchElement {

    private final Vector3f position;

    public BlockBenchNull(String uuid, String name, String type, boolean allowExport, Vector3f position) {
        super(uuid, name, type, allowExport);
        this.position = position;
    }

    public Vector3f getPosition() {
        return position;
    }

    public static class Builder extends BlockBenchElement.Builder {

        protected Vector3f position = Vector3f.ZERO;

        public void position(Vector3f position) {
            this.position = position;
        }

        @Override
        public BlockBenchNull build() {
            return new BlockBenchNull(uuid, name, type, allowExport, position);
        }
    }
}
