package moe.plushie.armourers_workshop.core.skin.geometry.mesh;

import moe.plushie.armourers_workshop.api.skin.geometry.ISkinGeometryType;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryFace;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryVertex;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTexturePos;

import java.util.List;

public class SkinMeshFace extends SkinGeometryFace {

    protected ISkinGeometryType type;
    protected List<SkinGeometryVertex> vertices;

    public SkinMeshFace(int id, ISkinGeometryType type, OpenTransform3f transform, SkinTexturePos texturePos, List<SkinGeometryVertex> vertices) {
        this.id = id;
        this.type = type;
        this.transform = transform;
        this.texturePos = texturePos;
        this.vertices = vertices;
    }

    @Override
    public ISkinGeometryType getType() {
        return type;
    }

    @Override
    public List<SkinGeometryVertex> getVertices() {
        return vertices;
    }
}
