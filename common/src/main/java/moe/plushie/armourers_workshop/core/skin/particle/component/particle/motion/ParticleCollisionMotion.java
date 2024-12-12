package moe.plushie.armourers_workshop.core.skin.particle.component.particle.motion;

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
}
