package moe.plushie.armourers_workshop.core.skin.particle;


import moe.plushie.armourers_workshop.api.skin.particle.ISkinParticleProvider;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;

import java.util.List;

public class SkinParticleData implements ISkinParticleProvider {

    private final String name;
    private final SkinParticleMaterial material;
    private final SkinTextureData texture;

    private final List<? extends SkinParticleComponent> components;

    public SkinParticleData(String name, SkinParticleMaterial material, SkinTextureData texture, List<? extends SkinParticleComponent> components) {
        this.name = name;
        this.material = material;
        this.texture = texture;
        this.components = components;
    }

    public String getName() {
        return name;
    }

    public SkinParticleMaterial getMaterial() {
        return material;
    }

    public SkinTextureData getTexture() {
        return texture;
    }

    public List<? extends SkinParticleComponent> getComponents() {
        return components;
    }
}
