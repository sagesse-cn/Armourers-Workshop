package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.lifetime;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleBuilder;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

public class EmitterOnceLifetime extends SkinParticleComponent {

    private final OpenPrimitive activeTime;

    public EmitterOnceLifetime(OpenPrimitive activeTime) {
        this.activeTime = activeTime;
    }

    public EmitterOnceLifetime(IInputStream stream) throws IOException {
        this.activeTime = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(activeTime);
    }

    @Override
    public void applyToBuilder(SkinParticleBuilder builder) throws Exception {
        var activeTime = builder.compile(this.activeTime, 10.0);
        builder.updateEmitter((emitter, context) -> {
            var active = activeTime.compute(context);
            emitter.setDuration(active);
            if (emitter.getTime() >= emitter.getDuration()) {
                emitter.stop();
            }
        });
    }
}
