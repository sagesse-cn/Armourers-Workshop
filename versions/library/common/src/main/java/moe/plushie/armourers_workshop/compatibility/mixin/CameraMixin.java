package moe.plushie.armourers_workshop.compatibility.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.builder.entity.CameraEntity;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.21, )")
@Mixin(Camera.class)
public class CameraMixin {

    @Inject(method = "setup", at = @At("HEAD"))
    private void aw2$setup(BlockGetter blockGetter, Entity entity, boolean bl, boolean bl2, float f, CallbackInfo ci) {
        var vehicle = entity.getVehicle();
        if (vehicle != null) {
            ClientWardrobeHandler.onRenderRiderAttachment(vehicle, entity, f);
        }
    }

    @ModifyVariable(method = "getMaxZoom", at = @At("HEAD"), argsOnly = true)
    private float aw2$getMaxZoom(float zoom) {
        var camera = Camera.class.cast(this);
        if (camera.getEntity() instanceof CameraEntity cameraEntity) {
            return cameraEntity.getMaxZoom(zoom);
        }
        return zoom;
    }
}
