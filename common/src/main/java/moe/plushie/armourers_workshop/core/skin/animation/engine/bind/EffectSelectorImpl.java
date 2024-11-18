package moe.plushie.armourers_workshop.core.skin.animation.engine.bind;

import moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector.EffectSelector;
import net.minecraft.world.effect.MobEffectInstance;

public class EffectSelectorImpl implements EffectSelector {

    protected MobEffectInstance effect;

    public EffectSelectorImpl apply(MobEffectInstance effect) {
        this.effect = effect;
        return this;
    }

    @Override
    public int getLevel() {
        return effect.getAmplifier() + 1;
    }
}
