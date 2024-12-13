package moe.plushie.armourers_workshop.core.skin.particle.component.particle.lifetime;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleBuilder;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.Collections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParticleOnlyInBlocksLifetime extends SkinParticleComponent {

    private final List<String> blocks;

    public ParticleOnlyInBlocksLifetime(List<String> blocks) {
        this.blocks = blocks;
    }

    public ParticleOnlyInBlocksLifetime(IInputStream stream) throws IOException {
        var size = stream.readVarInt();
        var blocks = new ArrayList<String>();
        for (int i = 0; i < size; ++i) {
            blocks.add(stream.readString());
        }
        this.blocks = blocks;
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writeVarInt(blocks.size());
        for (var block : blocks) {
            stream.writeString(block);
        }
    }

    @Override
    public void applyToBuilder(SkinParticleBuilder builder) throws Exception {
        // the blocks outside of which the particle expires.
        var blocks = Collections.compactMap(this.blocks, builder::getBlock);
        builder.updateParticle((emitter, particle, context) -> {
            if (particle.isAlive() && !blocks.contains(particle.getBlock())) {
                particle.kill();
            }
        });
    }
}
