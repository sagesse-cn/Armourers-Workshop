package moe.plushie.armourers_workshop.core.skin.particle.component.particle;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

public class ParticleInitialSpeed extends SkinParticleComponent {

    private final OpenPrimitive speed;

    public ParticleInitialSpeed(OpenPrimitive speed) {
        this.speed = speed;
    }

    public ParticleInitialSpeed(IInputStream stream) throws IOException {
        this.speed = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(speed);
    }
}
