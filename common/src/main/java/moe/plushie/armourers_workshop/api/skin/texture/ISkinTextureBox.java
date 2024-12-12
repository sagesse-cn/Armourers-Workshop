package moe.plushie.armourers_workshop.api.skin.texture;

import moe.plushie.armourers_workshop.api.core.utils.IDirection;
import org.jetbrains.annotations.Nullable;

public interface ISkinTextureBox {

    @Nullable
    ISkinTexturePos getTexture(IDirection dir);
}
