package com.apple.library.coregraphics;

import com.apple.library.impl.InterpolableImpl;

import java.util.Objects;

@SuppressWarnings("unused")
public class CGSize implements InterpolableImpl<CGSize> {

    public static final CGSize ZERO = new CGSize(0, 0);

    public float width;
    public float height;

    public CGSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void apply(CGAffineTransform t) {
        float w = t.a * width + t.c * height;
        float h = t.b * width + t.d * height;
        this.width = w;
        this.height = h;
    }

    public CGSize applying(CGAffineTransform t) {
        var size = copy();
        size.apply(t);
        return size;
    }

    public CGSize copy() {
        return new CGSize(width, height);
    }

    @Override
    public CGSize interpolating(CGSize in, float t) {
        if (t <= 0) {
            return this;
        }
        if (t >= 1) {
            return in;
        }
        float v = 1 - t;
        float w = v * this.width + t * in.width;
        float h = v * this.height + t * in.height;
        return new CGSize(w, h);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CGSize that)) return false;
        return Float.compare(width, that.width) == 0 && Float.compare(height, that.height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }

    @Override
    public String toString() {
        return String.format("(%f %f)", width, height);
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float width() {
        return width;
    }

    public float height() {
        return height;
    }
}
