package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.core.client.other.PlaceholderManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentPose;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentType;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

public class BakedAttachmentTransform {

    protected final SkinAttachmentType type;
    protected final Collection<BakedSkinPart> children;

    protected final SkinAttachmentPose output = new SkinAttachmentPose();

    protected BakedAttachmentTransform(String name, Collection<BakedSkinPart> children) {
        this.type = SkinAttachmentTypes.byName(name);
        this.children = children;
    }

    public static Collection<BakedAttachmentTransform> create(Collection<BakedSkinPart> parts) {
        var results = new ArrayList<BakedAttachmentTransform>();
        for (var skinPart : parts) {
            collect(skinPart, new Stack<>(), results);
        }
        // we need optimize it?
        return results;
    }

    private static void collect(BakedSkinPart part, Stack<BakedSkinPart> parent, ArrayList<BakedAttachmentTransform> results) {
        parent.push(part);
        // this object is a locator?
        if (part.getType() == SkinPartTypes.ADVANCED_LOCATOR) {
            results.add(new BakedAttachmentTransform(part.getName(), new ArrayList<>(parent)));
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

        poseStack.scale(16, 16, 16);

        output.last().set(poseStack.last());
        renderData.setAttachmentPose(type, output);
        if (ModDebugger.attachmentOverride && !PlaceholderManager.isPlaceholder(entity)) {
            var tesselator = AbstractBufferSource.tesselator();
            ShapeTesselator.vector(0, 0, 0, 1, 1, 1, poseStack, tesselator);
            tesselator.endBatch();
        }

        poseStack.popPose();
    }
}
