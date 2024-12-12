package moe.plushie.armourers_workshop.core.skin.particle.component.emitter;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

public class EmitterInitialization extends SkinParticleComponent {

    private final OpenPrimitive creation;
    private final OpenPrimitive update;

    public EmitterInitialization(OpenPrimitive creation, OpenPrimitive update) {
        this.creation = creation;
        this.update = update;
    }

    public EmitterInitialization(IInputStream stream) throws IOException {
        this.creation = stream.readPrimitiveObject();
        this.update = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(creation);
        stream.writePrimitiveObject(update);
    }
}
