package moe.plushie.armourers_workshop.compatibility.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.SimpleTexture;

@Available("[1.16, )")
@Environment(EnvType.CLIENT)
public class AbstractSimpleTexture extends SimpleTexture {

    public AbstractSimpleTexture(IResourceLocation location) {
        super(location.toLocation());
    }
}
