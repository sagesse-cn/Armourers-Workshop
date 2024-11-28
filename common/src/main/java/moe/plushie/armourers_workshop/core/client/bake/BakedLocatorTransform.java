package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.skin.locator.SkinLocatorType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

public class BakedLocatorTransform {

    public static final Vector3f RIGHT_HAND_OFFSET = new Vector3f(-1, 10, -2);
    public static final Vector3f LEFT_HAND_OFFSET = new Vector3f(1, 10, -2);

    protected final String name;
    protected final Collection<BakedSkinPart> children;

    protected final SkinLocatorType type;
    protected final Vector3f offset;
    protected final OpenPoseStack outputPose = new OpenPoseStack();

    protected BakedLocatorTransform(String name, Collection<BakedSkinPart> children) {
        this.name = name;
        this.children = children;
        // right_arm(-5,2,0) + Vector3f(-1,10,-2)
        // left_arm(5,2,0) + Vector3f(1,10,-2)
        if (name.equals("hand_l")) {
            this.type = SkinLocatorType.LEFT_HAND;
            this.offset = LEFT_HAND_OFFSET;
        } else if (name.equals("hand_r")) {
            this.type = SkinLocatorType.RIGHT_HAND;
            this.offset = RIGHT_HAND_OFFSET;
        } else {
            this.type = SkinLocatorType.ELYTRA;
            this.offset = Vector3f.ZERO;
        }
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
        var renderData = context.getRenderData();
        if (renderData == null) {
            return;
        }
        var poseStack = context.getPoseStack();
        poseStack.pushPose();

        for (var child : children) {
            var bakedTransform = armature.getTransform(child);
            if (bakedTransform != null) {
                bakedTransform.apply(poseStack);
            }
            child.getTransform().apply(poseStack);
        }

        poseStack.translate(-offset.getX(), -offset.getY(), -offset.getZ());
        poseStack.scale(16, 16, 16);

        outputPose.last().set(poseStack.last());
        renderData.setLocatorPose(type, outputPose);

        poseStack.popPose();
    }
}
