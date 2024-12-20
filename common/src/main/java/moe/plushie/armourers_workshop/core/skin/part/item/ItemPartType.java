package moe.plushie.armourers_workshop.core.skin.part.item;

import moe.plushie.armourers_workshop.api.skin.part.features.ICanHeld;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;

public class ItemPartType extends SkinPartType implements ICanHeld {

    public ItemPartType() {
        super();
        this.buildingSpace = new OpenRectangle3i(-32, -24, -32, 64, 88, 64);
        this.guideSpace = new OpenRectangle3i(-2, -2, 2, 4, 4, 8);
        //Offset -1 to match old skin system.
        this.offset = new OpenVector3i(0, -1, 0);
        this.renderOffset = OpenVector3i.ZERO;
        this.renderPolygonOffset = 10;
    }
}
