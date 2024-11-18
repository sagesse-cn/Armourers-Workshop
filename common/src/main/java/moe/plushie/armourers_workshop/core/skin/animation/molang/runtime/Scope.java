package moe.plushie.armourers_workshop.core.skin.animation.molang.runtime;

import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Result;
import org.jetbrains.annotations.Nullable;

public class Scope {

    private Node current;

    public Scope push(Kind kind) {
        current = new Node(kind, current);
        return this;
    }

    public Result pop() {
        // only pop when push.
        if (current != null) {
            var node = current;
            current = current.parent;
            return node.returnValue;
        }
        return Result.NULL;
    }

    public Scope beginEnumerate() {
        return push(Kind.ENUMERATE);
    }

    public Result endEnumerate() {
        return pop();
    }


    public State interrupt() {
        if (current != null) {
            return current.state;
        }
        return State.NONE;
    }

    public void setInterrupt(State state) {
        setInterrupt(state, Result.NULL);
    }

    public void setInterrupt(State state, Result result) {
        var target = state.kind();
        var node = current;
        while (node != null) {
            node.state = state;
            if (node.kind == target) {
                node.returnValue = result;
                break;
            }
            node = node.parent;
        }
    }

    public boolean isContinueOrBreakOrReturn() {
        return interrupt() != State.NONE;
    }

    public boolean isBreakOrReturn() {
        return interrupt() == State.BREAK || interrupt() == State.RETURN;
    }


    public enum Kind {
        BLOCK,
        ENUMERATE,
    }

    public enum State {
        NONE,
        BREAK,
        CONTINUE,
        RETURN;

        @Nullable
        public Kind kind() {
            return switch (this) {
                case RETURN -> Kind.BLOCK;
                case BREAK, CONTINUE -> Kind.ENUMERATE;
                case NONE -> null;
            };
        }
    }

    private static class Node {

        private final Kind kind;
        private final Node parent;

        private State state = State.NONE;
        private Result returnValue = Result.NULL;

        Node(Kind kind, Node parent) {
            this.kind = kind;
            this.parent = parent;
        }
    }
}
