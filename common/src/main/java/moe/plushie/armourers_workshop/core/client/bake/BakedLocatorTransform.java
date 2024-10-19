package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import moe.plushie.armourers_workshop.utils.math.OpenPoseStack;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

public class BakedLocatorTransform {

    public static final Vector3f RIGHT_HAND_OFFSET = new Vector3f(-1, 10, -2);
    public static final Vector3f LEFT_HAND_OFFSET = new Vector3f(1, 10, -2);

    private final String name;
    private final Collection<BakedSkinPart> children;

    public BakedLocatorTransform(String name, Collection<BakedSkinPart> children) {
        this.name = name;
        this.children = children;
    }

    public static Collection<BakedLocatorTransform> create(Collection<BakedSkinPart> parts) {
        var results = new ArrayList<BakedLocatorTransform>();
        for (var skinPart : parts) {
            collect(skinPart, new Stack<>(), results);
        }
        // we need optimize it?
        return results;
    }

    private static void collect(BakedSkinPart part, Stack<BakedSkinPart> parent, ArrayList<BakedLocatorTransform> results) {
        parent.push(part);
        // this object is a locator?
        if (part.getType() == SkinPartTypes.ADVANCED_LOCATOR) {
            results.add(new BakedLocatorTransform(part.getName(), new ArrayList<>(parent)));
        }
        // check the child tree.
        for (var child : part.getChildren()) {
            collect(child, parent, results);
        }
        parent.pop();
    }

    public void setup(@Nullable Entity entity, BakedArmature armature, SkinRenderContext context) {
//        var renderData = context.getRenderData();
//        if (renderData == null) {
//            return;
//        }
//        var poseStack = context.getPoseStack();
//        poseStack.pushPose();
//
//        if (ModDebugger.flag3 == 0) {
//            var tesselator = AbstractBufferSource.tesselator();
//            ShapeTesselator.vector(Vector3f.ZERO, 16, poseStack, tesselator);
//            tesselator.endBatch();
//        }
//
//        if (ModDebugger.flag2 == 0) {
//            for (var child : children) {
//                if (ModDebugger.flag1 == 0 && child.getType() == SkinPartTypes.ADVANCED_LOCATOR) {
//                    continue;
//                }
//                var bakedTransform = armature.getTransform(child);
//                if (bakedTransform != null) {
//                    bakedTransform.apply(poseStack);
//                    child.getTransform().apply(poseStack);
//                }
//            }
//        }
//
//        if (ModDebugger.flag3 == 2) {
//            var tesselator = AbstractBufferSource.tesselator();
//            ShapeTesselator.vector(Vector3f.ZERO, 16, poseStack, tesselator);
//            tesselator.endBatch();
//        }
//
//        // -5,2,0 + 0,4,0 + 5,18,0 + -5.5,-12,-2
//
//        ModDebugger.translate(poseStack);
//        ModDebugger.rotate(poseStack);
//        ModDebugger.scale(poseStack);
//
//        if (ModDebugger.flag3 == 3) {
//            var tesselator = AbstractBufferSource.tesselator();
//            ShapeTesselator.vector(Vector3f.ZERO, 16, poseStack, tesselator);
//            tesselator.endBatch();
//        }
//
//        // right_arm(-5,2,0) + Vector3f(-1,10,-2)
//        // left_arm(5,2,0) + Vector3f(1,10,-2)
//        var offset = RIGHT_HAND_OFFSET;
//        poseStack.translate(-offset.getX(), -offset.getY(), -offset.getZ());
//        poseStack.scale(16, 16, 16);
//
//        // copy
//        var finalPoseStack = new OpenPoseStack();
//        finalPoseStack.last().set(poseStack.last());
//        renderData.handPoseStack = finalPoseStack;
//
//        poseStack.popPose();
    }
}
