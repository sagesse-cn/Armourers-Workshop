package com.apple.library.coregraphics;

import com.apple.library.impl.InterpolableImpl;
import com.apple.library.uikit.UIEdgeInsets;

import java.util.Objects;

@SuppressWarnings("unused")
public class CGRect implements InterpolableImpl<CGRect> {

    public static final CGRect ZERO = new CGRect(0, 0, 0, 0);

    public float x;
    public float y;
    public float width;
    public float height;

    public CGRect(CGRect rect) {
        this(rect.x, rect.y, rect.width, rect.height);
    }

    public CGRect(CGPoint point, CGSize size) {
        this(point.x, point.y, size.width, size.height);
    }

    public CGRect(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void apply(CGAffineTransform t) {
        var tl = new CGPoint(minX(), minY());
        var tr = new CGPoint(maxX(), minY());
        var bl = new CGPoint(minX(), maxY());
        var br = new CGPoint(maxX(), maxY());
        tl.apply(t);
        tr.apply(t);
        bl.apply(t);
        br.apply(t);
        float minX = Math.min(Math.min(tl.x(), tr.x()), Math.min(bl.x(), br.x()));
        float minY = Math.min(Math.min(tl.y(), tr.y()), Math.min(bl.y(), br.y()));
        float maxX = Math.max(Math.max(tl.x(), tr.x()), Math.max(bl.x(), br.x()));
        float maxY = Math.max(Math.max(tl.y(), tr.y()), Math.max(bl.y(), br.y()));
        this.x = minX;
        this.y = minY;
        this.width = maxX - minX;
        this.height = maxY - minY;
    }

    public CGRect applying(CGAffineTransform t) {
        var rect = copy();
        rect.apply(t);
        return rect;
    }

    public CGRect intersection(CGRect r) {
        float tx1 = this.x;
        float ty1 = this.y;
        float rx1 = r.x;
        float ry1 = r.y;
        double tx2 = tx1;
        tx2 += this.width;
        double ty2 = ty1;
        ty2 += this.height;
        double rx2 = rx1;
        rx2 += r.width;
        double ry2 = ry1;
        ry2 += r.height;
        if (tx1 < rx1) tx1 = rx1;
        if (ty1 < ry1) ty1 = ry1;
        if (tx2 > rx2) tx2 = rx2;
        if (ty2 > ry2) ty2 = ry2;
        tx2 -= tx1;
        ty2 -= ty1;
        // tx2,ty2 will never overflow (they will never be
        // larger than the smallest of the two source w,h)
        // they might underflow, though...
        if (tx2 < Integer.MIN_VALUE) tx2 = Integer.MIN_VALUE;
        if (ty2 < Integer.MIN_VALUE) ty2 = Integer.MIN_VALUE;
        return new CGRect(tx1, ty1, (float) tx2, (float) ty2);
    }

    public boolean intersects(CGRect rect) {
        return intersects(rect.x, rect.y, rect.width, rect.height);
    }

    public boolean intersects(double x, double y, double w, double h) {
        if (w <= 0 || h <= 0) {
            return false;
        }
        double x0 = x();
        double y0 = y();
        return (x + w > x0 && y + h > y0 && x < x0 + width() && y < y0 + height());
    }

    public CGRect offset(CGPoint point) {
        return offset(point.x, point.y);
    }

    public CGRect offset(float dx, float dy) {
        return new CGRect(x + dx, y + dy, width, height);
    }

    public CGRect insetBy(UIEdgeInsets insets) {
        return insetBy(insets.top, insets.left, insets.bottom, insets.right);
    }

    public CGRect insetBy(float top, float left, float bottom, float right) {
        float x0 = x + left;
        float x1 = x + width - right;
        float y0 = y + top;
        float y1 = y + height - bottom;
        return new CGRect(x0, y0, Math.max(x1 - x0, 0), Math.max(y1 - y0, 0));
    }

    public boolean contains(CGPoint point) {
        return contains(point.x, point.y);
    }

    public boolean contains(double x, double y) {
        double x0 = x();
        double y0 = y();
        return (x >= x0 && y >= y0 && x <= x0 + width() && y <= y0 + height());
    }

    private boolean contains(float x, float y) {
        float x0 = x();
        float y0 = y();
        return (x >= x0 && y >= y0 && x < x0 + width() && y < y0 + height());
    }

    public CGRect copy() {
        return new CGRect(x, y, width, height);
    }

    @Override
    public CGRect interpolating(CGRect in, float t) {
        if (t <= 0) {
            return this;
        }
        if (t >= 1) {
            return in;
        }
        float v = 1 - t;
        float x = v * this.x + t * in.y;
        float y = v * this.x + t * in.y;
        float w = v * this.width + t * in.width;
        float h = v * this.height + t * in.height;
        return new CGRect(x, y, w, h);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CGRect that)) return false;
        return Float.compare(x, that.x) == 0 && Float.compare(y, that.y) == 0 && Float.compare(width, that.width) == 0 && Float.compare(height, that.height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height);
    }

    @Override
    public String toString() {
        return String.format("(%f %f; %f %f)", x, y, width, height);
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setSize(CGSize size) {
        this.width = size.width;
        this.height = size.height;
    }

    public void setOrigin(CGPoint origin) {
        this.x = origin.x;
        this.y = origin.y;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float width() {
        return width;
    }

    public float height() {
        return height;
    }

    public float minX() {
        return x;
    }

    public float minY() {
        return y;
    }

    public float midX() {
        return x + width / 2;
    }

    public float midY() {
        return y + height / 2;
    }

    public float maxX() {
        return x + width;
    }

    public float maxY() {
        return y + height;
    }

    public CGSize size() {
        return new CGSize(width, height);
    }

    public CGPoint origin() {
        return new CGPoint(x, y);
    }
}
