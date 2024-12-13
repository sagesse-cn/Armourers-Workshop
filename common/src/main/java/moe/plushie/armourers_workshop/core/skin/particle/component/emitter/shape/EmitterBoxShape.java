package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleBuilder;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
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

    private final EmitterShapeDirection direction;

    private final boolean surface;

    public EmitterBoxShape(OpenPrimitive x, OpenPrimitive y, OpenPrimitive z, OpenPrimitive width, OpenPrimitive height, OpenPrimitive depth, EmitterShapeDirection direction, boolean surface) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.direction = direction;
        this.surface = surface;
    }

    public EmitterBoxShape(IInputStream stream) throws IOException {
        this.x = stream.readPrimitiveObject();
        this.y = stream.readPrimitiveObject();
        this.z = stream.readPrimitiveObject();
        this.width = stream.readPrimitiveObject();
        this.height = stream.readPrimitiveObject();
        this.depth = stream.readPrimitiveObject();
        this.direction = EmitterShapeDirection.readFromStream(stream);
        this.surface = stream.readBoolean();
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
        stream.writeBoolean(surface);
    }

    @Override
    public void applyToBuilder(SkinParticleBuilder builder) throws Exception {
        var x = builder.compile(this.x, 0.0);
        var y = builder.compile(this.y, 0.0);
        var z = builder.compile(this.z, 0.0);
        var width = builder.compile(this.width, 0.0);
        var height = builder.compile(this.height, 0.0);
        var depth = builder.compile(this.depth, 0.0);
        builder.applyParticle((emitter, particle, context) -> {
            var cx = x.compute(context);
            var cy = y.compute(context);
            var cz = z.compute(context);
            var w = width.compute(context);
            var h = height.compute(context);
            var d = depth.compute(context);
            // TODO: NO IMPL @SAGESSE
//            particle.position.x = cx + ((float) Math.random() * 2 - 1F) * w;
//            particle.position.y = cy + ((float) Math.random() * 2 - 1F) * h;
//            particle.position.z = cz + ((float) Math.random() * 2 - 1F) * d;
//
//            if (this.surface) {
//                int roll = (int) (Math.random() * 6 * 100) % 6;
//
//                if (roll == 0) particle.position.x = cx + w;
//                else if (roll == 1) particle.position.x = cx - w;
//                else if (roll == 2) particle.position.y = cy + h;
//                else if (roll == 3) particle.position.y = cy - h;
//                else if (roll == 4) particle.position.z = cz + d;
//                else if (roll == 5) particle.position.z = cz - d;
//            }
//
//            this.direction.applyDirection(particle, centerX, cy, cz);
        });
    }
}
