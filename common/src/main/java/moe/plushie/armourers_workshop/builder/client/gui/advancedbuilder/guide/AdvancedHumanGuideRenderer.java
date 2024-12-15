package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide;

import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AdvancedHumanGuideRenderer extends AdvancedEntityGuideRenderer {

    @Override
    public BakedArmature getArmature() {
        return BakedArmature.defaultBy(Armatures.HUMANOID);
    }

    @Override
    public SkinTextureData getTexture() {
        return new SkinTextureData(ModTextures.MANNEQUIN_DEFAULT.toString(), 64, 64);
    }
}
