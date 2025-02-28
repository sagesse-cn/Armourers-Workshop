package moe.plushie.armourers_workshop.builder.client.render;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.builder.item.SkinCubeItem;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractItemStackRenderer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.render.ExtendedFaceRenderer;
import moe.plushie.armourers_workshop.core.data.color.BlockPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.init.ModBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class SkinCubeItemRenderer extends AbstractItemStackRenderer {

    private static SkinCubeItemRenderer INSTANCE;

    public static SkinCubeItemRenderer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SkinCubeItemRenderer();
        }
        return INSTANCE;
    }

    @Override
    public void renderByItem(ItemStack itemStack, OpenItemDisplayContext itemDisplayContext, IPoseStack poseStack, IBufferSource bufferSource, int light, int overlay) {
        if (itemStack.isEmpty()) {
            return;
        }
        var item = (SkinCubeItem) itemStack.getItem();
        var blockPaintColor = item.getItemColors(itemStack);
        if (blockPaintColor == null) {
            blockPaintColor = BlockPaintColor.WHITE;
        }
        var block = item.getBlock();

        var isGlowing = block.equals(ModBlocks.SKIN_CUBE_GLOWING.get()) || block.equals(ModBlocks.SKIN_CUBE_GLASS_GLOWING.get());
        var isGlass = block.equals(ModBlocks.SKIN_CUBE_GLASS.get()) || block.equals(ModBlocks.SKIN_CUBE_GLASS_GLOWING.get());

        var renderType = SkinRenderType.BLOCK_CUBE;
        if (isGlass) {
            renderType = SkinRenderType.BLOCK_CUBE_GLASS;
        }
        if (isGlowing) {
            var f1 = 1 / 16.0f;
            var f = 14 / 16.0f;
            var builder2 = bufferSource.getBuffer(renderType);
            poseStack.pushPose();
            poseStack.translate(f1, f1, f1);
            poseStack.scale(f, f, f);
            renderCube(blockPaintColor, light, overlay, poseStack, builder2);
            poseStack.popPose();
            renderType = SkinRenderType.BLOCK_CUBE_GLASS_UNSORTED;
        }
        var builder1 = bufferSource.getBuffer(renderType);
        renderCube(blockPaintColor, light, overlay, poseStack, builder1);
    }

    public void renderCube(BlockPaintColor blockPaintColor, int light, int overlay, IPoseStack poseStack, IVertexConsumer builder) {
        for (var dir : Direction.values()) {
            var paintColor = blockPaintColor.getOrDefault(dir, SkinPaintColor.WHITE);
            ExtendedFaceRenderer.render2(0, 0, 0, dir, paintColor, 255, light, overlay, poseStack, builder);
        }
    }
}
