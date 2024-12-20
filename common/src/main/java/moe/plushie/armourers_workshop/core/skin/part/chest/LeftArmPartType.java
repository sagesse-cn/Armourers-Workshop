package moe.plushie.armourers_workshop.core.skin.part.chest;

import moe.plushie.armourers_workshop.api.skin.part.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector2i;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;

public class LeftArmPartType extends SkinPartType implements ISkinPartTypeTextured {

    public LeftArmPartType() {
        super();
        this.buildingSpace = new OpenRectangle3i(-19, -28, -16, 24, 44, 32);
        this.guideSpace = new OpenRectangle3i(-3, -10, -2, 4, 12, 4);
        this.offset = new OpenVector3i(30, -1, 0);
        this.renderOffset = new OpenVector3i(5, 2, 0);
        this.renderPolygonOffset = 4;
    }

    @Override
    public OpenVector2i getTextureSkinPos() {
        return new OpenVector2i(40, 16);
    }

    @Override
    public OpenVector3i getTextureModelSize() {
        return new OpenVector3i(4, 12, 4);
    }
}
