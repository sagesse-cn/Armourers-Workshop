package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleDirection;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

public class EmitterBoxShape extends SkinParticleComponent {

    private final OpenPrimitive x;
    private final OpenPrimitive y;
    private final OpenPrimitive z;

    private final OpenPrimitive width;
    private final OpenPrimitive height;
    private final OpenPrimitive depth;

    private final SkinParticleDirection direction;

    private final boolean surfaceOnly;

    public EmitterBoxShape(OpenPrimitive x, OpenPrimitive y, OpenPrimitive z, OpenPrimitive width, OpenPrimitive height, OpenPrimitive depth, SkinParticleDirection direction, boolean surfaceOnly) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.direction = direction;
        this.surfaceOnly = surfaceOnly;
    }

    public EmitterBoxShape(IInputStream stream) throws IOException {
        this.x = stream.readPrimitiveObject();
        this.y = stream.readPrimitiveObject();
        this.z = stream.readPrimitiveObject();
        this.width = stream.readPrimitiveObject();
        this.height = stream.readPrimitiveObject();
        this.depth = stream.readPrimitiveObject();
        this.direction = SkinParticleDirection.readFromStream(stream);
        this.surfaceOnly = stream.readBoolean();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(x);
        stream.writePrimitiveObject(y);
        stream.writePrimitiveObject(z);
        stream.writePrimitiveObject(width);
        stream.writePrimitiveObject(height);
        stream.writePrimitiveObject(depth);
        direction.writeToStream(stream);
        stream.writeBoolean(surfaceOnly);
    }
}
