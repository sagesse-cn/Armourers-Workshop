package moe.plushie.armourers_workshop.core.skin.part.feet;

import moe.plushie.armourers_workshop.api.skin.part.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.core.math.Rectangle3i;
import moe.plushie.armourers_workshop.core.math.Vector2i;
import moe.plushie.armourers_workshop.core.math.Vector3i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;

public class LeftFootPartType extends SkinPartType implements ISkinPartTypeTextured {

    public LeftFootPartType() {
        super();
        this.buildingSpace = new Rectangle3i(-18, -14, -16, 24, 10, 32);
        this.guideSpace = new Rectangle3i(-2, -12, -2, 4, 12, 4);
        this.offset = new Vector3i(9, 0, 0);
        this.renderOffset = new Vector3i(2, 12, 0);
        this.renderPolygonOffset = 4;
    }

    @Override
    public boolean isTextureMirrored() {
        return true;
    }

    @Override
    public Vector2i getTextureSkinPos() {
        return new Vector2i(0, 16);
    }

    @Override
    public Vector2i getTextureBasePos() {
        return new Vector2i(16, 48);
    }

    @Override
    public Vector2i getTextureOverlayPos() {
        return new Vector2i(0, 48);
    }

    @Override
    public Vector3i getTextureModelSize() {
        return new Vector3i(4, 12, 4);
    }
}
