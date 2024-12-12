package moe.plushie.armourers_workshop.core.skin.particle.component.particle.appearance;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

public class ParticleTintingAppearance extends SkinParticleComponent {

    private final OpenPrimitive red;
    private final OpenPrimitive green;
    private final OpenPrimitive blue;
    private final OpenPrimitive alpha;

    public ParticleTintingAppearance(OpenPrimitive red, OpenPrimitive green, OpenPrimitive blue, OpenPrimitive alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public ParticleTintingAppearance(IInputStream stream) throws IOException {
        this.red = stream.readPrimitiveObject();
        this.green = stream.readPrimitiveObject();
        this.blue = stream.readPrimitiveObject();
        this.alpha = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(red);
        stream.writePrimitiveObject(green);
        stream.writePrimitiveObject(blue);
        stream.writePrimitiveObject(alpha);
    }
}
