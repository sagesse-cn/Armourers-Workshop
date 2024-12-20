package moe.plushie.armourers_workshop.builder.client.gui.armourer.guide;

import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class GuideRendererManager {

    private final HashMap<SkinPartType, GuideRenderer> renderers = new HashMap<>();

    public GuideRendererManager() {
        register(new HeadGuideRenderer());
        register(new ChestGuideRenderer());
        register(new FeetGuideRenderer());
        register(new HeldItemGuideRenderer());
        register(new WingsGuideRenderer());
    }

    private void register(AbstractGuideRenderer renderer) {
        renderer.init(this);
    }

    public void register(SkinPartType partType, GuideRenderer renderer) {
        renderers.put(partType, renderer);
    }

    public GuideRenderer getRenderer(SkinPartType partType) {
        return renderers.get(partType);
    }
}

