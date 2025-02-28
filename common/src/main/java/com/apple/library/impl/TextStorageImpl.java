package com.apple.library.impl;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSRange;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextPosition;
import com.apple.library.foundation.NSTextRange;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIFont;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

@Environment(EnvType.CLIENT)
public class TextStorageImpl {

    public CGPoint offset = CGPoint.ZERO;
    public int maxLength = 1000;

    public BiConsumer<CGRect, CGSize> sizeDidChange = (c, s) -> {
    };
    public BiConsumer<String, String> valueDidChange = (o, n) -> {
    };
    public BiPredicate<NSRange, String> valueShouldChange = (o, n) -> true;
    public Runnable selectionDidChange = () -> {
    };

    private String value = "";

    private NSString placeholder;
    private UIColor placeholderColor;

    private UIFont font;
    private UIColor textColor;

    private CGSize boundingSize = CGSize.ZERO;

    private NSTextPosition highlightPos = NSTextPosition.ZERO;
    private NSTextPosition cursorPos = NSTextPosition.ZERO;

    private int lineSpacing = 1;
    private boolean isFocused = false;
    private long cursorTimestamp = 0;

    private CGRect cursorRect = CGRect.ZERO;
    private Collection<CGRect> highlightedRects;
    private UIFont cachedFont;

    private Collection<TextLine> cachedTextLines;

    public void insertText(String inputText) {
        var oldValue = value;
        var replacementText = formattedString(inputText);
        var range = selectionRange();
        if (!valueShouldChange.test(range, replacementText)) {
            return;
        }
        var length = replacementText.length();
        if (maxLength > 0) {
            var remaining = Math.max((maxLength - (oldValue.length() - range.length)), 0);
            if (remaining < length) {
                replacementText = replacementText.substring(0, remaining);
                length = remaining;
            }
        }
        var startIndex = range.startIndex();
        var endIndex = range.endIndex();
        setValue((new StringBuilder(oldValue)).replace(startIndex, endIndex, replacementText).toString());
        setCursorAndHighlightPos(NSTextPosition.forward(startIndex + length));
        valueDidChange.accept(oldValue, value);
    }

    public void deleteText(TextTokenizer tokenizer, int count) {
        if (value.isEmpty()) {
            return;
        }
        if (!cursorPos.equals(highlightPos)) {
            insertText("");
            return;
        }
        var advancedIndex = tokenizer.advance(value, cursorPos.value, count);
        var startIndex = Math.min(advancedIndex, cursorPos.value);
        var endIndex = Math.max(advancedIndex, cursorPos.value);
        if (startIndex == endIndex) {
            return;
        }
        var oldValue = value;
        if (!valueShouldChange.test(NSRange.of(startIndex, endIndex), "")) {
            return;
        }
        setValue((new StringBuilder(oldValue)).delete(startIndex, endIndex).toString());
        setCursorAndHighlightPos(NSTextPosition.forward(startIndex));
        valueDidChange.accept(oldValue, value);
    }

    private void setCursorPos(NSTextPosition pos) {
        this.cursorPos = clamp(pos, beginOfDocument(), endOfDocument());
        this.cursorTimestamp = System.currentTimeMillis();
        this.setNeedsRemakeTextLine();
    }

    private void setHighlightPos(NSTextPosition pos) {
        this.highlightPos = clamp(pos, beginOfDocument(), endOfDocument());
        this.setNeedsRemakeTextLine();
    }

    public void sizeToFit() {
        remakeTextLineIfNeeded(boundingSize, font());
    }

    public void render(CGPoint point, CGGraphicsContext context) {
        // auto resize before the render.
        if (cachedTextLines == null) {
            sizeToFit();
        }
        var font = cachedFont;
        var textColor = defaultTextColor();
        if (cachedFont == null || cachedTextLines == null) {
            return;
        }
        context.saveGraphicsState();
        context.translateCTM(offset.x, offset.y, 0);

        if (placeholder != null && cachedTextLines.isEmpty()) {
            var placeholderColor = defaultPlaceholderColor();
            context.drawText(placeholder, 1, 0, placeholderColor, true, font, 0);
        }
        for (var line : cachedTextLines) {
            context.drawText(line.formattedText, line.rect.x, line.rect.y, textColor, true, font, 0);
            context.strokeDebugRect(line.index, line.rect);
        }

        renderHighlightedRectIfNeeded(context);
        renderCursorIfNeeded(context);

        context.restoreGraphicsState();
    }

    public void renderCursorIfNeeded(CGGraphicsContext context) {
        if (!isFocused || cursorRect == null) {
            return;
        }
        long diff = (System.currentTimeMillis() - cursorTimestamp) % 1200;
        if (diff > 600) {
            return;
        }
        context.fillRect(cursorRect, AppearanceImpl.TEXT_CURSOR_COLOR);
    }

