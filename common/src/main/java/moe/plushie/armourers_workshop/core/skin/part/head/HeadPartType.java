package moe.plushie.armourers_workshop.core.skin.part.head;

import moe.plushie.armourers_workshop.api.core.math.ITexturePos;
import moe.plushie.armourers_workshop.api.core.math.IVector3i;
import moe.plushie.armourers_workshop.api.skin.part.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.core.math.Rectangle3i;
import moe.plushie.armourers_workshop.core.math.TexturePos;
import moe.plushie.armourers_workshop.core.math.Vector3i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;

public class HeadPartType extends SkinPartType implements ISkinPartTypeTextured {

    public HeadPartType() {
        super();
        this.buildingSpace = new Rectangle3i(-32, -24, -32, 64, 56, 64);
        this.guideSpace = new Rectangle3i(-4, 0, -4, 8, 8, 8);
        this.offset = new Vector3i(0, 0, 0);
        this.renderOffset = Vector3i.ZERO;
        this.renderPolygonOffset = 6;
    }

    @Override
    public boolean isTextureMirrored() {
        return false;
    }

    @Override
    public ITexturePos getTextureSkinPos() {
        return TexturePos.ZERO;
    }

    @Override
    public ITexturePos getTextureBasePos() {
        return TexturePos.ZERO;
    }

    @Override
    public ITexturePos getTextureOverlayPos() {
        return new TexturePos(32, 0);
    }

    @Override
    public IVector3i getTextureModelSize() {
        return new Vector3i(8, 8, 8);
    }
}
