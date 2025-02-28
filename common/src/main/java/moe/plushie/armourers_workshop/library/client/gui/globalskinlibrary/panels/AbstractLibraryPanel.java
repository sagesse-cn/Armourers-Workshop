package moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.panels;

import com.apple.library.coregraphics.CGGradient;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.GlobalSkinLibraryWindow;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public abstract class AbstractLibraryPanel extends UIView {

    public final Predicate<GlobalSkinLibraryWindow.Page> predicate;

    protected GlobalSkinLibraryWindow.Router router;

    private final String baseKey;

    public AbstractLibraryPanel(String titleKey, Predicate<GlobalSkinLibraryWindow.Page> predicate) {
        super(new CGRect(0, 0, 320, 240));
        this.setContents(getDefaultColor());
        this.baseKey = titleKey;
        this.predicate = predicate;
    }

    public void tick() {
    }

    public void refresh() {
    }

    @Override
    public boolean pointInside(CGPoint point, UIEvent event) {
        if (super.pointInside(point, event)) {
            return true;
        }
        return subviews().stream().anyMatch(subview -> subview.pointInside(convertPointToView(point, subview), event));
    }

    protected NSString getDisplayText(String key, Object... objects) {
        return NSString.localizedString(baseKey + "." + key, objects);
    }

    protected NSString getURLText(String url) {
        var style = Style.EMPTY.withColor(ChatFormatting.BLUE).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        return new NSString(Component.literal(url).withStyle(style));
    }

    protected CGGradient getDefaultColor() {
        var startColor = UIColor.rgba(0xC0101010);
        var endColor = UIColor.rgba(0xD0101010);
        return new CGGradient(startColor, CGPoint.ZERO, endColor, CGPoint.ZERO);
    }

    public GlobalSkinLibraryWindow.Router getRouter() {
        return router;
    }

    public void setRouter(GlobalSkinLibraryWindow.Router router) {
        this.router = router;
    }

}
