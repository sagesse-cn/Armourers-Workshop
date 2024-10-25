package moe.plushie.armourers_workshop.core.skin.part.other;

import moe.plushie.armourers_workshop.api.skin.part.ISkinPartType;
import moe.plushie.armourers_workshop.core.math.Rectangle3i;
import moe.plushie.armourers_workshop.core.math.Vector3i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;

public class PartitionPartType extends SkinPartType {

    public final ISkinPartType parentPartType;

    public PartitionPartType(ISkinPartType parentPartType) {
        super();
        this.buildingSpace = new Rectangle3i(-32, -32, -32, 64, 64, 64);
        this.guideSpace = Rectangle3i.ZERO;
        this.offset = Vector3i.ZERO;
        this.renderOffset = new Vector3i(parentPartType.getRenderOffset());
        this.renderPolygonOffset = parentPartType.getRenderPolygonOffset();
        this.parentPartType = parentPartType;
    }
}
