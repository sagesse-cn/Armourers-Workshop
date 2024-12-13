package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.lifetime;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleBuilder;
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

    @Override
    public void applyToBuilder(SkinParticleBuilder builder) throws Exception {
        var activation = builder.compile(this.activation, 0.0);
        var expiration = builder.compile(this.expiration, 0.0);
        builder.updateEmitter((emitter, context) -> {
            // start when activation is true.
            if (activation.test(context)) {
                emitter.start();
            }
            // stop when expiration is true.
            if (expiration.test(context)) {
                emitter.stop();
            }
        });
    }
}
