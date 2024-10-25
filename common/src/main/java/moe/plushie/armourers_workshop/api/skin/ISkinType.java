package moe.plushie.armourers_workshop.api.skin;

import moe.plushie.armourers_workshop.api.core.IRegistryEntry;
import moe.plushie.armourers_workshop.api.skin.part.ISkinPartType;

import java.util.List;

public interface ISkinType extends IRegistryEntry {

    int getId();

    String getName();

    List<? extends ISkinPartType> getParts();
}
