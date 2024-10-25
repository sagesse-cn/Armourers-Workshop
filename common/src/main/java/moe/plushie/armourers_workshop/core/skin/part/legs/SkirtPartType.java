package moe.plushie.armourers_workshop.core.skin.part.legs;

import moe.plushie.armourers_workshop.core.math.Rectangle3i;
import moe.plushie.armourers_workshop.core.math.Vector3i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;

public class SkirtPartType extends SkinPartType {

    public SkirtPartType() {
        super();
        this.buildingSpace = new Rectangle3i(-16, -16, -16, 32, 32, 32);
        this.guideSpace = new Rectangle3i(-4, -12, -2, 8, 12, 4);
        this.offset = new Vector3i(0, -1, 36);
        this.renderOffset = new Vector3i(0, 12, 0);
        this.renderPolygonOffset = 6;
    }
}
