package moe.plushie.armourers_workshop.core.skin.serializer.v20.geometry.impl;

import moe.plushie.armourers_workshop.api.skin.geometry.ISkinGeometryType;
import moe.plushie.armourers_workshop.api.skin.texture.ITextureProvider;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.Rectangle3f;
import moe.plushie.armourers_workshop.core.math.Vector2f;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryTypes;
import moe.plushie.armourers_workshop.core.skin.geometry.cube.SkinCube;
import moe.plushie.armourers_workshop.core.skin.geometry.cube.SkinCubeFace;
import moe.plushie.armourers_workshop.core.skin.paint.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.TextureBox;
import moe.plushie.armourers_workshop.core.skin.texture.TextureOptions;
import moe.plushie.armourers_workshop.core.skin.texture.TexturePos;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOConsumer2;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkGeometrySlice;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkDataOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkPaletteData;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.geometry.ChunkGeometrySerializer;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.EnumMap;
import java.util.LinkedHashMap;

public class ChunkGeometrySerializerV2 extends ChunkGeometrySerializer {

    @Override
    public int stride(ISkinGeometryType geometryType, int options, ChunkPaletteData palette) {
        int faceCount = options & 0x0F;
        return Decoder.calcStride(palette.getTextureIndexBytes(), faceCount);
    }

    @Override
    public ChunkGeometrySerializer.Encoder<?> encoder(ISkinGeometryType geometryType) {
        return new Encoder();
    }

    @Override
    public ChunkGeometrySerializer.Decoder<?> decoder(ISkinGeometryType geometryType, ChunkGeometrySlice slice) {
        return new Decoder(geometryType, slice);
    }

    protected static class Decoder extends SkinCube implements ChunkGeometrySerializer.Decoder<SkinCube> {

        private final int faceCount;

        private final ChunkGeometrySlice slice;
        private final ChunkPaletteData palette;

        private final EnumMap<OpenDirection, Vector2f> startUVs = new EnumMap<>(OpenDirection.class);
        private final EnumMap<OpenDirection, Vector2f> endUVs = new EnumMap<>(OpenDirection.class);
        private final EnumMap<OpenDirection, TextureOptions> optionsValues = new EnumMap<>(OpenDirection.class);
        private final EnumMap<OpenDirection, TexturePos> texturePoss = new EnumMap<>(OpenDirection.class);

        private OpenTransform3f transform = OpenTransform3f.IDENTITY;

        public Decoder(ISkinGeometryType type, ChunkGeometrySlice slice) {
            this.palette = slice.getPalette();
            this.slice = slice;
            this.faceCount = slice.getGeometryOptions() & 0x0F;
        }

        public static int calcStride(int usedBytes, int size) {
            // rectangle(24B) + transform(64b) + (face flag + texture ref) * faceCount;
            return Rectangle3f.BYTES + OpenTransform3f.BYTES + (1 + usedBytes * 2) * size;
        }

        @Override
        public SkinCube begin() {
            return this;
        }

        @Override
        public ISkinGeometryType getType() {
            return SkinGeometryTypes.CUBE;
        }

        @Override
        public Rectangle3f getBoundingBox() {
            if (slice.once(0)) {
                boundingBox = slice.getRectangle3f(0);
            }
            return boundingBox;
        }

        @Override
        public OpenTransform3f getTransform() {
            if (slice.once(1)) {
                transform = slice.getTransform(24);
            }
            return transform;
        }

        @Override
        public SkinPaintColor getPaintColor(OpenDirection dir) {
            var key = getTexture(dir);
            if (key != null) {
                return SkinPaintColor.WHITE;
            }
            return SkinPaintColor.CLEAR;
        }

        @Override
        public TexturePos getTexture(OpenDirection dir) {
            if (slice.once(2)) {
                parseTextures();
            }
            return texturePoss.get(dir);
        }


        @Override
        public SkinCubeFace getFace(OpenDirection dir) {
            if (getTexture(dir) != null) {
                return super.getFace(dir);
            }
            return null;
        }

