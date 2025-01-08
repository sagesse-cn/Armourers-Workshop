package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.rate;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleBuilder;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

/**
 * Particles are spawned steadily during the lifetime of the emitter
 */
public class EmitterSteadyRate extends SkinParticleComponent {

    /// How often a particle is emitted, in particles/second.
    private final OpenPrimitive spawnRate;
    /// Maximum amount of particles that can be active before the emitter stops spawning new ones.
    private final OpenPrimitive maxParticles;

    public EmitterSteadyRate(OpenPrimitive spawnRate, OpenPrimitive maxParticles) {
        this.spawnRate = spawnRate;
        this.maxParticles = maxParticles;
    }

    public EmitterSteadyRate(IInputStream stream) throws IOException {
        this.spawnRate = stream.readPrimitiveObject();
        this.maxParticles = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(spawnRate);
        stream.writePrimitiveObject(maxParticles);
    }

    @Override
    public void applyToBuilder(SkinParticleBuilder builder) throws Exception {
        var spawnRate = builder.compile(this.spawnRate, 1.0);
        var maxParticles = builder.compile(this.maxParticles, 50.0);
        builder.renderEmitterPost((emitter, partialTicks, context) -> {
            if (!emitter.isRunning()) {
                return;
            }
            var rate = spawnRate.compute(context);
            var maxSize = maxParticles.compute(context);
            var targetSize = Math.ceil(rate * emitter.getTime());
            // create up to a specified size of particles.
            var size = emitter.getParticles().size();
            for (int i = size; i < targetSize && i < maxSize; i++) {
                emitter.spawnParticle();
            }
        });
    }
}
