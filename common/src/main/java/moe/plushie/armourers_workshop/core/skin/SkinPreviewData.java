package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.api.core.math.ITransform;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometrySet;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTransform;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SkinPreviewData {

    private final Collection<Pair<ITransform, SkinGeometrySet<?>>> allGeometries;

    public SkinPreviewData(Collection<Pair<ITransform, SkinGeometrySet<?>>> allGeometries) {
        this.allGeometries = allGeometries;
    }

    public static SkinPreviewData of(Skin skin) {
        // we can't re-generate the preview data for a preview skin.
        if (skin.getPreviewData() != null) {
            return skin.getPreviewData();
        }
        var allCubes = new ArrayList<Pair<ITransform, SkinGeometrySet<?>>>();
        eachPart(skin.getParts(), part -> {
            // apply the origin offset.
            var pos = part.getType().getRenderOffset();
            var offset = OpenTransform3f.createTranslateTransform(pos.x(), pos.y(), pos.z());
            // apply the marker rotation and offset.
            var transform = new SkinPartTransform(part, offset);
            allCubes.add(Pair.of(transform, part.getGeometries()));
        });
        return new SkinPreviewData(allCubes);
    }

    private static void eachPart(Collection<SkinPart> parts, Consumer<SkinPart> consumer) {
        for (var part : parts) {
            consumer.accept(part);
            eachPart(part.getChildren(), consumer);
        }
    }

    public void forEach(BiConsumer<ITransform, SkinGeometrySet<?>> consumer) {
        allGeometries.forEach(pair -> consumer.accept(pair.getKey(), pair.getValue()));
    }
}
