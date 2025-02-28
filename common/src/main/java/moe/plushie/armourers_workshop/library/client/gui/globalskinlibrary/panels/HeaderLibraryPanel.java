package moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.panels;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSMutableString;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIFont;
import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.core.client.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.GlobalSkinLibraryWindow;
import moe.plushie.armourers_workshop.library.data.GlobalSkinLibrary;
import moe.plushie.armourers_workshop.library.data.impl.ServerPermission;
import moe.plushie.armourers_workshop.library.data.impl.ServerUser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class HeaderLibraryPanel extends AbstractLibraryPanel {

    private final ArrayList<UIButton> rightButtons = new ArrayList<>();

    private final UIButton iconButtonHome = addRightButton(0, 0, "home", redirect(GlobalSkinLibraryWindow.Page.HOME));
    private final UIButton iconButtonMyFiles = addRightButton(0, 34, "myFiles", redirect(GlobalSkinLibraryWindow.Page.LIST_USER_SKINS));
    private final UIButton iconButtonUploadSkin = addRightButton(0, 51, "uploadSkin", "uploadSkinBan", redirect(GlobalSkinLibraryWindow.Page.SKIN_UPLOAD));
    private final UIButton iconButtonJoin = addRightButton(0, 68, "join", redirect(GlobalSkinLibraryWindow.Page.LIBRARY_JOIN));
    private final UIButton iconButtonInfo = addRightButton(0, 17, "info", redirect(GlobalSkinLibraryWindow.Page.LIBRARY_INFO));
    private final UIButton iconButtonModeration = addRightButton(0, 119, "moderation", redirect(GlobalSkinLibraryWindow.Page.LIBRARY_MODERATION));

    private final GlobalSkinLibrary library = GlobalSkinLibrary.getInstance();

    private EntityTextureDescriptor playerTexture;

    public HeaderLibraryPanel() {
        super("skin-library-global.header", p -> true);
        this.reloadData();
    }

    @Override
    public void tick() {
        super.tick();
        reloadData();
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        CGRect bounds = bounds();
        float x2 = bounds.width - 4;
        for (UIButton button : rightButtons) {
            if (button.isHidden()) {
                continue;
            }
            CGRect frame = button.frame();
            button.setFrame(new CGRect(x2 - frame.width, (bounds.height - frame.height) / 2, frame.width, frame.height));
            x2 = button.frame().minX() - 2;
        }
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        super.render(point, context);
        this.renderPlayerProfile(context, Minecraft.getInstance().getUser().getGameProfile());
    }

    public void reloadData() {
        iconButtonHome.setHidden(false);
        iconButtonMyFiles.setHidden(true);
        iconButtonUploadSkin.setHidden(true);
        iconButtonJoin.setHidden(true);
        iconButtonInfo.setHidden(false);
        iconButtonModeration.setHidden(true);

        ServerUser user = library.getUser();
        if (!user.isMember() && library.isConnected()) {
            iconButtonJoin.setHidden(false);
        }

        if (user.isMember()) {
            iconButtonMyFiles.setHidden(false);
            iconButtonUploadSkin.setHidden(false);
            iconButtonUploadSkin.setEnabled(user.hasPermission(ServerPermission.SKIN_UPLOAD));
            if (user.hasPermission(ServerPermission.GET_REPORT_LIST)) {
                iconButtonModeration.setHidden(false);
            }
        }

        setNeedsLayout();
    }

    private void renderPlayerProfile(CGGraphicsContext context, GameProfile gameProfile) {
        if (playerTexture == null) {
            playerTexture = EntityTextureDescriptor.fromProfile(gameProfile);
        }
        var tx = 5.0f;
        var ty = 5.0f;
        var texture = PlayerTextureLoader.getInstance().loadTextureLocation(playerTexture);
        context.drawResizableImage(texture, tx, ty, 16, 16, 8, 8, 8, 8, 64, 64, 0);
        context.drawResizableImage(texture, tx - 1, ty - 1, 16 + 2, 16 + 2, 40, 8, 8, 8, 64, 64, 0);


        // White - not a member.
        // Yellow - Member not authenticated.
        // Green - Authenticated member.
        // Red - Missing profile info.
        var rect = bounds();
        var profile = new NSMutableString(" - ");
        profile.append(gameProfile.getName());
        int textColor = 0xFFAAAA;
        var user = library.getUser();
        if (user.isMember()) {
            textColor = 0xFFFFAA;
        }
        if (user.isAuthenticated()) {
            textColor = 0xAAFFAA;
        }
        float lineHeight = UIFont.systemFont().lineHeight();
        context.drawText(profile, 24, (rect.height - lineHeight) / 2f, textColor);
    }

    private BiConsumer<HeaderLibraryPanel, UIControl> redirect(GlobalSkinLibraryWindow.Page page) {
        return (self, sender) -> {
            switch (page) {
                case HOME:
                    self.router.showNewHome();
                    break;
                case LIST_USER_SKINS:
                    self.router.showSkinList(library.getUser());
                    break;
                default:
                    self.router.showPage(page);
                    break;
            }
        };
    }

    private UIButton addRightButton(int u, int v, String key, BiConsumer<HeaderLibraryPanel, UIControl> handler) {
        return addRightButton(u, v, key, null, handler);
    }

    private UIButton addRightButton(int u, int v, String key, String key2, BiConsumer<HeaderLibraryPanel, UIControl> handler) {
        var button = new UIButton(new CGRect(0, 0, 18, 18));
        button.setImage(ModTextures.iconImage(u, v, 16, 16, ModTextures.GLOBAL_SKIN_LIBRARY), UIControl.State.ALL);
        button.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        button.setTooltip(getDisplayText(key), UIControl.State.NORMAL);
        if (key2 != null) {
            button.setTooltip(getDisplayText(key2), UIControl.State.DISABLED);
        }
        button.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, handler);
        addSubview(button);
        rightButtons.add(button);
        return button;
    }
}
