package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleBuilder;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

public class EmitterDiscShape extends SkinParticleComponent {

    private final OpenPrimitive x;
    private final OpenPrimitive y;
    private final OpenPrimitive z;

    private final OpenPrimitive radius;

    private final OpenPrimitive planeNormalX;
    private final OpenPrimitive planeNormalY;
    private final OpenPrimitive planeNormalZ;

    private final EmitterShapeDirection direction;

    private final boolean surface;

    public EmitterDiscShape(OpenPrimitive x, OpenPrimitive y, OpenPrimitive z, OpenPrimitive radius, OpenPrimitive planeNormalX, OpenPrimitive planeNormalY, OpenPrimitive planeNormalZ, EmitterShapeDirection direction, boolean surface) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.planeNormalX = planeNormalX;
        this.planeNormalY = planeNormalY;
        this.planeNormalZ = planeNormalZ;
        this.direction = direction;
        this.surface = surface;
    }

    public EmitterDiscShape(IInputStream stream) throws IOException {
        this.x = stream.readPrimitiveObject();
        this.y = stream.readPrimitiveObject();
        this.z = stream.readPrimitiveObject();
        this.radius = stream.readPrimitiveObject();
        this.planeNormalX = stream.readPrimitiveObject();
        this.planeNormalY = stream.readPrimitiveObject();
        this.planeNormalZ = stream.readPrimitiveObject();
        this.direction = EmitterShapeDirection.readFromStream(stream);
        this.surface = stream.readBoolean();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(x);
        stream.writePrimitiveObject(y);
        stream.writePrimitiveObject(z);
        stream.writePrimitiveObject(radius);
        stream.writePrimitiveObject(planeNormalX);
        stream.writePrimitiveObject(planeNormalY);
        stream.writePrimitiveObject(planeNormalZ);
        direction.writeToStream(stream);
        stream.writeBoolean(surface);
    }

    @Override
    public void applyToBuilder(SkinParticleBuilder builder) throws Exception {
        var x = builder.compile(this.x, 0.0);
        var y = builder.compile(this.y, 0.0);
        var z = builder.compile(this.z, 0.0);
        var radius = builder.compile(this.radius, 0.0);
        var normalX = builder.compile(this.planeNormalX, 0.0);
        var normalY = builder.compile(this.planeNormalY, 0.0);
        var normalZ = builder.compile(this.planeNormalZ, 0.0);
        builder.applyParticle((emitter, particle, context) -> {
            var cx = x.compute(context);
            var cy = y.compute(context);
            var cz = z.compute(context);
            var r = radius.compute(context);
            var nx = normalX.compute(context);
            var ny = normalY.compute(context);
            var nz = normalZ.compute(context);

            // TODO: NO IMPL @SAGESSE
            //        float centerX = (float) this.offset[0].get();
//        float centerY = (float) this.offset[1].get();
//        float centerZ = (float) this.offset[2].get();
//
//        Vector3f normal = new Vector3f((float) this.normal[0].get(), (float) this.normal[1].get(), (float) this.normal[2].get());
//
//        normal.normalize();
//
//        Quat4f quaternion = new Quat4f(normal.x, normal.y, normal.z, 1);
//        Matrix4f rotation = new Matrix4f();
//        rotation.set(quaternion);
//
//        Vector4f position = new Vector4f((float) Math.random() - 0.5F, 0, (float) Math.random() - 0.5F, 0);
//        position.normalize();
//        rotation.transform(position);
//
//        position.scale((float) (this.radius.get() * (this.surface ? 1 : Math.random())));
//        position.add(new Vector4f(centerX, centerY, centerZ, 0));
//
//        particle.position.x += position.x;
//        particle.position.y += position.y;
//        particle.position.z += position.z;
//
//        this.direction.applyDirection(particle, centerX, centerY, centerZ);
        });
    }
}
