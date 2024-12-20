package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.api.core.math.IMatrix4f;

public class OpenRay {

    public OpenVector3f origin;
    public OpenVector3f direction;

    public OpenRay(OpenVector3f origin, OpenVector3f direction) {
        this.origin = origin;
        this.direction = direction;
    }

    public boolean intersects(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        // https://web.archive.org/web/20240901111623/https://tavianator.com/2011/ray_box.html
        // https://web.archive.org/web/20240718070324/https://tavianator.com/2015/ray_box_nan.html
        float ix = 1.0f / direction.x();
        float iy = 1.0f / direction.y();
        float iz = 1.0f / direction.z();

        float t1 = (minX - origin.x()) * ix;
        float t2 = (maxX - origin.x()) * ix;
        float t3 = (minY - origin.y()) * iy;
        float t4 = (maxY - origin.y()) * iy;
        float t5 = (minZ - origin.z()) * iz;
        float t6 = (maxZ - origin.z()) * iz;

        float tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        float tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        // if tmax < 0, ray (line) is intersecting AABB, but the whole AABB is behind us.
        // if tmin > tmax, ray doesn't intersect AABB.
        return tmax >= 0 && tmax >= tmin;
    }

    public void transform(IMatrix4f matrix) {
        float[] v1 = {origin.x(), origin.y(), origin.z(), 1f};
        float[] v2 = {direction.x(), direction.y(), direction.z(), 0f};
        matrix.multiply(v1);
        matrix.multiply(v2);
        this.origin = new OpenVector3f(v1);
        this.direction = new OpenVector3f(v2);
    }

    public OpenRay transforming(IMatrix4f matrix) {
        OpenRay ret = copy();
        ret.transform(matrix);
        return ret;
    }

    public OpenRay copy() {
        return new OpenRay(origin, direction);
    }
}
