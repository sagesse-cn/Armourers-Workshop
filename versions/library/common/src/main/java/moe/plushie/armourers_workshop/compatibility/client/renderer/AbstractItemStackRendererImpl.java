package moe.plushie.armourers_workshop.compatibility.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.client.AbstractItemDisplayContext;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@Available("[1.20, )")
@Environment(EnvType.CLIENT)
public abstract class AbstractItemStackRendererImpl extends BlockEntityWithoutLevelRenderer {

    public AbstractItemStackRendererImpl() {
        this(Minecraft.getInstance());
    }

    public AbstractItemStackRendererImpl(Minecraft minecraft) {
        super(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
    }

    public void renderByItem(ItemStack itemStack, OpenItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource renderTypeBuffer, int light, int overlay) {
        super.renderByItem(itemStack, AbstractItemDisplayContext.unwrap(transformType), poseStack, renderTypeBuffer, light, overlay);
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource renderTypeBuffer, int light, int overlay) {
        this.renderByItem(itemStack, AbstractItemDisplayContext.wrap(transformType), poseStack, renderTypeBuffer, light, overlay);
    }
}
