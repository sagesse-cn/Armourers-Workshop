package moe.plushie.armourers_workshop.core.skin.molang.thirdparty;

import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.ContextBinding;
import moe.plushie.armourers_workshop.core.skin.molang.thirdparty.function.PlayAnimationFunction;

public class AWBinding extends ContextBinding {

    public AWBinding() {

        function("play_anim", PlayAnimationFunction::new);
    }
}
