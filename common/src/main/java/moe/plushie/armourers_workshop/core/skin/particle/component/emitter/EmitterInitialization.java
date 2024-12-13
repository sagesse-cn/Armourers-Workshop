package moe.plushie.armourers_workshop.core.skin.particle.component.emitter;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleBuilder;
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

    @Override
    public void applyToBuilder(SkinParticleBuilder builder) throws Exception {
        var creation = builder.compile(this.creation, 0.0);
        var update = builder.compile(this.update, 0.0);
        builder.applyEmitter((emitter, context) -> {
            creation.evaluate(context);
        });
        builder.updateEmitter((emitter, context) -> {
            update.evaluate(context);
        });
    }
}
