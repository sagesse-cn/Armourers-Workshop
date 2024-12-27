package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.rate;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleBuilder;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

/**
 * All particles are spawned instantly at the start of the emitter's lifetime
 */
public class EmitterInstantRate extends SkinParticleComponent {

    private final OpenPrimitive particles;

    public EmitterInstantRate(OpenPrimitive particles) {
        this.particles = particles;
    }

    public EmitterInstantRate(IInputStream stream) throws IOException {
        this.particles = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(particles);
    }

    @Override
    public void applyToBuilder(SkinParticleBuilder builder) throws Exception {
        var particles = builder.compile(this.particles, 10.0);
        builder.updateEmitter((emitter, context) -> {
            var time = emitter.getTime();
            if (!emitter.isRunning() || time != 0.0) {
                return;
            }
            int count = particles.evaluate(context).getAsInt();
            for (int i = 0; i < count; i++) {
                emitter.spawnParticle();
            }
        });
    }
}
