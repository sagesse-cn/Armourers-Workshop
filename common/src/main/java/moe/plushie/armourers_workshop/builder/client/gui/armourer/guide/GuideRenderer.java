package moe.plushie.armourers_workshop.builder.client.gui.armourer.guide;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;

public interface GuideRenderer {

    void render(IPoseStack poseStack, GuideDataProvider provider, int light, int overlay, IBufferSource bufferSource);
}
