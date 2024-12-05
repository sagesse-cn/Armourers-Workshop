package moe.plushie.armourers_workshop.core.client.animation;

import java.util.List;

public class AnimationEffectState {

    private final String name;

    private AnimatedPointValue.Effect value;
    private Object result;

    public AnimationEffectState(String name) {
        this.name = name;
    }

    public void reset() {
        setValue(null, null);
    }

    public void setValue(AnimatedPointValue.Effect effect, Object result) {
        this.clean(this.result);
        this.value = effect;
        this.result = result;
    }

    public AnimatedPointValue.Effect getValue() {
        return this.value;
    }

    public String getName() {
        return name;
    }

    private void clean(Object result) {
        // we need expand the multiple results.
        if (result instanceof List<?> list) {
            for (var value : list) {
                clean(value);
            }
        }
        // we need cancel it.
        if (result instanceof Runnable action) {
            action.run();
        }
    }
}
