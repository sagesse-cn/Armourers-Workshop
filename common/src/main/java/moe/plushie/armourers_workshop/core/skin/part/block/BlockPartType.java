package moe.plushie.armourers_workshop.core.skin.part.block;

import moe.plushie.armourers_workshop.core.math.Rectangle3i;
import moe.plushie.armourers_workshop.core.math.Vector3i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;

public class BlockPartType extends SkinPartType {

    public BlockPartType() {
        super();
        this.buildingSpace = new Rectangle3i(-8, -8, -8, 16, 16, 16);
        this.guideSpace = new Rectangle3i(0, 0, 0, 0, 0, 0);
        this.offset = new Vector3i(0, -1, 0);
    }

    @Override
    public int getMaximumMarkersNeeded() {
        return 1;
    }

    @Override
    public int getMinimumMarkersNeeded() {
        return 0;
    }
}
