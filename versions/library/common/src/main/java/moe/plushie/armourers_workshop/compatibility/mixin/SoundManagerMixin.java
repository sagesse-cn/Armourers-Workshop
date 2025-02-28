package moe.plushie.armourers_workshop.compatibility.mixin;

import com.google.common.collect.Maps;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.client.AbstractSimpleSound;
import moe.plushie.armourers_workshop.compatibility.client.AbstractSoundManagerImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;

@Available("[1.20, )")
@Mixin(SoundManager.class)
public class SoundManagerMixin implements AbstractSoundManagerImpl {

    @Unique
    private final Map<ResourceLocation, WeighedSoundEvents> aw2$registry = Maps.newHashMap();

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/ResourceProvider;fromMap(Ljava/util/Map;)Lnet/minecraft/server/packs/resources/ResourceProvider;"))
    private ResourceProvider aw2$init(Map<ResourceLocation, Resource> map) {
        return location -> {
            var resource = map.get(location);
            if (resource != null) {
                return Optional.of(resource);
            }
            return Minecraft.getInstance().getResourceManager().getResource(location);
        };
    }

    @Inject(method = "getSoundEvent", at = @At("HEAD"), cancellable = true)
    private void aw2$getSoundEvent(ResourceLocation location, CallbackInfoReturnable<WeighedSoundEvents> cir) {
        var event = aw2$registry.get(location);
        if (event != null) {
            cir.setReturnValue(event);
        }
    }

    @Override
    public void aw2$register(ResourceLocation location, AbstractSimpleSound sound) {
        var event = new WeighedSoundEvents(location, sound.getName());
        event.addSound(Sound.create(sound));
        aw2$registry.put(location, event);
    }

    @Override
    public void aw2$unregister(ResourceLocation location) {
        aw2$registry.remove(location);
    }
}
