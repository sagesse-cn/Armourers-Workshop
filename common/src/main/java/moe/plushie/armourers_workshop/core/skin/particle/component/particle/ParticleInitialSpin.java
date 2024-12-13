package moe.plushie.armourers_workshop.core.skin.particle.component.particle;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleBuilder;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

public class ParticleInitialSpin extends SkinParticleComponent {

    private final OpenPrimitive rotation;
    private final OpenPrimitive rotationRate;

    public ParticleInitialSpin(OpenPrimitive rotation, OpenPrimitive rotationRate) {
        this.rotation = rotation;
        this.rotationRate = rotationRate;
    }

    public ParticleInitialSpin(IInputStream stream) throws IOException {
        this.rotation = stream.readPrimitiveObject();
        this.rotationRate = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(rotation);
        stream.writePrimitiveObject(rotationRate);
    }

    @Override
    public void applyToBuilder(SkinParticleBuilder builder) throws Exception {
        var rotation = builder.compile(this.rotation, 0.0);
        var rotationRate = builder.compile(this.rotationRate, 0.0);
        builder.applyParticle((emitter, particle, context) -> {
            var rot = rotation.compute(context);
            var velocity = rotationRate.compute(context);
            // TODO: NO IMPL @SAGESSE
//            particle.initialRotation = (float) rot;
//            particle.rotationVelocity = (float) velocity / 20;
        });
    }
}
