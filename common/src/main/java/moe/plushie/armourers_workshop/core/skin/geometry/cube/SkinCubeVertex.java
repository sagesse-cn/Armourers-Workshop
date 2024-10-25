package moe.plushie.armourers_workshop.core.skin.geometry.cube;

import moe.plushie.armourers_workshop.core.math.Rectangle3f;
import moe.plushie.armourers_workshop.core.math.Vector2f;
import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryVertex;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;

public class SkinCubeVertex extends SkinGeometryVertex {

    private final SkinCubeFace face;

    public SkinCubeVertex(int id, Vector3f position, Vector3f normal, Vector2f textureCoords, Color color, SkinCubeFace face) {
        this.id = id;
        this.face = face;
        this.position = position;
        this.normal = normal;
        this.textureCoords = textureCoords;
        this.color = color;
    }

    public Rectangle3f getBoundingBox() {
        return face.getBoundingBox();
    }

    public OpenDirection getDirection() {
        return face.getDirection();
    }
}
