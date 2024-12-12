package moe.plushie.armourers_workshop.core.skin.particle.component.particle.motion;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

public class ParticleDynamicMotion extends SkinParticleComponent {

    private final OpenPrimitive linearAccelerationX;
    private final OpenPrimitive linearAccelerationY;
    private final OpenPrimitive linearAccelerationZ;
    private final OpenPrimitive linearDragCoefficient;
    private final OpenPrimitive rotationAcceleration;
    private final OpenPrimitive rotationDragCoefficient;

    public ParticleDynamicMotion(OpenPrimitive linearAccelerationX, OpenPrimitive linearAccelerationY, OpenPrimitive linearAccelerationZ, OpenPrimitive linearDragCoefficient, OpenPrimitive rotationAcceleration, OpenPrimitive rotationDragCoefficient) {
        this.linearAccelerationX = linearAccelerationX;
        this.linearAccelerationY = linearAccelerationY;
        this.linearAccelerationZ = linearAccelerationZ;
        this.linearDragCoefficient = linearDragCoefficient;
        this.rotationAcceleration = rotationAcceleration;
        this.rotationDragCoefficient = rotationDragCoefficient;
    }

    public ParticleDynamicMotion(IInputStream stream) throws IOException {
        this.linearAccelerationX = stream.readPrimitiveObject();
        this.linearAccelerationY = stream.readPrimitiveObject();
        this.linearAccelerationZ = stream.readPrimitiveObject();
        this.linearDragCoefficient = stream.readPrimitiveObject();
        this.rotationAcceleration = stream.readPrimitiveObject();
        this.rotationDragCoefficient = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(linearAccelerationX);
        stream.writePrimitiveObject(linearAccelerationY);
        stream.writePrimitiveObject(linearAccelerationZ);
        stream.writePrimitiveObject(linearDragCoefficient);
        stream.writePrimitiveObject(rotationAcceleration);
        stream.writePrimitiveObject(rotationDragCoefficient);
    }
}
