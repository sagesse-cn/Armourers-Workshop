package moe.plushie.armourers_workshop.core.skin.geometry.cube;

import moe.plushie.armourers_workshop.api.skin.geometry.ISkinGeometryType;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.Rectangle3f;
import moe.plushie.armourers_workshop.core.math.Vector2f;
import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryFace;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryVertex;
import moe.plushie.armourers_workshop.core.skin.paint.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.paint.SkinPaintType;
import moe.plushie.armourers_workshop.core.skin.paint.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.skin.paint.texture.TexturePos;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;

import java.util.ArrayList;

public class SkinCubeFace extends SkinGeometryFace {

    private static final float[][][] UVS = new float[][][]{
            {{1, 0}, {1, 1}, {0, 1}, {0, 0}}, // -y <- down
            {{0, 0}, {0, 1}, {1, 1}, {1, 0}}, // +y <- up
            {{0, 0}, {0, 1}, {1, 1}, {1, 0}}, // -z <- north
            {{0, 0}, {0, 1}, {1, 1}, {1, 0}}, // +z <- south
            {{0, 0}, {0, 1}, {1, 1}, {1, 0}}, // -x <- west
            {{0, 0}, {0, 1}, {1, 1}, {1, 0}}, // +x <- east
//            {{1, 0}, {1, 1}, {0, 1}, {0, 0}},
//            {{1, 0}, {1, 1}, {0, 1}, {0, 0}},
    };

    private static final float[][][] VERTICES = new float[][][]{
            {{1, 1, 1}, {1, 1, 0}, {0, 1, 0}, {0, 1, 1}, {0, 1, 0}},  // -y <- down
            {{0, 0, 1}, {0, 0, 0}, {1, 0, 0}, {1, 0, 1}, {0, -1, 0}}, // +y <- up
            {{0, 0, 0}, {0, 1, 0}, {1, 1, 0}, {1, 0, 0}, {0, 0, -1}}, // -z <- north
            {{1, 0, 1}, {1, 1, 1}, {0, 1, 1}, {0, 0, 1}, {0, 0, 1}},  // +z <- south
            {{1, 0, 0}, {1, 1, 0}, {1, 1, 1}, {1, 0, 1}, {1, 0, 0}},  // -x <- west
            {{0, 0, 1}, {0, 1, 1}, {0, 1, 0}, {0, 0, 0}, {-1, 0, 0}}, // +x <- east
    };

    public final int alpha;

    private final ISkinGeometryType type;
    private final OpenDirection direction;
    private final SkinPaintColor paintColor;

    private final Rectangle3f boundingBox;

    public SkinCubeFace(int id, ISkinGeometryType type, OpenTransform3f transform, TexturePos texturePos, Rectangle3f boundingBox, OpenDirection direction, SkinPaintColor color, int alpha) {
        this.id = id;
        this.type = type;
        this.transform = transform;
        this.texturePos = texturePos;
        this.paintColor = color;
        this.alpha = alpha;
        this.direction = direction;
        this.boundingBox = boundingBox;
    }

    public static float[][] getBaseUVs(OpenDirection direction) {
        return UVS[direction.get3DDataValue()];
    }

    public static float[][] getBaseVertices(OpenDirection direction) {
        return VERTICES[direction.get3DDataValue()];
    }

    public Rectangle3f getBoundingBox() {
        return boundingBox;
    }

    public SkinPaintColor getColor() {
        return paintColor;
    }

    public int getAlpha() {
        return alpha;
    }

    public OpenDirection getDirection() {
        return direction;
    }

    public SkinPaintType getPaintType() {
        return paintColor.getPaintType();
    }

    @Override
    public ISkinGeometryType getType() {
        return type;
    }

    @Override
    public TexturePos getTexturePos() {
        if (texturePos != null) {
            return texturePos;
        }
        return paintColor.getPaintType().getTexturePos();
    }

    @Override
    public float getPriority() {
        return direction.get3DDataValue();
    }

    @Override
    public boolean isVisible() {
        return paintColor.getPaintType() != SkinPaintTypes.NONE;
    }

    @Override
    public Iterable<? extends SkinGeometryVertex> getVertices() {
        var id = getId();
        var texturePos = getTexturePos();

        // https://learnopengl.com/Getting-started/Coordinate-Systems
        var x = boundingBox.getX();
        var y = boundingBox.getY();
        var z = boundingBox.getZ();
        var w = roundUp(boundingBox.getWidth());
        var h = roundUp(boundingBox.getHeight());
        var d = roundUp(boundingBox.getDepth());

        var u = texturePos.getU();
        var v = texturePos.getV();
        var s = roundDown(texturePos.getWidth());
        var t = roundDown(texturePos.getHeight());

        var color = new SkinGeometryVertex.Color(paintColor, alpha);
        var vertices = new ArrayList<SkinGeometryVertex>();

        var vertexes = getBaseVertices(direction);
        var uvs = getBaseUVs(getTextureDirection(direction, texturePos));

        for (int i = 0; i < 4; ++i) {
            var position = new Vector3f(x + w * vertexes[i][0], y + h * vertexes[i][1], z + d * vertexes[i][2]);
            var normal = new Vector3f(vertexes[4][0], vertexes[4][1], vertexes[4][2]);
            var textureCoords = new Vector2f(u + s * uvs[i][0], v + t * uvs[i][1]);
            vertices.add(new SkinCubeVertex(id * 4 + i, position, normal, textureCoords, color, this));
        }

        return vertices;
    }

    private float roundUp(float edg) {
        if (edg == 0) {
            return 0.002f;
        }
        return edg;
    }

    // avoid out-of-bounds behavior caused by floating point precision.
    private float roundDown(float edg) {
        if (edg < 0) {
            return edg + 0.002f;
        } else {
            return edg - 0.002f;
        }
    }

    private OpenDirection getTextureDirection(OpenDirection direction, TexturePos key) {
        var options = key.getOptions();
        if (options != null) {
            return switch (options.getRotation()) {
                case 90 -> switch (direction) {
                    case DOWN -> OpenDirection.SOUTH;
                    case UP -> OpenDirection.DOWN;
                    case NORTH -> OpenDirection.UP;
                    case SOUTH -> OpenDirection.NORTH;
                    default -> direction;
                };
                case 180 -> switch (direction) {
                    case DOWN -> OpenDirection.NORTH;
                    case UP -> OpenDirection.SOUTH;
                    case NORTH -> OpenDirection.DOWN;
                    case SOUTH -> OpenDirection.UP;
                    default -> direction;
                };
                case 270 -> switch (direction) {
                    case DOWN -> OpenDirection.UP;
                    case UP -> OpenDirection.NORTH;
                    case NORTH -> OpenDirection.SOUTH;
                    case SOUTH -> OpenDirection.DOWN;
                    default -> direction;
                };
                default -> direction;
            };
        }
        return direction;
    }
}
