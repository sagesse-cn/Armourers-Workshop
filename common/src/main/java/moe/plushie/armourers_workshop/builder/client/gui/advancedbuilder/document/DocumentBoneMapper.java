package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.armature.Armature;
import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

import java.util.Map;
import java.util.function.Function;

public class DocumentBoneMapper {

    private static final ImmutableMap<String, ISkinPartType> BOW_PARTS = new ImmutableMap.Builder<String, ISkinPartType>()
            .put("Arrow", SkinPartTypes.ITEM_ARROW)
            .put("Frame0", SkinPartTypes.ITEM_BOW0)
            .put("Frame1", SkinPartTypes.ITEM_BOW1)
            .put("Frame2", SkinPartTypes.ITEM_BOW2)
            .put("Frame3", SkinPartTypes.ITEM_BOW3)
            .build();

    private static final ImmutableMap<String, ISkinPartType> FINISHING_PARTS = new ImmutableMap.Builder<String, ISkinPartType>()
            .put("Hook", SkinPartTypes.ITEM_FISHING_HOOK)
            .put("Frame0", SkinPartTypes.ITEM_FISHING_ROD)
            .put("Frame1", SkinPartTypes.ITEM_FISHING_ROD1)
            .build();

    private final Function<String, Entry> provider;

    public DocumentBoneMapper(Function<String, Entry> provider) {
        this.provider = provider;
    }

    public static DocumentBoneMapper of(ISkinType skinType) {
        // read bow item
        if (skinType == SkinTypes.ITEM_BOW) {
            return of(BOW_PARTS);
        }
        // read fishing item
        if (skinType == SkinTypes.ITEM_FISHING) {
            return of(FINISHING_PARTS);
        }
        // read from armature
        return of(Armatures.byType(skinType));
    }

    public static DocumentBoneMapper of(Armature armature) {
        return new DocumentBoneMapper(name -> {
            var joint = armature.getJoint(name);
            if (joint != null) {
                var partType = armature.getPartType(joint);
                if (partType != null) {
                    return new Entry(joint, partType);
                }
            }
            return null;
        });
    }

    public static DocumentBoneMapper of(Map<String, ISkinPartType> map) {
        return new DocumentBoneMapper(name -> {
            var partType = map.get(name);
            if (partType != null) {
                return new Entry(null, partType);
            }
            return null;
        });
    }

    public Entry get(String name) {
        var entry = provider.apply(name);
        if (entry != null) {
            return entry;
        }
        return Entry.NONE;
    }

    public boolean isEmpty() {
        return false;
    }

    public static class Entry {

        public static final Entry NONE = new Entry(null, SkinPartTypes.ADVANCED);

        private final ISkinPartType type;

        public Entry(IJoint joint, ISkinPartType type) {
            this.type = type;
        }

        public boolean isRootPart() {
            return type != SkinPartTypes.ADVANCED;
        }

        public Vector3f getOffset() {
            if (type == SkinPartTypes.BIPPED_CHEST || type == SkinPartTypes.BIPPED_TORSO) {
                return new Vector3f(0, 6, 0);
            }
            return Vector3f.ZERO;
        }

        public ISkinPartType getType() {
            return type;
        }
    }
}
