package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocument;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
public class AdvancedBlockGuideRenderer extends AdvancedAbstractGuideRenderer {

    private final BlockState blockState;
    private final BlockRenderDispatcher blockRenderer;

    public AdvancedBlockGuideRenderer() {
        this.blockState = Blocks.GRASS_BLOCK.defaultBlockState();
        this.blockRenderer = Minecraft.getInstance().getBlockRenderer();
    }

    @Override
    public void render(SkinDocument document, IPoseStack poseStack, int light, int overlay, IBufferSource bufferSource) {
        poseStack.pushPose();
        poseStack.scale(-16, -16, 16);
        poseStack.translate(-0.5f, -1.5f, -0.5f);
        blockRenderer.renderSingleBlock(blockState, AbstractPoseStack.unwrap(poseStack), AbstractBufferSource.unwrap(bufferSource), 0xf000f0, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
