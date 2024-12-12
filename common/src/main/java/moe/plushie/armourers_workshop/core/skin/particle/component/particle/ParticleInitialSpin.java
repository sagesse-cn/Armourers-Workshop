package moe.plushie.armourers_workshop.core.skin.particle.component.particle;

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
}
