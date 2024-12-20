package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.client.resources.model.BakedModel;

import moe.plushie.armourers_workshop.compatibility.client.AbstractItemDisplayContext;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.resources.model.BakedModel;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public class ABI {

    public static ItemTransform getTransform(@This BakedModel bakedModel, OpenItemDisplayContext transformType) {
        return bakedModel.getTransforms().getTransform(AbstractItemDisplayContext.unwrap(transformType));
    }
}
