package moe.plushie.armourers_workshop.core.client.animation.handler;

import moe.plushie.armourers_workshop.core.client.animation.AnimatedPointValue;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;

public class AnimationInstructHandler implements AnimatedPointValue.Effect {

    private final Expression expression;

    public AnimationInstructHandler(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Runnable apply(ExecutionContext context) {
        if (ModConfig.Client.enableAnimationDebug) {
            ModLog.debug("execute {}", this);
        }
        expression.evaluate(context);
        return null;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "expr", expression);
    }
}
