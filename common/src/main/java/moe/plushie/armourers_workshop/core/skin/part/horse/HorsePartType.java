package moe.plushie.armourers_workshop.core.skin.part.horse;

import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;

public class HorsePartType extends SkinPartType {

    public HorsePartType() {
        super();
        this.buildingSpace = new OpenRectangle3i(-32, -32, -32, 64, 64, 64);
        this.guideSpace = new OpenRectangle3i(-5, -8, -19, 10, 10, 24);
        this.offset = new OpenVector3i(0, 0, 0);
    }
}
