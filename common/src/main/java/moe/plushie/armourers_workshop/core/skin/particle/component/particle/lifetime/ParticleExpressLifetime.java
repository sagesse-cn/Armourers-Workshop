package moe.plushie.armourers_workshop.core.skin.particle.component.particle.lifetime;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

public class ParticleExpressLifetime extends SkinParticleComponent {

    private final OpenPrimitive maxAge;
    private final OpenPrimitive expiration;

    public ParticleExpressLifetime(OpenPrimitive maxAge, OpenPrimitive expiration) {
        this.maxAge = maxAge;
        this.expiration = expiration;
    }

    public ParticleExpressLifetime(IInputStream stream) throws IOException {
        this.maxAge = stream.readPrimitiveObject();
        this.expiration = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(maxAge);
        stream.writePrimitiveObject(expiration);
    }
}
