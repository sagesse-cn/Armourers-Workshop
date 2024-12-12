package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.lifetime;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

public class EmitterExpressionLifetime extends SkinParticleComponent {

    private final OpenPrimitive activation;
    private final OpenPrimitive expiration;

    public EmitterExpressionLifetime(OpenPrimitive activation, OpenPrimitive expiration) {
        this.activation = activation;
        this.expiration = expiration;
    }

    public EmitterExpressionLifetime(IInputStream stream) throws IOException {
        this.activation = stream.readPrimitiveObject();
        this.expiration = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(activation);
        stream.writePrimitiveObject(expiration);
    }
}
