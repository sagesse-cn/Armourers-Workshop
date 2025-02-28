package moe.plushie.armourers_workshop.compatibility.fabric.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.platform.fabric.event.ClientPlayerLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.21, )")
@Mixin(ClientPacketListener.class)
public class FabricClientPlayerLifecycleMixin {

    private Player aw2$respawnOldPlayer;

    @Inject(method = "handleRespawn", at = @At("HEAD"))
    public void aw2$respawnPre(ClientboundRespawnPacket packet, CallbackInfo ci) {
        aw2$respawnOldPlayer = Minecraft.getInstance().player;
    }

    @Inject(method = "handleRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;addEntity(Lnet/minecraft/world/entity/Entity;)V"))
    public void aw2$respawnPost(ClientboundRespawnPacket packet, CallbackInfo ci) {
        var oldPlayer = aw2$respawnOldPlayer;
        var newPlayer = Minecraft.getInstance().player;
        ClientPlayerLifecycleEvents.CLONE.invoker().accept(oldPlayer, newPlayer);
        aw2$respawnOldPlayer = null;
    }
}
