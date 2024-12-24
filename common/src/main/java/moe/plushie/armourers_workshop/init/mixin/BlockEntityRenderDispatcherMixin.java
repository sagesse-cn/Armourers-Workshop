package moe.plushie.armourers_workshop.init.mixin;

import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractBlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherMixin {

    @Inject(method = "getRenderer", at = @At("RETURN"), cancellable = true)
    public void aw2$getRenderer(BlockEntity blockEntity, CallbackInfoReturnable<@Nullable BlockEntityRenderer<BlockEntity>> cir) {
        if (cir.getReturnValue() instanceof AbstractBlockEntityRenderer<BlockEntity> renderer && !renderer.shouldRender(blockEntity)) {
            cir.setReturnValue(null);
        }
    }
}
