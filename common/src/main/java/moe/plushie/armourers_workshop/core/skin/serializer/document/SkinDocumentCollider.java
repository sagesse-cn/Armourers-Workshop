package moe.plushie.armourers_workshop.core.skin.serializer.document;

import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenTransformedBoundingBox;
import moe.plushie.armourers_workshop.core.math.Rectangle3i;
import moe.plushie.armourers_workshop.core.math.Vector3i;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class SkinDocumentCollider {

    public static HashMap<Vector3i, Rectangle3i> generateCollisionBox(SkinDocumentNode node) {
        var boxes = generateCollisionBox(node, new OpenPoseStack());
        var results = new LinkedHashMap<Vector3i, Rectangle3i>();
        for (var it : boxes) {
            var box = it.getTransformedBoundingBox();

            var minX = OpenMath.floori(box.getMinX() + 8);
            var minY = OpenMath.floori(box.getMinY() + 8);
            var minZ = OpenMath.floori(box.getMinZ() + 8);
            var maxX = OpenMath.ceili(box.getMaxX() + 8);
            var maxY = OpenMath.ceili(box.getMaxY() + 8);
            var maxZ = OpenMath.ceili(box.getMaxZ() + 8);
            var tt = new Rectangle3i(minX, minY, minZ, maxX - minX, maxY - minY, maxZ - minZ);

            var blockMinX = OpenMath.floori(minX / 16f);
            var blockMinY = OpenMath.floori(minY / 16f);
            var blockMinZ = OpenMath.floori(minZ / 16f);
            var blockMaxX = OpenMath.ceili(maxX / 16f);
            var blockMaxY = OpenMath.ceili(maxY / 16f);
            var blockMaxZ = OpenMath.ceili(maxZ / 16f);
            for (int z = blockMinZ; z <= blockMaxZ; ++z) {
                for (var y = blockMinY; y <= blockMaxY; ++y) {
                    for (var x = blockMinX; x <= blockMaxX; ++x) {
                        var rr = new Rectangle3i(x * 16, y * 16, z * 16, 16, 16, 16);
                        rr.intersection(tt);
                        if (rr.getWidth() <= 0 || rr.getHeight() <= 0 || rr.getDepth() <= 0) {
                            continue;
                        }
                        results.computeIfAbsent(new Vector3i(x, y, z), pos -> rr).union(rr);
                    }
                }
            }
        }
        return results;
    }

    private static ArrayList<OpenTransformedBoundingBox> generateCollisionBox(SkinDocumentNode node, OpenPoseStack poseStack) {
        var result = new ArrayList<OpenTransformedBoundingBox>();

        if (node.getId().equals("float")) {
            return result;
        }

        poseStack.pushPose();

        node.getTransform().apply(poseStack);
        var skin = SkinLoader.getInstance().loadSkin(node.getSkin().getIdentifier());
        if (skin != null) {
            for (var part : skin.getParts()) {
                result.addAll(generateCollisionBox(part, poseStack));
            }
        }

        node.children().forEach(child -> {
            result.addAll(generateCollisionBox(child, poseStack));
        });

        poseStack.popPose();
        return result;
    }

    private static ArrayList<OpenTransformedBoundingBox> generateCollisionBox(SkinPart part, OpenPoseStack poseStack) {
        var result = new ArrayList<OpenTransformedBoundingBox>();
        poseStack.pushPose();
        part.getTransform().apply(poseStack);
        part.getGeometries().forEach(geometry -> {
            poseStack.pushPose();
            geometry.getTransform().apply(poseStack);
            var aabb = geometry.getShape().aabb();
            var tbb = new OpenTransformedBoundingBox(poseStack.last().pose().copy(), aabb);
            result.add(tbb);
            poseStack.popPose();
        });
        part.getChildren().forEach(child -> {
            result.addAll(generateCollisionBox(child, poseStack));
        });
        poseStack.popPose();
        return result;
    }
}
