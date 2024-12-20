package moe.plushie.armourers_workshop.core.skin.serializer.v20.geometry.impl;

import moe.plushie.armourers_workshop.api.skin.geometry.ISkinGeometryType;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector2f;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryVertex;
import moe.plushie.armourers_workshop.core.skin.geometry.mesh.SkinMesh;
import moe.plushie.armourers_workshop.core.skin.geometry.mesh.SkinMeshFace;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkDataOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkGeometrySlice;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkPaletteData;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.geometry.ChunkGeometrySerializer;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTexturePos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ChunkGeometrySerializerV3 extends ChunkGeometrySerializer {

    @Override
    public int stride(ISkinGeometryType geometryType, int options, ChunkPaletteData palette) {
        return Encoder.HEADER_SIZE + options; // header + bytes
    }

    @Override
    public ChunkGeometrySerializer.Encoder<?> encoder(ISkinGeometryType geometryType) {
        return new Encoder();
    }

    @Override
    public ChunkGeometrySerializer.Decoder<?> decoder(ISkinGeometryType geometryType, ChunkGeometrySlice slice) {
        return new Decoder(geometryType, slice);
    }

    protected static class Decoder extends SkinMesh implements ChunkGeometrySerializer.Decoder<SkinMesh> {

        private final ChunkGeometrySlice slice;
        private final ChunkPaletteData palette;

        private final ArrayList<SkinMeshFace> faces = new ArrayList<>();
        private final ArrayList<SkinGeometryVertex> vertices = new ArrayList<>();

        private SkinTextureData textureProvider;

        public Decoder(ISkinGeometryType type, ChunkGeometrySlice slice) {
            this.palette = slice.getPalette();
            this.slice = slice;
        }

        public static int calcStride(int usedBytes, int size) {
            return Encoder.HEADER_SIZE + Encoder.VERTEX_STRIDE * size;
        }

        @Override
        public SkinMesh begin() {
            return this;
        }

        @Override
        public OpenTransform3f getTransform() {
            if (slice.once(0)) {
                transform = slice.getTransform(4);
            }
            return transform;
        }

        @Override
        public SkinTexturePos getTexturePos() {
            if (slice.once(1)) {
                getVertices();
                if (textureProvider != null) {
                    texturePos = new SkinTexturePos(0, 0, 0, 0, textureProvider);
                }
            }
            return texturePos;
        }

        @Override
        public List<SkinMeshFace> getFaces() {
            if (slice.once(2)) {
                parseFaces();
            }
            return faces;
        }

        public List<SkinGeometryVertex> getVertices() {
            if (slice.once(3)) {
                parseVertices();
            }
            return vertices;
        }

        protected void parseFaces() {
            faces.clear();

            int vertexCount = slice.getInt(68);
            int indexCount = slice.getInt(72);

            int usedBytes = palette.getTextureIndexBytes();
            int offset = calcStride(usedBytes, vertexCount); // skip the vertex data.

            var vertices = getVertices();
            var transform = getTransform();
            var texturePos = getTexturePos();

            int cursor = 0;
            for (int i = 0; i < indexCount; i += 2) {
                int type = slice.getInt(offset + i * 4);
                int length = slice.getInt(offset + i * 4 + 4);
                int faceVertexCount = type & 0xFF;
                for (int j = 0; j < length; j += faceVertexCount) {
                    faces.add(parseFace(faces.size(), cursor + j, faceVertexCount, transform, texturePos, vertices));
                }
                cursor += length;
            }
        }

        //
        protected void parseVertices() {
            vertices.clear();
            int usedBytes = palette.getTextureIndexBytes();
            int vertexCount = slice.getInt(68);
            for (int i = 0; i < vertexCount; i++) {
                vertices.add(parseVertex(i, usedBytes));
            }
        }

        protected SkinMeshFace parseFace(int faceId, int offset, int vertexCount, OpenTransform3f transform, SkinTexturePos texturePos, List<SkinGeometryVertex> vertices) {
            var faceVertices = new ArrayList<SkinGeometryVertex>(vertexCount);
            for (int i = 0; i < vertexCount; i++) {
                faceVertices.add(vertices.get(offset + i));
            }
            return new SkinMeshFace(faceId, transform, texturePos, faceVertices);
        }

        protected SkinGeometryVertex parseVertex(int i, int usedBytes) {
            int offset = calcStride(usedBytes, i);

            var position = slice.getVector3f(offset);
            var normal = slice.getVector3f(offset + 12);
            var textureCoords = slice.getTexturePos(offset + 24);

            // fix texture location.
            textureCoords = parseTextureCoords(textureCoords);

            return new SkinGeometryVertex(i, position, normal, textureCoords);
        }

        protected OpenVector2f parseTextureCoords(OpenVector2f uv) {
            var ref = palette.readTexture(uv);
            if (ref != null) {
                textureProvider = ref.getProvider();
                return ref.getPos();
            }
            return OpenVector2f.ZERO;
        }
    }

    protected static class Encoder implements ChunkGeometrySerializer.Encoder<SkinMesh> {

        private static final int HEADER_SIZE = 128;
        private static final int INDEX_STRIDE = 4;
        private static final int VERTEX_STRIDE = 32; // vertex(12B) + normal(12B) + uv(VB)

        private OpenTransform3f transform = OpenTransform3f.IDENTITY;
        private SkinTexturePos texturePos;

        private final List<Integer> indices = new ArrayList<>();
        private final List<SkinGeometryVertex> vertices = new ArrayList<>();

        private final byte[] reserved = new byte[128];

        @Override
        public int begin(SkinMesh mesh) {
            indices.clear();
            vertices.clear();

            transform = mesh.getTransform();
            texturePos = mesh.getTexturePos();

            var groupedVertices = new LinkedHashMap<Integer, ArrayList<SkinGeometryVertex>>();
            for (var face : mesh.getFaces()) {
                var faceVertices = face.getVertices();
                groupedVertices.computeIfAbsent(faceVertices.size(), k -> new ArrayList<>()).addAll(faceVertices);
            }

            for (var entry : groupedVertices.entrySet()) {
                int faceVertexCount = entry.getKey();
                int type = faceVertexCount & 0xFF;
                int length = entry.getValue().size();
                indices.add(type);
                indices.add(length);
                vertices.addAll(entry.getValue());
            }

            return vertices.size() * VERTEX_STRIDE + indices.size() * INDEX_STRIDE;
        }

        @Override
        public void end(ChunkPaletteData palette, ChunkDataOutputStream stream) throws IOException {
            // type(4b) + transform(64b) + vertex count(4B) + index count(4B) + reserved(52B)
            stream.writeInt(0);
            stream.writeTransformf(transform);
            stream.writeInt(vertices.size());
            stream.writeInt(indices.size());
            stream.write(reserved, 0, 52);
            // vertices: vertex(12B) + normal(12B) + uv(VB)
            for (var vertex : vertices) {
                stream.writeVector3f(vertex.getPosition());
                stream.writeVector3f(vertex.getNormal());
                stream.writeVariable(palette.writeTexture(vertex.getTextureCoords(), texturePos.getProvider()));
            }
            // indices: int(4B)
            for (var vertexId : indices) {
                stream.writeInt(vertexId);
            }
            vertices.clear();
            indices.clear();
            texturePos = null;
        }
    }
}
