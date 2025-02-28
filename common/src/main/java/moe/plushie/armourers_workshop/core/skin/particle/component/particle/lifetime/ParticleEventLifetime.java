package moe.plushie.armourers_workshop.core.skin.particle.component.particle.lifetime;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleBuilder;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ParticleEventLifetime extends SkinParticleComponent {

    private final List<String> creation;
    private final List<String> expiration;
    private final Map<Float, List<String>> timelineEvents;

    public ParticleEventLifetime(List<String> creation, List<String> expiration, Map<Float, List<String>> timelineEvents) {
        this.creation = creation;
        this.expiration = expiration;
        this.timelineEvents = timelineEvents;
    }

    public ParticleEventLifetime(IInputStream stream) throws IOException {
        this.creation = readEventsFromStream(stream);
        this.expiration = readEventsFromStream(stream);
        this.timelineEvents = readKeyedEventsFromStream(stream);
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        writeEventsToStream(creation, stream);
        writeEventsToStream(expiration, stream);
        writeKeyedEventsToStream(timelineEvents, stream);
    }

    @Override
    public void applyToBuilder(SkinParticleBuilder builder) throws Exception {
        // TODO: NO IMPL @SAGESSE
    }

    private List<String> readEventsFromStream(IInputStream stream) throws IOException {
        var events = new ArrayList<String>();
        int size = stream.readVarInt();
        for (int i = 0; i < size; i++) {
            events.add(stream.readString());
        }
        return events;
    }

    private void writeEventsToStream(List<String> events, IOutputStream stream) throws IOException {
        stream.writeVarInt(events.size());
        for (var eventId : events) {
            stream.writeString(eventId);
        }
    }

    private Map<Float, List<String>> readKeyedEventsFromStream(IInputStream stream) throws IOException {
        var events = new LinkedHashMap<Float, List<String>>();
        int timelineEventSize = stream.readVarInt();
        while (timelineEventSize != 0) {
            var key = stream.readFloat();
            var value = new ArrayList<String>();
            for (int i = 0; i < timelineEventSize; i++) {
                value.add(stream.readString());
            }
            events.put(key, value);
            timelineEventSize = stream.readVarInt();
        }
        return events;
    }

    private void writeKeyedEventsToStream(Map<Float, List<String>> events, IOutputStream stream) throws IOException {
        for (var entry : events.entrySet()) {
            if (entry.getValue().isEmpty()) {
                continue; // ignore when empty.
            }
            stream.writeVarInt(entry.getValue().size());
            stream.writeFloat(entry.getKey());
            for (var eventId : entry.getValue()) {
                stream.writeString(eventId);
            }
        }
        stream.writeVarInt(0);
    }
}
