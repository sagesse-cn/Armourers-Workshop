package moe.plushie.armourers_workshop.core.client.texture;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.skin.geometry.ISkinGeometryType;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.other.SmartResourceManager;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryTypes;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureProperties;
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

    private final SkinTextureProperties properties;
    private final TextureAnimationController animationController;

    private final Map<IResourceLocation, ByteBuf> textureBuffers;

    private RenderType cubeRenderType;
    private RenderType meshRenderType;

    public SmartTexture(SkinTextureData provider) {
        this.location = ModConstants.key("textures/dynamic/" + OpenRandomSource.nextInt(SmartTexture.class) + ".png");
        this.properties = provider.getProperties();
        this.textureBuffers = resolveTextureBuffers(location, provider);
        this.animationController = new TextureAnimationController(provider.getAnimation());
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

    private Map<IResourceLocation, ByteBuf> resolveTextureBuffers(IResourceLocation location, SkinTextureData provider) {
        var path = FileUtils.removeExtension(location.getPath());
        var builder = new TextureBufferBuilder(provider.getProperties());
        builder.addData(location, provider);
        for (var variant : provider.getVariants()) {
            if (variant.getProperties().isNormal()) {
                builder.addData(location.withPath(path + "_n.png"), variant);
            }
            if (variant.getProperties().isSpecular()) {
                builder.addData(location.withPath(path + "_s.png"), variant);
            }
        }
        return builder.build();
    }

    private static class TextureBufferBuilder {

        private final Map<IResourceLocation, ByteBuf> buffers = new LinkedHashMap<IResourceLocation, ByteBuf>();

        private final SkinTextureProperties parentProperties;

        private TextureBufferBuilder(SkinTextureProperties parentProperties) {
            this.parentProperties = parentProperties;
        }

        public void addData(IResourceLocation location, SkinTextureData provider) {
            buffers.put(location, provider.getBuffer());
            addMeta(location, provider.getProperties());
        }

        private void addMeta(IResourceLocation location, SkinTextureProperties properties) {
            var isBlurFilter = properties.isBlurFilter() || parentProperties.isBlurFilter();
            var isClampToEdge = properties.isClampToEdge() || parentProperties.isClampToEdge();
            if (!isBlurFilter && !isClampToEdge) {
                return; // not needs.
            }
            // https://minecraft.wiki/w/Resource_pack#Properties
            var blur = String.valueOf(isBlurFilter);
            var clamp = String.valueOf(isClampToEdge);
            var meta = String.format("{\"texture\":{\"blur\":%s,\"clamp\":%s}}", blur, clamp);
            buffers.put(location.withPath(location.getPath() + ".mcmeta"), Unpooled.wrappedBuffer(meta.getBytes()));
        }

        public Map<IResourceLocation, ByteBuf> build() {
            return buffers;
        }
    }
}
