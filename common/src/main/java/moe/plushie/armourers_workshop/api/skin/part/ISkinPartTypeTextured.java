package moe.plushie.armourers_workshop.api.skin.part;

import moe.plushie.armourers_workshop.api.core.math.IVector2i;
import moe.plushie.armourers_workshop.api.core.math.IVector3i;

public interface ISkinPartTypeTextured extends ISkinPartType {

    /**
     * Should this texture be mirrored?
     */
    boolean isTextureMirrored();

    /**
     * Location of the texture in skin storage.
     */
    IVector2i getTextureSkinPos();

    /**
     * UV location of the models base texture.
     */
    IVector2i getTextureBasePos();

    /**
     * UV location of the models overlay texture.
     */
    IVector2i getTextureOverlayPos();

    /**
     * Size of the model the texture is used on.
     *
     * @return
     */
    IVector3i getTextureModelSize();

}
