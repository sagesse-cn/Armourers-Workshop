package moe.plushie.armourers_workshop.core.utils;

import net.minecraft.util.ResourceLocation;

// The path of ResourceLocation does not support uppercase, but there are uppercase in the old data
public class SkinResourceLocation extends ResourceLocation {

    private final String realPath;

    public SkinResourceLocation(String namespace, String path) {
        super(namespace, path.toLowerCase());
        this.realPath = path;
    }

    @Override
    public String getPath() {
        return realPath;
    }

    @Override
    public String toString() {
        return this.namespace + ':' + this.realPath;
    }
}