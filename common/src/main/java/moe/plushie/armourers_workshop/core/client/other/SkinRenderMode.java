package moe.plushie.armourers_workshop.core.client.other;

import java.util.Stack;

public enum SkinRenderMode {

    GUI,
    NORMAL;

    private static final Stack<SkinRenderMode> STACK = new Stack<>();

    public static boolean inGUI() {
        if (!STACK.isEmpty()) {
            return STACK.peek() == GUI;
        }
        return false;
    }

    public static void push(SkinRenderMode mode) {
        STACK.push(mode);
    }

    public static void pop() {
        STACK.pop();
    }
}
