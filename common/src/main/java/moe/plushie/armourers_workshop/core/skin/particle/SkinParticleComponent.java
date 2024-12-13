package moe.plushie.armourers_workshop.core.skin.particle;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;

import java.io.IOException;

public abstract class SkinParticleComponent {

    public abstract void writeToStream(IOutputStream stream) throws IOException;

    public abstract void applyToBuilder(SkinParticleBuilder builder) throws Exception;
}
