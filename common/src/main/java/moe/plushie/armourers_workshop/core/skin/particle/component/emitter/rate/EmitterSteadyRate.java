package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.rate;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

/**
 * Particles are spawned steadily during the lifetime of the emitter
 */
public class EmitterSteadyRate extends SkinParticleComponent {

    private final OpenPrimitive spawnRate;
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
}
