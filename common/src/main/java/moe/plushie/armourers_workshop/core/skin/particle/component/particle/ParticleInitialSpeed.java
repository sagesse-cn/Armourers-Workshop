package moe.plushie.armourers_workshop.core.skin.particle.component.particle;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleBuilder;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

public class ParticleInitialSpeed extends SkinParticleComponent {

    private final OpenPrimitive speed;

    public ParticleInitialSpeed(OpenPrimitive speed) {
        this.speed = speed;
    }

    public ParticleInitialSpeed(IInputStream stream) throws IOException {
        this.speed = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(speed);
    }

    @Override
    public void applyToBuilder(SkinParticleBuilder builder) throws Exception {
        var speed = builder.compile(this.speed, 1.0);
        builder.applyParticle((emitter, particle, context) -> {
            var sp = speed.compute(context);
            var speed1 = particle.getSpeed();
            particle.setSpeed(speed1.scaling((float) sp));
        });
    }
}
