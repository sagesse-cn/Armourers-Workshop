package moe.plushie.armourers_workshop.api.registry;

import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import net.minecraft.sounds.SoundEvent;

@SuppressWarnings("unused")
public interface ISoundEventBuilder<T extends SoundEvent> extends IEntryBuilder<IRegistryHolder<T>> {
}
