package moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.panels;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UIScrollView;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.GlobalSkinLibraryWindow;
import moe.plushie.armourers_workshop.library.client.gui.widget.ServerItemList;
import moe.plushie.armourers_workshop.library.data.GlobalSkinLibrary;
import moe.plushie.armourers_workshop.library.data.impl.SearchColumnType;
import moe.plushie.armourers_workshop.library.data.impl.SearchOrderType;
import moe.plushie.armourers_workshop.library.data.impl.ServerSkin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class HomeLibraryPanel extends AbstractLibraryPanel implements GlobalSkinLibraryWindow.ISkinListListener {

    private final UIScrollView scrollView = new UIScrollView(CGRect.ZERO);

    //    private final GuiScrollbar scrollbar;
    private final ServerItemList skinPanelRecentlyUploaded = buildFileList(0, 0, 300, 307);
    private final ServerItemList skinPanelMostDownloaded = buildFileList(0, 0, 300, 307);
    private final ServerItemList skinPanelTopRated = buildFileList(0, 0, 300, 307);
    private final ServerItemList skinPanelNeedRated = buildFileList(0, 0, 300, 307);

    private int lastRequestSize = 0;
    private final GlobalSkinLibrary library = GlobalSkinLibrary.getInstance();

    public HomeLibraryPanel() {
        super("skin-library-global.home", GlobalSkinLibraryWindow.Page.HOME::equals);
        this.setup();
    }

    private void setup() {
        scrollView.setFrame(bounds());
        scrollView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        scrollView.setContentSize(new CGSize(0, 1000));
        scrollView.setContentOffset(new CGPoint(0, 20));
        addSubview(scrollView);

        scrollView.addSubview(skinPanelRecentlyUploaded);
        scrollView.addSubview(skinPanelTopRated);
        scrollView.addSubview(skinPanelNeedRated);
        scrollView.addSubview(skinPanelMostDownloaded);

        buildTitle(skinPanelRecentlyUploaded, "recentlyUploaded");
        buildTitle(skinPanelMostDownloaded, "mostDownloaded");
        buildTitle(skinPanelTopRated, "topRated");
        buildTitle(skinPanelNeedRated, "needRated");

        UIButton button = new UIButton(new CGRect(4, 6, 80, 16));
        button.setTitle(getDisplayText("showAllSkins"), UIControl.State.NORMAL);
        button.setTitleColor(UIColor.WHITE, UIControl.State.NORMAL);
        button.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        button.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, HomeLibraryPanel::showAll);
        scrollView.addSubview(button);

        setNeedsLayout();
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        float listTop = 20;
        float listLeft = 4;
        float width = bounds().width();
        for (ServerItemList fileList : lists()) {
            fileList.setFrame(new CGRect(listLeft, listTop + 20, width, 307));
            fileList.setItemSize(new CGSize(50, 50));
            fileList.setBackgroundColor(0);
            fileList.setShowsName(false);
            fileList.reloadData();
            listTop = fileList.frame().maxY();
        }
        scrollView.setContentSize(new CGSize(0, listTop + 4));
        scrollView.setContentOffset(scrollView.contentOffset());

        int pageSize = skinPanelRecentlyUploaded.getTotalCount();
        if (lastRequestSize > 0 && lastRequestSize < pageSize) {
            reloadData();
        }
    }

    public void reloadData() {
        int requestSize = skinPanelRecentlyUploaded.getTotalCount();
        lastRequestSize = requestSize;
        ModLog.debug("refresh home skin list, page size: {}", lastRequestSize);

        // get recently uploaded skins.
        library.searchSkin("", 0, requestSize, SearchColumnType.DATE_CREATED, SearchOrderType.DESC, SkinTypes.UNKNOWN, (result, exception) -> {
            if (result != null) {
                skinPanelRecentlyUploaded.setEntries(result.getSkins());
                skinPanelRecentlyUploaded.reloadData();
            }
        });

        // get most downloaded skins.
        library.searchSkin("", 0, requestSize, SearchColumnType.DOWNLOADS, SearchOrderType.DESC, SkinTypes.UNKNOWN, (result, exception) -> {
            if (result != null) {
                skinPanelMostDownloaded.setEntries(result.getSkins());
                skinPanelMostDownloaded.reloadData();
            }
        });

        // get top rated skins.
        library.searchSkin("", 0, requestSize, SearchColumnType.RATING, SearchOrderType.DESC, SkinTypes.UNKNOWN, (result, exception) -> {
            if (result != null) {
                skinPanelTopRated.setEntries(result.getSkins());
                skinPanelTopRated.reloadData();
            }
        });

        // get need rated skins.
        library.searchSkin("", 0, requestSize, SearchColumnType.RATING_COUNT, SearchOrderType.ASC, SkinTypes.UNKNOWN, (result, exception) -> {
            if (result != null) {
                skinPanelNeedRated.setEntries(result.getSkins());
                skinPanelNeedRated.reloadData();
            }
        });
    }

    @Override
    public void skinDidChange(String skinId, @Nullable ServerSkin newValue) {
        // only update for remove
        if (newValue != null) {
            return;
        }
        for (var fileList : lists()) {
            if (indexOf(fileList, skinId) != -1) {
                // removed skin in here
                reloadData();
                return;
            }
        }
    }

    private void showAll(UIControl sender) {
        router.showSkinList("", SkinTypes.UNKNOWN, SearchColumnType.DATE_CREATED, SearchOrderType.DESC);
    }

    private void showSkinInfo(ServerSkin sender) {
        router.showSkinDetail(sender, GlobalSkinLibraryWindow.Page.HOME);
    }

    private void buildTitle(ServerItemList list, String titleKey) {
        var label = new UILabel(new CGRect(1, -16, list.frame().width - 2, 16));
        label.setText(getDisplayText(titleKey));
        label.setTextColor(UIColor.WHITE);
        label.setAutoresizingMask(AutoresizingMask.flexibleWidth);
        list.addSubview(label);
    }

    private ServerItemList buildFileList(float x, float y, float width, float height) {
        var fileList = new ServerItemList(new CGRect(x, y, width, height));
        fileList.setItemSize(new CGSize(50, 50));
        fileList.setBackgroundColor(0);
        fileList.setShowsName(false);
        fileList.setItemSelector(this::showSkinInfo);
        return fileList;
    }

    private int indexOf(ServerItemList list, String skinId) {
        return Collections.indexOf(list.getEntries(), e -> Objects.equals(e.getId(), skinId));
    }

    private Iterable<ServerItemList> lists() {
        return Collections.collect(scrollView.subviews(), ServerItemList.class);
    }
}
