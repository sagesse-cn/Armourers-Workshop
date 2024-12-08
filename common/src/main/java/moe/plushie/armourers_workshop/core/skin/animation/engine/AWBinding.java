package moe.plushie.armourers_workshop.core.skin.animation.engine;

import moe.plushie.armourers_workshop.core.skin.animation.engine.function.PlayAnimationFunction;
import moe.plushie.armourers_workshop.core.skin.animation.molang.bind.ContextBinding;

public class AWBinding extends ContextBinding {

    public AWBinding() {

        function("play_anim", PlayAnimationFunction::new);
    }
}
