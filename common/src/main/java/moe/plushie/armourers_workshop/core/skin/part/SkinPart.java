package moe.plushie.armourers_workshop.core.skin.part;

import moe.plushie.armourers_workshop.api.skin.ISkinPart;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinTransform;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.skin.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.cube.impl.SkinCubesV0;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkinPart implements ISkinPart {

    protected String name;
    protected ISkinPartType type;

    protected ISkinTransform transform = SkinTransform.IDENTITY;
    protected SkinProperties properties = SkinProperties.EMPTY;

    private SkinCubes cubeData;

    private HashMap<BlockPos, Rectangle3i> blockBounds;

    private final List<SkinPart> children = new ArrayList<>();
    private final List<SkinMarker> markerBlocks = new ArrayList<>();

    private final Object blobs;

    protected SkinPart(ISkinPartType type, List<SkinMarker> markers, SkinCubes cubes, Object blobs) {
        this.type = type;

        this.cubeData = cubes;
        this.cubeData.getUsedCounter().addMarkers(markers.size());

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

    public Map<BlockPos, Rectangle3i> getBlockBounds() {
        if (blockBounds != null) {
            return blockBounds;
        }
        var blockGrid = new HashMap<Long, Rectangle3i>();
        blockBounds = new HashMap<>();
        cubeData.forEach(cube -> {
            var pos = cube.getPosition();
            var x = pos.getX();
            var y = pos.getY();
            var z = pos.getZ();
            var tx = MathUtils.floor((x + 8) / 16f);
            var ty = MathUtils.floor((y + 8) / 16f);
            var tz = MathUtils.floor((z + 8) / 16f);
            var key = BlockPos.asLong(-tx, -ty, tz);
            var rec = new Rectangle3i(-(x - tx * 16) - 1, -(y - ty * 16) - 1, z - tz * 16, 1, 1, 1);
            blockGrid.computeIfAbsent(key, k -> rec).union(rec);
        });
        blockGrid.forEach((key, value) -> blockBounds.put(BlockPos.of(key), value));
        return blockBounds;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setType(ISkinPartType type) {
        this.type = type;
    }

    @Override
    public ISkinPartType getType() {
        return this.type;
    }

    public void setTransform(ISkinTransform transform) {
        this.transform = transform;
    }

    public ISkinTransform getTransform() {
        return transform;
    }

    public void setCubeData(SkinCubes cubeData) {
        this.cubeData = cubeData;
    }

    public SkinCubes getCubeData() {
        return cubeData;
    }

    @Override
    public List<SkinPart> getParts() {
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
        return ObjectUtils.makeDescription(this, "name", name, "type", type, "transform", transform, "markers", markerBlocks, "cubes", cubeData);
    }

    public static class Builder {

        private final ISkinPartType type;

        private String name;
        private SkinCubes cubes = SkinCubesV0.EMPTY;
        private ISkinTransform transform = SkinTransform.IDENTITY;
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

        public Builder transform(ISkinTransform transform) {
            if (transform != null) {
                this.transform = transform;
            }
            return this;
        }

        public Builder cubes(SkinCubes cubes) {
            this.cubes = cubes;
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
            var skinPart = new SkinPart(type, markers, cubes, blobs);
            skinPart.setName(name);
            skinPart.setTransform(transform);
            children.forEach(skinPart::addPart);
            return skinPart;
        }
    }
}
