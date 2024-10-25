package moe.plushie.armourers_workshop.core.skin.geometry;

import moe.plushie.armourers_workshop.api.skin.geometry.ISkinGeometryVertex;
import moe.plushie.armourers_workshop.core.math.Vector2f;
import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.skin.paint.SkinPaintColor;
import moe.plushie.armourers_workshop.core.utils.Objects;

public class SkinGeometryVertex implements ISkinGeometryVertex {

    protected int id;

    protected Vector3f position = Vector3f.ZERO;
    protected Vector3f normal = Vector3f.ZERO;
    protected Vector2f textureCoords = Vector2f.ZERO;

    protected Color color = Color.WHITE;

    public SkinGeometryVertex() {
    }

    public SkinGeometryVertex(int id, Vector3f position, Vector3f normal, Vector2f textureCoords) {
        this.id = id;
        this.position = position;
        this.normal = normal;
        this.textureCoords = textureCoords;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Vector3f getPosition() {
        return position;
    }

    @Override
    public Vector3f getNormal() {
        return normal;
    }

    @Override
    public Vector2f getTextureCoords() {
        return textureCoords;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "id", getId(), "position", getPosition(), "normal", getNormal(), "uv", getTextureCoords(), "color", getColor());
    }

    public static class Color extends SkinPaintColor {

        public static final Color WHITE = new Color(SkinPaintColor.WHITE, 255);

        protected final int alpha;

        public Color(SkinPaintColor paintColor, int alpha) {
            super(paintColor.getRawValue(), paintColor.getRGB(), paintColor.getPaintType());
            this.alpha = alpha;
        }

        public int getAlpha() {
            return alpha;
        }

        @Override
        public String toString() {
            var alpha = getAlpha();
            if (alpha < 255) {
                return String.format("%s * %f", super.toString(), getAlpha() / 255f);
            }
            return super.toString();
        }
    }
}
