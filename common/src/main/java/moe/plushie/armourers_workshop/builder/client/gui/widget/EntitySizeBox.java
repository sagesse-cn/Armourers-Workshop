package moe.plushie.armourers_workshop.builder.client.gui.widget;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.panel.AdvancedPanel;
import moe.plushie.armourers_workshop.core.math.Vector3f;

public class EntitySizeBox extends UIView {

    float cursorY = 0;
    float cursorX = 0;

    private final NewSlider widthSlider;
    private final NewSlider heightSlider;
    private final NewSlider eyeHeightSlider;

    private Vector3f entitySize = new Vector3f(0.6f, 1.88f, 1.62f);

    public EntitySizeBox(CGRect frame) {
        super(frame);
        this.widthSlider = addSlider(getDisplayText("overrideEntityWidth"), entitySize.getX());
        this.heightSlider = addSlider(getDisplayText("overrideEntityHeight"), entitySize.getY());
        this.eyeHeightSlider = addSlider(getDisplayText("overrideEntityEyeHeight"), entitySize.getZ());
        for (var child : subviews()) {
            if (child instanceof NewSlider) {
                var nframe = child.frame().copy();
                nframe.setX(cursorX + 8);
                nframe.setWidth(bounds().getWidth() - cursorX - 8);
                child.setFrame(nframe);
            }
        }
    }

    public void setEntitySize(Vector3f size) {
        this.entitySize = size;
        this.widthSlider.setValue(size.getX());
        this.heightSlider.setValue(size.getY());
        this.eyeHeightSlider.setValue(size.getZ());
    }

    public Vector3f getEntitySize() {
        return entitySize;
    }

    protected void update() {
        double x = widthSlider.value();
        double y = heightSlider.value();
        double z = eyeHeightSlider.value();
        entitySize = new Vector3f(x, y, z);
    }

    protected void beginEditing() {

    }

    protected void endEditing() {

    }

    private NewSlider addSlider(NSString name, double defaultValue) {
        var title = new UILabel(new CGRect(0, cursorY + 3, 80, 10));
        title.setText(name);
        title.setTextColor(UIColor.WHITE);
        title.setTextHorizontalAlignment(NSTextAlignment.Horizontal.LEFT);
        title.sizeToFit();
        cursorX = title.frame().getWidth();

        var view = new NewSlider(new CGRect(0, cursorY, 80, 16));
        view.setFormatter(AdvancedPanel.Group.Unit.POINT);
        view.setStepValue(0.01);
        view.setMinValue(0.01);
        view.setMaxValue(10.0);
        view.setValue(defaultValue);
        view.addTarget(this, UIControl.Event.EDITING_DID_BEGIN, (pro, ctrl) -> pro.beginEditing());
        view.addTarget(this, UIControl.Event.EDITING_DID_END, (pro, ctrl) -> pro.endEditing());
        view.addTarget(this, UIControl.Event.VALUE_CHANGED, (pro, ctrl) -> pro.update());

        addSubview(title);
        addSubview(view);

        cursorY = view.frame().getMaxY();
        return view;
    }

    protected NSString getDisplayText(String key, Object... objects) {
        return NSString.localizedString("armourer.skinSettings." + key, objects);
    }
}
