package moe.plushie.armourers_workshop.compatibility.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.SimpleTexture;

@Available("[1.16, )")
@Environment(EnvType.CLIENT)
public class AbstractSimpleTexture {

    public static SimpleTexture create(IResourceLocation location) {
        // NOTE: do not modify to subclass, because we rely on iris's simple pbr loader behavior.
        // https://github.com/IrisShaders/Iris/blob/d2038d159054b07aedd93f173de3ce52130fea82/common/src/main/java/net/irisshaders/iris/pbr/texture/PBRTextureManager.java#L111
        return new SimpleTexture(location.toLocation());
    }
}
