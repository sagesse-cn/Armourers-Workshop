package moe.plushie.armourers_workshop.library.client.gui.skinlibrary;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UICheckBox;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIImageView;
import com.apple.library.uikit.UITextField;
import com.apple.library.uikit.UITextFieldDelegate;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.api.library.ISkinLibrary;
import moe.plushie.armourers_workshop.api.library.ISkinLibraryListener;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.gui.notification.UserNotificationCenter;
import moe.plushie.armourers_workshop.core.client.gui.widget.ConfirmDialog;
import moe.plushie.armourers_workshop.core.client.gui.widget.InputDialog;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import moe.plushie.armourers_workshop.core.client.gui.widget.SkinComboBox;
import moe.plushie.armourers_workshop.core.client.gui.widget.SkinFileList;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileOptions;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.FileUtils;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.library.data.SkinLibrary;
import moe.plushie.armourers_workshop.library.data.SkinLibraryFile;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.library.menu.SkinLibraryMenu;
import moe.plushie.armourers_workshop.library.network.SaveSkinPacket;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class SkinLibraryWindow extends MenuWindow<SkinLibraryMenu> implements UITextFieldDelegate, ISkinLibraryListener {

    private final UICheckBox fileOptionsBox = new UICheckBox(CGRect.ZERO);
    private final UIButton actionButton = new UIButton(CGRect.ZERO);

    private final UIButton localFileButton = buildIconButton(0, 0, 0, 0, 50, 30);
    private final UIButton remotePublicButton = buildIconButton(55, 0, 0, 31, 50, 30);
    private final UIButton remotePrivateButton = buildIconButton(110, 0, 0, 62, 50, 30);

    private final UIButton openFolderButton = buildIconButton(0, 0, 0, 93, 24, 24);
    private final UIButton deleteButton = buildIconButton(25, 0, 0, 118, 24, 24);
    private final UIButton refreshButton = buildIconButton(50, 0, 73, 93, 24, 24);
    private final UIButton newFolderButton = buildIconButton(75, 0, 73, 118, 24, 24);
    private final UIButton backButton = buildIconButton(138, 0, 146, 93, 24, 24);

    private final UITextField nameTextField = new UITextField(CGRect.ZERO);
    private final UITextField searchTextField = new UITextField(CGRect.ZERO);

    private final SkinComboBox skinTypeList = new SkinComboBox(CGRect.ZERO);
    private final SkinFileList<SkinLibraryFile> fileList = new SkinFileList<>(new CGRect(0, 0, 100, 100));
    private final HashMap<String, CGPoint> contentOffsets = new HashMap<>();

    protected boolean didRemoved = false;

    protected SkinType skinType = SkinTypes.UNKNOWN;
    protected SkinLibraryFile selectedFile = null;
    protected String selectedPath;
    protected ItemStack lastInputItem;

    protected SkinLibrary selectedLibrary;
    protected SkinLibraryManager.Client libraryManager = SkinLibraryManager.getClient();

    private final Inventory playerInventory;

    public SkinLibraryWindow(SkinLibraryMenu container, Inventory inventory, NSString title) {
        super(container, inventory, title);
        this.setFrame(new CGRect(0, 0, 640, 480));
        this.libraryManager.addListener(this);
        this.selectedLibrary = libraryManager.getLocalSkinLibrary();
        this.selectedPath = selectedLibrary.getRootPath();
        this.playerInventory = inventory;
        this.inventoryView.removeFromSuperview();
    }

    @Override
    public void screenWillResize(CGSize size) {
        setFrame(new CGRect(0, 0, size.width, size.height));
        menu.reload(0, 0, (int) size.width, (int) size.height);
    }

    @Override
    public void init() {
        super.init();
        var rect = bounds().insetBy(23, 5, 5, 5);

        titleView.setTextColor(new UIColor(0xcccccc));

        setupInputView(rect);
        setupInventoryView(rect);
        setupFileView(rect.insetBy(0, 162 + 5, 0, 0));

        reloadData(this);
        reloadStatus();
    }

    @Override
    public void menuDidChange() {
        reloadStatus();
        reloadInputName();
    }

    @Override
    public boolean textFieldShouldReturn(UITextField textField) {
        renameItem(textField.text());
        return true;
    }

    private void setupInputView(CGRect rect) {
        int width = 162;

        UIView group1 = new UIView(new CGRect(rect.minX(), rect.minY(), width, 30));
        addSubview(group1);

        localFileButton.setTooltip(getDisplayText("rollover.localFiles"), UIControl.State.DISABLED);
        localFileButton.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, SkinLibraryWindow::selectLibrary);
        group1.addSubview(localFileButton);

        remotePublicButton.setTooltip(getDisplayText("rollover.notOnServer"), UIControl.State.DISABLED);
        remotePublicButton.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, SkinLibraryWindow::selectLibrary);
        group1.addSubview(remotePublicButton);

        remotePrivateButton.setTooltip(getDisplayText("rollover.notOnServer"), UIControl.State.DISABLED);
        remotePrivateButton.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, SkinLibraryWindow::selectLibrary);
        group1.addSubview(remotePrivateButton);

        nameTextField.setFrame(new CGRect(rect.minX(), group1.frame().maxY() + 5, width, 20));
        nameTextField.setPlaceholder(getDisplayText("label.enterFileName"));
        nameTextField.setMaxLength(255);
        nameTextField.setDelegate(this);
        addSubview(nameTextField);

        UIView group3 = new UIView(new CGRect(rect.minX(), nameTextField.frame().maxY() + 5, width, 24));
        addSubview(group3);

        openFolderButton.setTooltip(getDisplayText("rollover.openLibraryFolder"), UIControl.State.NORMAL);
        openFolderButton.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, SkinLibraryWindow::openFolder);
        group3.addSubview(openFolderButton);

        refreshButton.setTooltip(getDisplayText("rollover.refresh"), UIControl.State.NORMAL);
        refreshButton.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, SkinLibraryWindow::refreshLibrary);
        group3.addSubview(refreshButton);

        deleteButton.setTooltip(getDisplayText("rollover.deleteSkin"), UIControl.State.NORMAL);
        deleteButton.setTooltip(getDisplayText("rollover.deleteSkinSelect"), UIControl.State.DISABLED);
        deleteButton.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, SkinLibraryWindow::removeItem);
        group3.addSubview(deleteButton);

        newFolderButton.setTooltip(getDisplayText("rollover.newFolder"), UIControl.State.NORMAL);
        newFolderButton.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, SkinLibraryWindow::addFolder);
        group3.addSubview(newFolderButton);

        backButton.setTooltip(getDisplayText("rollover.back"), UIControl.State.NORMAL);
        backButton.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, SkinLibraryWindow::backFolder);
        group3.addSubview(backButton);
    }

    private void setupInventoryView(CGRect rect) {
        int width = 162;
        int height = 76;

        var group1 = new UIView(new CGRect(rect.minX(), rect.maxY() - height, width, height));
        group1.setOpaque(false);
        group1.setAutoresizingMask(AutoresizingMask.flexibleTopMargin);
        group1.setContents(UIImage.of(ModTextures.SKIN_LIBRARY).uv(0, 180).build());
        addSubview(group1);

        var group2 = new UIView(new CGRect(rect.minX(), group1.frame().minY() - 31, width, 26));
        group2.setAutoresizingMask(AutoresizingMask.flexibleTopMargin);
        addSubview(group2);

        fileOptionsBox.setFrame(new CGRect(rect.minX(), group2.frame().minY() - 10, width, 10));
        fileOptionsBox.setTitle(getDisplayText("fileOptions"));
        fileOptionsBox.setTitleColor(UIColor.WHITE);
        fileOptionsBox.setSelected(false);
        fileOptionsBox.addTarget(this, UIControl.Event.VALUE_CHANGED, SkinLibraryWindow::changeFileOptions);
        fileOptionsBox.setAutoresizingMask(AutoresizingMask.flexibleTopMargin);
        addSubview(fileOptionsBox);

        actionButton.setTitleColor(UIColor.WHITE, UIControl.State.ALL);
        actionButton.setTitle(getDisplayText("load"), UIControl.State.ALL);
        actionButton.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        actionButton.setFrame(new CGRect(23, 4, width - 54, 20));
        actionButton.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, SkinLibraryWindow::loadOrSaveItem);
        group2.addSubview(actionButton);

        var bg2 = new UIImageView(new CGRect(0, 4, 18, 18));
        var bg3 = new UIImageView(new CGRect(width - 26, 0, 26, 26));
        bg2.setOpaque(false);
        bg3.setOpaque(false);
        bg2.setImage(UIImage.of(ModTextures.SKIN_LIBRARY).uv(0, 162).build());
        bg3.setImage(UIImage.of(ModTextures.SKIN_LIBRARY).uv(18, 154).build());
        group2.addSubview(bg2);
        group2.addSubview(bg3);
    }

    private void setupFileView(CGRect rect) {
        float width = rect.width();

        var group1 = new UIView(new CGRect(rect.minX(), rect.minY(), width, 20));
        group1.setAutoresizingMask(AutoresizingMask.flexibleWidth);
        addSubview(group1);

        fileList.setFrame(rect.insetBy(22, 0, 0, 0));
        fileList.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        fileList.addTarget(this, UIControl.Event.VALUE_CHANGED, SkinLibraryWindow::selectFile);
        addSubview(fileList);

        searchTextField.setFrame(new CGRect(0, 2, width - 86, 16));
        searchTextField.setPlaceholder(getDisplayText("label.typeToSearch"));
        searchTextField.setAutoresizingMask(AutoresizingMask.flexibleWidth);
        searchTextField.setMaxLength(255);
        searchTextField.addTarget(this, UIControl.Event.VALUE_CHANGED, SkinLibraryWindow::reloadData);
        group1.addSubview(searchTextField);

        skinTypeList.setFrame(new CGRect(rect.maxX() - 80, rect.minY() + 2, 80, 16));
        skinTypeList.setMaxRows(12);
        skinTypeList.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin);
        skinTypeList.reloadSkins(SkinTypes.values());
        skinTypeList.setSelectedSkin(skinType);
        skinTypeList.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, c) -> {
            self.skinType = self.skinTypeList.selectedSkin();
            self.reloadData(c);
        });
        addSubview(skinTypeList);
    }

    @Override
    public void deinit() {
        super.deinit();
        this.didRemoved = true;
        this.libraryManager.removeListener(this);
    }

    @Override
    public void libraryDidReload(ISkinLibrary library) {
        RenderSystem.recordRenderCall(() -> {
            if (didRemoved) {
                return; // removed, ignore.
            }
            if (selectedLibrary == library) {
                reloadData(this);
            }
            reloadStatus();
        });
    }

    public void reloadStatus() {
        boolean isFile = selectedFile != null && (!selectedFile.isDirectory() || !selectedFile.getName().equals(".."));
        boolean isLoadable = isFile && !selectedFile.isDirectory();
        boolean isAuthorized = isAuthorized();
        fileOptionsBox.setSelected(SkinLibrarySettingWindow.hasChanges());
        remotePublicButton.setEnabled(libraryManager.getPublicSkinLibrary().isReady());
        remotePrivateButton.setEnabled(libraryManager.getPrivateSkinLibrary().isReady());
        deleteButton.setEnabled(isAuthorized && isFile);
        newFolderButton.setEnabled(isAuthorized);
        openFolderButton.setEnabled(libraryManager.getLocalSkinLibrary() == selectedLibrary);
        if (hasInputSkin()) {
            actionButton.setEnabled(true);
            actionButton.setTitle(getDisplayText("save"), UIControl.State.ALL);
        } else {
            actionButton.setEnabled(isLoadable);
            actionButton.setTitle(getDisplayText("load"), UIControl.State.ALL);
        }
    }

    public void reloadInputName() {
        ItemStack itemStack = menu.getInputStack();
        if (this.lastInputItem == itemStack) {
            return;
        }
        this.lastInputItem = itemStack;
        var descriptor = SkinDescriptor.of(itemStack);
        var bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, Tickets.RENDERER);
        var name = "";
        if (bakedSkin != null) {
            name = bakedSkin.getSkin().getCustomName();
        }
        this.nameTextField.setText(name);
    }

    public void reloadData(Object value) {
        var keyword = searchTextField.text();
        var results = selectedLibrary.search(keyword, skinType, selectedPath);
        fileList.setSelectedItem(null);
        fileList.reloadData(new ArrayList<>(results));
        fileList.setContentOffset(contentOffsets.getOrDefault(selectedPath, CGPoint.ZERO));
    }

    private NSString getDisplayText(String key, Object... args) {
        return NSString.localizedString("skin-library" + "." + key, args);
    }

    private void selectLibrary(UIControl sender) {
        var newLibrary = libraryManager.getLocalSkinLibrary();
        if (sender == remotePublicButton) {
            newLibrary = libraryManager.getPublicSkinLibrary();
        }
        if (sender == remotePrivateButton) {
            newLibrary = libraryManager.getPrivateSkinLibrary();
            newLibrary.setRootPath("/private/" + menu.getPlayer().getStringUUID());
        }
        if (!newLibrary.isReady()) {
            newLibrary = libraryManager.getLocalSkinLibrary();
        }
        contentOffsets.clear();
        selectedLibrary = newLibrary;
        setSelectedPath(newLibrary.getRootPath());
        if (isAuthorized()) {
            deleteButton.setTooltip(getDisplayText("rollover.deleteSkinSelect"), UIControl.State.DISABLED);
            newFolderButton.setTooltip(null, UIControl.State.DISABLED);
        } else {
            deleteButton.setTooltip(getDisplayText("rollover.unauthorized"), UIControl.State.DISABLED);
            newFolderButton.setTooltip(getDisplayText("rollover.unauthorized"), UIControl.State.DISABLED);
        }
        libraryDidReload(newLibrary);
    }

    private void addFolder(UIControl sender) {
        var dialog = new InputDialog();
        dialog.setTitle(getDisplayText("dialog.newFolder.title"));
        dialog.setMessageColor(new UIColor(0xff5555));
        dialog.setPlaceholder(getDisplayText("dialog.newFolder.enterFolderName"));
        dialog.setMessage(getDisplayText("dialog.newFolder.invalidFolderName"));
        dialog.setConfirmText(getDisplayText("dialog.newFolder.create"));
        dialog.setCancelText(getDisplayText("dialog.newFolder.close"));
        dialog.setVerifier(value -> value.replaceAll("[:\\\\/]|^[.]+$", "_").equals(value));
        dialog.showInView(this, () -> {
            if (!dialog.isCancelled()) {
                String newPath = FileUtils.normalize(FileUtils.concat(selectedPath, dialog.value()), true);
                selectedLibrary.mkdir(newPath);
            }
        });
    }

    private void openFolder(UIControl sender) {
        Util.getPlatform().openFile(EnvironmentManager.getSkinLibraryDirectory());
    }

    private void backFolder(UIControl sender) {
        var entry = fileList.getItem(0);
        if (entry != null && entry.isDirectory() && entry.getName().equals("..")) {
            setSelectedPath(entry.getPath());
            reloadData(sender);
        }
    }

    private void changeFileOptions(UIControl sender) {
        var dialog = new SkinLibrarySettingWindow();
        dialog.setTitle(getDisplayText("setting.title"));
        dialog.sizeToFit();
        dialog.showInView(this, () -> {
            if (!dialog.isCancelled()) {
                SkinLibrarySettingWindow.setChanges(dialog.getProperties());
                fileOptionsBox.setSelected(SkinLibrarySettingWindow.hasChanges());
            }
        });
        fileOptionsBox.setSelected(SkinLibrarySettingWindow.hasChanges());
    }

    private void loadOrSaveItem(UIControl button) {
        if (!menu.getOutputStack().isEmpty()) {
            return; // output has many items.
        }
        var descriptor = SkinDescriptor.of(menu.getInputStack());
        if (descriptor.isEmpty()) {
            loadSkin(null);
            return;
        }
        var newName = nameTextField.text();
        if (newName.isEmpty()) {
            toast(getDisplayText("error.noFileName"));
            return; // must input name
        }
        var newPath = FileUtils.normalize(FileUtils.concat(selectedPath, newName + Constants.EXT), true);
        if (selectedLibrary.get(newPath) != null) {
            if (!isAuthorized()) {
                toast(getDisplayText("error.illegalOperation"));
                return;
            }
            overwriteItem(newPath, () -> saveSkin(descriptor, newPath));
            return;
        }
        saveSkin(descriptor, newPath);
    }

    private void removeItem(UIControl sender) {
        if (selectedFile == null || !isAuthorized()) {
            return;
        }
        var dialog = new ConfirmDialog();
        dialog.setTitle(getDisplayText("dialog.delete.title"));
        dialog.setMessageColor(new UIColor(0xff5555));
        dialog.setConfirmText(getDisplayText("dialog.delete.delete"));
        dialog.setCancelText(getDisplayText("dialog.delete.close"));
        dialog.setMessage(getDisplayText("dialog.delete.deleteFile", selectedFile.getName()));
        if (selectedFile.isDirectory()) {
            dialog.setMessage(getDisplayText("dialog.delete.deleteFolder", selectedFile.getName()));
        }
        dialog.showInView(this, () -> {
            if (!dialog.isCancelled()) {
                selectedLibrary.delete(selectedFile);
            }
        });
    }

    private void renameItem(String sender) {
        if (selectedFile == null || !isAuthorized()) {
            return;
        }
        if (sender.equals(selectedFile.getName())) {
            return; // not changes.
        }
        var ext = selectedFile.isDirectory() ? "" : Constants.EXT;
        var newPath = FileUtils.normalize(selectedFile.getPath() + "/../" + sender + ext, true);
        if (selectedLibrary.get(newPath) != null) {
            overwriteItem(newPath, () -> selectedLibrary.rename(selectedFile, newPath));
            return;
        }
        selectedLibrary.rename(selectedFile, newPath);
    }

    private void overwriteItem(String path, Runnable handler) {
        var dialog = new ConfirmDialog();
        dialog.setTitle(getDisplayText("dialog.overwrite.title"));
        dialog.setMessage(getDisplayText("dialog.overwrite.overwriteFile", FileUtils.getBaseName(path)));
        dialog.setMessageColor(new UIColor(0xff5555));
        dialog.setConfirmText(getDisplayText("dialog.overwrite.ok"));
        dialog.setCancelText(getDisplayText("dialog.overwrite.close"));
        dialog.showInView(this, () -> {
            if (!dialog.isCancelled()) {
                handler.run();
            }
        });
    }

    private void toast(NSString message) {
        var dialog = new ConfirmDialog();
        dialog.setTitle(NSString.localizedString("common.text.error"));
        dialog.setMessage(message);
        dialog.setMessageColor(new UIColor(0xff5555));
        dialog.showInView(this);
    }

    private void refreshLibrary(UIControl sender) {
        selectedLibrary.reload();
    }

    private void selectFile(UIControl sender) {
        var oldValue = selectedFile;
        var newValue = fileList.getSelectedItem();
        selectedFile = newValue;
        boolean isFile = newValue != null && (!newValue.isDirectory() || !newValue.getName().equals(".."));
        if (isFile) {
            nameTextField.setText(newValue.getName());
        } else {
            nameTextField.setText("");
        }
        reloadStatus();
        if (newValue != null && newValue.isDirectory() && oldValue == newValue) {
            setSelectedPath(newValue.getPath());
            reloadData(sender);
        }
    }


    private UIButton buildIconButton(int x, int y, int u, int v, int width, int height) {
        var button = new UIButton(new CGRect(x, y, width, height));
        button.setImage(ModTextures.iconImage(u, v, width, height, ModTextures.SKIN_LIBRARY), UIControl.State.ALL);
        button.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        return button;
    }

    private void saveSkin(SkinDescriptor descriptor, String path) {
        // check skin load status
        var bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, Tickets.RENDERER);
        if (bakedSkin == null || !menu.shouldSaveStack()) {
            ModLog.debug("can't save unbaked skin of '{}'", descriptor);
            return; // skin not ready for using
        }
        // we can't save any read-only skin.
        if (!bakedSkin.getSkin().getSettings().isSavable()) {
            ModLog.debug("can't save readonly skin of '{}'", descriptor);
            toast(getDisplayText("error.illegalSkinMode"));
            return;
        }
        // save 1: copy local skin to local library
        // save 2: upload local skin to server library
        // save 3: copy server skin to server library
        // save 4: download server skin to local library
        var options = SkinLibrarySettingWindow.getFileOptions();
        var packet = new SaveSkinPacket(descriptor.getIdentifier(), null, selectedLibrary.getNamespace() + ":" + path, options);
        if (!packet.isReady(playerInventory.player)) {
            ModLog.debug("can't save skin of '{}'", descriptor);
            toast(getDisplayText("error.illegalOperation"));
            return;
        }
        NetworkManager.sendToServer(packet);
    }

    private void loadSkin(SkinFileOptions options) {
        if (selectedFile == null || selectedFile.isDirectory()) {
            return;
        }
        // when the file is encrypted, we require a password.
        var securityData = getSecurityData(selectedFile);
        if (securityData != null && options == null) {
            var keychainWindow = new SkinLibraryKeychainWindow(securityData);
            keychainWindow.showInView(this, (newOptions, exception) -> {
                if (newOptions != null) {
                    loadSkin(newOptions);
                } else {
                    var title = getDisplayText("dialog.passwordProvider.invalidPassword.title");
                    UserNotificationCenter.showToast(exception, title, null);
                }
            });
            return;
        }
        // check skin load status
        if (!menu.shouldLoadStack()) {
            return;
        }
        var source = DataDomain.DATABASE;
//        if (trackCheckBox.isSelected()) {
//            source = DataDomain.DATABASE_LINK;
//        }
        // check skin load status (only non-encrypted skin).
        var descriptor = new SkinDescriptor(selectedFile.getSkinIdentifier(), selectedFile.getSkinType(), SkinPaintScheme.EMPTY);
        if (securityData == null) {
            var bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, Tickets.RENDERER);
            if (bakedSkin == null) {
                ModLog.debug("can't load unbaked skin of '{}'", selectedFile.getSkinIdentifier());
                return; // skin not ready for using
            }
        }
        // load 1: upload local skin to database
        // load 2: copy server skin to database
        // load 3: make item stack(db/link)
        var packet = new SaveSkinPacket(descriptor.getIdentifier(), options, source.normalize(""), null);
        if (!packet.isReady(playerInventory.player)) {
            ModLog.debug("can't load skin of '{}'", descriptor);
            toast(getDisplayText("error.illegalOperation"));
            return;
        }
        NetworkManager.sendToServer(packet);
    }

    private void setSelectedPath(String newSelectedPath) {
        if (Objects.equals(selectedPath, newSelectedPath)) {
            return;
        }
        contentOffsets.put(selectedPath, fileList.contentOffset());
        // when enter a new subdirectory,
        // we need to clear the content offset.
        if (newSelectedPath.startsWith(selectedPath)) {
            contentOffsets.remove(newSelectedPath);
        }
        selectedPath = newSelectedPath;
    }

    private String getSecurityData(ISkinLibrary.Entry entry) {
        var header = entry.getSkinHeader();
        if (header == null) {
            return null;
        }
        var properties = header.getProperties();
        if (properties == null) {
            return null;
        }
        return properties.get(SkinProperty.SECURITY_DATA);
    }

    private boolean hasInputSkin() {
        return !SkinDescriptor.of(menu.getInputStack()).isEmpty();
    }

    private boolean isAuthorized() {
        // op can manage the public folder.
        if (selectedLibrary == libraryManager.getPublicSkinLibrary()) {
            return libraryManager.shouldMaintenanceFile(inventory.player);
        }
        return true;
    }
}
