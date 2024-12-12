package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.lifetime;

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
}
