package moe.plushie.armourers_workshop.core.skin.animation;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.Objects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SkinAnimationKeyframe {

    private final float time;

    private final String key;
    private final SkinAnimationFunction function;

    private final List<SkinAnimationPoint> points;

    public SkinAnimationKeyframe(float time, String key, SkinAnimationFunction function, List<SkinAnimationPoint> points) {
        this.time = time;
        this.key = key;
        this.function = function;
        this.points = points;
    }

    public static SkinAnimationKeyframe readFromStream(String key, IInputStream stream) throws IOException {
        var time = stream.readFloat();
        var function = SkinAnimationFunction.readFromStream(stream);
        var points = new ArrayList<SkinAnimationPoint>();
        int type = stream.readVarInt();
        // old version is: 0,3,6
        if (type == 3 || type == 6) {
            points.addAll(readLegacyPointsFromStream(type, stream));
            type = 0;
        }
        while (type != 0) {
            var decoder = SkinAnimationPoint.getDecoder(type);
            if (decoder == null) {
                throw new IOException("can't read animation point of type: " + type);
            }
            points.add(decoder.apply(stream));
            type = stream.readVarInt();
        }
        return new SkinAnimationKeyframe(time, key, function, points);
    }

    public void writeToStream(String key, IOutputStream stream) throws IOException {
        stream.writeFloat(time);
        function.writeToStream(stream);
        // write all points into stream.
        for (var point : points) {
            int type = SkinAnimationPoint.getType(point);
            if (type != 0) {
                stream.writeVarInt(type);
                point.writeToStream(stream);
            }
        }
        stream.writeVarInt(0);
    }

    public float getTime() {
        return time;
    }

    public String getKey() {
        return key;
    }

    public SkinAnimationFunction getFunction() {
        return function;
    }

    public List<SkinAnimationPoint> getPoints() {
        return points;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "key", key, "time", time, "function", function);
    }

    private static List<SkinAnimationPoint> readLegacyPointsFromStream(int length, IInputStream stream) throws IOException {
        var points = new ArrayList<SkinAnimationPoint>();
        var objects = new ArrayList<Object>();
        for (int i = 0; i < length; i++) {
            var flags = stream.readVarInt();
            if ((flags & 0x40) != 0) {
                objects.add(stream.readString());
            } else {
                objects.add(stream.readFloat());
            }
        }
        for (int i = 0; i < objects.size(); i += 3) {
            var x = objects.get(i);
            var y = objects.get(i + 1);
            var z = objects.get(i + 2);
            points.add(new SkinAnimationPoint.Bone(x, y, z));
        }
        return points;
    }
}
