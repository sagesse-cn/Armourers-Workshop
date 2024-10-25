package moe.plushie.armourers_workshop.core.data.color;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import moe.plushie.armourers_workshop.api.skin.paint.ISkinPaintType;
import moe.plushie.armourers_workshop.core.skin.paint.SkinPaintColor;

public class TexturedPaintColor extends SkinPaintColor {

    // we need an object pool to reduce color object
    private final static Cache<Integer, TexturedPaintColor> POOL = CacheBuilder.newBuilder()
            .maximumSize(2048)
            .build();

    protected TexturedPaintColor(int value, int rgb, ISkinPaintType paintType) {
        super(value, rgb, paintType);
    }

    public static SkinPaintColor of(int value) {
        if (value == 0) {
            return CLEAR;
        }
        return of(value, getPaintType(value));
    }

    public static SkinPaintColor of(int rgb, ISkinPaintType paintType) {
        var value = (rgb & 0xffffff) | ((paintType.getId() & 0xff) << 24);
        var paintColor = POOL.getIfPresent(value);
        if (paintColor == null) {
            paintColor = new TexturedPaintColor(value, rgb, paintType);
            POOL.put(value, paintColor);
        }
        return paintColor;
    }
}
