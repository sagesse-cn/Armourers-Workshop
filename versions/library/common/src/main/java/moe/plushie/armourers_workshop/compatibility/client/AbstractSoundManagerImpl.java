package moe.plushie.armourers_workshop.compatibility.client;

import net.minecraft.resources.ResourceLocation;

public interface AbstractSoundManagerImpl {

    void register(ResourceLocation relocation, AbstractSimpleSound sound);

    void unregister(ResourceLocation relocation);
}
