package moe.plushie.armourers_workshop.core.skin.part.bow;

import moe.plushie.armourers_workshop.api.skin.part.features.ICanHeld;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;

public class ArrowPartType extends SkinPartType implements ICanHeld {

    public ArrowPartType() {
        super();
        this.buildingSpace = new OpenRectangle3i(-8, -8, -6, 16, 16, 24);
        this.guideSpace = new OpenRectangle3i(0, 0, 0, 0, 0, 0);
        this.offset = new OpenVector3i(0, 0, 25);
    }
}
