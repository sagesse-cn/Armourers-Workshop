package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleBuilder;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

public class EmitterEntityShape extends SkinParticleComponent {

    private final OpenPrimitive x;
    private final OpenPrimitive y;
    private final OpenPrimitive z;

    private final EmitterShapeDirection direction;
    private final boolean surfaceOnly;

    public EmitterEntityShape(OpenPrimitive x, OpenPrimitive y, OpenPrimitive z, EmitterShapeDirection direction, boolean surfaceOnly) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.direction = direction;
        this.surfaceOnly = surfaceOnly;
    }

    public EmitterEntityShape(IInputStream stream) throws IOException {
        this.x = stream.readPrimitiveObject();
        this.y = stream.readPrimitiveObject();
        this.z = stream.readPrimitiveObject();
        this.direction = EmitterShapeDirection.readFromStream(stream);
        this.surfaceOnly = stream.readBoolean();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(x);
        stream.writePrimitiveObject(y);
        stream.writePrimitiveObject(z);
        direction.writeToStream(stream);
        stream.writeBoolean(surfaceOnly);
    }

    @Override
    public void applyToBuilder(SkinParticleBuilder builder) throws Exception {
        var x = builder.compile(this.x, 0.0);
        var y = builder.compile(this.y, 0.0);
        var z = builder.compile(this.z, 0.0);
        builder.applyParticle((emitter, particle, context) -> {
            var cx = x.compute(context);
            var cy = y.compute(context);
            var cz = z.compute(context);

            // TODO: NO IMPL @SAGESSE
//        float w = 0;
//        float h = 0;
//        float d = 0;
//
//        if (emitter.target != null)
//        {
//            w = emitter.target.width;
//            h = emitter.target.height;
//            d = emitter.target.width;
//        }
//
//        particle.position.x = centerX + ((float) Math.random() - 0.5F) * w;
//        particle.position.y = centerY + ((float) Math.random() - 0.5F) * h;
//        particle.position.z = centerZ + ((float) Math.random() - 0.5F) * d;
//
//        if (this.surface)
//        {
//            int roll = (int) (Math.random() * 6 * 100) % 6;
//
//            if (roll == 0) particle.position.x = centerX + w / 2F;
//            else if (roll == 1) particle.position.x = centerX - w / 2F;
//            else if (roll == 2) particle.position.y = centerY + h / 2F;
//            else if (roll == 3) particle.position.y = centerY - h / 2F;
//            else if (roll == 4) particle.position.z = centerZ + d / 2F;
//            else if (roll == 5) particle.position.z = centerZ - d / 2F;
//        }
//
//        this.direction.applyDirection(particle, centerX, centerY, centerZ);
        });
    }
}
