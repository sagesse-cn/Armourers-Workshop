package com.apple.library.impl;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSTextPosition;
import org.lwjgl.glfw.GLFW;

import java.util.function.Predicate;

public class TextInputImpl {

    public Predicate<String> returnHandler;

    private boolean isEditable = true;
    private CGRect lastUserCursorRect;

    private final TextStorageImpl storage;

    public TextInputImpl(TextStorageImpl storage) {
        this.storage = storage;
    }

    public boolean mouseDown(CGPoint point) {
        var tx = Math.max(point.x - storage.offset.x, 0);
        var ty = Math.max(point.y - storage.offset.y, 0);
        var pos = storage.positionAtPoint(new CGPoint(tx, ty));
        if (pos != null) {
            storage.moveCursorTo(pos, InputManagerImpl.hasShiftDown());
        }
        return false;
    }

    public boolean keyDown(int key) {
        // some methods may rely on this info.
        boolean hasShiftDown = InputManagerImpl.hasShiftDown();
        boolean hasControlDown = InputManagerImpl.hasControlDown();
        // each input causes the user cursor to reset, even if it doesn't.
        var userCursorRect = lastUserCursorRect;
        lastUserCursorRect = null;
        // select all text
        if (InputManagerImpl.isSelectAll(key)) {
            storage.setCursorAndHighlightPos(storage.endOfDocument(), storage.beginOfDocument());
            return true;
        }
        // cut selected text.
        if (InputManagerImpl.isCut(key)) {
            InputManagerImpl.setClipboard(storage.highlightedText());
            if (isEditable) {
                storage.insertText("");
            }
            return true;
        }
        // copy selected text
        if (InputManagerImpl.isCopy(key)) {
            InputManagerImpl.setClipboard(storage.highlightedText());
            return true;
        }
        // paste some text into selected range.
        if (InputManagerImpl.isPaste(key)) {
            if (isEditable) {
                storage.insertText(InputManagerImpl.getClipboard());
            }
            return true;
        }
        if (InputManagerImpl.hasShortcutDown()) {
            key = InputManagerImpl.getShortcutKey(key);
        }
        switch (key) {
            case GLFW.GLFW_KEY_BACKSPACE: {
                if (isEditable) {
                    if (hasControlDown) {
                        storage.deleteText(TextStorageImpl.TextTokenizer.WORLD_BEFORE, 1);
                    } else {
                        storage.deleteText(TextStorageImpl.TextTokenizer.CHAR_BEFORE, 1);
                    }
                }
                return true;
            }
            case GLFW.GLFW_KEY_DELETE: {
                if (isEditable) {
                    if (hasControlDown) {
                        storage.deleteText(TextStorageImpl.TextTokenizer.WORLD_AFTER, 1);
                    } else {
                        storage.deleteText(TextStorageImpl.TextTokenizer.CHAR_AFTER, 1);
                    }
                }
                return true;
            }
            case GLFW.GLFW_KEY_INSERT:
            case GLFW.GLFW_KEY_PAGE_UP:
            case GLFW.GLFW_KEY_PAGE_DOWN: {
                return false;
            }
            case GLFW.GLFW_KEY_DOWN: {
                if (storage.isMultipleLineMode()) {
                    moveToNextLine(userCursorRect, 1, hasShiftDown);
                }
                return false;
            }
            case GLFW.GLFW_KEY_UP: {
                if (storage.isMultipleLineMode()) {
                    moveToNextLine(userCursorRect, -1, hasShiftDown);
                }
                return false;
            }
            case GLFW.GLFW_KEY_RIGHT: {
                if (hasControlDown) {
                    storage.moveCursorTo(TextStorageImpl.TextTokenizer.WORLD_AFTER, 1, hasShiftDown);
                } else {
                    storage.moveCursorTo(TextStorageImpl.TextTokenizer.CHAR_AFTER, 1, hasShiftDown);
                }
                return true;
            }
            case GLFW.GLFW_KEY_LEFT: {
                if (hasControlDown) {
                    storage.moveCursorTo(TextStorageImpl.TextTokenizer.WORLD_BEFORE, 1, hasShiftDown);
                } else {
                    storage.moveCursorTo(TextStorageImpl.TextTokenizer.CHAR_BEFORE, 1, hasShiftDown);
                }
                return true;
            }
            case GLFW.GLFW_KEY_HOME: {
                storage.moveCursorTo(storage.beginOfDocument(), hasShiftDown);
                return true;
            }
            case GLFW.GLFW_KEY_END: {
                storage.moveCursorTo(storage.endOfDocument(), hasShiftDown);
                return true;
            }
            case GLFW.GLFW_KEY_ENTER: {
                if (storage.isMultipleLineMode()) {
                    storage.insertText("\n");
                    return true;
                }
                if (returnHandler != null && returnHandler.test(storage.value())) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public boolean charTyped(char ch) {
        if (storage.isAllowedChatCharacter(ch)) {
            storage.insertText(Character.toString(ch));
            return true;
        }
        return false;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    private void moveToNextLine(CGRect userCursorRect, int step, boolean selectMode) {
        CGRect rect = storage.cursorRect();
        if (userCursorRect == null) {
            userCursorRect = rect;
        }
        if (rect == null) {
            return;
        }
        lastUserCursorRect = userCursorRect;
        NSTextPosition pos = storage.beginOfDocument();
        float ty = rect.midY() + rect.height * step;
        if (ty >= 0) {
            pos = storage.positionAtPoint(new CGPoint(userCursorRect.x, ty));
        }
        if (pos != null) {
            storage.moveCursorTo(pos, selectMode);
        }
    }

}
