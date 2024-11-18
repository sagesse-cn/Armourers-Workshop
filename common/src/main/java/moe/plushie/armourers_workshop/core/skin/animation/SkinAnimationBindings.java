package moe.plushie.armourers_workshop.core.skin.animation;

import moe.plushie.armourers_workshop.core.skin.animation.engine.CustomBinding;
import moe.plushie.armourers_workshop.core.skin.animation.molang.MolangVirtualMachine;

public class SkinAnimationBindings {

    public static void init() {
        // add aw molang support.
        MolangVirtualMachine.register("aw2", new CustomBinding());

        // add `Yes Steve Model` molang compat.
        MolangVirtualMachine.register("ysm", new CustomBinding());
    }
}
