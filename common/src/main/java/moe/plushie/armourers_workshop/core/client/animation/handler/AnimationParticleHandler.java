package moe.plushie.armourers_workshop.core.client.animation.handler;

import moe.plushie.armourers_workshop.core.client.animation.AnimatedPointValue;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationPoint;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;

public class AnimationParticleHandler implements AnimatedPointValue.Effect {

    private final SkinAnimationPoint.Particle particle;

    public AnimationParticleHandler(SkinAnimationPoint.Particle particle) {
        this.particle = particle;
    }

    @Override
    public Runnable apply(ExecutionContext context) {
        if (ModConfig.Client.enableAnimationDebug) {
            ModLog.debug("start play {}", this);
        }
        return null;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "particle", particle);
    }
}
