package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.core.skin.molang.MolangVirtualMachine;
import moe.plushie.armourers_workshop.core.skin.molang.thirdparty.AWBinding;
import moe.plushie.armourers_workshop.core.skin.molang.thirdparty.YSMBinding;

public class ModMolangBindings {

    public static void init() {
        // add aw molang support.
        MolangVirtualMachine.register("aw2", new AWBinding());

        // add `Yes Steve Model` molang compat.
        MolangVirtualMachine.register("ysm", new YSMBinding());
    }
}
