package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleBuilder;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

public class EmitterSphereShape extends SkinParticleComponent {

    private final OpenPrimitive x;
    private final OpenPrimitive y;
    private final OpenPrimitive z;

    private final OpenPrimitive radius;

    private final EmitterShapeDirection direction;

    private final boolean surface;

    public EmitterSphereShape(OpenPrimitive x, OpenPrimitive y, OpenPrimitive z, OpenPrimitive radius, EmitterShapeDirection direction, boolean surface) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.direction = direction;
        this.surface = surface;
    }

    public EmitterSphereShape(IInputStream stream) throws IOException {
        this.x = stream.readPrimitiveObject();
        this.y = stream.readPrimitiveObject();
        this.z = stream.readPrimitiveObject();
        this.radius = stream.readPrimitiveObject();
        this.direction = EmitterShapeDirection.readFromStream(stream);
        this.surface = stream.readBoolean();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(x);
        stream.writePrimitiveObject(y);
        stream.writePrimitiveObject(z);
        stream.writePrimitiveObject(radius);
        direction.writeToStream(stream);
        stream.writeBoolean(surface);
    }

    @Override
    public void applyToBuilder(SkinParticleBuilder builder) throws Exception {
        var x = builder.compile(this.x, 0.0);
        var y = builder.compile(this.y, 0.0);
        var z = builder.compile(this.z, 0.0);
        var radius = builder.compile(this.radius, 0.0);
        builder.applyParticle((emitter, particle, context) -> {

            var cx = x.compute(context);
            var cy = y.compute(context);
            var cz = z.compute(context);
            var r = radius.compute(context);

            // TODO: NO IMPL @SAGESSE
//        Vector3f direction = new Vector3f((float) Math.random() * 2 - 1, (float) Math.random() * 2 - 1, (float) Math.random() * 2 - 1);
//        direction.normalize();
//
//        if (!this.surface)
//        {
//            radius *= Math.random();
//        }
//
//        direction.scale(radius);
//
//        particle.position.x = centerX + direction.x;
//        particle.position.y = centerY + direction.y;
//        particle.position.z = centerZ + direction.z;
//
//        this.direction.applyDirection(particle, centerX, centerY, centerZ);

        });
    }

//    @Override
//    public void apply(BedrockEmitter emitter, BedrockParticle particle)
//    {
//    }
}
