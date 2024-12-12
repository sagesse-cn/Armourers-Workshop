package moe.plushie.armourers_workshop.core.skin.part.head;

import moe.plushie.armourers_workshop.api.skin.part.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.core.math.Rectangle3i;
import moe.plushie.armourers_workshop.core.math.Vector2i;
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
    public Vector2i getTextureSkinPos() {
        return Vector2i.ZERO;
    }

    @Override
    public Vector3i getTextureModelSize() {
        return new Vector3i(8, 8, 8);
    }
}
