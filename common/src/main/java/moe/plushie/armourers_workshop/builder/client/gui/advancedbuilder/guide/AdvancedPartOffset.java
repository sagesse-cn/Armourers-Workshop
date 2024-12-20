package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide;

import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Function;

public class AdvancedPartOffset {

//    public static final Builder<MannequinEntity> MANNEQUIN_ENTITY = new Builder<MannequinEntity>()
//            .put(SkinPartTypes.BIPPED_HAT, 0, 0, 0)
//            .put(SkinPartTypes.BIPPED_HEAD, 0, 0, 0)
//
//            .put(SkinPartTypes.BIPPED_CHEST, 0, 0, 0)
//            .put(SkinPartTypes.BIPPED_TORSO, 0, 6, 0)
//
//            .put(SkinPartTypes.BIPPED_LEFT_ARM, 5, 2, 0, -10, 0, -10)
//            .put(SkinPartTypes.BIPPED_RIGHT_ARM, -5, 2, 0, -15, 0, 10)
//
//            .put(SkinPartTypes.BIPPED_LEFT_HAND, 5, 2, 0, -10, 0, -10, 0, 6, 0)
//            .put(SkinPartTypes.BIPPED_RIGHT_HAND, -5, 2, 0, -15, 0, 10, 0, 6, 0)
//
//            .put(SkinPartTypes.BIPPED_SKIRT, 0, 12, 0)
//
//            .put(SkinPartTypes.BIPPED_LEFT_THIGH, 2, 12, 0, -1, 0, -1)
//            .put(SkinPartTypes.BIPPED_RIGHT_THIGH, -2, 12, 0, 1, 0, 1)
//
//            .put(SkinPartTypes.BIPPED_LEFT_LEG, 2, 12, 0, -1, 0, -1, 0, 6, 0)
//            .put(SkinPartTypes.BIPPED_RIGHT_LEG, -2, 12, 0, 1, 0, 1, 0, 6, 0)
//
//            .put(SkinPartTypes.BIPPED_LEFT_FOOT, 2, 12, 0, -1, 0, -1, 0, 8, 0)
//            .put(SkinPartTypes.BIPPED_RIGHT_FOOT, -2, 12, 0, 1, 0, 1, 0, 8, 0)
//
//            .put(SkinPartTypes.BIPPED_LEFT_WING, 0, 0, 2)
//            .put(SkinPartTypes.BIPPED_RIGHT_WING, 0, 0, 2)
//            .put(SkinPartTypes.BIPPED_LEFT_PHALANX, 0, 0, 0)
//            .put(SkinPartTypes.BIPPED_RIGHT_PHALANX, 0, 0, 0);


    public static class Builder<T> {

        private final HashMap<SkinPartType, Function<T, OpenTransform3f>> poses = new HashMap<>();

//        public Builder<T> put(SkinPartType partType) {
//            return put(partType, partType.getRenderOffset());
//        }
//
//        public Builder<T> put(SkinPartType partType, Function<T, Rotations> provider) {
//            return put(partType, partType.getRenderOffset(), provider);
//        }
//
//        public Builder<T> put(SkinPartType partType, IVector3i off) {
//            ModelPartPose pose = new ModelPartPose(off.getX(), off.getY(), off.getZ(), 0, 0, 0);
//            return add(partType, it -> pose);
//        }

        public Builder<T> put(SkinPartType partType, float tx, float ty, float tz) {
            return put(partType, tx, ty, tz, 0, 0, 0, 0, 0, 0);
        }

        public Builder<T> put(SkinPartType partType, float tx, float ty, float tz, float xRot, float yRot, float zRot) {
            return put(partType, tx, ty, tz, xRot, yRot, zRot, 0, 0, 0);
        }

        public Builder<T> put(SkinPartType partType, float tx, float ty, float tz, float xRot, float yRot, float zRot, float ax, float ay, float az) {
            var translate = new OpenVector3f(tx, ty, tz);
            var rotation = new OpenVector3f(xRot, yRot, zRot);
            var afterTranslate = new OpenVector3f(ax, ay, az);
            var transform = OpenTransform3f.create(translate, rotation, OpenVector3f.ONE, OpenVector3f.ZERO, afterTranslate);
            return add(partType, it -> transform);
        }


        @Nullable
        public OpenTransform3f get(T entity, SkinPartType partType) {
            var provider = poses.get(partType);
            if (provider != null) {
                return provider.apply(entity);
            }
            return null;
        }

        private Builder<T> add(SkinPartType partType, Function<T, OpenTransform3f> provider) {
            poses.put(partType, provider);
            return this;
        }
    }
}
