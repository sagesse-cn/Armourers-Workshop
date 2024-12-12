package moe.plushie.armourers_workshop.core.skin.serializer.importer.blockbench;

import moe.plushie.armourers_workshop.core.math.Vector2f;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BlockBenchMeshFace {

    protected final Map<String, Vector2f> uv;
    protected final List<String> vertices;

    protected final int textureId;

    public BlockBenchMeshFace(int textureId, Map<String, Vector2f> uv, List<String> vertices) {
        this.textureId = textureId;
        this.uv = uv;
        this.vertices = vertices;
    }

    public int getTextureId() {
        return textureId;
    }

    public Map<String, Vector2f> getUV() {
        return uv;
    }

    public List<String> getVertices() {
        return vertices;
    }

    protected static class Builder {

        protected final Map<String, Vector2f> uv = new LinkedHashMap<>();
        protected final List<String> vertices = new ArrayList<>();

        protected int texture = -1;

        public void texture(int texture) {
            this.texture = texture;
        }

        public void addUV(String key, Vector2f pos) {
            this.uv.put(key, pos);
        }

        public void addVertex(String key) {
            this.vertices.add(key);
        }

        public BlockBenchMeshFace build() {
            return new BlockBenchMeshFace(texture, uv, vertices);
        }
    }
}
