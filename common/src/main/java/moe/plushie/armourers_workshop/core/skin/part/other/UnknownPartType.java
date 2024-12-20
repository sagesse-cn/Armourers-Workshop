package moe.plushie.armourers_workshop.core.skin.part.other;

import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;

public class UnknownPartType extends SkinPartType {

    public UnknownPartType() {
        super();
        this.buildingSpace = new OpenRectangle3i(-32, -32, -32, 64, 64, 64);
        this.guideSpace = new OpenRectangle3i(0, 0, 0, 0, 0, 0);
        this.offset = new OpenVector3i(0, 0, 0);
    }
}
