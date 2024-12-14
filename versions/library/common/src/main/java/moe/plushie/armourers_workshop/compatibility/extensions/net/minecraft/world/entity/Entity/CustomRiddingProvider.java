package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.world.entity.Entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentTypes;
import moe.plushie.armourers_workshop.utils.DataContainer;
import moe.plushie.armourers_workshop.utils.DataContainerKey;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.16, )")
@Extension
public class CustomRiddingProvider {

    private static final DataContainerKey<IndexMap> KEY = DataContainerKey.of("Passengers", IndexMap.class);

    @Nullable
    public static Vector3f getCustomRidding(@This Entity entity, int index) {
        var container = DataContainer.getValue(entity, KEY);
        if (container == null) {
            return null; // not provided.
        }
        var renderData = EntityRenderData.of(entity);
        if (renderData != null) {
            var attachmentPos = renderData.getAttachmentPose(SkinAttachmentTypes.RIDING, index);
            if (attachmentPos == null) {
                return null; // not provided.
            }
        }
        var value = container.get(index);
        if (value != null) {
            return value;
        }
        return container.get(-1); // get from fallback.
    }

    public static void setCustomRidding(@This Entity entity, int index, @Nullable Vector3f position) {
        var container = DataContainer.getValue(entity, KEY);
        if (container == null) {
            container = new IndexMap();
            DataContainer.setValue(entity, KEY, container);
        }
        container.put(index, position);
    }

    private static class IndexMap extends Int2ObjectOpenHashMap<Vector3f> {

    }
}
