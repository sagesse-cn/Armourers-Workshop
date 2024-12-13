package moe.plushie.armourers_workshop.core.skin.particle.component.particle.motion;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleBuilder;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParticleCollisionMotion extends SkinParticleComponent {

    private final OpenPrimitive enabled;

    private final float collisionDrag;
    private final float collisionRadius;
    private final float coefficientOfRestitution;

    private final boolean expireOnContact;

    private final Map<Float, String> events;

    public ParticleCollisionMotion(OpenPrimitive enabled, float collisionDrag, float collisionRadius, float coefficientOfRestitution, boolean expireOnContact, Map<Float, String> events) {
        this.enabled = enabled;
        this.collisionDrag = collisionDrag;
        this.collisionRadius = collisionRadius;
        this.coefficientOfRestitution = coefficientOfRestitution;
        this.expireOnContact = expireOnContact;
        this.events = events;
    }

    public ParticleCollisionMotion(IInputStream stream) throws IOException {
        this.enabled = stream.readPrimitiveObject();
        this.collisionDrag = stream.readFloat();
        this.collisionRadius = stream.readFloat();
        this.coefficientOfRestitution = stream.readFloat();
        this.expireOnContact = stream.readBoolean();
        this.events = new LinkedHashMap<>();
        int eventSize = stream.readVarInt();
        for (int i = 0; i < eventSize; ++i) {
            float key = stream.readFloat();
            var value = stream.readString();
            this.events.put(key, value);
        }
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(enabled);
        stream.writeFloat(collisionDrag);
        stream.writeFloat(collisionRadius);
        stream.writeFloat(coefficientOfRestitution);
        stream.writeBoolean(expireOnContact);
        stream.writeVarInt(events.size());
        for (var entry : events.entrySet()) {
            stream.writeFloat(entry.getKey());
            stream.writeString(entry.getValue());
        }
    }

    @Override
    public void applyToBuilder(SkinParticleBuilder builder) throws Exception {

        // TODO: NO IMPL @SAGESSE
    }

    //    @Override
//    public void update(BedrockEmitter emitter, BedrockParticle particle) {
//        if (emitter.world == null) {
//            return;
//        }
//
//        if (!particle.manual && !Operation.equals(this.enabled.get(), 0)) {
//            float r = this.radius;
//
//            this.previous.set(particle.getGlobalPosition(emitter, particle.prevPosition));
//            this.current.set(particle.getGlobalPosition(emitter));
//
//            Vector3d prev = this.previous;
//            Vector3d now = this.current;
//
//            double x = now.x - prev.x;
//            double y = now.y - prev.y;
//            double z = now.z - prev.z;
//            boolean veryBig = Math.abs(x) > 10 || Math.abs(y) > 10 || Math.abs(z) > 10;
//
//            this.pos.setPos(now.x, now.y, now.z);
//
//            if (veryBig || !emitter.world.isBlockLoaded(this.pos))
//            {
//                return;
//            }
//
//            AxisAlignedBB aabb = new AxisAlignedBB(prev.x - r, prev.y - r, prev.z - r, prev.x + r, prev.y + r, prev.z + r);
//
//            double d0 = y;
//            double origX = x;
//            double origZ = z;
//
//            List<AxisAlignedBB> list = emitter.world.getCollisionBoxes(null, aabb.expand(x, y, z));
//
//            for (AxisAlignedBB axisalignedbb : list)
//            {
//                y = axisalignedbb.calculateYOffset(aabb, y);
//            }
//
//            aabb = aabb.offset(0.0D, y, 0.0D);
//
//            for (AxisAlignedBB axisalignedbb1 : list)
//            {
//                x = axisalignedbb1.calculateXOffset(aabb, x);
//            }
//
//            aabb = aabb.offset(x, 0.0D, 0.0D);
//
//            for (AxisAlignedBB axisalignedbb2 : list)
//            {
//                z = axisalignedbb2.calculateZOffset(aabb, z);
//            }
//
//            aabb = aabb.offset(0.0D, 0.0D, z);
//
//            if (d0 != y || origX != x || origZ != z)
//            {
//                if (this.expireOnImpact)
//                {
//                    particle.dead = true;
//
//                    return;
//                }
//
//                if (particle.relativePosition)
//                {
//                    particle.relativePosition = false;
//                    particle.prevPosition.set(prev);
//                }
//
//                now.set(aabb.getXMin() + r, aabb.getYMin() + r, aabb.getZMin() + r);
//
//                if (d0 != y)
//                {
//                    particle.accelerationFactor.y *= -this.bounciness;
//                    now.y += d0 < y ? r : -r;
//                }
//
//                if (origX != x)
//                {
//                    particle.accelerationFactor.x *= -this.bounciness;
//                    now.x += origX < x ? r : -r;
//                }
//
//                if (origZ != z)
//                {
//                    particle.accelerationFactor.z *= -this.bounciness;
//                    now.z += origZ < z ? r : -r;
//                }
//
//                particle.position.set(now);
//                particle.dragFactor += this.collissionDrag;
//            }
//        }
//    }
//
//    @Override
//    public int getSortingIndex() {
//        return 50;
//    }
}
