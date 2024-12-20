package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.uikit.UIView;

import java.util.ArrayList;

public class VerticalStackView extends UIView {

    protected float spacing = 2f;
    protected final ArrayList<UIView> _arrangedSubviews = new ArrayList<>();

    public VerticalStackView(CGRect frame) {
        super(frame);
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        _calcLayout(bounds().size(), true);
    }

    @Override
    public CGSize sizeThatFits(CGSize size) {
        return _calcLayout(size, false);
    }

    @Override
    public void sizeToFit() {
        _setSize(_calcLayout(bounds().size(), true));
    }

    public void addArrangedSubview(UIView view) {
        addSubview(view);
        _arrangedSubviews.add(view);
        setNeedsLayout();
    }

    public void removeArrangedSubview(UIView view) {
        view.removeFromSuperview();
        _arrangedSubviews.remove(view);
        setNeedsLayout();
    }

    public void setSpacing(float spacing) {
        this.spacing = spacing;
    }

    public float spacing() {
        return spacing;
    }

    private CGSize _calcLayout(CGSize size, boolean apply) {
        float top = 0;
        float width = size.width;
        for (var subview : _arrangedSubviews) {
            var rect = subview.frame().copy();
            if (apply) {
                rect.setY(top);
                rect.setWidth(width);
                subview.setFrame(rect);
            }
            top += rect.height + spacing;
        }
        return new CGSize(width, Math.max(top - spacing, 0));
    }
}
