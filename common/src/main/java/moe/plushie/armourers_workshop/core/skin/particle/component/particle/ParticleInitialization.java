package moe.plushie.armourers_workshop.core.skin.particle.component.particle;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleBuilder;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

public class ParticleInitialization extends SkinParticleComponent {

    private final OpenPrimitive update;
    private final OpenPrimitive render;

    public ParticleInitialization(OpenPrimitive update, OpenPrimitive render) {
        this.update = update;
        this.render = render;
    }

    public ParticleInitialization(IInputStream stream) throws IOException {
        this.update = stream.readPrimitiveObject();
        this.render = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(update);
        stream.writePrimitiveObject(render);
    }

    @Override
    public void applyToBuilder(SkinParticleBuilder builder) throws Exception {
        var update = builder.compile(this.update, 0.0);
        var render = builder.compile(this.render, 0.0);
        builder.updateParticle((emitter, particle, context) -> {
            update.evaluate(context);
        });
        builder.renderParticlePre((emitter, particle, partialTicks, context) -> {
            render.evaluate(context);
        });
    }
}
