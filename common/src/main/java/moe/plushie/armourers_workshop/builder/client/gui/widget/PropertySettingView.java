package moe.plushie.armourers_workshop.builder.client.gui.widget;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UICheckBox;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperty;
import moe.plushie.armourers_workshop.core.client.gui.widget.InventoryBox;
import moe.plushie.armourers_workshop.core.math.Vector2f;
import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.utils.Objects;

import java.util.Collection;

public abstract class PropertySettingView extends UIView {

    private float cursorY = 0;

    protected UICheckBox blockBed;
    protected UICheckBox blockEnderInventory;
    protected UICheckBox blockInventory;

    protected UILabel inventoryTitle;
    protected UILabel inventorySlot;
    protected InventoryBox inventoryBox;

    protected EntitySizeBox entitySizeBox;

    public PropertySettingView(CGRect rect, Collection<ISkinProperty<?>> properties) {
        super(rect);
        for (var property : properties) {
            if (property.getDefaultValue() instanceof Boolean) {
                addCheckBox(Objects.unsafeCast(property));
            }
            if (property == SkinProperty.OVERRIDE_ENTITY_SIZE_WIDTH) {
                addEntitySize();
            }
            if (property == SkinProperty.BLOCK_INVENTORY_WIDTH) {
                addInventoryBox();
            }
        }
        this.setFrame(new CGRect(rect.x, rect.y, rect.width, cursorY));
        this.resolveConflicts();
    }

    public void beginEditing() {
    }

    public abstract <T> void putValue(ISkinProperty<T> property, T value);

    public abstract <T> T getValue(ISkinProperty<T> property);

    public void endEditing() {
    }

