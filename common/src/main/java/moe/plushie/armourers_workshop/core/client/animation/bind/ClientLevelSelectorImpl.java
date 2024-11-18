package moe.plushie.armourers_workshop.core.client.animation.bind;

import moe.plushie.armourers_workshop.core.skin.animation.engine.bind.LevelSelectorImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;

@Environment(EnvType.CLIENT)
public class ClientLevelSelectorImpl<T extends ClientLevel> extends LevelSelectorImpl<T> {
}
