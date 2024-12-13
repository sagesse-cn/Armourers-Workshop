package moe.plushie.armourers_workshop.core.skin.particle;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;
import net.minecraft.world.level.block.Block;

public interface SkinParticleBuilder {

    Expression compile(OpenPrimitive value, double defaultValue) throws Exception;


    Block getBlock(String blockId);


    void applyEmitter(EmitterApplyEvent event);

    void updateEmitter(EmitterUpdateEvent event);

    void renderEmitterPre(EmitterRenderEvent event);

    void renderEmitterPost(EmitterRenderEvent event);


    void applyParticle(ParticleApplyEvent event);

    void updateParticle(ParticleUpdateEvent event);

    void renderParticlePre(ParticleRenderEvent event);

    void renderParticlePost(ParticleRenderEvent event);


    interface EmitterApplyEvent {
        void accept(SkinParticleEmitter emitter, ExecutionContext context);
    }

    interface EmitterUpdateEvent {
        void accept(SkinParticleEmitter emitter, ExecutionContext context);
    }

    interface EmitterRenderEvent {
        void accept(SkinParticleEmitter emitter, float partialTicks, ExecutionContext context);
    }

    interface ParticleApplyEvent {
        void accept(SkinParticleEmitter emitter, SkinParticle particle, ExecutionContext context);
    }

    interface ParticleUpdateEvent {
        void accept(SkinParticleEmitter emitter, SkinParticle particle, ExecutionContext context);
    }

    interface ParticleRenderEvent {
        void accept(SkinParticleEmitter emitter, SkinParticle particle, float partialTicks, ExecutionContext context);
    }
}
