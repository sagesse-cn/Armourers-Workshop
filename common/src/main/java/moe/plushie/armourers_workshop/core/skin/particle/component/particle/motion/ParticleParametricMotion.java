package moe.plushie.armourers_workshop.core.skin.particle.component.particle.motion;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

public class ParticleParametricMotion extends SkinParticleComponent {

    private final OpenPrimitive relativePositionX;
    private final OpenPrimitive relativePositionY;
    private final OpenPrimitive relativePositionZ;
    private final OpenPrimitive directionX;
    private final OpenPrimitive directionY;
    private final OpenPrimitive directionZ;
    private final OpenPrimitive rotation;

    public ParticleParametricMotion(OpenPrimitive relativePositionX, OpenPrimitive relativePositionY, OpenPrimitive relativePositionZ, OpenPrimitive directionX, OpenPrimitive directionY, OpenPrimitive directionZ, OpenPrimitive rotation) {
        this.relativePositionX = relativePositionX;
        this.relativePositionY = relativePositionY;
        this.relativePositionZ = relativePositionZ;
        this.directionX = directionX;
        this.directionY = directionY;
        this.directionZ = directionZ;
        this.rotation = rotation;
    }

    public ParticleParametricMotion(IInputStream stream) throws IOException {
        this.relativePositionX = stream.readPrimitiveObject();
        this.relativePositionY = stream.readPrimitiveObject();
        this.relativePositionZ = stream.readPrimitiveObject();
        this.directionX = stream.readPrimitiveObject();
        this.directionY = stream.readPrimitiveObject();
        this.directionZ = stream.readPrimitiveObject();
        this.rotation = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(relativePositionX);
        stream.writePrimitiveObject(relativePositionY);
        stream.writePrimitiveObject(relativePositionZ);
        stream.writePrimitiveObject(directionX);
        stream.writePrimitiveObject(directionY);
        stream.writePrimitiveObject(directionZ);
        stream.writePrimitiveObject(rotation);
    }
}
