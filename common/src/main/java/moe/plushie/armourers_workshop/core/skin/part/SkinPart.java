package moe.plushie.armourers_workshop.core.skin.part;

import moe.plushie.armourers_workshop.api.core.math.ITransform;
import moe.plushie.armourers_workshop.api.skin.part.ISkinPart;
import moe.plushie.armourers_workshop.api.skin.part.ISkinPartType;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.Rectangle3f;
import moe.plushie.armourers_workshop.core.math.Vector3i;
import moe.plushie.armourers_workshop.core.skin.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometrySet;
import moe.plushie.armourers_workshop.core.skin.geometry.collection.SkinGeometrySetV0;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.utils.Objects;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkinPart implements ISkinPart {

    protected final String name;

    protected final ISkinPartType type;
    protected final ITransform transform;

    protected final SkinGeometrySet<?> geometries;

    protected final List<SkinPart> children = new ArrayList<>();
    protected final List<SkinMarker> markerBlocks = new ArrayList<>();

    protected final Object blobs;

    protected SkinProperties properties = SkinProperties.EMPTY;

    private HashMap<Vector3i, Rectangle3f> blockBounds;


    protected SkinPart(String name, ISkinPartType type, ITransform transform, SkinGeometrySet<?> geometries, List<SkinMarker> markers, Object blobs) {
        this.name = name;

        this.type = type;
        this.transform = transform;

        this.geometries = geometries;
        this.geometries.getUsedCounter().addMarkers(markers.size());

        this.markerBlocks.addAll(markers);

        this.blobs = blobs;
    }

    public void addPart(SkinPart part) {
        children.add(part);
    }

    public void removePart(SkinPart part) {
        children.remove(part);
    }

    public void setProperties(SkinProperties properties) {
        this.properties = properties;
    }

    public SkinProperties getProperties() {
        return properties;
    }

    public int getModelCount() {
        return 0;
    }

    public Map<Vector3i, Rectangle3f> getBlockBounds() {
        if (blockBounds != null) {
            return blockBounds;
        }
        var blockGrid = new HashMap<Vector3i, Rectangle3f>();
        blockBounds = new HashMap<>();
        geometries.forEach(geometry -> {
            var boundingBox = geometry.getShape().bounds();
            var x = boundingBox.getX();
            var y = boundingBox.getY();
            var z = boundingBox.getZ();
            var tx = OpenMath.floori((x + 8) / 16f);
            var ty = OpenMath.floori((y + 8) / 16f);
            var tz = OpenMath.floori((z + 8) / 16f);
            var rec = new Rectangle3f(-(x - tx * 16) - 1, -(y - ty * 16) - 1, z - tz * 16, 1, 1, 1);
            blockGrid.computeIfAbsent(new Vector3i(-tx, -ty, tz), k -> rec).union(rec);
        });
        blockGrid.forEach((key, value) -> blockBounds.put(key, value));
        return blockBounds;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Override
    public ISkinPartType getType() {
        return this.type;
    }

    @Override
    public ITransform getTransform() {
        return transform;
    }

    @Override
    public SkinGeometrySet<?> getGeometries() {
        return geometries;
    }

    @Override
    public List<SkinPart> getChildren() {
        return children;
    }

    @Override
    public List<SkinMarker> getMarkers() {
        return markerBlocks;
    }

    public Object getBlobs() {
        return blobs;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "name", name, "type", type, "transform", transform, "markers", markerBlocks, "cubes", geometries);
    }

    public static class Builder {

        private final ISkinPartType type;

        private String name;
        private SkinGeometrySet<?> geometries = SkinGeometrySetV0.EMPTY;
        private ITransform transform = OpenTransform3f.IDENTITY;
        private ArrayList<SkinMarker> markers = new ArrayList<>();
        private ArrayList<SkinPart> children = new ArrayList<>();
        private SkinProperties properties;
        private Object blobs;

        public Builder(ISkinPartType type) {
            this.type = type;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder transform(ITransform transform) {
            if (transform != null) {
                this.transform = transform;
            }
            return this;
        }

        public Builder geometries(SkinGeometrySet<?> geometries) {
            this.geometries = geometries;
            return this;
        }

        public Builder markers(List<SkinMarker> markers) {
            if (markers != null) {
                this.markers = new ArrayList<>(markers);
            }
            return this;
        }

        public Builder children(List<SkinPart> children) {
            if (children != null) {
                this.children = new ArrayList<>(children);
            }
            return this;
        }

        public Builder properties(SkinProperties properties) {
            this.properties = properties;
            return this;
        }

        public Builder blobs(Object blobs) {
            this.blobs = blobs;
            return this;
        }

        public SkinPart build() {
            var skinPart = new SkinPart(name, type, transform, geometries, markers, blobs);
            children.forEach(skinPart::addPart);
            return skinPart;
        }
    }
}
