package moe.plushie.armourers_workshop.compatibility.client.gui;

import com.apple.library.impl.EntityRendererImpl;
import com.mojang.blaze3d.platform.Lighting;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;

@Available("[1.21, )")
@Environment(EnvType.CLIENT)
public class AbstractGraphicsRendererImpl {

    private static final EntityRendererImpl<LivingEntity> DEFAULT_ENTITY_RENDERER = (entity, origin, scale, focus, context) -> {
        // forward to vanilla implements.
        int tx = (int) origin.x;
        int ty = (int) origin.y;
        float s = entity.getScale();
        float f = -entity.getBbHeight() / 2.0f / s; // remove internal offset.
        var guiGraphics = AbstractGraphicsRenderer.of(context);
        var poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(0, 0, 50);
        poseStack.scale(s, s, s);
        poseStack.translate(0, 0, -50);
        InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, tx, ty, tx, ty, scale, f, focus.x(), focus.y(), entity);
        poseStack.popPose();
    };

    private static final EntityRendererImpl<Entity> CUSTOM_ENTITY_RENDERER = (entity, origin, scale, focus, context) -> {
        // custom entity renderer from the InventoryScreen.renderEntityInInventory
        float p = (float) Math.atan((0 - focus.x) / 40.0f);
        float q = (float) Math.atan((0 - focus.y) / 40.0f);
        var quaternion = OpenVector3f.ZP.rotationDegrees(180.0f);
        var quaternion2 = OpenVector3f.XP.rotationDegrees(q * 20.0f);
        quaternion.mul(OpenVector3f.YP.rotationDegrees(180.0f));
        quaternion.mul(quaternion2);
        //float m = livingEntity.yBodyRot;
        float s = entity.getYRot();
        float t = entity.getXRot();
        //float p = livingEntity.yHeadRotO;
        //float q = livingEntity.yHeadRot;
        //livingEntity.yBodyRot = 180.0F + h * 20.0F;
        entity.setYRot(p * 40.0f);
        entity.setXRot(-q * 20.0f);
        //livingEntity.yHeadRot = livingEntity.getYRot();
        //livingEntity.yHeadRotO = livingEntity.getYRot();
        var guiGraphics = AbstractGraphicsRenderer.of(context);
        var poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(origin.x, origin.y, 50.0);
        poseStack.mulPose(new Matrix4f().scaling(scale, scale, -scale));
        //poseStack.translate(0, center, 0);
        poseStack.mulPose(quaternion);
        Lighting.setupForEntityInInventory();
        var renderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion2.conjugate();
        renderDispatcher.overrideCameraOrientation(AbstractPoseStack.copyQuaternion(quaternion2));
        renderDispatcher.setRenderShadow(false);
        RenderSystem.runAsFancy(() -> renderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0f, 1.0f, poseStack, guiGraphics.bufferSource(), 0xF000F0));
        guiGraphics.flush();
        renderDispatcher.setRenderShadow(true);
        //livingEntity.yBodyRot = m;
        entity.setYRot(s);
        entity.setXRot(t);
        //livingEntity.yHeadRotO = p;
        //livingEntity.yHeadRot = q;
        poseStack.popPose();
        Lighting.setupFor3DItems();
    };

    @SuppressWarnings("unchecked")
    public static <T extends Entity> EntityRendererImpl<T> getRenderer(T entity) {
        if (entity instanceof LivingEntity) {
            return (EntityRendererImpl<T>) DEFAULT_ENTITY_RENDERER;
        }
        return (EntityRendererImpl<T>) CUSTOM_ENTITY_RENDERER;
    }
}
