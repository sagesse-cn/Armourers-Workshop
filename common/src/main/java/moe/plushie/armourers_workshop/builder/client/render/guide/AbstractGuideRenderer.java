package moe.plushie.armourers_workshop.builder.client.render.guide;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public abstract class AbstractGuideRenderer {

    public abstract void init(GuideRendererManager rendererManager);
}
