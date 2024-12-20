package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.api.core.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.core.math.IRectangle3f;
import moe.plushie.armourers_workshop.api.core.math.IRectangle3i;
import moe.plushie.armourers_workshop.api.core.math.IVector3f;
import moe.plushie.armourers_workshop.api.core.math.IVoxelShape;
import moe.plushie.armourers_workshop.core.utils.Collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

@SuppressWarnings("unused")
public class OpenVoxelShape implements IVoxelShape, Iterable<OpenVector4f> {

    private OpenAxisAlignedBoundingBox aabb;
    private OpenRectangle3f box;
    private List<OpenVector4f> vertexes;

    public OpenVoxelShape() {
    }

    public static OpenVoxelShape empty() {
        return new OpenVoxelShape();
    }


    public static OpenVoxelShape box(IRectangle3f bounds) {
        if (bounds instanceof OpenRectangle3f rect) {
            return box(rect);
        }
        return box(new OpenRectangle3f(bounds));
    }

    public static OpenVoxelShape box(IRectangle3i bounds) {
        return box(new OpenRectangle3f(bounds));
    }

    public static OpenVoxelShape box(OpenRectangle3f bounds) {
        var shape = new OpenVoxelShape();
        shape.box = bounds;
        return shape;
    }

    public OpenAxisAlignedBoundingBox aabb() {
        if (aabb != null) {
            return aabb;
        }
        aabb = new OpenAxisAlignedBoundingBox(bounds());
        return aabb;
    }

    public OpenRectangle3f bounds() {
        if (box != null) {
            return box;
        }
        if (vertexes == null || vertexes.isEmpty()) {
            return OpenRectangle3f.ZERO;
        }
        var iterator = vertexes.iterator();
        var fp = iterator.next();
        float minX = fp.x(), minY = fp.y(), minZ = fp.z();
        float maxX = fp.x(), maxY = fp.y(), maxZ = fp.z();
        while (iterator.hasNext()) {
            var point = iterator.next();
            minX = Math.min(minX, point.x());
            minY = Math.min(minY, point.y());
            minZ = Math.min(minZ, point.z());
            maxX = Math.max(maxX, point.x());
            maxY = Math.max(maxY, point.y());
            maxZ = Math.max(maxZ, point.z());
        }
        box = new OpenRectangle3f(minX, minY, minZ, maxX - minX, maxY - minY, maxZ - minZ);
        return box;
    }

    public void mul(IMatrix4f matrix) {
        for (var vector : getVertexes()) {
            vector.transform(matrix);
        }
        box = null;
        aabb = null;
    }

    public void add(float x, float y, float z, float width, float height, float depth) {
        var list = getVertexes();
        list.add(new OpenVector4f(x, y, z, 1.0f));
        list.add(new OpenVector4f(x + width, y, z, 1.0f));
        list.add(new OpenVector4f(x + width, y + height, z, 1.0f));
        list.add(new OpenVector4f(x, y + height, z, 1.0f));
        list.add(new OpenVector4f(x, y, z + depth, 1.0f));
        list.add(new OpenVector4f(x + width, y, z + depth, 1.0f));
        list.add(new OpenVector4f(x + width, y + height, z + depth, 1.0f));
        list.add(new OpenVector4f(x, y + height, z + depth, 1.0f));
        box = null;
        aabb = null;
    }

    public void add(OpenVoxelShape shape1) {
        var list = getVertexes();
        list.addAll(shape1.getVertexes());
        box = null;
    }

    public void add(IRectangle3f rect) {
        add(rect.x(), rect.y(), rect.z(), rect.width(), rect.height(), rect.depth());
    }

    public void add(IVector3f vertex) {
        add(new OpenVector4f(vertex.x(), vertex.y(), vertex.z(), 1.0f));
    }

    public void add(OpenVector4f vertex) {
        var list = getVertexes();
        list.add(vertex);
        box = null;
    }

    public boolean isEmpty() {
        return vertexes == null && box == null;
    }

    public void optimize() {
        if (vertexes == null || vertexes.size() <= 8) {
            return;
        }
        var list = getVertexes();
        var uniquesVertexes = new LinkedHashSet<OpenVector4f>(list.size());
        uniquesVertexes.addAll(list);
        vertexes = Collections.newList(uniquesVertexes);
    }

    public OpenVoxelShape copy() {
        var shape = new OpenVoxelShape();
        shape.box = box;
        shape.aabb = aabb;
        if (vertexes != null) {
            var newVertexes = new ArrayList<OpenVector4f>();
            newVertexes.ensureCapacity(vertexes.size());
            for (var vector : vertexes) {
                newVertexes.add(vector.copy());
            }
            shape.vertexes = newVertexes;
        }
        return shape;
    }

    @Override
    public Iterator<OpenVector4f> iterator() {
        if (vertexes != null) {
            return vertexes.iterator();
        }
        return getVertexes(box).iterator();
    }

    private List<OpenVector4f> getVertexes() {
        if (vertexes == null) {
            vertexes = getVertexes(box);
        }
        return vertexes;
    }

    private List<OpenVector4f> getVertexes(IRectangle3f box) {
        if (box == null) {
            return Collections.newList();
        }
        return Collections.newList(
                new OpenVector4f(box.minX(), box.minY(), box.minZ(), 1.0f),
                new OpenVector4f(box.maxX(), box.minY(), box.minZ(), 1.0f),
                new OpenVector4f(box.maxX(), box.maxY(), box.minZ(), 1.0f),
                new OpenVector4f(box.minX(), box.maxY(), box.minZ(), 1.0f),
                new OpenVector4f(box.minX(), box.minY(), box.maxZ(), 1.0f),
                new OpenVector4f(box.maxX(), box.minY(), box.maxZ(), 1.0f),
                new OpenVector4f(box.maxX(), box.maxY(), box.maxZ(), 1.0f),
                new OpenVector4f(box.minX(), box.maxY(), box.maxZ(), 1.0f)
        );
    }
}
