package moe.plushie.armourers_workshop.core.skin.part.block;

import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;

public class MultiBlockPartType extends BlockPartType {

    public MultiBlockPartType() {
        super();
        this.buildingSpace = new OpenRectangle3i(-24, -8, -8, 48, 48, 48);
        this.guideSpace = new OpenRectangle3i(0, 0, 0, 0, 0, 0);
        this.offset = new OpenVector3i(0, -1, 0);
    }
}
