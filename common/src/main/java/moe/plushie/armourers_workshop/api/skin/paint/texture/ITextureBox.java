package moe.plushie.armourers_workshop.api.skin.paint.texture;

import moe.plushie.armourers_workshop.api.core.utils.IDirection;
import org.jetbrains.annotations.Nullable;

public interface ITextureBox {

    @Nullable
    ITextureKey getTexture(IDirection dir);
}
