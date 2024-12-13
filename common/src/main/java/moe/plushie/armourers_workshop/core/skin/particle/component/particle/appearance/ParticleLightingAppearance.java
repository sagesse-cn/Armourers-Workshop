package moe.plushie.armourers_workshop.core.skin.particle.component.particle.appearance;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleBuilder;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;

import java.io.IOException;

public class ParticleLightingAppearance extends SkinParticleComponent {

    public ParticleLightingAppearance() {
    }

    public ParticleLightingAppearance(IInputStream stream) {
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
    }

    @Override
    public void applyToBuilder(SkinParticleBuilder builder) throws Exception {
        builder.applyEmitter((emitter, context) -> {
            emitter.setEmissive(false);
        });
    }
}
