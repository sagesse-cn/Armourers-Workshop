package moe.plushie.armourers_workshop.builder.client.gui.widget;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.panel.AdvancedPanel;
import moe.plushie.armourers_workshop.builder.data.properties.DataProperty;
import moe.plushie.armourers_workshop.builder.data.properties.Vector3dProperty;
import moe.plushie.armourers_workshop.core.math.OpenVector3d;

import java.util.function.Consumer;

public class EntitySizeBox extends UIView {

    private float cursorY = 0;
    private float cursorX = 0;

    private final Vector3dProperty valueProperty = new Vector3dProperty(0.6f, 1.88f, 1.62f);

    public EntitySizeBox(CGRect frame) {
        super(frame);
        addSlider(getDisplayText("overrideEntityWidth"), valueProperty.x());
        addSlider(getDisplayText("overrideEntityHeight"), valueProperty.y());
        addSlider(getDisplayText("overrideEntityEyeHeight"), valueProperty.z());
        for (var child : subviews()) {
            if (child instanceof NewSlider) {
                var nframe = child.frame().copy();
                nframe.setX(cursorX + 8);
                nframe.setWidth(bounds().width() - cursorX - 8);
                child.setFrame(nframe);
                child.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
            }
        }
    }

    public void addObserver(Consumer<OpenVector3d> observer) {
        valueProperty.addObserver(observer);
    }

    public void addEditingObserver(Consumer<Boolean> observer) {
        valueProperty.addEditingObserver(observer);
    }

    public void setValue(OpenVector3d value) {
        valueProperty.set(value);
    }

    public OpenVector3d getValue() {
        return valueProperty.get();
    }

    private void addSlider(NSString name, DataProperty<Double> property) {
        var title = new UILabel(new CGRect(0, cursorY + 3, 80, 10));
        title.setText(name);
        title.setTextColor(UIColor.WHITE);
        title.setTextHorizontalAlignment(NSTextAlignment.Horizontal.LEFT);
        title.sizeToFit();
        cursorX = Math.max(cursorX, title.frame().width());

        var view = new NewSlider(new CGRect(0, cursorY, 80, 16));
        view.setFormatter(AdvancedPanel.Group.Unit.POINT);
        view.setStepValue(0.01);
        view.setMinValue(0.01);
        view.setMaxValue(10.0);
        view.setValue(property.getOrDefault(0.0));
        view.addTarget(this, UIControl.Event.EDITING_DID_BEGIN, (pro, ctrl) -> property.beginEditing());
        view.addTarget(this, UIControl.Event.EDITING_DID_END, (pro, ctrl) -> property.endEditing());
        view.addTarget(this, UIControl.Event.VALUE_CHANGED, (pro, ctrl) -> {
            var slider = (NewSlider) ctrl;
            property.set(slider.value());
        });
        property.addObserver(view::setValue);

        addSubview(title);
        addSubview(view);

        cursorY = view.frame().maxY();
    }

    protected NSString getDisplayText(String key, Object... objects) {
        return NSString.localizedString("armourer.skinSettings." + key, objects);
    }
}
