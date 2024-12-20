package moe.plushie.armourers_workshop.core.data.color;

import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintType;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;

import java.util.HashMap;
import java.util.Set;

public class ColorDescriptor {

    private final HashMap<SkinPaintType, Channel> channels = new HashMap<>();

    public void add(SkinPaintColor color) {
        var paintType = color.getPaintType();
        if (shouldRecordChannel(paintType)) {
            var ch = channels.computeIfAbsent(paintType, k -> new Channel());
            ch.red += color.getRed();
            ch.green += color.getGreen();
            ch.blue += color.getBlue();
            ch.total += 1;
            ch.setChanged();
        }
    }

    public void add(ColorDescriptor descriptor) {
        descriptor.channels.forEach((paintType, otherChannel) -> {
            var ch = channels.computeIfAbsent(paintType, k -> new Channel());
            ch.red += otherChannel.red;
            ch.green += otherChannel.green;
            ch.blue += otherChannel.blue;
            ch.total += otherChannel.total;
            ch.setChanged();
        });
    }

    public boolean isEmpty() {
        return channels.isEmpty();
    }

    public SkinPaintColor getAverageColor(SkinPaintType paintType) {
        var channel = channels.get(paintType);
        if (channel != null) {
            return channel.getResolvedColor();
        }
        return null;
    }

    public Set<SkinPaintType> getPaintTypes() {
        return channels.keySet();
    }

    public ColorDescriptor copy() {
        var result = new ColorDescriptor();
        result.add(this);
        return result;
    }

    private boolean shouldRecordChannel(SkinPaintType paintType) {
        if (paintType == SkinPaintTypes.RAINBOW) {
            return true;
        }
        if (paintType == SkinPaintTypes.TEXTURE) {
            return true;
        }
        return paintType.getDyeType() != null;
    }

    private static class Channel {
        int total = 0;
        int red = 0;
        int green = 0;
        int blue = 0;
        SkinPaintColor resolvedColor;

        void setChanged() {
            resolvedColor = null;
        }

        SkinPaintColor getResolvedColor() {
            if (resolvedColor != null) {
                return resolvedColor;
            }
            if (total == 0) {
                resolvedColor = SkinPaintColor.CLEAR;
                return resolvedColor;
            }
            int r = red / total;
            int g = green / total;
            int b = blue / total;
            resolvedColor = SkinPaintColor.of(r, g, b, SkinPaintTypes.NORMAL);
            return resolvedColor;
        }
    }
}
