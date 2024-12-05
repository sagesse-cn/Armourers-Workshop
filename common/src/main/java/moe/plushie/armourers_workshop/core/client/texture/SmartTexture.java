package moe.plushie.armourers_workshop.core.client.texture;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.skin.geometry.ISkinGeometryType;
import moe.plushie.armourers_workshop.api.skin.texture.ITextureProperties;
import moe.plushie.armourers_workshop.api.skin.texture.ITextureProvider;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.other.SmartResourceManager;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryTypes;
import moe.plushie.armourers_workshop.core.skin.texture.TextureAnimation;
import moe.plushie.armourers_workshop.core.utils.FileUtils;
import moe.plushie.armourers_workshop.core.utils.OpenRandomSource;
import moe.plushie.armourers_workshop.core.utils.ReferenceCounted;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.DataContainer;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class SmartTexture extends ReferenceCounted {

    private final IResourceLocation location;
    private final ITextureProperties properties;

    private final TextureAnimationController animationController;

    private final Map<IResourceLocation, ByteBuf> textureBuffers;

    private RenderType cubeRenderType;
    private RenderType meshRenderType;

    public SmartTexture(ITextureProvider provider) {
        this.location = ModConstants.key("textures/dynamic/" + OpenRandomSource.nextInt(SmartTexture.class) + ".png");
        this.properties = provider.getProperties();
        this.textureBuffers = resolveTextureBuffers(location, provider);
        this.animationController = new TextureAnimationController((TextureAnimation) provider.getAnimation());
    }

    @Nullable
    public static SmartTexture of(RenderType renderType) {
        return DataContainer.get(renderType, null);
    }

    @Override
    protected void init() {
        RenderSystem.safeCall(() -> {
            textureBuffers.forEach(SmartResourceManager.getInstance()::register);
            SmartTextureManager.getInstance().uploadTexture(this);
        });
    }

    @Override
    protected void dispose() {
        RenderSystem.safeCall(() -> {
            SmartTextureManager.getInstance().releaseTexture(this);
            textureBuffers.keySet().forEach(SmartResourceManager.getInstance()::unregister);
        });
    }

    public RenderType getRenderType(ISkinGeometryType type) {
        if (type == SkinGeometryTypes.MESH) {
            if (meshRenderType == null) {
                meshRenderType = SkinRenderType.meshFace(location, properties.isEmissive());
                DataContainer.set(meshRenderType, this);
            }
            return meshRenderType;
        } else {
            if (cubeRenderType == null) {
                cubeRenderType = SkinRenderType.cubeFace(location, properties.isEmissive());
                DataContainer.set(cubeRenderType, this);
            }
            return cubeRenderType;
        }
    }

    public IResourceLocation getLocation() {
        return location;
    }

    public TextureAnimationController getAnimationController() {
        return animationController;
    }

    @Override
    public String toString() {
        return location.toString();
    }

    protected void unbind() {
        if (cubeRenderType != null) {
            DataContainer.set(cubeRenderType, null);
        }
        if (meshRenderType != null) {
            DataContainer.set(meshRenderType, null);
        }
        // when unbind the object, we must ensure that all resources release.
        while (refCnt() > 0) {
            release();
        }
    }

    private Map<IResourceLocation, ByteBuf> resolveTextureBuffers(IResourceLocation location, ITextureProvider provider) {
        var path = FileUtils.removeExtension(location.getPath());
        var results = new LinkedHashMap<IResourceLocation, ByteBuf>();
        results.put(location, provider.getBuffer());
        for (var variant : provider.getVariants()) {
            if (variant.getProperties().isNormal()) {
                results.put(ModConstants.key(path + "_n.png"), variant.getBuffer());
            }
            if (variant.getProperties().isSpecular()) {
                results.put(ModConstants.key(path + "_s.png"), variant.getBuffer());
            }
        }
        return results;
    }
}
