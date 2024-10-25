package moe.plushie.armourers_workshop.core.skin.paint;

import com.google.common.collect.Iterables;
import moe.plushie.armourers_workshop.api.skin.paint.ISkinPaintColor;
import moe.plushie.armourers_workshop.api.skin.paint.ISkinPaintType;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.utils.Constants;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("unused")
public class SkinPaintScheme {

    public final static SkinPaintScheme EMPTY = new SkinPaintScheme();

    private final HashMap<ISkinPaintType, ISkinPaintColor> colors = new HashMap<>();
    private HashMap<ISkinPaintType, ISkinPaintColor> resolvedColors;

    private SkinPaintScheme reference;
    private OpenResourceLocation texture;

    private int hashCode;

    public SkinPaintScheme() {
    }

    public SkinPaintScheme(CompoundTag nbt) {
        for (var key : nbt.getAllKeys()) {
            var paintType = SkinPaintTypes.byName(key);
            if (paintType != SkinPaintTypes.NONE && nbt.contains(key, Constants.TagFlags.INT)) {
                colors.put(paintType, SkinPaintColor.of(nbt.getInt(key)));
            }
        }
    }

    public CompoundTag serializeNBT() {
        var nbt = new CompoundTag();
        colors.forEach((paintType, paintColor) -> nbt.putInt(paintType.getRegistryName().toString(), paintColor.getRawValue()));
        return nbt;
    }

    public SkinPaintScheme copy() {
        var scheme = new SkinPaintScheme();
        scheme.colors.putAll(colors);
        scheme.reference = reference;
        scheme.texture = texture;
        return scheme;
    }

    public boolean isEmpty() {
        if (this == EMPTY) {
            return true;
        }
        if (reference != null && !reference.isEmpty()) {
            return false;
        }
        if (texture != null) {
            return false;
        }
        return colors.isEmpty();
    }

    @Nullable
    public ISkinPaintColor getColor(ISkinPaintType paintType) {
        var color = colors.get(paintType);
        if (color != null) {
            return color;
        }
        if (reference != null) {
            return reference.getColor(paintType);
        }
        return null;
    }

    public void setColor(ISkinPaintType paintType, ISkinPaintColor color) {
        colors.put(paintType, color);
        resolvedColors = null;
        hashCode = 0;
    }

    public ISkinPaintColor getResolvedColor(ISkinPaintType paintType) {
        if (resolvedColors == null) {
            resolvedColors = getResolvedColors();
        }
        return resolvedColors.get(paintType);
    }

    public OpenResourceLocation getTexture() {
        return texture;
    }

    public void setTexture(OpenResourceLocation texture) {
        this.texture = texture;
    }

    public SkinPaintScheme getReference() {
        if (reference != null) {
            return reference;
        }
        return SkinPaintScheme.EMPTY;
    }

    public void setReference(SkinPaintScheme reference) {
        // referring empty scheme not have any effect.
        if (reference != null && reference.isEmpty()) {
            reference = null;
        }
        if (!Objects.equals(this.reference, reference)) {
            this.reference = reference;
            this.resolvedColors = null;
            this.hashCode = 0;
        }
    }

    private HashMap<ISkinPaintType, ISkinPaintColor> getResolvedColors() {
        var resolvedColors = new HashMap<ISkinPaintType, ISkinPaintColor>();
        var dependencies = new HashMap<ISkinPaintType, ArrayList<ISkinPaintType>>();
        // build all reference dependencies
        if (reference != null) {
            resolvedColors.putAll(reference.getResolvedColors());
        }
        // build all item dependencies
        Iterables.concat(colors.entrySet(), getReference().colors.entrySet()).forEach(e -> {
            var paintType = e.getKey();
            var color = e.getValue();
            if (color.getPaintType().getDyeType() != null) {
                dependencies.computeIfAbsent(color.getPaintType(), k -> new ArrayList<>()).add(paintType);
            } else {
                resolvedColors.put(paintType, color);
            }
        });
        if (resolvedColors.isEmpty()) {
            return resolvedColors;
        }
        // merge all items whens dependencies
        dependencies.forEach((key, value) -> Iterables.tryFind(dependencies.values(), v -> v.contains(key)).toJavaUtil().ifPresent(target -> {
            if (target != value) {
                target.addAll(value);
            }
            value.clear(); // clear to prevent infinite loop occurs
        }));
        dependencies.forEach((key, value) -> value.forEach(paintType -> resolvedColors.put(paintType, resolvedColors.get(key))));
        return resolvedColors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinPaintScheme that)) return false;
        return colors.equals(that.colors) && Objects.equals(texture, that.texture) && Objects.equals(reference, that.reference);
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = Objects.hash(colors, texture, reference);
            if (hashCode == 0) {
                hashCode = ~hashCode;
            }
        }
        return hashCode;
    }

    @Override
    public String toString() {
        return "[" + getResolvedColors() + "]";
    }
}
