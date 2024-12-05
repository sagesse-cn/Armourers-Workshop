package moe.plushie.armourers_workshop.compatibility.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.resources.ResourceLocation;

@Available("[1.16, )")
public class AbstractSimpleSound {

    private final ResourceLocation id;
    private final String name;

    private final float volume = 1.0f;
    private final float pitch = 1.0f;
    private final int weight = 1; // must > 0
    private final int attenuationDistance = 16;

    public AbstractSimpleSound(ResourceLocation id, String name) {
        this.id = id;
        this.name = name;
    }

    public ResourceLocation getId() {
        return id;
    }

    public String getName() {
        if (name != null && !name.isEmpty()) {
            return name;
        }
        return null;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public int getWeight() {
        return weight;
    }

    public int getAttenuationDistance() {
        return attenuationDistance;
    }
}
