package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.lifetime;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleBuilder;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

public class EmitterLoopingLifetime extends SkinParticleComponent {

    private final OpenPrimitive activeTime;
    private final OpenPrimitive sleepTime;

    public EmitterLoopingLifetime(OpenPrimitive activeTime, OpenPrimitive sleepTime) {
        this.activeTime = activeTime;
        this.sleepTime = sleepTime;
    }

    public EmitterLoopingLifetime(IInputStream stream) throws IOException {
        this.activeTime = stream.readPrimitiveObject();
        this.sleepTime = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(activeTime);
        stream.writePrimitiveObject(sleepTime);
    }

    @Override
    public void applyToBuilder(SkinParticleBuilder builder) throws Exception {
        var activeTime = builder.compile(this.activeTime, 10.0);
        var sleepTime = builder.compile(this.sleepTime, 0.0);
        builder.updateEmitter((emitter, context) -> {
            var active = activeTime.compute(context);
            var sleep = sleepTime.compute(context);
            var time = emitter.getTime();
            emitter.setDuration(active);
            if (time >= active && emitter.isRunning()) {
                emitter.stop();
            }
            if (time >= sleep && !emitter.isRunning()) {
                emitter.start();
            }
        });
    }
}
