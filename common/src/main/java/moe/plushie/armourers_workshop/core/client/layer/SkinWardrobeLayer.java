package moe.plushie.armourers_workshop.core.client.layer;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractRenderLayer;
import moe.plushie.armourers_workshop.compatibility.client.model.AbstractModelHolder;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.patch.EpicFightEntityRendererPatch;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class SkinWardrobeLayer<T extends Entity, V extends EntityModel<T>, M extends IModel> extends AbstractRenderLayer<T, V> {

    protected final BakedArmature armature;
    protected final RenderLayerParent<T, V> entityRenderer;

    public SkinWardrobeLayer(BakedArmatureTransformer armatureTransformer, RenderLayerParent<T, V> renderer) {
        super(renderer);
        this.armature = new BakedArmature(armatureTransformer.getArmature());
        this.entityRenderer = renderer;
    }

    @Override
    public void render(T entity, float limbSwing, float limbSwingAmount, int packedLightIn, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, IPoseStack poseStack, IBufferSource bufferSource) {
        // respect invisibility potions etc.
        if (entity.isInvisible()) {
            return;
        }
        var renderData = EntityRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        var renderingTasks = renderData.getArmorSkins();
        if (renderingTasks.isEmpty()) {
            return;
        }
        var renderPatch = renderData.getRenderPatch();
        if (renderPatch == null) {
            return;
        }
        var transformer = renderPatch.getTransformer();
        if (transformer == null) {
            return;
        }
        var poseStack1 = poseStack;
        var epicFlightContext = Objects.safeCast(renderPatch, EpicFightEntityRendererPatch.class);
        if (epicFlightContext != null) {
            poseStack = epicFlightContext.getOverridePose();
        }

        poseStack.pushPose();

        // apply the model baby scale.
        if (epicFlightContext == null) {
            applyModelScale(poseStack, AbstractModelHolder.of(getParentModel()));
        }

        var f = 1 / 16f;
        poseStack.scale(f, f, f);

        transformer.applyTo(armature);

        var pluginContext = renderPatch.getPluginContext();
        var renderingContext = renderPatch.getRenderingContext();

        renderingContext.setOverlay(pluginContext.getOverlay());
        renderingContext.setLightmap(pluginContext.getLightmap());
        renderingContext.setPartialTicks(pluginContext.getPartialTicks());
        renderingContext.setAnimationTicks(pluginContext.getAnimationTicks());

        renderingContext.setPoseStack(poseStack);
        renderingContext.setBufferSource(bufferSource);
        renderingContext.setModelViewStack(AbstractPoseStack.create(RenderSystem.getExtendedModelViewStack()));

        renderingContext.setOutlineColor(entity.getOutlineColor());

        for (var entry : renderingTasks) {
            renderingContext.setOverlay(entry.getOverrideOverlay(entity));
            renderingContext.setItemSource(SkinItemSource.create(entry.getRenderPriority(), entry.getItemStack()));
            var bakedSkin = entry.getSkin();
            bakedSkin.setupAnim(entity, armature, renderingContext);
            var paintScheme = bakedSkin.resolve(entity, entry.getPaintScheme());
            SkinRenderer.render(entity, armature, bakedSkin, paintScheme, renderingContext);
        }

        poseStack.popPose();
    }

    protected void applyModelScale(IPoseStack poseStack, M model) {
        var babyPose = model.getBabyPose();
        if (babyPose != null) {
            var scale = 1 / babyPose.getHeadScale();
            var offset = babyPose.getHeadOffset();
            poseStack.scale(scale, scale, scale);
            poseStack.translate(offset.x() / 16f, offset.y() / 16f, offset.z() / 16f);
        }
    }
}
