package moe.plushie.armourers_workshop.core.skin.particle.component.particle.lifetime;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleBuilder;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;

import java.io.IOException;

public class ParticleKillInPlaneLifetime extends SkinParticleComponent {

    private final float a;
    private final float b;
    private final float c;
    private final float d;

    public ParticleKillInPlaneLifetime(float a, float b, float c, float d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public ParticleKillInPlaneLifetime(IInputStream stream) throws IOException {
        this.a = stream.readFloat();
        this.b = stream.readFloat();
        this.c = stream.readFloat();
        this.d = stream.readFloat();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writeFloat(a);
        stream.writeFloat(b);
        stream.writeFloat(c);
        stream.writeFloat(d);
    }

    @Override
    public void applyToBuilder(SkinParticleBuilder builder) throws Exception {
        // the particles that cross this plane expire.
        // the plane is relative to the emitter, but oriented in world space.
        builder.updateParticle((emitter, particle, context) -> {
            if (!particle.isAlive()) {
                return;
            }
            var p0 = particle.getLocalPositionOld();
            var p1 = particle.getLocalPosition();
            var prev = a * p0.getX() + b * p0.getY() + c * p0.getZ() + d;
            var now = a * p1.getX() + b * p1.getY() + c * p1.getY() + d;
            if ((prev > 0 && now < 0) || (prev < 0 && now > 0)) {
                particle.kill();
            }
        });
    }
}
