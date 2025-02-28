package moe.plushie.armourers_workshop.compatibility.fabric.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderMode;
import moe.plushie.armourers_workshop.init.platform.fabric.event.ClientScreenRenderEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.21, )")
@Mixin(GameRenderer.class)
public class FabricClientScreenRenderMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", shift = At.Shift.BEFORE))
    private void aw2$renderPre(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci) {
        SkinRenderMode.push(SkinRenderMode.GUI);
        ClientScreenRenderEvents.START.invoker().onStart(Minecraft.getInstance());
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", shift = At.Shift.AFTER))
    private void aw2$renderPost(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci) {
        ClientScreenRenderEvents.END.invoker().onEnd(Minecraft.getInstance());
        SkinRenderMode.pop();
    }
}
