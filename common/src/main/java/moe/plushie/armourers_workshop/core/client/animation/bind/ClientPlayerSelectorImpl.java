package moe.plushie.armourers_workshop.core.client.animation.bind;

import moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind.PlayerSelectorImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.LocalPlayer;

@Environment(EnvType.CLIENT)
public class ClientPlayerSelectorImpl<T extends LocalPlayer> extends PlayerSelectorImpl<T> {
}
