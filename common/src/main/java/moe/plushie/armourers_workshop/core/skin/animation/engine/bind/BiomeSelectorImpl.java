package moe.plushie.armourers_workshop.core.skin.animation.engine.bind;

import moe.plushie.armourers_workshop.compatibility.core.AbstractRegistryManager;
import moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector.BiomeSelector;

public class BiomeSelectorImpl implements BiomeSelector {

    protected AbstractRegistryManager.Biome biome;

    public BiomeSelectorImpl apply(AbstractRegistryManager.Biome biome) {
        this.biome = biome;
        return this;
    }

    @Override
    public boolean hasTag(String name) {
        return AbstractRegistryManager.hasBiomeTag(biome, name);
    }
}
