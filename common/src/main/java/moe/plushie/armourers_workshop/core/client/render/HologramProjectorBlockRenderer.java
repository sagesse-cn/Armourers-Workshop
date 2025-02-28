package moe.plushie.armourers_workshop.core.client.render;

import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.other.BlockEntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.PlaceholderManager;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class HologramProjectorBlockRenderer<T extends HologramProjectorBlockEntity> extends AbstractBlockEntityRenderer<T> {

    public HologramProjectorBlockRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float partialTicks, IPoseStack poseStack, IBufferSource bufferSource, int light, int overlay) {
        if (!entity.isPowered()) {
            return;
        }
        var renderData = BlockEntityRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        renderData.tick(entity);
        var renderingTasks = renderData.getAllSkins();
        if (renderingTasks.isEmpty()) {
            return;
        }
//        var itemStack = entity.getItem(0);
//        var descriptor = SkinDescriptor.of(itemStack);
//        var context = SkinRenderTesselator.create(descriptor, Tickets.RENDERER);
//        if (context == null) {
//            return;
//        }
        var f = 1 / 16f;
        var overLight = light;
        if (entity.isOverrideLight()) {
            overLight = 0xf000f0;
        }

        var blockState = entity.getBlockState();
        var renderPatch = renderData.getRenderPatch();
        var mannequinEntity = PlaceholderManager.MANNEQUIN.get();

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.rotate(entity.getRenderRotations(blockState));
        poseStack.translate(0.0f, 0.5f, 0.0f);

        poseStack.scale(f, f, f);
        poseStack.scale(-1, -1, 1);

        renderPatch.activate(entity, partialTicks, overLight, overlay, poseStack);

        var pluginContext = renderPatch.getPluginContext();
        var renderingContext = renderPatch.getRenderingContext();

        renderingContext.setOverlay(pluginContext.getOverlay());
        renderingContext.setLightmap(pluginContext.getLightmap());
        renderingContext.setPartialTicks(pluginContext.getPartialTicks());
        renderingContext.setAnimationTicks(pluginContext.getAnimationTicks());

        renderingContext.setPoseStack(poseStack);
        renderingContext.setBufferSource(bufferSource);
        renderingContext.setModelViewStack(AbstractPoseStack.create(RenderSystem.getExtendedModelViewStack()));

        for (var entry : renderingTasks) {
            var itemSource = SkinItemSource.create(entry.getItemStack());
            var bakedSkin = entry.getSkin();
            var bakedArmature = BakedArmature.defaultBy(bakedSkin.getType());
            var rect = bakedSkin.getRenderBounds();

            renderingContext.setItemSource(itemSource);
            renderingContext.setColorScheme(entry.getPaintScheme());
            //renderPatch.setOverlay(entry.getOverrideOverlay(entity));

            apply(entity, rect, renderingContext.getAnimationTicks(), poseStack, bufferSource);

            bakedSkin.setupAnim(mannequinEntity, bakedArmature, renderingContext);
            var paintScheme = bakedSkin.resolve(mannequinEntity, entry.getPaintScheme());
            SkinRenderer.render(mannequinEntity, bakedArmature, bakedSkin, paintScheme, renderingContext);
        }

        poseStack.popPose();

        if (ModDebugger.hologramProjector) {
            var pos = entity.getBlockPos();
            poseStack.pushPose();
            poseStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());
            ShapeTesselator.stroke(entity.getRenderShape(blockState), UIColor.ORANGE, poseStack, bufferSource);
            poseStack.popPose();
        }

        renderPatch.deactivate(entity);
    }

    private void apply(T entity, OpenRectangle3f rect, double animationTime, IPoseStack poseStack, IBufferSource bufferSource) {
        var angle = entity.getModelAngle();
        var offset = entity.getModelOffset();
        var rotationOffset = entity.getRotationOffset();
        var rotationSpeed = entity.getRotationSpeed();

        var rotX = angle.x();
        var speedX = rotationSpeed.x() / 1000f;
        if (speedX != 0) {
            rotX += (float) (((animationTime % speedX) / speedX) * 360.0);
        }

        var rotY = angle.y();
        var speedY = rotationSpeed.y() / 1000f;
        if (speedY != 0) {
            rotY += (float) (((animationTime % speedY) / speedY) * 360.0);
        }

        var rotZ = angle.z();
        var speedZ = rotationSpeed.z() / 1000f;
        if (speedZ != 0) {
            rotZ += (float) (((animationTime % speedZ) / speedZ) * 360.0);
        }

        var scale = entity.getModelScale();
        poseStack.scale(scale, scale, scale);
        if (entity.isOverrideOrigin()) {
            poseStack.translate(0, -rect.maxY(), 0); // to model center
        }
        poseStack.translate(-offset.x(), -offset.y(), offset.z());

        if (entity.shouldShowRotationPoint()) {
            ShapeTesselator.stroke(-1, -1, -1, 1, 1, 1, UIColor.MAGENTA, poseStack, bufferSource);
        }

        if (ModDebugger.hologramProjector) {
            ShapeTesselator.vector(OpenVector3f.ZERO, 128, poseStack, bufferSource);
        }

        poseStack.rotate(new OpenQuaternionf(rotX, -rotY, rotZ, true));
        poseStack.translate(rotationOffset.x(), -rotationOffset.y(), rotationOffset.z());

        if (ModDebugger.hologramProjector) {
            ShapeTesselator.vector(OpenVector3f.ZERO, 128, poseStack, bufferSource);
        }
    }

    @Override
    public int getViewDistance() {
        return 272;
    }
}
