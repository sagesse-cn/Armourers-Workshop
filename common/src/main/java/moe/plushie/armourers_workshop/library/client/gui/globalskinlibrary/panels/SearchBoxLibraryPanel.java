package moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.panels;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSMutableString;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIComboBox;
import com.apple.library.uikit.UIComboItem;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UITextField;
import com.apple.library.uikit.UITextFieldDelegate;
import moe.plushie.armourers_workshop.core.client.gui.widget.SkinComboBox;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.GlobalSkinLibraryWindow;
import moe.plushie.armourers_workshop.library.data.impl.SearchColumnType;
import moe.plushie.armourers_workshop.library.data.impl.SearchOrderType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class SearchBoxLibraryPanel extends AbstractLibraryPanel implements UITextFieldDelegate {

    private final SearchOrderType[] orderTypes = {SearchOrderType.DESC, SearchOrderType.ASC};
    private final SearchColumnType[] columnTypes = {SearchColumnType.DATE_CREATED, SearchColumnType.DATE_CREATED, SearchColumnType.NAME, SearchColumnType.NAME, SearchColumnType.DOWNLOADS, SearchColumnType.DOWNLOADS, SearchColumnType.RATING, SearchColumnType.RATING};

    private final UITextField searchText = new UITextField(CGRect.ZERO);
    private final UIComboBox sortList = new UIComboBox(CGRect.ZERO);
    private final SkinComboBox skinTypeList = new SkinComboBox(CGRect.ZERO);
    private final UIButton searchButton = new UIButton(CGRect.ZERO);

    private String keyword = "";
    private SkinType skinType = SkinTypes.UNKNOWN;
    private SearchOrderType orderType = SearchOrderType.DESC;
    private SearchColumnType columnType = SearchColumnType.DATE_CREATED;

    public SearchBoxLibraryPanel() {
        super("skin-library-global.searchBox", GlobalSkinLibraryWindow.Page::hasSearch);
        this.setup();
    }

    private void setup() {
        CGRect rect = bounds();
        setupSearchField(new CGRect(5, 3, rect.width - 10 - 180 - 70 - 5, 16));
        setupSortView(new CGRect(rect.width - 180 - 70 - 5, 3, 90, 16));
        setupSkinListView(new CGRect(rect.width - 160, 3, 70, 16));
        searchButton.setFrame(new CGRect(rect.width - 84, 3, 80, 16));
        searchButton.setTitle(getDisplayText("search"), UIControl.State.NORMAL);
        searchButton.setTitleColor(UIColor.WHITE, UIControl.State.NORMAL);
        searchButton.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        searchButton.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, SearchBoxLibraryPanel::search);
        searchButton.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin);
        addSubview(searchButton);
    }

    private void setupSearchField(CGRect rect) {
        searchText.setFrame(rect);
        searchText.setPlaceholder(getDisplayText("typeToSearch"));
        searchText.setText(keyword);
        searchText.setMaxLength(255);
        searchText.setDelegate(this);
        searchText.setAutoresizingMask(AutoresizingMask.flexibleWidth);
        addSubview(searchText);
    }

    private void setupSkinListView(CGRect rect) {
        skinTypeList.setFrame(rect);
        skinTypeList.reloadSkins(SkinTypes.values());
        skinTypeList.setSelectedSkin(skinType);
        skinTypeList.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin);
        skinTypeList.setMaxRows(10);
        skinTypeList.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, sender) -> {
            self.skinType = ((SkinComboBox) sender).selectedSkin();
            self.search(null);
        });
        addSubview(skinTypeList);
    }

    private void setupSortView(CGRect rect) {
        int selectedIndex = 0;
        ArrayList<SearchColumnType> columnTypes1 = new ArrayList<>();
        ArrayList<UIComboItem> items = new ArrayList<>();
        for (SearchColumnType columnType : columnTypes) {
            SearchOrderType orderType = orderTypes[columnTypes1.size() % 2];
            NSMutableString title = new NSMutableString("");
            if (orderType == SearchOrderType.DESC) {
                title.append("↑ "); // up
            } else {
                title.append("↓ "); // down
            }
            title.append(NSString.localizedTableString("skin_search_column", columnType.toString().toLowerCase()));
            UIComboItem item = new UIComboItem(title);
            if (columnType == this.columnType && orderType == this.orderType) {
                selectedIndex = items.size();
            }
            items.add(item);
            columnTypes1.add(columnType);
        }
        sortList.setFrame(rect);
        sortList.setSelectedIndex(selectedIndex);
        sortList.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin);
        sortList.reloadData(items);
        sortList.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, sender) -> {
            int newValue = ((UIComboBox) sender).selectedIndex();
            self.orderType = orderTypes[newValue % 2];
            self.columnType = columnTypes1.get(newValue);
            self.search(sender);
        });
        addSubview(sortList);
    }

    @Override
    public boolean textFieldShouldReturn(UITextField textField) {
        this.search(textField);
        return true;
    }

    public void reloadData(String keyword, SkinType skinType, SearchColumnType columnType, SearchOrderType orderType) {
        this.keyword = keyword;
        this.skinType = skinType;
        this.orderType = orderType;
        this.columnType = columnType;
        if (this.searchText == null) {
            return;
        }
        this.searchText.setText(keyword);
        this.sortList.setSelectedIndex(getSortIndex(columnType, orderType));
        this.skinTypeList.setSelectedSkin(skinType);
    }

    private int getSortIndex(SearchColumnType columnType, SearchOrderType orderType) {
        for (int i = 0; i < columnTypes.length; ++i) {
            if (columnType == columnTypes[i] && orderType == orderTypes[i % 2]) {
                return i;
            }
        }
        return 0;
    }

    private void search(Object button) {
        keyword = searchText.text();
        router.showSkinList(keyword, skinType, columnType, orderType);
    }
}
