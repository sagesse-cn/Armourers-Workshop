package moe.plushie.armourers_workshop.core.math;

@SuppressWarnings("unused")
public class OpenNearPlane {

    private final float fov;
    private final float width;
    private final float height;

    private final OpenVector3f forwards = new OpenVector3f(0, 0, 1);
    private final OpenVector3f up = new OpenVector3f(0, 1, 0);
    private final OpenVector3f left = new OpenVector3f(1, 0, 0);

    public OpenNearPlane(float rx, float ry, float width, float height, float fov) {
        var quaternion = OpenQuaternionf.fromEulerAnglesYXZ(-ry, rx, 0.0f, true);
        this.forwards.transform(quaternion);
        this.up.transform(quaternion);
        this.left.transform(quaternion);
        this.fov = fov;
        this.width = width;
        this.height = height;
    }

    public OpenVector3f at(float deltaX, float deltaY, float deltaZ) {
        float d0 = width / height;
        float d1 = (float) Math.tan((fov / 2.0) * (Math.PI / 180));

        float sx = deltaX * deltaZ * d1 * d0;
        float sy = deltaY * deltaZ * d1;
        float sz = deltaZ;

        // (forwards * sz) + (up * sy) - (left * sx)
        float tx = forwards.x() * sz + up.x() * sy - left.x() * sx;
        float ty = forwards.y() * sz + up.y() * sy - left.y() * sx;
        float tz = forwards.z() * sz + up.z() * sy - left.z() * sx;

        return new OpenVector3f(tx, ty, tz);
    }

    public OpenVector3f lookVector() {
        return forwards;
    }

    public OpenVector3f upVector() {
        return up;
    }

    public OpenVector3f leftVector() {
        return left;
    }
}
