package moe.plushie.armourers_workshop.builder.client.gui.armourer.guide;

import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;

public interface GuideDataProvider {

    boolean shouldRenderOverlay(SkinProperty<Boolean> property);
}
