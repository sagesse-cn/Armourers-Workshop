package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleDirection;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

public class EmitterSphereShape extends SkinParticleComponent {

    private final OpenPrimitive x;
    private final OpenPrimitive y;
    private final OpenPrimitive z;

    private final OpenPrimitive radius;

    private final SkinParticleDirection direction;

    private final boolean surfaceOnly;

    public EmitterSphereShape(OpenPrimitive x, OpenPrimitive y, OpenPrimitive z, OpenPrimitive radius, SkinParticleDirection direction, boolean surfaceOnly) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.direction = direction;
        this.surfaceOnly = surfaceOnly;
    }

    public EmitterSphereShape(IInputStream stream) throws IOException {
        this.x = stream.readPrimitiveObject();
        this.y = stream.readPrimitiveObject();
        this.z = stream.readPrimitiveObject();
        this.radius = stream.readPrimitiveObject();
        this.direction = SkinParticleDirection.readFromStream(stream);
        this.surfaceOnly = stream.readBoolean();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(x);
        stream.writePrimitiveObject(y);
        stream.writePrimitiveObject(z);
        stream.writePrimitiveObject(radius);
        direction.writeToStream(stream);
        stream.writeBoolean(surfaceOnly);
    }
}
