package moe.plushie.armourers_workshop.core.utils;

import com.mojang.blaze3d.platform.NativeImage;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.core.client.texture.BakedEntityTexture;
import moe.plushie.armourers_workshop.core.client.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.core.math.OpenVector2i;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;

@Environment(EnvType.CLIENT)
public final class TextureUtils {

    public static IResourceLocation registerTexture(String key, DynamicTexture texture) {
        var location = Minecraft.getInstance().getTextureManager().register(key, texture);
        return OpenResourceLocation.create(location);
    }

    public static OpenResourceLocation getTexture(Entity entity) {
        if (entity instanceof AbstractClientPlayer player) {
            var location = player.getSkin().texture();
            return OpenResourceLocation.create(location);
        }
        return ModTextures.MANNEQUIN_DEFAULT;
    }

    public static OpenResourceLocation getPlayerTextureLocation(EntityTextureDescriptor descriptor) {
        var bakedTexture = PlayerTextureLoader.getInstance().loadTexture(descriptor);
        if (bakedTexture != null && bakedTexture.isDownloaded()) {
            return bakedTexture.getLocation();
        }
//        ClientPlayer player = Minecraft.getInstance().player;
//        if (player != null) {
//            return player.getSkinTextureLocation();
//        }
        return ModTextures.MANNEQUIN_DEFAULT;
    }

    @Nullable
    public static BakedEntityTexture getPlayerTextureModel(EntityTextureDescriptor descriptor) {
        var texture = getPlayerTextureLocation(descriptor);
        if (texture != null) {
            return PlayerTextureLoader.getInstance().getTextureModel(texture);
        }
        return null;
    }

    public static SkinPaintColor getPlayerTextureModelColor(EntityTextureDescriptor descriptor, OpenVector2i texturePos) {
        var textureModel = getPlayerTextureModel(descriptor);
        if (textureModel != null) {
            return textureModel.getColor(texturePos);
        }
        return null;
    }

    public static NativeImage readTextureImage(ByteBuffer buffer) {
        try {
            return NativeImage.read(buffer.asReadOnlyBuffer());
        } catch (IOException e) {
            return new NativeImage(NativeImage.Format.RGBA, 128, 128, false);
        }
    }
}
