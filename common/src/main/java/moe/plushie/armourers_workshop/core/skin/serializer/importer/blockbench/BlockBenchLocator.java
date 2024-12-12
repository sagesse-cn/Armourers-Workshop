package moe.plushie.armourers_workshop.core.skin.serializer.importer.blockbench;

import moe.plushie.armourers_workshop.core.math.Vector3f;

public class BlockBenchLocator extends BlockBenchElement {

    private final Vector3f rotation;
    private final Vector3f position;

    public BlockBenchLocator(String uuid, String name, String type, boolean allowExport, Vector3f rotation, Vector3f position) {
        super(uuid, name, type, allowExport);
        this.rotation = rotation;
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getPosition() {
        return position;
    }

    protected static class Builder extends BlockBenchElement.Builder {

        protected Vector3f rotation = Vector3f.ZERO;
        protected Vector3f position = Vector3f.ZERO;

        public void rotation(Vector3f rotation) {
            this.rotation = rotation;
        }

        public void position(Vector3f position) {
            this.position = position;
        }

        @Override
        public BlockBenchLocator build() {
            return new BlockBenchLocator(uuid, name, type, allowExport, rotation, position);
        }
    }
}
