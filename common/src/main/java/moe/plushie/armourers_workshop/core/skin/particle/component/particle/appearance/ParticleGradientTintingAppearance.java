package moe.plushie.armourers_workshop.core.skin.particle.component.particle.appearance;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParticleGradientTintingAppearance extends SkinParticleComponent {

    private final OpenPrimitive interpolant;
    private final Map<Float, Integer> gradientValues;

    public ParticleGradientTintingAppearance(OpenPrimitive interpolant, Map<Float, Integer> gradientValues) {
        this.interpolant = interpolant;
        this.gradientValues = gradientValues;
    }

    public ParticleGradientTintingAppearance(IInputStream stream) throws IOException {
        this.interpolant = stream.readPrimitiveObject();
        this.gradientValues = new LinkedHashMap<>();
        var size = stream.readVarInt();
        for (int i = 0; i < size; i++) {
            var key = stream.readFloat();
            var value = stream.readInt();
            this.gradientValues.put(key, value);
        }
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(interpolant);
        stream.writeVarInt(gradientValues.size());
        for (var entry : gradientValues.entrySet()) {
            stream.writeFloat(entry.getKey());
            stream.writeInt(entry.getValue());
        }
    }
}
