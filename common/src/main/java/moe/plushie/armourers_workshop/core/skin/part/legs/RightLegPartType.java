package moe.plushie.armourers_workshop.core.skin.part.legs;

import moe.plushie.armourers_workshop.api.skin.part.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.core.math.Rectangle3i;
import moe.plushie.armourers_workshop.core.math.Vector2i;
import moe.plushie.armourers_workshop.core.math.Vector3i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;

public class RightLegPartType extends SkinPartType implements ISkinPartTypeTextured {

    public RightLegPartType() {
        super();
        this.buildingSpace = new Rectangle3i(-6, -16, -16, 24, 32, 32);
        this.guideSpace = new Rectangle3i(-2, -12, -2, 4, 12, 4);
        this.offset = new Vector3i(-9, -1, 0);
        this.renderOffset = new Vector3i(-2, 12, 0);
        this.renderPolygonOffset = 2;
    }

    @Override
    public Vector2i getTextureSkinPos() {
        return new Vector2i(0, 16);
    }

    @Override
    public Vector3i getTextureModelSize() {
        return new Vector3i(4, 12, 4);
    }
}
