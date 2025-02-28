package moe.plushie.armourers_workshop.library.client.gui.widget;


import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIFont;
import com.apple.library.uikit.UIScrollView;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.core.utils.Collections;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ReportList extends UIScrollView {

    private final ArrayList<GuiDetailListColumn> columns = new ArrayList<>();
    private final ArrayList<GuiDetailListItem> items = new ArrayList<>();

    protected int selectedIndex;
    protected int contentHeight = 0;

    protected UIFont font;
    protected IEventListener listener;

    private float lastWidth;

    public ReportList(CGRect frame) {
        super(frame);
        this.font = UIFont.systemFont();
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        float width = bounds().width();
        if (lastWidth != width) {
            lastWidth = width;
            this.contentHeight = 0;
            this.items.forEach(item -> {
                item.layout(0, contentHeight, width - 2, 10);
                contentHeight += item.contentHeight + 1;
            });
            setContentSize(new CGSize(0, contentHeight));
        }
    }

    public void addColumn(String name, int width) {
        columns.add(new GuiDetailListColumn(name, width));
    }

    public GuiDetailListColumn getColumn(int index) {
        if (index >= 0 & index < columns.size()) {
            return columns.get(index);
        }
        return null;
    }

    public void removeColumn(int index) {
        columns.remove(index);
    }

    public void clearColumns() {
        columns.clear();
    }

    public void addItem(String... names) {
        float width = bounds().width;
        GuiDetailListItem item = new GuiDetailListItem(names);
        item.layout(0, contentHeight, width - 2, 10);
        items.add(item);
        addSubview(item);
        contentHeight += item.contentHeight + 1;
        setContentSize(new CGSize(0, contentHeight));
    }

    public GuiDetailListItem getItem(int index) {
        if (index >= 0 & index < items.size()) {
            return items.get(index);
        }
        return null;
    }

    public void removeItem(int index) {
        items.get(index).removeFromSuperview();
        items.remove(index);
    }

    public void clearItems() {
        items.clear();
    }

//    @Override
//    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float p_230431_4_) {
//        if (!visible) {
//            return;
//        }
//        this.isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
////        RenderUtils.bind(RenderUtils.TEX_WIDGETS);
////        GuiUtils.drawContinuousTexturedBox(poseStack, x, y, 0, 46, width, height, 200, 20, 2, 3, 2, 2, 0);
//        RenderSystem.addClipRect(x, y, width, height);
//        int dy = -scrollAmount;
//        for (GuiDetailListItem item : items) {
//            if (RenderSystem.inScissorRect(x + 1, y + 1 + dy, item.contentWidth, item.contentHeight)) {
//                poseStack.pushPose();
//                poseStack.translate(x + 1, y + 1 + dy, 0);
//                item.render(poseStack, mouseX, mouseY, p_230431_4_);
//                poseStack.popPose();
//            }
//            dy += item.contentHeight + 1;
//        }
//        RenderSystem.removeClipRect();
//    }
//
//    public int getMaxScroll() {
//        return Math.max(contentHeight - height, 0);
//    }
//
//    public int getScrollAmount() {
//        return scrollAmount;
//    }
//
//    public void setScrollAmount(int scrollAmount) {
//        int oldScrollAmount = this.scrollAmount;
//        this.scrollAmount = OpenMath.clamp(scrollAmount, 0, this.getMaxScroll());
//        if (this.listener != null && oldScrollAmount != this.scrollAmount) {
//            this.listener.listDidScroll(this, this.scrollAmount);
//        }
//    }
//
//    @Override
//    public boolean mouseClicked(double mouseX, double mouseY, int p_231044_5_) {
//        if (!isMouseOver(mouseX, mouseY)) {
//            return false;
//        }
//        int dy = -scrollAmount;
//        for (int i = 0; i < items.size(); ++i) {
//            GuiDetailListItem item = items.get(i);
//            int y0 = y + 1 + dy;
//            if (y0 <= mouseY && mouseY < (y0 + item.contentHeight + 1)) {
//                if (listener != null) {
//                    listener.listDidSelect(this, i);
//                }
//                return true;
//            }
//            dy += item.contentHeight + 1;
//        }
//        return false;
//    }
//
//    public boolean mouseScrolled(double p_231043_1_, double p_231043_3_, double p_231043_5_) {
//        this.setScrollAmount(this.getScrollAmount() - (int) (p_231043_5_ * height / 4));
//        return true;
//    }


    @Override
    protected void didScroll() {
        super.didScroll();
        if (this.listener != null) {
            this.listener.listDidScroll(this, contentOffset);
        }
    }

    public ReportList asReportList() {
        return this;
    }

    public int getContentHeight() {
        return contentHeight;
    }

    public IEventListener getListener() {
        return listener;
    }

    public void setListener(IEventListener listener) {
        this.listener = listener;
    }

    public interface IEventListener {

        void listDidSelect(ReportList reportList, int index);

        void listDidScroll(ReportList reportList, CGPoint contentOffset);
    }

    public static class GuiDetailListColumn {

        private final String name;
        private final float width;

        public GuiDetailListColumn(String name, float width) {
            this.name = name;
            this.width = width;
        }

        public String getName() {
            return name;
        }

        public float getWidth(float listWidth) {
            return width;
        }
    }

    public class GuiDetailListItem extends UIView {

        public List<NSString> names;

        public HashMap<Integer, List<NSString>> wrappedTextLines = new HashMap<>();

        public float contentWidth = 0;
        public float contentHeight = 0;

        public GuiDetailListItem(String[] names) {
            super(CGRect.ZERO);
            this.names = Collections.compactMap(names, NSString::new);
        }

        @Override
        public void mouseDown(UIEvent event) {
            super.mouseDown(event);
            if (listener != null) {
                listener.listDidSelect(asReportList(), items.indexOf(this));
            }
        }

        public void layout(float x, float y, float itemWidth, float itemHeight) {
            wrappedTextLines.clear();
            int xOffset = 0;
            for (int i = 0; i < names.size(); i++) {
                float columnWidth = 10;
                NSString name = names.get(i);
                GuiDetailListColumn column = getColumn(i);
                if (column != null) {
                    columnWidth = column.getWidth(itemWidth);
                    if (columnWidth == -1) {
                        columnWidth = itemWidth - 2 - xOffset;
                    }
                    if (!name.isEmpty()) {
                        List<NSString> lines = name.split(font, columnWidth);
                        itemHeight = Math.max(itemHeight, lines.size() * 10);
                        wrappedTextLines.put(i, lines);
                    }
                }
                xOffset += columnWidth + 1;
            }
            this.contentWidth = itemWidth;
            this.contentHeight = itemHeight;

            setFrame(new CGRect(x + 1, y + 1, contentWidth, contentHeight));
        }

        @Override
        public void render(CGPoint point, CGGraphicsContext context) {
            super.render(point, context);
            int xOffset = 0;
            for (int i = 0; i < names.size(); i++) {
                float columnWidth = 10;
                GuiDetailListColumn column = getColumn(i);
                if (column != null) {
                    columnWidth = column.getWidth(contentWidth);
                    if (columnWidth == -1) {
                        columnWidth = contentWidth - 2 - xOffset;
                    }
                    context.fillRect(xOffset, 0, xOffset + columnWidth, contentHeight, 0xCC808080);
                    List<NSString> lines = wrappedTextLines.get(i);
                    if (lines != null) {
                        int dy = 0;
                        for (NSString line : lines) {
                            context.drawText(line, 1 + xOffset, 1 + dy, 0xffffff);
                            dy += 10;
                        }
                    } else {
                        context.drawText(names.get(i), 1 + xOffset, 1, 0xffffff);
                    }
                    xOffset += columnWidth + 1;
                }
            }
        }
    }
}
