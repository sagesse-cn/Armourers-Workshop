package moe.plushie.armourers_workshop.core.skin.particle.component.particle.motion;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleBuilder;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

public class ParticleDynamicMotion extends SkinParticleComponent {

    private final OpenPrimitive motionAccelerationX;
    private final OpenPrimitive motionAccelerationY;
    private final OpenPrimitive motionAccelerationZ;
    private final OpenPrimitive motionDragCoefficient;
    private final OpenPrimitive rotationAcceleration;
    private final OpenPrimitive rotationDragCoefficient;

    public ParticleDynamicMotion(OpenPrimitive motionAccelerationX, OpenPrimitive motionAccelerationY, OpenPrimitive motionAccelerationZ, OpenPrimitive motionDragCoefficient, OpenPrimitive rotationAcceleration, OpenPrimitive rotationDragCoefficient) {
        this.motionAccelerationX = motionAccelerationX;
        this.motionAccelerationY = motionAccelerationY;
        this.motionAccelerationZ = motionAccelerationZ;
        this.motionDragCoefficient = motionDragCoefficient;
        this.rotationAcceleration = rotationAcceleration;
        this.rotationDragCoefficient = rotationDragCoefficient;
    }

    public ParticleDynamicMotion(IInputStream stream) throws IOException {
        this.motionAccelerationX = stream.readPrimitiveObject();
        this.motionAccelerationY = stream.readPrimitiveObject();
        this.motionAccelerationZ = stream.readPrimitiveObject();
        this.motionDragCoefficient = stream.readPrimitiveObject();
        this.rotationAcceleration = stream.readPrimitiveObject();
        this.rotationDragCoefficient = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(motionAccelerationX);
        stream.writePrimitiveObject(motionAccelerationY);
        stream.writePrimitiveObject(motionAccelerationZ);
        stream.writePrimitiveObject(motionDragCoefficient);
        stream.writePrimitiveObject(rotationAcceleration);
        stream.writePrimitiveObject(rotationDragCoefficient);
    }

    @Override
    public void applyToBuilder(SkinParticleBuilder builder) throws Exception {
        var motionAccelerationX = builder.compile(this.motionAccelerationX, 0.0);
        var motionAccelerationY = builder.compile(this.motionAccelerationY, 0.0);
        var motionAccelerationZ = builder.compile(this.motionAccelerationZ, 0.0);
        var motionDragCoefficient = builder.compile(this.motionDragCoefficient, 0.0);
        var rotationAcceleration = builder.compile(this.rotationAcceleration, 0.0);
        var rotationDragCoefficient = builder.compile(this.rotationDragCoefficient, 0.0);
        builder.updateParticle((emitter, particle, context) -> {
            var tx = motionAccelerationX.compute(context);
            var ty = motionAccelerationY.compute(context);
            var tz = motionAccelerationZ.compute(context);
            var td = motionDragCoefficient.compute(context);
            var ra = rotationAcceleration.compute(context);
            var rd = rotationDragCoefficient.compute(context);
            // TODO: NO IMPL @SAGESSE
//            particle.acceleration.x += (float) this.motionAcceleration[0].get();
//            particle.acceleration.y += (float) this.motionAcceleration[1].get();
//            particle.acceleration.z += (float) this.motionAcceleration[2].get();
//            particle.drag = (float) this.motionDrag.get();
//            particle.rotationAcceleration += (float) this.rotationAcceleration.get() / 20F;
//            particle.rotationDrag = (float) this.rotationDrag.get();
        });
    }
}
