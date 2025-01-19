package moe.plushie.armourers_workshop.compatibility.forge.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderMode;
import net.neoforged.neoforge.client.ClientHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.21, )")
@Mixin(ClientHooks.class)
public class ForgeClientScreenRenderMixin {

    @Inject(method = "drawScreen", at = @At(value = "HEAD"), remap = false)
    private static void aw2$drawScreenPre(CallbackInfo ci) {
        SkinRenderMode.push(SkinRenderMode.GUI);
    }

    @Inject(method = "drawScreen", at = @At(value = "RETURN"), remap = false)
    private static void aw2$drawScreenPost(CallbackInfo ci) {
        SkinRenderMode.pop();
    }
}
