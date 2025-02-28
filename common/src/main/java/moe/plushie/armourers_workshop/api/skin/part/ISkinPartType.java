package moe.plushie.armourers_workshop.api.skin.part;

import moe.plushie.armourers_workshop.api.core.IRegistryEntry;
import moe.plushie.armourers_workshop.api.core.math.IRectangle3i;
import moe.plushie.armourers_workshop.api.core.math.IVector3i;

public interface ISkinPartType extends IRegistryEntry {

    String getName();

    /**
     * The last 3 values are used to define the size of this part, the first 3
     * values will change the origin. Example -5, -5, -5, 10, 10, 10, Will create a
     * 10x10x10 cube with it's origin in the centre.
     *
     * @return
     */
    IRectangle3i getBuildingSpace();

    /**
     * The last 3 values set the size of the invisible blocks that cubes can be
     * placed on, the first 3 set the offset. Use 0, 0, 0, 0, 0, 0, if you don't
     * want to use this. Setting showArmourerDebugRender to true in the config will
     * show this box.
     *
     * @return
     */
    IRectangle3i getGuideSpace();

    /**
     * This is used by the armourer to position this part
     */
    IVector3i getOffset();

    IVector3i getRenderOffset();

    IRectangle3i getBounds();

    default float getRenderPolygonOffset() {
        return 0;
    }

    /**
     * Get the minimum number of markers needed for this skin part.
     *
     * @return
     */
    int getMinimumMarkersNeeded();

    /**
     * Gets the maximum number of markers allowed for this skin part.
     *
     * @return
     */
    int getMaximumMarkersNeeded();

    /**
     * If true this part must be present for the skin to be saved.
     *
     * @return
     */
    boolean isPartRequired();
}
