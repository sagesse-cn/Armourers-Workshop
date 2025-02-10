package moe.plushie.armourers_workshop.core.skin.geometry.mesh;

import moe.plushie.armourers_workshop.core.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometry;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTexturePos;

import java.util.List;

public abstract class SkinMesh extends SkinGeometry {

    protected SkinTexturePos texturePos;

    public SkinTexturePos getTexturePos() {
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
