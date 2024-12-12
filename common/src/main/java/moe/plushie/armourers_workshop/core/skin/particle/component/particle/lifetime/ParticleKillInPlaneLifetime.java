package moe.plushie.armourers_workshop.core.skin.particle.component.particle.lifetime;

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
}
