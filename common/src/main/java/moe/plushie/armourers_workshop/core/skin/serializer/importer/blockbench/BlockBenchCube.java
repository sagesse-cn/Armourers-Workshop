package moe.plushie.armourers_workshop.core.skin.serializer.importer.blockbench;

import moe.plushie.armourers_workshop.core.math.Vector2f;
import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;

import java.util.HashMap;
import java.util.Map;

public class BlockBenchCube extends BlockBenchElement {

    private final boolean boxUV;
    private final boolean mirrorUV;
    private final boolean allowMirrorModeling;

    private final Vector2f uvOffset;

    private final Vector3f from;
    private final Vector3f to;

    private final Vector3f origin;
    private final Vector3f rotation;
    private final float inflate;

    private final Map<OpenDirection, BlockBenchCubeFace> faces;

    public BlockBenchCube(String uuid, String name, String type, boolean allowExport, boolean allowMirrorModeling, boolean boxUV, boolean mirrorUV, Vector2f uvOffset, Vector3f from, Vector3f to, Vector3f origin, Vector3f rotation, float inflate, Map<OpenDirection, BlockBenchCubeFace> faces) {
        super(uuid, name, type, allowExport);
        this.boxUV = boxUV;
        this.mirrorUV = mirrorUV;
        this.allowMirrorModeling = allowMirrorModeling;
        this.uvOffset = uvOffset;
        this.from = from;
        this.to = to;
        this.origin = origin;
        this.rotation = rotation;
        this.inflate = inflate;
        this.faces = faces;
    }

    public Vector3f getFrom() {
        return from;
    }

    public Vector3f getTo() {
        return to;
    }

    public Vector3f getOrigin() {
        return origin;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public float getInflate() {
        return inflate;
    }

    public boolean isBoxUV() {
        return boxUV;
    }

    public boolean isMirrorUV() {
        return mirrorUV;
    }

    public Vector2f getUVOffset() {
        return uvOffset;
    }

    public Map<OpenDirection, BlockBenchCubeFace> getFaces() {
        return faces;
    }

    protected static class Builder extends BlockBenchElement.Builder {

        protected boolean boxUV = false;
        protected boolean mirrorUV = false;
        protected boolean allowMirrorModeling = false;

        protected Vector2f uvOffset = Vector2f.ZERO;

        protected Vector3f from = Vector3f.ZERO;
        protected Vector3f to = Vector3f.ZERO;

        protected Vector3f origin = Vector3f.ZERO;
        protected Vector3f rotation = Vector3f.ZERO;

        protected float inflate = 0;

        protected final Map<OpenDirection, BlockBenchCubeFace> faces = new HashMap<>();

        public void boxUV(boolean boxUV) {
            this.boxUV = boxUV;
        }

        public void mirrorUV(boolean mirrorUV) {
            this.mirrorUV = mirrorUV;
        }

        public void allowMirrorModeling(boolean allowMirrorModeling) {
            this.allowMirrorModeling = allowMirrorModeling;
        }

        public void from(Vector3f from) {
            this.from = from;
        }

        public void to(Vector3f to) {
            this.to = to;
        }

        public void origin(Vector3f origin) {
            this.origin = origin;
        }

        public void rotation(Vector3f rotation) {
            this.rotation = rotation;
        }

        public void inflate(float inflate) {
            this.inflate = inflate;
        }

        public void uvOffset(Vector2f uvOffset) {
            this.uvOffset = uvOffset;
        }

        public void addFace(OpenDirection dir, BlockBenchCubeFace face) {
            this.faces.put(dir, face);
        }

        @Override
        public BlockBenchCube build() {
            return new BlockBenchCube(uuid, name, type, allowExport, allowMirrorModeling, boxUV, mirrorUV, uvOffset, from, to, origin, rotation, inflate, faces);
        }
    }
}
