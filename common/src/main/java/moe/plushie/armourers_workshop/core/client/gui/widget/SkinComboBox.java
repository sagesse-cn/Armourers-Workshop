package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIComboBox;
import com.apple.library.uikit.UIComboItem;
import com.apple.library.uikit.UIEdgeInsets;
import com.apple.library.uikit.UIImage;
import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class SkinComboBox extends UIComboBox {

    protected final ArrayList<SkinType> skinTypes = new ArrayList<>();

    public SkinComboBox(CGRect frame) {
        super(frame);
    }

    public void reloadSkins(List<SkinType> skinTypes) {
        var items = new ArrayList<UIComboItem>();
        for (var skinType : skinTypes) {
            var title = new NSString(TranslateUtils.Name.of(skinType));
            var rl = ArmourersWorkshop.getItemIcon(skinType);
            UIImage image = null;
            if (rl != null) {
                image = UIImage.of(rl).resizable(16, 16).fixed(9, 9).limit(16, 16).build();
            }
            items.add(new UIComboItem(image, title));
        }
        this.skinTypes.clear();
        this.skinTypes.addAll(skinTypes);
        this.reloadData(items);
    }

    @Nullable
    public SkinType selectedSkin() {
        int index = selectedIndex();
        if (index >= 0 && index < skinTypes.size()) {
            return skinTypes.get(index);
        }
        return null;
    }

    public void setSelectedSkin(@Nullable SkinType skinType) {
        super.setSelectedIndex(skinTypes.indexOf(skinType));
    }

    public List<SkinType> skinTypes() {
        return skinTypes;
    }

    @Override
    protected void updateTitleView(UIComboItem item) {
        if (item != null) {
            item = new UIComboItem(item.title, item.isEnabled);
        }
        super.updateTitleView(item);
    }

    @Override
    protected void updateEntryView(Entry entry, UIComboItem item) {
        if (item != null && item.image != null) {
            var button = entry.titleView;
            button.setContentEdgeInsets(UIEdgeInsets.ZERO);
            button.setTitleEdgeInsets(new UIEdgeInsets(0, 2, 0, 0));
            button.imageView().setOpaque(false);
        }
        super.updateEntryView(entry, item);
    }
}
