package moe.plushie.armourers_workshop.core.skin.particle.component.particle.appearance;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleBuilder;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParticleTintingAppearance extends SkinParticleComponent {

    private final ColorComponent color;

    public ParticleTintingAppearance(OpenPrimitive interpolation, Map<Float, Integer> gradientValues) {
        this.color = new GradientColor(interpolation, gradientValues);
    }

    public ParticleTintingAppearance(OpenPrimitive red, OpenPrimitive green, OpenPrimitive blue, OpenPrimitive alpha) {
        this.color = new SolidColor(red, green, blue, alpha);
    }

    public ParticleTintingAppearance(IInputStream stream) throws IOException {
        var isGradient = stream.readBoolean();
        if (isGradient) {
            this.color = new GradientColor(stream);
        } else {
            this.color = new SolidColor(stream);
        }
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writeBoolean(color instanceof GradientColor);
        color.writeToStream(stream);
    }

    @Override
    public void applyToBuilder(SkinParticleBuilder builder) throws Exception {
        color.applyToBuilder(builder);
    }

    private interface ColorComponent {

        void writeToStream(IOutputStream stream) throws IOException;

        void applyToBuilder(SkinParticleBuilder builder) throws Exception;
    }

    private static class SolidColor implements ColorComponent {

        private final OpenPrimitive red;
        private final OpenPrimitive green;
        private final OpenPrimitive blue;
        private final OpenPrimitive alpha;

        private SolidColor(OpenPrimitive red, OpenPrimitive green, OpenPrimitive blue, OpenPrimitive alpha) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }

        public SolidColor(IInputStream stream) throws IOException {
            this.red = stream.readPrimitiveObject();
            this.green = stream.readPrimitiveObject();
            this.blue = stream.readPrimitiveObject();
            this.alpha = stream.readPrimitiveObject();
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            stream.writePrimitiveObject(red);
            stream.writePrimitiveObject(green);
            stream.writePrimitiveObject(blue);
            stream.writePrimitiveObject(alpha);
        }

        @Override
        public void applyToBuilder(SkinParticleBuilder builder) throws Exception {
            var red = builder.compile(this.red, 1.0);
            var green = builder.compile(this.green, 1.0);
            var blue = builder.compile(this.blue, 1.0);
            var alpha = builder.compile(this.alpha, 1.0);
            builder.renderParticlePre((emitter, particle, partialTicks, context) -> {
                var r = red.compute(context);
                var g = green.compute(context);
                var b = blue.compute(context);
                var a = alpha.compute(context);
                particle.setColor((float) r, (float) g, (float) b, (float) a);
            });
        }
    }

    private static class GradientColor implements ColorComponent {

        private final OpenPrimitive interpolation;
        private final Map<Float, Integer> gradientValues;

        private GradientColor(OpenPrimitive interpolation, Map<Float, Integer> gradientValues) {
            this.interpolation = interpolation;
            this.gradientValues = gradientValues;
        }

        public GradientColor(IInputStream stream) throws IOException {
            this.interpolation = stream.readPrimitiveObject();
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
            stream.writePrimitiveObject(interpolation);
            stream.writeVarInt(gradientValues.size());
            for (var entry : gradientValues.entrySet()) {
                stream.writeFloat(entry.getKey());
                stream.writeInt(entry.getValue());
            }
        }

        @Override
        public void applyToBuilder(SkinParticleBuilder builder) throws Exception {
            var interpolation = builder.compile(this.interpolation, 1.0);
            builder.renderParticlePre((emitter, particle, partialTicks, context) -> {
                // TODO: NO IMPL @SAGESSE
//                int length = this.gradientValues.size();
//                if (length == 0) {
//                    particle.updateColor(1.0f, 1.0f, 1.0f, 1.0f);
//                }
//                if (length == 1) {
//                    this.stops.get(0).color.compute(particle);
//                    return;
//                }
//
                var factor = interpolation.compute(context);
//                factor = MathUtils.clamp(factor, 0, 1);
//                ColorStop prev = this.stops.get(0);
//                if (factor < prev.stop) {
//                    prev.color.compute(particle);
//                    return;
//                }
//
//                for (int i = 1; i < length; i++) {
//                    ColorStop stop = this.stops.get(i);
//                    if (stop.stop > factor) {
//                        prev.color.compute(particle);
//                        stop.color.lerp(particle, (float) (factor - prev.stop) / (stop.stop - prev.stop));
//                        return;
//                    }
//
//                    prev = stop;
//                }
            });
        }
    }
}
