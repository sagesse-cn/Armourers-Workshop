package moe.plushie.armourers_workshop.core.skin.particle.component.particle.lifetime;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleBuilder;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

public class ParticleExpressLifetime extends SkinParticleComponent {

    private final OpenPrimitive duration;
    private final OpenPrimitive expiration;

    public ParticleExpressLifetime(OpenPrimitive duration, OpenPrimitive expiration) {
        this.duration = duration;
        this.expiration = expiration;
    }

    public ParticleExpressLifetime(IInputStream stream) throws IOException {
        this.duration = stream.readPrimitiveObject();
        this.expiration = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(duration);
        stream.writePrimitiveObject(expiration);
    }

    @Override
    public void applyToBuilder(SkinParticleBuilder builder) throws Exception {
        // when the duration not provided, we will use -1s(infinity)
        var duration = builder.compile(this.duration, -1.0);
        var expiration = builder.compile(this.expiration, 0.0);
        builder.applyParticle((emitter, particle, context) -> {
            var t = duration.compute(context);
            particle.setDuration(t);
        });
        // when the expiration expression not provided, skip check.
        if (this.expiration.isNull()) {
            return;
        }
        builder.updateParticle((emitter, particle, context) -> {
            if (expiration.test(context)) {
                particle.kill();
            }
        });
    }
}