    public void renderHighlightedRectIfNeeded(CGGraphicsContext context) {
        if (!isFocused || highlightedRects == null || highlightedRects.isEmpty()) {
            return;
        }
        var pose = context.state().ctm().last();
        var buffers = AbstractBufferSource.buffer();
        var builder = buffers.getBuffer(SkinRenderType.GUI_HIGHLIGHTED_TEXT);
        for (var rect : highlightedRects) {
            builder.vertex(pose, rect.minX(), rect.maxY(), 0).endVertex();
            builder.vertex(pose, rect.maxX(), rect.maxY(), 0).endVertex();
            builder.vertex(pose, rect.maxX(), rect.minY(), 0).endVertex();
            builder.vertex(pose, rect.minX(), rect.minY(), 0).endVertex();
        }
        context.setBlendColor(AppearanceImpl.TEXT_HIGHLIGHTED_COLOR);
        buffers.endBatch();
        context.setBlendColor(UIColor.WHITE);
    }

    public String value() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        this.setNeedsRemakeTextLine();
    }

    public UIColor textColor() {
        return textColor;
    }

    public void setTextColor(UIColor textColor) {
        this.textColor = textColor;
    }

    public NSString placeholder() {
        return placeholder;
    }

    public void setPlaceholder(NSString placeholder) {
        this.placeholder = placeholder;
    }

    public UIColor placeholderColor() {
        return placeholderColor;
    }

    public void setPlaceholderColor(UIColor placeholderColor) {
        this.placeholderColor = placeholderColor;
    }

    public UIFont font() {
        if (font != null) {
            return font;
        }
        return UIFont.systemFont();
    }

    public void setFont(UIFont font) {
        this.font = font;
        this.setNeedsRemakeTextLine();
    }

    public void setBoundingSize(CGSize size) {
        this.boundingSize = size;
        this.setNeedsRemakeTextLine();
    }

    public void setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public NSTextPosition beginOfDocument() {
        return NSTextPosition.ZERO;
    }

    public NSTextPosition endOfDocument() {
        return NSTextPosition.forward(value.length());
    }

    public NSTextPosition positionAtPoint(CGPoint point) {
        // not layout or renderer.
        if (cachedFont == null || cachedTextLines == null) {
            return null;
        }
        var selectedLines = new ArrayList<TextLine>();
        if (isMultipleLineMode()) {
            for (var line : cachedTextLines) {
                if (line.insideAtY(point.y)) {
                    selectedLines.add(line);
                }
            }
        } else {
            selectedLines.addAll(cachedTextLines);
        }
        for (var line : selectedLines) {
            if (line.insideAtX(point.x)) {
                var value = cachedFont._getTextByWidth(line.text, (point.x - line.rect.x));
                int index = line.range.startIndex() + value.length();
                return NSTextPosition.forward(index);
            }
        }
        if (!selectedLines.isEmpty()) {
            var line = selectedLines.get(selectedLines.size() - 1);
            return NSTextPosition.backward(line.range.endIndex());
        }
        return endOfDocument();
    }

    public boolean isFocused() {
        return isFocused;
    }

    public void setFocused(boolean focused) {
        isFocused = focused;
        // when focus is lose, we must automatically cancel the highlighted.
        if (!isFocused) {
            setHighlightPos(cursorPos);
        }
    }

    public boolean isMultipleLineMode() {
        return boundingSize.width != 0;
    }

    public String highlightedText() {
        NSTextPosition startPos = min(cursorPos, highlightPos);
        NSTextPosition endPos = max(cursorPos, highlightPos);
        return value.substring(startPos.value, endPos.value);
    }

    public CGRect cursorRect() {
        return cursorRect;
    }

    public void setCursorAndHighlightPos(NSTextPosition pos) {
        setCursorAndHighlightPos(pos, pos);
    }

    public void setCursorAndHighlightPos(NSTextPosition cursorPos, NSTextPosition highlightPos) {
        setCursorPos(cursorPos);
        setHighlightPos(highlightPos);
        selectionDidChange.run();
    }

    public void checkCursorAndHighlightPos() {
        NSTextPosition cursorPos1 = clamp(cursorPos, beginOfDocument(), endOfDocument());
        NSTextPosition highlightPos1 = clamp(highlightPos, beginOfDocument(), endOfDocument());
        if (cursorPos1.equals(cursorPos) && highlightPos1.equals(highlightPos)) {
            return;
        }
        setCursorAndHighlightPos(cursorPos1, highlightPos1);
    }

    public NSTextRange selectedTextRange() {
        return new NSTextRange(highlightPos, cursorPos);
    }

    public void setSelectedTextRange(NSTextRange range) {
        setCursorAndHighlightPos(range.end, range.start);
    }

    private void setNeedsRemakeTextLine() {
        cachedTextLines = null;
    }

    private void remakeTextLineIfNeeded(CGSize boundingSize, UIFont font) {
        if (cachedTextLines != null) {
            return;
        }
        float x = 0;
        float y = 0;
        float lineHeight = font.lineHeight() + lineSpacing;
        float maxHeight = 0;
        int lineIndex = 0;

        NSRange selection = NSRange.of(cursorPos.value, highlightPos.value);

        var lines = split(value, selection, font, boundingSize.width);
        for (var line : lines) {
            var width = font._getTextWidth(line.formattedText.characters());
            if (lineIndex != line.index) {
                lineIndex = line.index;
                y += maxHeight;
                x = 0;
            }
            line.rect = new CGRect(x, y, width, lineHeight);
            maxHeight = lineHeight;
            x += width;
        }

        var lastLineRect = new CGRect(0, y + maxHeight, 0, lineHeight);
        remakeHighlightedLines(lines, boundingSize, lastLineRect);
        cursorRect = cursorRectAtIndex(cursorPos, lines, lastLineRect);

        cachedTextLines = lines;
        cachedFont = font;

        sizeDidChange.accept(cursorRect, new CGSize(x, Math.max(lastLineRect.y, lineHeight)));
    }

    private void remakeHighlightedLines(List<TextLine> lines, CGSize boundingSize, CGRect lastLineRect) {
        if (highlightPos.equals(cursorPos)) {
            highlightedRects = null;
            return;
        }
        var startPoint = pointAtIndex(min(cursorPos, highlightPos), lines, lastLineRect, false);
        var endPoint = pointAtIndex(max(cursorPos, highlightPos), lines, lastLineRect, false);
        if (startPoint.y == endPoint.y) {
            var rect = new CGRect(startPoint.x, startPoint.y, endPoint.x - startPoint.x, lastLineRect.height);
            highlightedRects = Collections.singletonList(rect);
            return;
        }
        var rects = new ArrayList<CGRect>();
        rects.add(new CGRect(startPoint.x, startPoint.y, boundingSize.width - startPoint.x, lastLineRect.height));
        var my = startPoint.y + lastLineRect.height;
        if (my != endPoint.y) {
            rects.add(new CGRect(0, my, boundingSize.width, endPoint.y - my));
        }
        rects.add(new CGRect(0, endPoint.y, endPoint.x, lastLineRect.height));
        highlightedRects = rects;
    }

    private NSRange selectionRange() {
        return NSRange.of(cursorPos.value, highlightPos.value);
    }

    private String formattedString(String value) {
        if (isMultipleLineMode()) {
            return value;
        }
        var stringBuilder = new StringBuilder();
        for (var c : value.toCharArray()) {
            if (!isAllowedChatCharacter(c)) continue;
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    private CGPoint pointAtIndex(NSTextPosition pos, Iterable<TextLine> lines, CGRect lastLineRect, boolean enabledBackward) {
        // If you are backward mode, we preferentially check endIndex.
        if (pos.isBackward && enabledBackward) {
            for (var line : lines) {
                if (line.range.endIndex() == pos.value) {
                    return line.endPoint();
                }
            }
        }
        for (var line : lines) {
            if (line.range.startIndex() == pos.value) {
                return line.startPoint();
            }
        }
        for (var line : lines) {
            if (line.range.endIndex() == pos.value) {
                return line.endPoint();
            }
        }
        return new CGPoint(lastLineRect.x, lastLineRect.y);
    }

    private CGRect cursorRectAtIndex(NSTextPosition pos, Iterable<TextLine> lines, CGRect lastLineRect) {
        var point = pointAtIndex(pos, lines, lastLineRect, true);
        return new CGRect(point.x, point.y - 2, 1, lastLineRect.height + 2);
    }

    private int defaultPlaceholderColor() {
        if (placeholderColor != null) {
            return placeholderColor.getRGB();
        }
        return 0xff333333;
    }

    private int defaultTextColor() {
        if (textColor != null) {
            return textColor.getRGB();
        }
        return 0xffffffff;
    }

    private List<TextLine> split(String value, UIFont font, float maxWidth) {
        if (value.isEmpty()) {
            return Collections.emptyList();
        }
        if (maxWidth == 0) {
            return Collections.singletonList(new TextLine(0, 0, value.length(), value));
        }
        var counter = new AtomicInteger();
        return font._splitLines(value, maxWidth, false, (substring, begin, end) -> new TextLine(counter.getAndIncrement(), begin, end, substring));
    }

    private ArrayList<TextLine> split(String value, NSRange selection, UIFont font, float maxWidth) {
        var wrappedTextLines = split(value, font, maxWidth);
        if (wrappedTextLines.isEmpty()) {
            return new ArrayList<>();
        }
        var resolvedTextLines = new ArrayList<TextLine>(wrappedTextLines.size() + 2);
        for (var line : wrappedTextLines) {
            // a special case when has a blank line we can't to split it.
            if (line.range.intersects(selection) && !line.range.isEmpty()) {
                // split
                var lineIndex = line.index;
                var leftStartIndex = line.range.startIndex();
                var leftEndIndex = Math.max(leftStartIndex, selection.startIndex());
                var rightEndIndex = line.range.endIndex();
                var rightStartIndex = Math.min(selection.endIndex(), rightEndIndex);
                if (leftStartIndex != leftEndIndex) {
                    // left part
                    resolvedTextLines.add(new TextLine(lineIndex, leftStartIndex, leftEndIndex, value.substring(leftStartIndex, leftEndIndex)));
                }
                if (leftEndIndex != rightStartIndex) {
                    // highlight part
                    resolvedTextLines.add(new TextLine(lineIndex, leftEndIndex, rightStartIndex, value.substring(leftEndIndex, rightStartIndex)));
                }
                if (rightStartIndex != rightEndIndex) {
                    // right part
                    resolvedTextLines.add(new TextLine(lineIndex, rightStartIndex, rightEndIndex, value.substring(rightStartIndex, rightEndIndex)));
                }
                continue;
            }
            resolvedTextLines.add(line);
        }
        return resolvedTextLines;
    }

    NSTextPosition clamp(NSTextPosition value, NSTextPosition minValue, NSTextPosition maxValue) {
        if (value.value < minValue.value) {
            return minValue;
        }
        if (value.value > maxValue.value) {
            return maxValue;
        }
        return value;
    }

    NSTextPosition min(NSTextPosition value, NSTextPosition value1) {
        if (value.value < value1.value) {
            return value;
        }
        return value1;
    }

    NSTextPosition max(NSTextPosition value, NSTextPosition value1) {
        if (value.value > value1.value) {
            return value;
        }
        return value1;
    }

    void moveCursorTo(TextTokenizer tokenizer, int count, boolean selectMode) {
        var index = tokenizer.advance(value, cursorPos.value, count);
        moveCursorTo(NSTextPosition.forward(index), selectMode);
    }

    void moveCursorTo(NSTextPosition pos, boolean selectMode) {
        if (selectMode) {
            setCursorAndHighlightPos(pos, highlightPos);
        } else {
            setCursorAndHighlightPos(pos, pos);
        }
    }

    boolean isAllowedChatCharacter(char c) {
        return c != '§' && c >= ' ' && c != '\u007f';
    }

    interface TextTokenizer {

        TextTokenizer WORLD_AFTER = (value, index, step) -> {
            for (var k = 0; k < step; ++k) {
                var l = value.length();
                index = value.indexOf(' ', index);
                if (index == -1) {
                    index = l;
                } else {
                    while (index < l && value.charAt(index) == ' ') {
                        ++index;
                    }
                }
            }
            return index;
        };

        TextTokenizer WORLD_BEFORE = (value, index, step) -> {
            for (var k = 0; k < step; ++k) {
                while (index > 0 && value.charAt(index - 1) == ' ') {
                    --index;
                }
                while (index > 0 && value.charAt(index - 1) != ' ') {
                    --index;
                }
            }
            return index;
        };

        TextTokenizer CHAR_AFTER = (value, index, step) -> {
            var length = value.length();
            for (var k = 0; index < length && k < step; ++k) {
                if (Character.isHighSurrogate(value.charAt(index++)) && index < length && Character.isLowSurrogate(value.charAt(index))) {
                    ++index;
                }
            }
            return index;
        };

        TextTokenizer CHAR_BEFORE = (value, index, step) -> {
            for (var k = 0; index > 0 && k < step; ++k) {
                --index;
                if (Character.isLowSurrogate(value.charAt(index)) && index > 0 && Character.isHighSurrogate(value.charAt(index - 1))) {
                    --index;
                }
            }
            return index;
        };

        int advance(String value, int index, int step);
    }

    static class TextLine {

        final int index;

        final String text;
        final NSString formattedText;
        final NSRange range;

        CGRect rect = CGRect.ZERO;

        TextLine(int index, int startIndex, int endIndex, String text) {
            this.text = text;
            this.formattedText = new FormattedStringImpl(text);
            this.index = index;
            this.range = new NSRange(startIndex, endIndex - startIndex);
        }

        boolean insideAtX(float x) {
            return rect.minX() <= x && x < rect.maxX();
        }

        boolean insideAtY(float y) {
            return rect.minY() <= y && y < rect.maxY();
        }

        CGPoint startPoint() {
            return new CGPoint(rect.x, rect.y);
        }

        CGPoint endPoint() {
            return new CGPoint(rect.x + rect.width, rect.y);
        }
    }
}
