package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.nbt.CompoundTag;

import moe.plushie.armourers_workshop.api.skin.paint.ISkinPaintColor;
import moe.plushie.armourers_workshop.core.data.color.BlockPaintColor;
import moe.plushie.armourers_workshop.core.skin.paint.SkinPaintColor;
import moe.plushie.armourers_workshop.core.utils.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NumericTag;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public class OptionalAPI {

    public static boolean getOptionalBoolean(@This CompoundTag tag, String key, boolean defaultValue) {
        if (tag.contains(key, Constants.TagFlags.BYTE)) {
            return tag.getBoolean(key);
        }
        return defaultValue;
    }

    public static void putOptionalBoolean(@This CompoundTag tag, String key, boolean value, boolean defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            tag.putBoolean(key, value);
        }
    }

    public static int getOptionalInt(@This CompoundTag tag, String key, int defaultValue) {
        if (tag.contains(key, Constants.TagFlags.INT)) {
            return tag.getInt(key);
        }
        return defaultValue;
    }

    public static void putOptionalInt(@This CompoundTag tag, String key, int value, int defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            tag.putInt(key, value);
        }
    }

    public static Number getOptionalNumber(@This CompoundTag tag, String key, Number defaultValue) {
        if (tag.contains(key, Constants.TagFlags.ANY_NUMERIC)) {
            if (tag.get(key) instanceof NumericTag numericTag) {
                return numericTag.getAsNumber();
            }
        }
        return defaultValue;
    }

    public static void putOptionalNumber(@This CompoundTag tag, String key, Number value, Number defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            if (value instanceof Byte byteValue) {
                tag.putByte(key, byteValue);
            } else if (value instanceof Short shortValue) {
                tag.putShort(key, shortValue);
            } else if (value instanceof Integer intValue) {
                tag.putInt(key, intValue);
            } else if (value instanceof Long longValue) {
                tag.putLong(key, longValue);
            } else if (value instanceof Float floatValue) {
                tag.putFloat(key, floatValue);
            } else if (value instanceof Double doubleValue) {
                tag.putDouble(key, doubleValue);
            }
        }
    }

    public static String getOptionalString(@This CompoundTag tag, String key, String defaultValue) {
        if (tag.contains(key, Constants.TagFlags.STRING)) {
            return tag.getString(key);
        }
        return defaultValue;
    }

    public static void putOptionalString(@This CompoundTag tag, String key, String value, String defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            tag.putString(key, value);
        }
    }

    public static BlockPos getOptionalBlockPos(@This CompoundTag tag, String key, BlockPos defaultValue) {
        if (tag.contains(key, Constants.TagFlags.LONG)) {
            return BlockPos.of(tag.getLong(key));
        }
        return defaultValue;
    }

    public static void putOptionalBlockPos(@This CompoundTag tag, String key, BlockPos value, BlockPos defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            tag.putLong(key, value.asLong());
        }
    }

    public static Rotations getOptionalRotations(@This CompoundTag tag, String key, Rotations defaultValue) {
        ListTag listTag = tag.getList(key, Constants.TagFlags.FLOAT);
        if (listTag.size() >= 3) {
            return new Rotations(listTag);
        }
        return defaultValue;
    }

    public static void putOptionalRotations(@This CompoundTag tag, String key, Rotations value, Rotations defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            tag.put(key, value.save());
        }
    }

    public static SkinPaintColor getOptionalPaintColor(@This CompoundTag tag, String key, SkinPaintColor defaultValue) {
        if (tag != null && tag.contains(key, Constants.TagFlags.INT)) {
            return SkinPaintColor.of(tag.getInt(key));
        }
        return defaultValue;
    }

    public static void putOptionalPaintColor(@This CompoundTag tag, String key, ISkinPaintColor value, SkinPaintColor defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            tag.putInt(key, value.getRawValue());
        }
    }

//    public static BlockPaintColor getOptionalBlockPaintColor(@This CompoundTag tag, String key, BlockPaintColor defaultValue) {
//        if (!tag.contains(key, Constants.TagFlags.COMPOUND)) {
//            return defaultValue;
//        }
//        CompoundTag colorNBT = tag.getCompound(key);
//        if (colorNBT.isEmpty()) {
//            return defaultValue;
//        }
//        BlockPaintColor color = new BlockPaintColor();
//        color.deserializeNBT(colorNBT);
//        return color;
//    }
//
//    public static void putOptionalBlockPaintColor(@This CompoundTag tag, String key, BlockPaintColor value, BlockPaintColor defaultValue) {
//        if (_shouldPutValue(tag, key, value, defaultValue)) {
//            tag.put(key, value.serializeNBT());
//        }
//    }

    private static <T> boolean _shouldPutValue(CompoundTag tag, String key, T value, T defaultValue) {
        if (tag == null || key == null) {
            return false;
        }
        if (value == null || value.equals(defaultValue)) {
            tag.remove(key);
            return false;
        }
        return true;
    }
}
