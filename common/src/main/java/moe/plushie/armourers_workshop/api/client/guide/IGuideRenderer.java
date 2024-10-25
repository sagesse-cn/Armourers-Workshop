package moe.plushie.armourers_workshop.api.client.guide;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;

public interface IGuideRenderer {

    void render(IPoseStack poseStack, IGuideDataProvider provider, int light, int overlay, IBufferSource bufferSource);
}
