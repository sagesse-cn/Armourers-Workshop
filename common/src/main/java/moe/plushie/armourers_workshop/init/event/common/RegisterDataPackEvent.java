package moe.plushie.armourers_workshop.init.event.common;

import net.minecraft.server.packs.resources.PreparableReloadListener;

public interface RegisterDataPackEvent {

    void register(PreparableReloadListener provider);
}
