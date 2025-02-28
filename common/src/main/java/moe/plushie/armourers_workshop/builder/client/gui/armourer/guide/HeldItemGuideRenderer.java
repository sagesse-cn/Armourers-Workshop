package moe.plushie.armourers_workshop.builder.client.gui.armourer.guide;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.OpenModelPart;
import moe.plushie.armourers_workshop.core.utils.OpenModelPartBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class HeldItemGuideRenderer extends AbstractGuideRenderer {

    private final OpenModelPart armSolid;
    private final OpenModelPart armTransparent;

    public HeldItemGuideRenderer() {
        armSolid = OpenModelPartBuilder.player().uv(40, 16).cube(-2, -10, -4, 4, 8, 4).offset(0, 0, 0).build();
        armTransparent = OpenModelPartBuilder.player().uv(40, 24).cube(-2, -2, -4, 4, 4, 4).offset(0, 0, 0).build();
    }

    @Override
    public void init(GuideRendererManager rendererManager) {
        rendererManager.register(SkinPartTypes.ITEM_AXE, this::render);
        rendererManager.register(SkinPartTypes.ITEM_HOE, this::render);
        rendererManager.register(SkinPartTypes.ITEM_PICKAXE, this::render);
        rendererManager.register(SkinPartTypes.ITEM_SHOVEL, this::render);
        rendererManager.register(SkinPartTypes.ITEM_SHIELD, this::render);
        rendererManager.register(SkinPartTypes.ITEM_SWORD, this::render);
        rendererManager.register(SkinPartTypes.ITEM_TRIDENT, this::render);
        rendererManager.register(SkinPartTypes.ITEM_BOW0, this::render);
        rendererManager.register(SkinPartTypes.ITEM_BOW1, this::render);
        rendererManager.register(SkinPartTypes.ITEM_BOW2, this::render);
        rendererManager.register(SkinPartTypes.ITEM_BOW3, this::render);
        rendererManager.register(SkinPartTypes.ITEM, this::render);
    }

    public void render(IPoseStack poseStack, GuideDataProvider provider, int light, int overlay, IBufferSource bufferSource) {
        float f = 1 / 16f;
        poseStack.pushPose();
        poseStack.rotate(OpenVector3f.XP.rotationDegrees(-90));
        armSolid.render(poseStack, bufferSource.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        poseStack.translate(0, -0.001f * f, 0);
        armTransparent.render(poseStack, bufferSource.getBuffer(SkinRenderType.PLAYER_TRANSLUCENT), light, overlay, 0xbfffffff);
        poseStack.popPose();
    }
}
