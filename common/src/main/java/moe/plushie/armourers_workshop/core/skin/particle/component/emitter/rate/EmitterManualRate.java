package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.rate;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleBuilder;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

/**
 * Particles are spawned manually, independent from the emitter. This is used for some vanilla effects, and for particle effects that are triggered by events using the "particle" type.
 */
public class EmitterManualRate extends SkinParticleComponent {

    private final OpenPrimitive maxParticles;

    public EmitterManualRate(OpenPrimitive maxParticles) {
        this.maxParticles = maxParticles;
    }

    public EmitterManualRate(IInputStream stream) throws IOException {
        this.maxParticles = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(maxParticles);
    }

    @Override
    public void applyToBuilder(SkinParticleBuilder builder) throws Exception {

    }
}
