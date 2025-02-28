package moe.plushie.armourers_workshop.core.client.render;

import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.api.skin.texture.ISkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;
import net.minecraft.core.Direction;

public class ExtendedFaceRenderer {

    // we manually reduced by 0.002 pixels,
    // it will prevent out of bound in the texture.
    private static final float[][] FACE_MARK_TEXTURES = {
            {0.998f, 0.998f}, {0.998f, 0.000f}, {0.000f, 0.000f}, {0, 0.998f}
    };

    // we define a float to reduce runtime type conversion.
    private static final float[][][] FACE_MARK_VERTEXES = new float[][][]{
            {{0, 0, 1}, {0, 0, 0}, {1, 0, 0}, {1, 0, 1}, {0, -1, 0}},   // +y
            {{1, 1, 1}, {1, 1, 0}, {0, 1, 0}, {0, 1, 1}, {0, 1, 0}},    // -y
            {{0, 0, 0}, {0, 1, 0}, {1, 1, 0}, {1, 0, 0}, {0, 0, -1}},   // +z
            {{1, 0, 1}, {1, 1, 1}, {0, 1, 1}, {0, 0, 1}, {0, 0, 1}},    // -z
            {{0, 0, 1}, {0, 1, 1}, {0, 1, 0}, {0, 0, 0}, {-1, 0, 0}},   // +x
            {{1, 0, 0}, {1, 1, 0}, {1, 1, 1}, {1, 0, 1}, {1, 0, 0}},    // -x
    };

    public static void renderMarker(int x, int y, int z, Direction direction, ISkinPaintColor paintColor, int alpha, int light, int overlay, IPoseStack poseStack, IVertexConsumer builder) {
        if (paintColor.getPaintType() == SkinPaintTypes.NORMAL) {
            return;
        }
        var pose = poseStack.last();
        var paintType = paintColor.getPaintType();
        var u = paintType.getIndex() % 8;
        var v = paintType.getIndex() / 8;
        var vertexes = FACE_MARK_VERTEXES[direction.get3DDataValue()];
        for (var i = 0; i < 4; ++i) {
            builder.vertex(pose, x + vertexes[i][0], y + vertexes[i][1], z + vertexes[i][2])
                    .color(255, 255, 255, alpha & 0xff)
                    .uv((u + FACE_MARK_TEXTURES[i][0]) / 8f, (v + FACE_MARK_TEXTURES[i][1]) / 8f)
                    .overlayCoords(overlay)
                    .uv2(light)
                    .normal(pose, vertexes[4][0], vertexes[4][1], vertexes[4][2])
                    .endVertex();
        }
    }

    public static void render2(int x, int y, int z, Direction direction, ISkinPaintColor paintColor, int alpha, int light, int overlay, IPoseStack poseStack, IVertexConsumer builder) {
        var entry = poseStack.last();
        var u = 0;
        var v = 0;
        var color = paintColor.getRGB();
        var vertexes = FACE_MARK_VERTEXES[direction.get3DDataValue()];
        for (var i = 0; i < 4; ++i) {
            builder.vertex(entry, x + vertexes[i][0], y + vertexes[i][1], z + vertexes[i][2])
                    .color(color >> 16 & 0xff, color >> 8 & 0xff, color & 0xff, alpha & 0xff)
                    .uv((u + FACE_MARK_TEXTURES[i][0]), (v + FACE_MARK_TEXTURES[i][1]))
                    .overlayCoords(overlay)
                    .uv2(light)
                    .normal(entry, vertexes[4][0], vertexes[4][1], vertexes[4][2])
                    .endVertex();
        }
    }
}