        protected void parseTextures() {
            startUVs.clear();
            endUVs.clear();
            optionsValues.clear();
            texturePoss.clear();
            TextureBox textureBox = null;
            int usedBytes = palette.getTextureIndexBytes();
            for (int i = 0; i < faceCount; ++i) {
                int index = calcStride(usedBytes, i);
                int face = slice.getByte(index);
                if ((face & 0x40) != 0) {
                    var opt = slice.getTextureOptions(index + 1);
                    for (var dir : OpenDirection.valuesFromSet(face)) {
                        optionsValues.put(dir, opt);
                    }
                    continue;
                }
                var pos = slice.getTexturePos(index + 1);
                for (var dir : OpenDirection.valuesFromSet(face)) {
                    endUVs.put(dir, pos);
                    if (!startUVs.containsKey(dir)) {
                        startUVs.put(dir, pos);
                    }
                }
                if ((face & 0x80) != 0) {
                    var ref = palette.readTexture(pos);
                    if (ref == null) {
                        continue;
                    }
                    var rect = getBoundingBox();
                    float width = rect.getWidth();
                    float height = rect.getHeight();
                    float depth = rect.getDepth();
                    textureBox = new TextureBox(width, height, depth, false, ref.getPos(), ref.getProvider());
                }
            }
            for (var dir : OpenDirection.values()) {
                var start = startUVs.get(dir);
                var end = endUVs.get(dir);
                if (start != null && end != null) {
                    var opt = optionsValues.get(dir);
                    var ref = palette.readTexture(start);
                    if (ref == null) {
                        continue;
                    }
                    float u = ref.getU();
                    float v = ref.getV();
                    float width = end.getX() - start.getX();
                    float height = end.getY() - start.getY();
                    texturePoss.put(dir, new TexturePos(u, v, width, height, opt, ref.getProvider()));
                } else if (textureBox != null) {
                    texturePoss.put(dir, textureBox.getTexture(dir));
                }
            }
        }
    }

    protected static class Encoder implements ChunkGeometrySerializer.Encoder<SkinCube> {

        private Rectangle3f boundingBox = Rectangle3f.ZERO;
        private OpenTransform3f transform = OpenTransform3f.IDENTITY;

        private final SortedMap<Vector2f> startValues = new SortedMap<>();
        private final SortedMap<Vector2f> endValues = new SortedMap<>();
        private final SortedMap<TextureOptions> optionsValues = new SortedMap<>();

        @Override
        public int begin(SkinCube geometry) {
            // merge all values
            for (var dir : OpenDirection.values()) {
                var value = geometry.getTexture(dir);
                if (value == null) {
                    continue;
                }
                var provider = value.getProvider();
                if (value instanceof TextureBox.Entry entry) {
                    startValues.put(0x80, entry.getParent(), provider);
                    // box need options?
                    continue;
                }
                int face = 1 << dir.get3DDataValue();
                float u = value.getU();
                float v = value.getV();
                float s = value.getWidth();
                float t = value.getHeight();
                startValues.put(face, new Vector2f(u, v), provider);
                endValues.put(face, new Vector2f(u + s, v + t), provider);
                if (value.getOptions() instanceof TextureOptions textureOptions) {
                    optionsValues.put(face, textureOptions, provider);
                }
            }
            transform = geometry.getTransform();
            boundingBox = geometry.getBoundingBox();
            return startValues.size() + endValues.size() + optionsValues.size();
        }

        @Override
        public void end(ChunkPaletteData palette, ChunkDataOutputStream stream) throws IOException {
            // rectangle(24B) + transform(64b)
            stream.writeRectangle3f(boundingBox);
            stream.writeTransformf(transform);

            // face: <texture ref>
            optionsValues.forEach((key, value) -> {
                stream.writeByte(0x40 | value);
                stream.writeVariable(palette.writeTextureOptions(key.getKey(), key.getValue()));
            });
            startValues.forEach((key, value) -> {
                stream.writeByte(value);
                stream.writeVariable(palette.writeTexture(key.getKey(), key.getValue()));
            });
            endValues.forEach((key, value) -> {
                stream.writeByte(value);
                stream.writeVariable(palette.writeTexture(key.getKey(), key.getValue()));
            });

            startValues.clear();
            endValues.clear();
            optionsValues.clear();
        }
    }

    protected static class SortedMap<T> {

        private final LinkedHashMap<Pair<T, ITextureProvider>, Integer> impl = new LinkedHashMap<>();

        public void forEach(IOConsumer2<Pair<T, ITextureProvider>, Integer> consumer) throws IOException {
            for (var entry : impl.entrySet()) {
                consumer.accept(entry.getKey(), entry.getValue());
            }
        }

        public void put(int face, T pos, ITextureProvider provider) {
            var index = Pair.of(pos, provider);
            int newFace = impl.getOrDefault(index, 0);
            newFace |= face;
            impl.put(index, newFace);
        }

        public void clear() {
            impl.clear();
        }

        public int size() {
            return impl.size();
        }
    }
}
