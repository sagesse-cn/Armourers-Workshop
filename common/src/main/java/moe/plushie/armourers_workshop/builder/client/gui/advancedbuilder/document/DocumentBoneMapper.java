package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.part.ISkinPartType;
import moe.plushie.armourers_workshop.core.armature.Armature;
import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.Collections;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class DocumentBoneMapper {

    private static final Set<ISkinType> SINGLE_TYPES = Collections.immutableSet(builder -> {
        builder.add(SkinTypes.ITEM_SWORD);
        builder.add(SkinTypes.ITEM_SHIELD);
        //builder.add(SkinTypes.ITEM_BOW);
        builder.add(SkinTypes.ITEM_TRIDENT);

        builder.add(SkinTypes.ITEM_PICKAXE);
        builder.add(SkinTypes.ITEM_AXE);
        builder.add(SkinTypes.ITEM_SHOVEL);
        builder.add(SkinTypes.ITEM_HOE);

        builder.add(SkinTypes.ITEM_FISHING);

        builder.add(SkinTypes.ITEM);
        builder.add(SkinTypes.BLOCK);
    });

    private static final Map<String, ISkinPartType> BOW_PARTS = Collections.immutableMap(builder -> {
        builder.put("Arrow", SkinPartTypes.ITEM_ARROW);
        builder.put("Frame0", SkinPartTypes.ITEM_BOW0);
        builder.put("Frame1", SkinPartTypes.ITEM_BOW1);
        builder.put("Frame2", SkinPartTypes.ITEM_BOW2);
        builder.put("Frame3", SkinPartTypes.ITEM_BOW3);
    });

    private static final Map<String, ISkinPartType> FINISHING_PARTS = Collections.immutableMap(builder -> {
        builder.put("Hook", SkinPartTypes.ITEM_FISHING_HOOK);
        builder.put("Frame0", SkinPartTypes.ITEM_FISHING_ROD);
        builder.put("Frame1", SkinPartTypes.ITEM_FISHING_ROD1);
    });

    private final ISkinType type;
    private final Function<String, Entry> provider;

    public DocumentBoneMapper(ISkinType type, Function<String, Entry> provider) {
        this.type = type;
        this.provider = provider;
    }

    public static DocumentBoneMapper of(ISkinType type) {
        // read bow item
        if (type == SkinTypes.ITEM_BOW) {
            return of(type, BOW_PARTS);
        }
        // read fishing item
        if (type == SkinTypes.ITEM_FISHING) {
            return of(type, FINISHING_PARTS);
        }
        // read from armature
        return of(type, Armatures.byType(type));
    }

    private static DocumentBoneMapper of(ISkinType type, Armature armature) {
        return new DocumentBoneMapper(type, name -> {
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

    private static DocumentBoneMapper of(ISkinType type, Map<String, ISkinPartType> map) {
        return new DocumentBoneMapper(type, name -> {
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


    public Entry getRoot() {
        if (SINGLE_TYPES.contains(type)) {
            for (var partType : type.getParts()) {
                return new Entry(null, partType);
            }
        }
        return null;
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
