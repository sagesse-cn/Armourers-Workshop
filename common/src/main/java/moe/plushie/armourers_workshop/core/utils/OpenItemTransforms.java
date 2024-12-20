package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;

public class OpenItemTransforms extends LinkedHashMap<String, OpenTransform3f> {

    public static IDataCodec<OpenItemTransforms> CODEC = IDataCodec.COMPOUND_TAG.xmap(OpenItemTransforms::new, OpenItemTransforms::serializeNBT);

    public OpenItemTransforms() {
    }

    public OpenItemTransforms(CompoundTag nbt) {
        for (var key : nbt.getAllKeys()) {
            put(key, deserializeTransform(nbt.getList(key, Constants.TagFlags.FLOAT)));
        }
    }

    public void put(OpenItemDisplayContext key, OpenTransform3f value) {
        put(key.getName(), value);
    }

    public OpenTransform3f get(OpenItemDisplayContext key) {
        return get(key.getName());
    }


    public void setOffset(OpenTransform3f offset) {
        put("offset", offset);
    }

    @Nullable
    public OpenTransform3f getOffset() {
        return get("offset");
    }


    public CompoundTag serializeNBT() {
        var nbt = new CompoundTag();
        forEach((key, value) -> nbt.put(key, serializeTransform(value)));
        return nbt;
    }

    private ListTag serializeTransform(OpenTransform3f transform) {
        var tag = new ListTag();
        if (transform.isIdentity()) {
            return tag;
        }
        var translate = transform.translate();
        tag.add(FloatTag.valueOf(translate.x()));
        tag.add(FloatTag.valueOf(translate.y()));
        tag.add(FloatTag.valueOf(translate.z()));

        var rotation = transform.rotation();
        tag.add(FloatTag.valueOf(rotation.x()));
        tag.add(FloatTag.valueOf(rotation.y()));
        tag.add(FloatTag.valueOf(rotation.z()));

        var scale = transform.scale();
        tag.add(FloatTag.valueOf(scale.x()));
        tag.add(FloatTag.valueOf(scale.y()));
        tag.add(FloatTag.valueOf(scale.z()));

        return tag;
    }

    private OpenTransform3f deserializeTransform(ListTag tag) {
        if (tag.isEmpty() || tag.size() < 9) {
            return OpenTransform3f.IDENTITY;
        }
        var tx = tag.getFloat(0);
        var ty = tag.getFloat(1);
        var tz = tag.getFloat(2);
        var translate = new OpenVector3f(tx, ty, tz);

        var rx = tag.getFloat(3);
        var ry = tag.getFloat(4);
        var rz = tag.getFloat(5);
        var rotation = new OpenVector3f(rx, ry, rz);

        var sx = tag.getFloat(6);
        var sy = tag.getFloat(7);
        var sz = tag.getFloat(8);
        var scale = new OpenVector3f(sx, sy, sz);

        return OpenTransform3f.create(translate, rotation, scale);
    }
}
