package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.PlaceholderManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderMode;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentPose;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentType;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

public class BakedAttachmentTransform {

    protected final SkinAttachmentType type;
    protected final Collection<BakedSkinPart> children;

    protected final SkinAttachmentPose output = new SkinAttachmentPose();

    protected BakedAttachmentTransform(SkinAttachmentType type, Collection<BakedSkinPart> children) {
        this.type = type;
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
            results.add(create(part.getName(), new ArrayList<>(parent)));
        }
        // check the child tree.
        for (var child : part.getChildren()) {
            collect(child, parent, results);
        }
        parent.pop();
    }

    protected static BakedAttachmentTransform create(String name, Collection<BakedSkinPart> children) {
        var type = SkinAttachmentTypes.byName(name);
        if (type == SkinAttachmentTypes.RIDING) {
            return new Ridding(type, children);
        }
        return new BakedAttachmentTransform(type, children);
    }

    public void setup(@Nullable Entity entity, BakedArmature armature, SkinRenderContext context) {
        var renderData = context.getRenderData();
        if (renderData == null) {
            return;
        }
        var partialTicks = context.getPartialTicks();
        var poseStack = context.getPoseStack();
        setup(entity, armature, partialTicks, poseStack, renderData);
    }

    protected void setup(Entity entity, BakedArmature armature, float partialTicks, IPoseStack poseStack, EntityRenderData renderData) {
        poseStack.pushPose();

        apply(entity, armature, partialTicks, poseStack, renderData);

        if (ModDebugger.attachmentOverride && !PlaceholderManager.isPlaceholder(entity)) {
            var tesselator = AbstractBufferSource.tesselator();
            ShapeTesselator.vector(0, 0, 0, 1, 1, 1, poseStack, tesselator);
            tesselator.endBatch();
        }

        poseStack.popPose();
    }

    protected void apply(Entity entity, BakedArmature armature, float partialTicks, IPoseStack poseStack, EntityRenderData renderData) {
        for (var child : children) {
            var jointTransform = armature.getTransform(child);
            if (jointTransform != null) {
                jointTransform.apply(poseStack);
            }
            child.getTransform().apply(poseStack);
        }

        poseStack.scale(16, 16, 16);

        output.last().set(poseStack.last());
        renderData.setAttachmentPose(type, output);
    }

    private static class Ridding extends BakedAttachmentTransform {

        protected Ridding(SkinAttachmentType type, Collection<BakedSkinPart> children) {
            super(type, children);
        }


        @Override
        protected void setup(Entity entity, BakedArmature armature, float partialTicks, IPoseStack poseStack, EntityRenderData renderData) {
            // ig
            if (SkinRenderMode.inGUI()) {
                return;
            }

            // ..
            apply(entity, armature, partialTicks, new OpenPoseStack(), renderData);

            // ..
            var offset = entity.getCustomRidding(partialTicks, output);
            entity.setCustomRidding(0, offset);

            if (ModDebugger.attachmentOverride && !PlaceholderManager.isPlaceholder(entity)) {
                var tesselator = AbstractBufferSource.tesselator();
                poseStack.pushPose();
                poseStack.setIdentity();
                var cameraPos = Minecraft.getInstance().getCameraPosition();
                double d0 = OpenMath.lerp(partialTicks, entity.xOld, entity.getX()) + offset.getX() - cameraPos.getX();
                double d1 = OpenMath.lerp(partialTicks, entity.yOld, entity.getY()) + offset.getY() - cameraPos.getY();
                double d2 = OpenMath.lerp(partialTicks, entity.zOld, entity.getZ()) + offset.getZ() - cameraPos.getZ();
                poseStack.translate((float) d0, (float) d1, (float) d2);
                ShapeTesselator.vector(0, 0, 0, 1, 1, 1, poseStack, tesselator);
                tesselator.endBatch();
                poseStack.popPose();
            }
        }
    }
}
