package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleDirection;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;

import java.io.IOException;

public class EmitterEntityShape extends SkinParticleComponent {

    private final SkinParticleDirection direction;
    private final boolean surfaceOnly;

    public EmitterEntityShape(SkinParticleDirection direction, boolean surfaceOnly) {
        this.direction = direction;
        this.surfaceOnly = surfaceOnly;
    }

    public EmitterEntityShape(IInputStream stream) throws IOException {
        this.direction = SkinParticleDirection.readFromStream(stream);
        this.surfaceOnly = stream.readBoolean();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        direction.writeToStream(stream);
        stream.writeBoolean(surfaceOnly);
    }
}
