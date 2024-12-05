package moe.plushie.armourers_workshop.core.skin.geometry.mesh;

import moe.plushie.armourers_workshop.api.skin.geometry.ISkinGeometryType;
import moe.plushie.armourers_workshop.core.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometry;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryTypes;
import moe.plushie.armourers_workshop.core.skin.texture.TexturePos;

import java.util.List;

public abstract class SkinMesh extends SkinGeometry {

    protected TexturePos texturePos;

    @Override
    public ISkinGeometryType getType() {
        return SkinGeometryTypes.MESH;
    }

    public TexturePos getTexturePos() {
        return texturePos;
    }

    @Override
    public OpenVoxelShape getShape() {
        var shape = new OpenVoxelShape();
        getFaces().forEach(face -> face.getVertices().forEach(vertex -> shape.add(vertex.getPosition())));
        return OpenVoxelShape.box(shape.bounds());
    }

    public abstract List<? extends SkinMeshFace> getFaces();
}
