package com.apple.library.coregraphics;

import com.apple.library.impl.InterpolableImpl;

import java.util.Objects;

@SuppressWarnings("unused")
public class CGPoint implements InterpolableImpl<CGPoint> {

    public static final CGPoint ZERO = new CGPoint(0, 0);

    public float x;
    public float y;

    public CGPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public CGPoint(double x, double y) {
        this((float) x, (float) y);
    }

    public void apply(CGAffineTransform t) {
        float tx = t.a * x + t.c * y + t.tx;
        float ty = t.b * x + t.d * y + t.ty;
        this.x = tx;
        this.y = ty;
    }

    public CGPoint applying(CGAffineTransform t) {
        var pos = copy();
        pos.apply(t);
        return pos;
    }

    public CGPoint copy() {
        return new CGPoint(x, y);
    }

    @Override
    public CGPoint interpolating(CGPoint in, float t) {
        if (t <= 0) {
            return this;
        }
        if (t >= 1) {
            return in;
        }
        float v = 1 - t;
        float x = v * this.x + t * in.x;
        float y = v * this.y + t * in.y;
        return new CGPoint(x, y);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CGPoint that)) return false;
        return Float.compare(x, that.x) == 0 && Float.compare(y, that.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("(%f %f)", x, y);
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }
}

