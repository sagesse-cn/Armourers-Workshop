package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.panel;

import com.apple.library.coregraphics.CGAffineTransform;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.api.skin.part.features.ICanOverride;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.AdvancedBuilderWindow;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document.DocumentConnector;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document.DocumentEditor;
import moe.plushie.armourers_workshop.builder.client.gui.widget.PartPickerView;
import moe.plushie.armourers_workshop.builder.network.AdvancedImportPacket;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;

public class AdvancedGeneralPanel extends AdvancedPanel {

    private final DocumentConnector connector;

    public AdvancedGeneralPanel(DocumentEditor editor) {
        super(editor);
        this.connector = editor.getConnector();
        this.barItem.setImage(UIImage.of(ModTextures.TAB_ICONS).uv(192, 0).fixed(16, 16).build());
        this.setup();
    }

    private void setup() {

        addHeader();

        addGroup(translatable("transform"), builder -> {
            builder.vector(translatable("location"), connector.location, Group.Unit.POINT);
            builder.vector(translatable("rotation"), connector.rotation, Group.Unit.DEGREES);
            //builder.vector(translatable("pivot"), connector.pivot, Group.Unit.POINT);
            builder.vector(translatable("scale"), connector.scale, Group.Unit.SCALE);
        });

        addGroup(translatable("properties"), builder -> {
            builder.bool(translatable("enabled"), connector.enabled);
            builder.bool(translatable("mirror"), connector.mirror);
        });
    }

    private void addHeader() {
        var headerView = new AdvancedHeaderView(connector.name, connector.part, new CGRect(0, 0, 200, 24));
        connector.lock.addObserver(it -> headerView.setEditable(!it));
        headerView.setPicker((sender) -> {
            var rect = new CGRect(0, 0, 242, 208);
            var transform = CGAffineTransform.createScale(0.5f, 0.5f);
            var pickerView = new PartPickerView(rect);
            pickerView.setAutoresizingMask(UIView.AutoresizingMask.flexibleRightMargin | UIView.AutoresizingMask.flexibleBottomMargin);
            pickerView.setContents(UIImage.of(ModTextures.MENUS).uv(0, 0).fixed(44, 44).clip(4, 4, 4, 4).build());
            pickerView.setTransform(transform);
            pickerView.setSelectedPart(connector.part.get());
            pickerView.setChangeListener(connector.part::set);
            pickerView.setHistorySkins(connector.getEditor().getHistory());
            pickerView.setFilter(it -> it.getType() == SkinTypes.ADVANCED);
            pickerView.setImporter(this::pickAction);
            pickerView.showInView(sender);
        });
        addContent(headerView);
    }

    private void pickAction() {
        var node = connector.getNode();
        var window = Objects.safeCast(window(), AdvancedBuilderWindow.class);
        if (node == null || window == null) {
            return;
        }
        window.importNewSkin(SkinTypes.ADVANCED, skin -> {
            var blockEntity = editor.getBlockEntity();
            NetworkManager.sendToServer(new AdvancedImportPacket(blockEntity, skin, node.getId()));
        });
    }
}
