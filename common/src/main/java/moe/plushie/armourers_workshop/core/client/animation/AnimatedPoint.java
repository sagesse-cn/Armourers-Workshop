package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.core.math.OpenVector3f;

public class AnimatedPoint {

    protected final OpenVector3f translate = new OpenVector3f();
    protected final OpenVector3f rotation = new OpenVector3f();
    protected final OpenVector3f scale = new OpenVector3f(1, 1, 1);

    protected int dirty = 0;

    public void setTranslate(OpenVector3f value) {
        setTranslate(value.x(), value.y(), value.z());
    }

    public void setTranslate(float x, float y, float z) {
        if (x != 0 || y != 0 || z != 0) {
            translate.set(x, y, z);
            setDirty(0x10);
        }
    }

    public OpenVector3f getTranslate() {
        if ((dirty & 0x10) != 0) {
            return translate;
        }
        return OpenVector3f.ZERO;
    }

    public void setRotation(OpenVector3f value) {
        setRotation(value.x(), value.y(), value.z());
    }

    public void setRotation(float x, float y, float z) {
        if (x != 0 || y != 0 || z != 0) {
            rotation.set(x, y, z);
            setDirty(0x20);
        }
    }

    public OpenVector3f getRotation() {
        if ((dirty & 0x20) != 0) {
            return rotation;
        }
        return OpenVector3f.ZERO;
    }

    public void setScale(OpenVector3f value) {
        setScale(value.x(), value.y(), value.z());
    }

    public void setScale(float x, float y, float z) {
        if (x != 1 || y != 1 || z != 1) {
            scale.set(x, y, z);
            setDirty(0x40);
        }
    }

    public OpenVector3f getScale() {
        if ((dirty & 0x40) != 0) {
            return scale;
        }
        return OpenVector3f.ONE;
    }

    public void clear() {
        dirty = 0x00;
    }

    public void setDirty(int newFlags) {
        dirty |= newFlags;
    }
}
