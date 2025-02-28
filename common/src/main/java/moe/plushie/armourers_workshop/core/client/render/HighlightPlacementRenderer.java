package moe.plushie.armourers_workshop.core.client.render;

import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.data.MannequinHitResult;
import moe.plushie.armourers_workshop.core.data.SkinBlockPlaceContext;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

@Environment(EnvType.CLIENT)
public class HighlightPlacementRenderer {

    public static void renderBlock(ItemStack itemStack, Player player, BlockHitResult traceResult, Camera renderInfo, IPoseStack poseStack, IBufferSource bufferSource) {
        var descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.getType() != SkinTypes.BLOCK) {
            return;
        }

        poseStack.pushPose();

        var f = 1 / 16.f;
        var origin = renderInfo.getPosition();
        var context = new SkinBlockPlaceContext(player, InteractionHand.MAIN_HAND, itemStack, traceResult);
        var location = context.getClickedPos();

        poseStack.translate(location.getX() - (float) origin.x(), location.getY() - (float) origin.y(), location.getZ() - (float) origin.z());
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.scale(f, f, f);

        for (var part : context.getParts()) {
            var pos = part.getOffset();
            var color = UIColor.RED;
            if (context.canPlace(part)) {
                color = UIColor.WHITE;
            }
            poseStack.pushPose();
            poseStack.translate(pos.getX() * 16f, pos.getY() * 16f, pos.getZ() * 16f);
            ShapeTesselator.stroke(part.getShape(), color, poseStack, bufferSource);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    public static void renderEntity(Player player, BlockHitResult traceResult, Camera renderInfo, IPoseStack poseStack, IBufferSource bufferSource) {
        var origin = renderInfo.getPosition();
        var target = MannequinHitResult.test(player, origin, traceResult.getLocation(), traceResult.getBlockPos());
        poseStack.pushPose();

        var location = target.getLocation();

        poseStack.translate((float) (location.x() - origin.x()), (float) (location.y() - origin.y()), (float) (location.z() - origin.z()));
        poseStack.rotate(OpenVector3f.YP.rotationDegrees(-target.getRotation()));

        var model = SkinItemRenderer.getInstance().getMannequinModel();
        if (model != null) {
            var f = target.getScale() * 0.9375f; // base scale from player model
            var buffers1 = AbstractBufferSource.unwrap(bufferSource);
            var builder = buffers1.getBuffer(SkinRenderType.HIGHLIGHTED_ENTITY_LINES);
            poseStack.pushPose();
            poseStack.scale(f, f, f);
            poseStack.scale(-1, -1, 1);
            poseStack.translate(0.0f, -1.501f, 0.0f);
            model.renderToBuffer(AbstractPoseStack.unwrap(poseStack), builder, 0xf000f0, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);  // m,vb,l,p,color
            poseStack.popPose();
        }

        poseStack.popPose();
    }
}
