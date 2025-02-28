package com.apple.library.uikit;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.impl.DelegateImpl;

import java.util.function.BiFunction;

@SuppressWarnings("unused")
public class UIScrollView extends UIView {

    protected CGPoint contentOffset = CGPoint.ZERO;
    protected CGSize contentSize = CGSize.ZERO;
    protected UIEdgeInsets contentInsets = UIEdgeInsets.ZERO;

    protected UIEdgeInsets scrollIndicatorInsets = new UIEdgeInsets(2, 2, 2, 2);

    protected final DelegateImpl<UIScrollViewDelegate> delegate = DelegateImpl.of(new UIScrollViewDelegate() {
    });

    private final boolean isInit;
    private final Indicator verticalIndicator = new Indicator((a, b) -> b);
    private final Indicator horizontalIndicator = new Indicator((a, b) -> a);

    public UIScrollView(CGRect frame) {
        super(frame);
        this.setClipBounds(true);
        super.addSubview(verticalIndicator);
        super.addSubview(horizontalIndicator);
        this.isInit = true;
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        var bounds = bounds();
        var rect = bounds.insetBy(scrollIndicatorInsets);
        verticalIndicator.setProgress(rect, contentOffset.y, contentSize.height - bounds.height);
        horizontalIndicator.setProgress(rect, contentOffset.x, contentSize.width - bounds.width);
    }

    @Override
    public void mouseWheel(UIEvent event) {
        if (isVerticalScrollable()) {
            var delta = event.delta().y() * bounds().height() / 5;
            var tx = contentOffset.x;
            var ty = contentOffset.y - delta; // revert
            this.setContentOffset(new CGPoint(tx, ty));
            return;
        }
        super.mouseWheel(event);
    }

    public void flashScrollIndicators() {
        verticalIndicator.flash();
        horizontalIndicator.flash();
    }

    public CGPoint contentOffset() {
        return contentOffset;
    }

    public void setContentOffset(CGPoint contentOffset) {
        contentOffset = clamp(contentOffset);
        var rect = bounds();
        this.contentOffset = contentOffset;
        super.setBounds(new CGRect(contentOffset.x, contentOffset.y, rect.width, rect.height));
        this.didScroll();
    }

    public CGSize contentSize() {
        return contentSize;
    }

    public void setContentSize(CGSize contentSize) {
        this.contentSize = contentSize;
        this.updateIndicatorIfNeeded();
        this.setNeedsLayout();
        // when the content size did changes,
        // we needs check the content offset is still valid.
        var newContentOffset = clamp(contentOffset);
        if (!newContentOffset.equals(contentOffset)) {
            setContentOffset(newContentOffset);
        }
    }

    public UIEdgeInsets contentInsets() {
        return contentInsets;
    }

    public void setContentInsets(UIEdgeInsets contentInsets) {
        this.contentInsets = contentInsets;
        this.setNeedsLayout();
    }

    public UIEdgeInsets scrollIndicatorInsets() {
        return scrollIndicatorInsets;
    }

    public void setScrollIndicatorInsets(UIEdgeInsets scrollIndicatorInsets) {
        this.scrollIndicatorInsets = scrollIndicatorInsets;
        this.setNeedsLayout();
    }

    public boolean showsHorizontalScrollIndicator() {
        return horizontalIndicator.isEnabled();
    }

    public void setShowsHorizontalScrollIndicator(boolean showsHorizontalScrollIndicator) {
        horizontalIndicator.setEnabled(showsHorizontalScrollIndicator);
        updateIndicatorIfNeeded();
    }

    public boolean showsVerticalScrollIndicator() {
        return verticalIndicator.isEnabled();
    }

    public void setShowsVerticalScrollIndicator(boolean showsVerticalScrollIndicator) {
        verticalIndicator.setEnabled(showsVerticalScrollIndicator);
        updateIndicatorIfNeeded();
    }

    public UIScrollViewDelegate delegate() {
        return delegate.get();
    }

    public void setDelegate(UIScrollViewDelegate delegate) {
        this.delegate.set(delegate);
    }

    @Override
    public void setBounds(CGRect bounds) {
        super.setBounds(bounds);
        if (!isInit) {
            return;
        }
        this.contentOffset = new CGPoint(bounds.x, bounds.y);
        this.updateIndicatorIfNeeded();
        this.didScroll();
    }

    @Override
    public void addSubview(UIView view) {
        super.insertViewAtIndex(view, Math.max(subviews().size() - 2, 0));
    }

    @Override
    public void insertViewAtIndex(UIView view, int index) {
        super.insertViewAtIndex(view, Math.min(index, Math.max(subviews().size() - 2, 0)));
    }

    private boolean isVerticalScrollable() {
        return bounds().height() < contentSize.height;
    }

    private CGPoint clamp(CGPoint point) {
        var rect = bounds();
        var edg = contentInsets;
        float tx = Math.max(Math.min(point.x, contentSize.width - rect.width + edg.right), -edg.left);
        float ty = Math.max(Math.min(point.y, contentSize.height - rect.height + edg.bottom), -edg.top);
        if (point.x == tx && point.y == ty) {
            return point;
        }
        return new CGPoint(tx, ty);
    }

    protected void didScroll() {
        delegate.invoker().scrollViewDidScroll(this);
    }

    private void updateIndicatorIfNeeded() {
        var bounds = bounds();
        var size = contentSize();
        verticalIndicator.setRadio(bounds.height, size.height);
        horizontalIndicator.setRadio(bounds.width, size.width);
    }

    protected static class Indicator extends UIView {

        protected float size = 3;
        protected float radio = 0;

        protected boolean allowsDisplay = true;
        protected boolean enabled = true;

        private final BiFunction<Float, Float, Float> selector;

        public Indicator(BiFunction<Float, Float, Float> selector) {
            super(CGRect.ZERO);
            this.selector = selector;
            this.setBackgroundColor(new UIColor(0x7f000000, true));
            this.setHidden(true);
        }

        private static float eval(float lhs, float rhs) {
            if (lhs == 0 || rhs <= 0) {
                return 0;
            }
            return lhs / rhs;
        }

        static float clamp(float value, float minValue, float maxValue) {
            if (value < minValue) {
                return minValue;
            }
            if (value > maxValue) {
                return maxValue;
            }
            return value;
        }

        public void setProgress(CGRect rect, float offset, float maxSize) {
            float m = selector.apply(rect.width, rect.height);
            float v = eval(offset, maxSize) * m * (1 - radio);
            float p = clamp(v, 0, m);
            float q = clamp(v + m * radio, 0, m);
            float x = selector.apply(rect.minX() + (int) p, rect.maxX() - size);
            float y = selector.apply(rect.maxY() - size, rect.minY() + (int) p);
            float width = selector.apply(q - p, size);
            float height = selector.apply(size, q - p);
            setFrame(new CGRect(x, y, width, height));
            flash();
        }

        public void setRadio(float value, float maxValue) {
            if (value == 0 || maxValue == 0 || value >= maxValue) {
                setHidden(true);
                allowsDisplay = false;
                radio = 0;
                return;
            }
            radio = clamp(value / maxValue, 0.35f, 1.0f);
            allowsDisplay = isEnabled();
            flash();
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        private void flash() {
            if (!enabled) {
                return;
            }
            setHidden(false);
            // after and the hidden
        }
    }
}