    protected void addCheckBox(ISkinProperty<Boolean> property) {
        var checkBox = new UICheckBox(new CGRect(0, cursorY, bounds().width, 10));
        checkBox.setTitle(getDisplayText(property.getKey()));
        checkBox.setTitleColor(UIColor.WHITE);
        checkBox.setTitleColor(UIColor.GRAY, UIControl.State.DISABLED);
        checkBox.setSelected(getValue(property));
        checkBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, c) -> {
            UICheckBox checkBox1 = Objects.unsafeCast(c);
            self.beginEditing();
            self.putValue(property, checkBox1.isSelected());
            self.resolveConflicts();
            self.endEditing();
        });
        addSubview(checkBox);
        if (property == SkinProperty.BLOCK_BED) {
            checkBox.setFrame(checkBox.frame().insetBy(0, 4, 0, 0));
            blockBed = checkBox;
        }
        if (property == SkinProperty.BLOCK_ENDER_INVENTORY) {
            blockEnderInventory = checkBox;
        }
        if (property == SkinProperty.BLOCK_INVENTORY) {
            blockInventory = checkBox;
        }
        cursorY = checkBox.frame().getMaxY() + 2;
    }

    protected void addEntitySize() {
        entitySizeBox = new EntitySizeBox(new CGRect(12, cursorY, bounds().getWidth() - 34, 48)) {
            @Override
            protected void update() {
                super.update();
                PropertySettingView.this.setEntitySize(getEntitySize());
            }

            @Override
            protected void beginEditing() {
                PropertySettingView.this.beginEditing();
            }

            @Override
            protected void endEditing() {
                PropertySettingView.this.endEditing();
            }
        };
        entitySizeBox.setHidden(true);
        addSubview(entitySizeBox);
        cursorY = entitySizeBox.frame().getMaxY() + 2;
    }

    private void setEntitySize(Vector3f entitySize) {
        putValue(SkinProperty.OVERRIDE_ENTITY_SIZE_WIDTH, (double) entitySize.getX());
        putValue(SkinProperty.OVERRIDE_ENTITY_SIZE_HEIGHT, (double) entitySize.getY());
        putValue(SkinProperty.OVERRIDE_ENTITY_SIZE_EYE_HEIGHT, (double) entitySize.getZ());
    }

    protected void addInventoryBox() {
        inventoryTitle = new UILabel(new CGRect(0, cursorY - 2, bounds().width, 9));
        inventorySlot = new UILabel(new CGRect(0, cursorY + 6, bounds().width, 9));
        inventoryBox = new InventoryBox(new CGRect(0, cursorY, 9 * 10, 6 * 10));

        inventoryTitle.setText(getDisplayText("label.inventorySize"));
        inventoryBox.addTarget(this, UIControl.Event.VALUE_CHANGED, PropertySettingView::setInventorySize);

        addSubview(inventoryTitle);
        addSubview(inventorySlot);
        addSubview(inventoryBox);

        cursorY = inventoryBox.frame().getMaxY() + 2;
    }

    private void setInventorySize(UIControl sender) {
        var offset = inventoryBox.getOffset();
        var width = (int) (offset.x / 10) + 1;
        var height = (int) (offset.y / 10) + 1;
        beginEditing();
        putValue(SkinProperty.BLOCK_INVENTORY_WIDTH, width);
        putValue(SkinProperty.BLOCK_INVENTORY_HEIGHT, height);
        endEditing();
        resolveInventorySlots();
    }

    private void resolveEntitySize() {
        if (entitySizeBox == null) {
            return;
        }
        var isEnabled = getValue(SkinProperty.OVERRIDE_ENTITY_SIZE);
        var width = getValue(SkinProperty.OVERRIDE_ENTITY_SIZE_WIDTH);
        var height = getValue(SkinProperty.OVERRIDE_ENTITY_SIZE_HEIGHT);
        var eyeHeight = getValue(SkinProperty.OVERRIDE_ENTITY_SIZE_EYE_HEIGHT);
        entitySizeBox.setHidden(!isEnabled);
        entitySizeBox.setEntitySize(new Vector3f(width, height, eyeHeight));
    }

    private void resolveInventorySlots() {
        if (inventorySlot == null) {
            return;
        }
        var isEnabled = getValue(SkinProperty.BLOCK_INVENTORY) && !getValue(SkinProperty.BLOCK_ENDER_INVENTORY);
        var width = getValue(SkinProperty.BLOCK_INVENTORY_WIDTH);
        var height = getValue(SkinProperty.BLOCK_INVENTORY_HEIGHT);
        inventorySlot.setText(getDisplayText("label.inventorySlots", width * height, width, height));
        inventoryBox.setOffset(new CGPoint(Math.max(width - 1, 0) * 10, Math.max(height - 1, 0) * 10));
        inventoryTitle.setHidden(!isEnabled);
        inventorySlot.setHidden(!isEnabled);
        inventoryBox.setHidden(!isEnabled);
    }

    private void resolveConflicts() {
        if (blockBed != null) {
            blockBed.setEnabled(getValue(SkinProperty.BLOCK_MULTIBLOCK));
            if (!blockBed.isEnabled() && blockBed.isSelected()) {
                blockBed.setSelected(false);
                putValue(SkinProperty.BLOCK_BED, false);
            }
        }
        if (blockEnderInventory != null) {
            blockEnderInventory.setEnabled(!getValue(SkinProperty.BLOCK_INVENTORY));
            if (!blockEnderInventory.isEnabled() && blockEnderInventory.isSelected()) {
                blockEnderInventory.setSelected(false);
                putValue(SkinProperty.BLOCK_ENDER_INVENTORY, false);
            }
        }
        if (blockInventory != null) {
            blockInventory.setEnabled(!getValue(SkinProperty.BLOCK_ENDER_INVENTORY));
            if (!blockInventory.isEnabled() && blockInventory.isSelected()) {
                blockInventory.setSelected(false);
                putValue(SkinProperty.BLOCK_INVENTORY, false);
            }
        }
        resolveEntitySize();
        resolveInventorySlots();
    }

    protected NSString getDisplayText(String key, Object... objects) {
        return NSString.localizedString("armourer.skinSettings." + key, objects);
    }
}
